package grebnev.yadoa.service;

import grebnev.yadoa.dto.SystemItemImport;
import grebnev.yadoa.dto.SystemItemImportRequest;
import grebnev.yadoa.mapper.SystemItemMapper;
import grebnev.yadoa.repository.SystemItemEntity;
import grebnev.yadoa.repository.SystemItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemItemService {
    private final SystemItemRepository repository;
    private final SystemItemMapper mapper;

    private Map<String, SystemItemEntity> entityToCreate;
//    private Map<String, SystemItemEntity> otherEntitiesToCreate;
    private Map<String, SystemItemEntity> existingEntities;
    private Set<Object> idsFromRequest;

    @Transactional
    public void add(SystemItemImportRequest request) {
        List<SystemItemEntity> entitiesToAdd = convertRequestToEntities(request);






//        Set<String> parentsIdsFromRequest = getParentsIdsFromRequest(request);
//        List<SystemItemEntity> entitiesToSave = new ArrayList<>();
//
//        for (SystemItemImport dto : request.getItems()) {
//            String curId = dto.getId();
//            String parentId = dto.getParentId();
//            Optional<SystemItemEntity> currentOptionalEntity;
//
//            if (parentId == null || parentsIdsFromRequest.contains(parentId)) {
//                currentOptionalEntity = repository.findById(curId);
//            } else {
//                List<SystemItemEntity> currentOrParent = repository.findAllById(List.of(curId, parentId));
//                Optional<SystemItemEntity> parentOptionalEntity = getFromListById(currentOrParent, parentId);
//                if (parentOptionalEntity.isEmpty()) throw new ValidationException("Parent for item isn't exist");
//                currentOptionalEntity = getFromListById(currentOrParent, curId);
//            }
//            entitiesToSave.add(mapper.dtoToEntity(dto, request.getUpdateDate()));
//        }
//        repository.saveAll(entitiesToSave);
        //first revision
//            SystemItemEntity entity;
//            if (currentOptionalEntity.isPresent()) {
//                entity = currentOptionalEntity.get();
//                checkTypeChangingOrThrow(entity, dto);
//                mapper.updateEntity(currentOptionalEntity.get(), dto, request.getUpdateDate());
//            } else {
//                entity = mapper.dtoToEntity(dto, request.getUpdateDate());
//                repository.save(entity);
//            }
//        }
    }

    private List<SystemItemEntity> convertRequestToEntities(SystemItemImportRequest request) {
        List<SystemItemImport> imports = request.getItems();
        fillMapsToCreate(imports);
        List<SystemItemEntity> entitiesFromRepo = repository.findAllById(entityToCreate.keySet());
        for (SystemItemEntity existingEntity : entitiesFromRepo) {
            String curId = existingEntity.getId();
            entityToCreate.remove(curId);
            existingEntities.put(curId, existingEntity);
        }
        ArrayList<SystemItemEntity> entitiesToSave = new ArrayList<>(imports.size());
        fillEntitiesToSave(entitiesToSave, )

    }

    private void fillMapsToCreate(List<SystemItemImport> imports) {
        entityToCreate = new HashMap<>(imports.size());
        existingEntities = new HashMap<>(imports.size());
        idsFromRequest = new HashSet<>(imports.size());
        for (SystemItemImport item : imports) {
            String parentId = item.getParentId();
            String curId = item.getId();
            if (parentId != null) {
                entityToCreate.put(parentId, null);
            }
            entityToCreate.put(curId, null);

        }
    }

    private Set<String> getParentsIdsFromRequest(SystemItemImportRequest request) {
        return request.getItems().stream()
                .map(SystemItemImport::getParentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private void checkTypeChangingOrThrow(SystemItemEntity entity, SystemItemImport dto) {
        if (entity.getType() != SystemItemType.valueOf(dto.getType()))
            throw new ValidationException("Item cannot change its type");
    }

    private Optional<SystemItemEntity> getFromListById(List<SystemItemEntity> entities, String requiredId) {
        for (SystemItemEntity entity : entities) {
            if (entity.getId().equals(requiredId)) return Optional.of(entity);
        }
        return Optional.empty();
    }
}
