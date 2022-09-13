package grebnev.yadoa.service;

import grebnev.yadoa.controller.dto.SystemItemExport;
import grebnev.yadoa.controller.dto.SystemItemImport;
import grebnev.yadoa.controller.dto.SystemItemImportRequest;
import grebnev.yadoa.exception.NotFoundException;
import grebnev.yadoa.mapper.HierarchyMakerMapper;
import grebnev.yadoa.mapper.SystemItemMapper;
import grebnev.yadoa.service.model.SystemItem;
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
}
