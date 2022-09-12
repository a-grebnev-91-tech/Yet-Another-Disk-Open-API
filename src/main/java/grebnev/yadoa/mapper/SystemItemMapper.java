package grebnev.yadoa.mapper;

import grebnev.yadoa.dto.SystemItemExport;
import grebnev.yadoa.dto.SystemItemImport;
import grebnev.yadoa.entity.SystemItemEntity;
import grebnev.yadoa.model.SystemItem;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", uses = {SystemItemReferenceMapper.class})
public interface SystemItemMapper {
    @Mapping(source = "updateDate", target = "date")
    @Mapping(source = "dto.type", target = "type")
    SystemItemEntity dtoToEntity(SystemItemImport dto, Instant updateDate);

    List<SystemItemExport> filesToDto(List<SystemItemEntity> files);

    @Mapping(source = "parent", target = "parentId")
    SystemItemExport modelToDto(SystemItem model);

    //todo remove?
    //TODO test if set null to null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "updateDate", target = "date")
    void updateEntity(
            @MappingTarget SystemItemEntity currentOptionalEntity,
            SystemItemImport dto,
            Instant updateDate
    );
}
