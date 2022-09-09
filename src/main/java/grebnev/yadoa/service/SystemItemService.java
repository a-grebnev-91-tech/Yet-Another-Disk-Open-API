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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemItemService {
    private final SystemItemRepository repository;
    private final SystemItemMapper mapper;

    @Transactional
    public void add(SystemItemImportRequest request) {
        for (SystemItemImport dto : request.getItems()) {
            String curId = dto.getId();
            String parentId = dto.getParentId();
            Optional<SystemItemEntity> parentOptionalEntity;
            Optional<SystemItemEntity> currentOptionalEntity;

            if (parentId != null) {
                List<SystemItemEntity> currentOrParent = repository.findAllById(List.of(curId, parentId));
                parentOptionalEntity = getById(currentOrParent, parentId);
                if (parentOptionalEntity.isEmpty()) throw new ValidationException("Parent for item isn't exist");
                currentOptionalEntity = getById(currentOrParent, curId);
            } else {
                currentOptionalEntity = repository.findById(curId);
            }

            SystemItemEntity entity;
            if (currentOptionalEntity.isPresent()) {
                entity = currentOptionalEntity.get();
                checkTypeChangingOrThrow(entity, dto);
                mapper.updateEntity(currentOptionalEntity.get(), dto, request.getUpdateDate());
            } else {
                entity = mapper.dtoToEntity(dto, request.getUpdateDate());
                repository.save(entity);
            }
        }
    }

    private void checkTypeChangingOrThrow(SystemItemEntity entity, SystemItemImport dto) {
        if (entity.getType() != SystemItemType.valueOf(dto.getType()))
            throw new ValidationException("Item cannot change its type");
    }

    private Optional<SystemItemEntity> getById(List<SystemItemEntity> currentOrParent, String parentId) {
        for (SystemItemEntity systemItemEntity : currentOrParent) {
            if (systemItemEntity.getId().equals(parentId)) return Optional.of(systemItemEntity);
        }
        return Optional.empty();
    }
}
