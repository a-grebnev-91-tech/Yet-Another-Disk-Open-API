package grebnev.yadoa.mapper;

import grebnev.yadoa.controller.dto.SystemItemExport;
import grebnev.yadoa.controller.dto.SystemItemImport;
import grebnev.yadoa.repository.entity.SystemItemEntity;
import grebnev.yadoa.service.model.SystemItem;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", uses = {SystemItemReferenceMapper.class})
public interface SystemItemMapper {
    List<SystemItemExport> filesToDto(List<SystemItemEntity> files);

    SystemItemExport modelToDto(SystemItem model);

    SystemItem dtoToModel(SystemItemImport dto, Instant date);

    SystemItem entityToModel(SystemItemEntity entity);

    List<SystemItemEntity> modelsToEntities(List<SystemItem> itemsToSave);
}
