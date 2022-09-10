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

@Service
@RequiredArgsConstructor
public class SystemItemService {
    private final SystemItemRepository repository;
    private final SystemItemMapper mapper;

    @Transactional
    public void add(SystemItemImportRequest request) {
        Set<String> idsFromRequest = getIdsFromRequest(request);
        List<SystemItemEntity> existingEntities = repository.findAllById(idsFromRequest);
        List<SystemItemEntity> entitiesToSave = getEntitiesToSave(request, idsFromRequest, existingEntities);
        repository.saveAll(entitiesToSave);
    }


    private void checkParentIsPresentOrThrow(String parentId, Set<String> idsFromRequest, Set<String> existingIds) {
        if (parentId == null) return;
        if (existingIds.contains(parentId)) return;
        if (idsFromRequest.contains(parentId)) return;
        throw new ValidationException("Parent for item isn't exist");
    }

    private void checkTypeChangingOrThrow(SystemItemImport dto, List<SystemItemEntity> entities) {
        String curId = dto.getId();
        for (SystemItemEntity entity : entities) {
            if (entity.getId().equals(curId)) {
                if (!dto.getType().equals(entity.getType().name()))
                    throw new ValidationException("Parent for item isn't exist");
                ;
            }
        }
    }

    private Set<String> getExistingIds(List<SystemItemEntity> existingEntities) {
        Set<String> result = new HashSet<>(existingEntities.size() * 2);
        for (SystemItemEntity existingEntity : existingEntities) {
            result.add(existingEntity.getId());
            String parentId = existingEntity.getParentId();
            if (parentId != null) result.add(parentId);
        }
        return result;
    }

    private List<SystemItemEntity> getEntitiesToSave(
            SystemItemImportRequest request,
            Set<String> idsFromRequest,
            List<SystemItemEntity> existingEntities
    ) {
        List<SystemItemEntity> entitiesToSave = new ArrayList<>();
        Set<String> existingIds = getExistingIds(existingEntities);
        for (SystemItemImport dto : request.getItems()) {
            String curId = dto.getId();
            String parentId = dto.getParentId();
            checkParentIsPresentOrThrow(parentId, idsFromRequest, existingIds);
            if (existingIds.contains(curId)) checkTypeChangingOrThrow(dto, existingEntities);
            entitiesToSave.add(mapper.dtoToEntity(dto, request.getUpdateDate()));
        }
        return entitiesToSave;
    }

    private Set<String> getIdsFromRequest(SystemItemImportRequest request) {
        Set<String> idsFromRequest = new HashSet<>(request.getItems().size() * 2);
        for (SystemItemImport dto : request.getItems()) {
            idsFromRequest.add(dto.getId());
            String parentId = dto.getParentId();
            if (parentId != null) idsFromRequest.add(parentId);
        }
        return idsFromRequest;
    }

    //    private Map<String, SystemItemEntity> entityToCreate;
//    private Map<String, SystemItemEntity> otherEntitiesToCreate;
//    private Map<String, SystemItemEntity> existingEntities;
//    private Set<Object> idsFromRequest;
    //        for (SystemItemImport item : imports) {
//        idsFromRequest = new HashSet<>(imports.size());
//        existingEntities = new HashMap<>(imports.size());
//        entityToCreate = new HashMap<>(imports.size());
//    private void fillMapsToCreate(List<SystemItemImport> imports) {
//
////    }
////
//        fillEntitiesToSave(entitiesToSave, )
//        ArrayList<SystemItemEntity> entitiesToSave = new ArrayList<>(imports.size());
//        }
//            existingEntities.put(curId, existingEntity);
//            entityToCreate.remove(curId);
//            String curId = existingEntity.getId();
//        for (SystemItemEntity existingEntity : entitiesFromRepo) {
//        List<SystemItemEntity> entitiesFromRepo = repository.findAllById(entityToCreate.keySet());
//        fillMapsToCreate(imports);
//        List<SystemItemImport> imports = request.getItems();
//    private List<SystemItemEntity> convertRequestToEntities(SystemItemImportRequest request) {

//            String parentId = item.getParentId();
//            String curId = item.getId();
//            if (parentId != null) {
//                entityToCreate.put(parentId, null);
//            }
//            entityToCreate.put(curId, null);
//
//        }
//    }
//
//    private Set<String> getParentsIdsFromRequest(SystemItemImportRequest request) {
//        return request.getItems().stream()
//                .map(SystemItemImport::getParentId)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toSet());

//    }

//    private void checkTypeChangingOrThrow(SystemItemEntity entity, SystemItemImport dto) {
//        if (entity.getType() != SystemItemType.valueOf(dto.getType()))
//            throw new ValidationException("Item cannot change its type");
//    }

//    private Optional<SystemItemEntity> getFromListById(List<SystemItemEntity> entities, String requiredId) {
//        for (SystemItemEntity entity : entities) {
//            if (entity.getId().equals(requiredId)) return Optional.of(entity);
//        }
//        return Optional.empty();
//    }
}
