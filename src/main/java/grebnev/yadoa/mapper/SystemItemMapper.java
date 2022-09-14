package grebnev.yadoa.mapper;

import grebnev.yadoa.controller.dto.SystemItemExport;
import grebnev.yadoa.controller.dto.SystemItemHistoryResponse;
import grebnev.yadoa.controller.dto.SystemItemHistoryUnit;
import grebnev.yadoa.controller.dto.SystemItemImport;
import grebnev.yadoa.repository.entity.SystemItemEntity;
import grebnev.yadoa.repository.entity.SystemItemHistoryEntity;
import grebnev.yadoa.service.model.SystemItem;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {SystemItemReferenceMapper.class})
public interface SystemItemMapper {
    //TODO REMOVE
//    @Mapping(source = "entities", target = "items")
//    List<SystemItemHistoryResponse> entitiesToHistoryDto(List<SystemItemEntity> entities);

    SystemItemHistoryUnit entityToHistoryUnit(SystemItemEntity entity);

    @Mapping(source = "itemId", target = "id")
    SystemItemHistoryUnit historyEntityToHistoryUnit(SystemItemHistoryEntity entity);

    SystemItemExport modelToDto(SystemItem model);

    SystemItem dtoToModel(SystemItemImport dto, Instant date);

    SystemItem entityToModel(SystemItemEntity entity);

    List<SystemItemEntity> modelsToEntities(List<SystemItem> itemsToSave);

}
