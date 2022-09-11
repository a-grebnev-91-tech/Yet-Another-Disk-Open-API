package grebnev.yadoa.mapper;

import grebnev.yadoa.dto.SystemItemImport;
import grebnev.yadoa.entity.SystemItemEntity;
import org.mapstruct.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = {SystemItemReferenceMapper.class})
public interface SystemItemMapper {
    @Mapping(source = "updateDate", target = "date")
    @Mapping(source = "dto.type", target = "type")
    SystemItemEntity dtoToEntity(SystemItemImport dto, LocalDateTime updateDate);

    //TODO test if set null to null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(source = "updateDate", target = "date")
    void updateEntity(
            @MappingTarget SystemItemEntity currentOptionalEntity,
            SystemItemImport dto,
            LocalDateTime updateDate
    );
}
