package grebnev.yadoa.service;

import grebnev.yadoa.dto.SystemItemImport;
import grebnev.yadoa.dto.SystemItemImportRequest;
import grebnev.yadoa.exception.NotFoundException;
import grebnev.yadoa.mapper.SystemItemMapper;
import grebnev.yadoa.repository.SystemItemEntity;
import grebnev.yadoa.repository.SystemItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemItemService {
    private final SystemItemRepository repository;
    private final SystemItemMapper mapper;

    @Transactional
    public void add(SystemItemImportRequest request) {
        Map<String, Optional<SystemItemImport>> idsFromRequest = getIdsFromRequest(request);
        Map<String, SystemItemEntity> existingEntities = repository.findAllById(idsFromRequest.keySet()).stream()
                .collect(Collectors.toMap(SystemItemEntity::getId, Function.identity()));
        List<SystemItemEntity> entitiesToSave = getEntitiesToSave(request, idsFromRequest, existingEntities);
        repository.saveAll(entitiesToSave);
    }

    @Transactional
    public void delete(String id, LocalDateTime date) {
        Optional<SystemItemEntity> maybeParent = findParentById(id);
        repository.deleteById(id);
        if (maybeParent.isPresent()) {
            maybeParent.get().setDate(date);
            repository.save(maybeParent.get());
        }
    }

    private Optional<SystemItemEntity> findParentById(String id) {
        Optional<SystemItemEntity> maybeExisting = repository.findById(id);
        if (maybeExisting.isPresent()) return repository.findById(maybeExisting.get().getParentId());
        else throw new NotFoundException(String.format("Entity with id %s isn't exits", id));
    }

    private void checkIfExistOrThrow(String id) {

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
            checkParentTypeOrThrow(parent.getType());
            return;
        }
        //check if parent is present in import and type is FOLDER
        Optional<SystemItemImport> maybeParent = idsFromRequest.get(parentId);
        if (maybeParent.isPresent()) {
            SystemItemImport parent = maybeParent.get();
            checkParentTypeOrThrow(SystemItemType.valueOf(parent.getType()));
        } else {
            throw new ValidationException("Parent for item isn't exist");
        }
    }

    private void checkParentTypeOrThrow(SystemItemType type) {
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
            String curId = dto.getId();
            String parentId = dto.getParentId();
            checkParentOrThrow(parentId, idsFromRequest, existingEntities);
            if (existingEntities.containsKey(curId)) checkTypeChangingOrThrow(dto, existingEntities);
            entitiesToSave.add(mapper.dtoToEntity(dto, request.getUpdateDate()));
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
}
