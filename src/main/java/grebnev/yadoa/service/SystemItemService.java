package grebnev.yadoa.service;

import grebnev.yadoa.controller.dto.SystemItemExport;
import grebnev.yadoa.controller.dto.SystemItemImport;
import grebnev.yadoa.controller.dto.SystemItemImportRequest;
import grebnev.yadoa.exception.NotFoundException;
import grebnev.yadoa.mapper.HierarchyMakerMapper;
import grebnev.yadoa.mapper.SystemItemMapper;
import grebnev.yadoa.service.model.SystemItem;
import grebnev.yadoa.service.model.SystemItemType;
import grebnev.yadoa.repository.entity.SystemItemEntity;
import grebnev.yadoa.repository.SystemItemRepository;
import grebnev.yadoa.validation.ItemValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemItemService {
    private final SystemItemRepository repository;
    private final SystemItemMapper mapper;
    private final HierarchyMakerMapper hierarchyMakerMapper;
    private final ItemValidator validator;

    @Transactional
    public void add(SystemItemImportRequest request) {
        if (validator.isInvalid(request.getItems())) {
            throw new ValidationException("Request is invalid");
        }
//        ItemsHierarchy requestHierarchy = ItemsHierarchy.getBuilder().makeByImport(request.getItems(), request.getUpdateDate(), mapper);
//        Set<String> idsFromRequest = requestHierarchy.getAllIds();
        //remove above
        Set<String> idsFromRequest = new HashSet<>(request.getItems().size() * 2);
        Map<String, SystemItem> itemsFromReq = request.getItems()
                .stream()
                .peek(itemImport -> {
                    if (itemImport.getParentId() != null) idsFromRequest.add(itemImport.getParentId());
                    idsFromRequest.add(itemImport.getId());
                })
                .collect(Collectors.toMap(SystemItemImport::getId, dto -> mapper.dtoToModel(dto, request.getUpdateDate())));
        ItemsHierarchy existing = ItemsHierarchy
                .getBuilder()
                .makeByEntities(repository.findAllElementsInTreeByIds(idsFromRequest), mapper);
        existing.addAll(itemsFromReq);
        List<SystemItem> itemsToSave = existing.getAll();
        repository.saveAll(mapper.modelsToEntities(itemsToSave));
        //1st iteration
//        ItemsHierarchy requestHierarchy = ItemsHierarchy.getBuilder().makeByImport(request.getItems(), request.getUpdateDate(), mapper);
//        Set<String> idsFromRequest = requestHierarchy.getAllIds();
//        ItemsHierarchy existing = ItemsHierarchy
//                        .getBuilder()
//                        .makeByEntities(repository.findAllElementsInTreeByIds(idsFromRequest), mapper);
//        existing.addAll(requestHierarchy);
//        repository.saveAll(existing.getAllEntities());
    }

    //Deletion of nested items is implemented by a stored procedure in the db
    @Transactional
    public void delete(String id, Instant date) {
        Optional<SystemItemEntity> maybeParent = findParentById(id);
        repository.deleteById(id);
        if (maybeParent.isPresent()) {
            maybeParent.get().setDate(date);
            repository.save(maybeParent.get());
        }
    }

    public List<SystemItemExport> findLastUpdated(Instant to) {
        int secondsInDay = 24 * 60 * 60;
        Instant from = to.minusSeconds(secondsInDay);
        return mapper.filesToDto(repository.findLastUpdated(from, to));
    }

    public SystemItemExport findById(String id) {
        List<SystemItemRepository.LeveledSystemItemEntity> items = repository.findAllElementsByRoot(id);
        if (items.size() == 0) throw new NotFoundException(String.format("Item with id %s isn't exist", id));
        SystemItem root = hierarchyMakerMapper.getHierarchy(items);
        return mapper.modelToDto(root);
    }

    private Optional<SystemItemEntity> findParentById(String childId) {
        Optional<SystemItemEntity> maybeChild = repository.findById(childId);
        if (maybeChild.isPresent()) {
            String parentId = maybeChild.get().getParentId();
            if (parentId != null) {
                return repository.findById(parentId);
            } else {
                return Optional.empty();
            }
        } else {
            throw new NotFoundException(String.format("Entity with id %s isn't exits", childId));
        }
    }

    private void checkParentOrThrow(
            String parentId,
            Map<String, Optional<SystemItemImport>> idsFromRequest,
            Map<String, SystemItemEntity> existingIds
    ) {
        if (parentId == null) {
            return;
        }
        //check parent existing & correct type
        if (existingIds.containsKey(parentId)) {
            SystemItemEntity parent = existingIds.get(parentId);
            checkParentIsFolderOrThrow(parent.getType());
            return;
        }
        //check if parent is present in import and type is FOLDER
        Optional<SystemItemImport> maybeParent = idsFromRequest.get(parentId);
        if (maybeParent.isPresent()) {
            SystemItemImport parent = maybeParent.get();
            checkParentIsFolderOrThrow(SystemItemType.valueOf(parent.getType()));
        } else {
            throw new ValidationException("Parent for item isn't exist");
        }
    }

    private void checkParentIsFolderOrThrow(SystemItemType type) {
        if (type.equals(SystemItemType.FILE)) throw new ValidationException("Parent for item is FILE");
    }

    private void checkTypeChangingOrThrow(SystemItemImport dto, Map<String, SystemItemEntity> entities) {
        SystemItemEntity entity = entities.get(dto.getId());
        if (!dto.getType().equals(entity.getType().name()))
            throw new ValidationException("Detected attempt to change item Type");
    }

    private List<SystemItemEntity> getEntitiesToSave(
            SystemItemImportRequest request,
            Map<String, Optional<SystemItemImport>> idsFromRequest,
            Map<String, SystemItemEntity> existingEntities
    ) {
        List<SystemItemEntity> entitiesToSave = new ArrayList<>();
        for (SystemItemImport dto : request.getItems()) {
            checkParentOrThrow(dto.getParentId(), idsFromRequest, existingEntities);
            if (existingEntities.containsKey(dto.getId())) checkTypeChangingOrThrow(dto, existingEntities);
            entitiesToSave.add(mapper.dtoToEntity(dto, request.getUpdateDate()));
            //should update parent date if current item was moved
            if (parentShouldBeUpdate(dto, existingEntities)) {
                SystemItemEntity existingEntity = existingEntities.get(dto.getId());
                String parentId = existingEntity.getParentId();
                if (parentId != null) {
                    Optional<SystemItemEntity> maybeParent = repository.findById(existingEntity.getParentId());
                    if (maybeParent.isPresent()) {
                        SystemItemEntity existingParent = maybeParent.get();
                        existingParent.setDate(request.getUpdateDate());
                        entitiesToSave.add(existingParent); //TODO check if can be del
                    }
                }
            }
        }
        return entitiesToSave;
    }

    private Map<String, Optional<SystemItemImport>> getIdsFromRequest(SystemItemImportRequest request) {
        Map<String, Optional<SystemItemImport>> idsFromRequest = new HashMap<>(request.getItems().size() * 2);
        for (SystemItemImport dto : request.getItems()) {
            idsFromRequest.put(dto.getId(), Optional.of(dto));
            String parentId = dto.getParentId();
            if (parentId != null && !idsFromRequest.containsKey(parentId)) {
                idsFromRequest.put(parentId, Optional.empty());
            }
        }
        return idsFromRequest;
    }

    //check if file or dir was moved
    private boolean parentShouldBeUpdate(SystemItemImport dto, Map<String, SystemItemEntity> existingEntities) {
        String id = dto.getId();
        SystemItemEntity existingEntity = existingEntities.get(id);
        if (existingEntity == null) return false;
        return !Objects.equals(dto.getParentId(), existingEntity.getParentId());
    }
}
