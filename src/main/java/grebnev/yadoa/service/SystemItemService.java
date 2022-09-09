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
import java.util.ArrayList;
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
            Optional<SystemItemEntity> currentOptionalEntity = findExistingEntity(dto);
            SystemItemEntity entity;
            //todo добавить проверку родителя в текущем запросе
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

    private Optional<SystemItemEntity> findExistingEntity(SystemItemImport dto) {
        String curId = dto.getId();
        String parentId = dto.getParentId();
        Optional<SystemItemEntity> parentOptionalEntity;
        Optional<SystemItemEntity> currentOptionalEntity;

        if (parentId != null) {
            List<String> ids = new ArrayList<>();
            ids.add(curId);
            ids.add(parentId);
            List<SystemItemEntity> currentOrParent = repository.findAllByIdIn(ids);
            parentOptionalEntity = getFromListById(currentOrParent, parentId);
            if (parentOptionalEntity.isEmpty()) throw new ValidationException("Parent for item isn't exist");
            currentOptionalEntity = getFromListById(currentOrParent, curId);
        } else {
            currentOptionalEntity = repository.findById(curId);
        }
        return currentOptionalEntity;
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
