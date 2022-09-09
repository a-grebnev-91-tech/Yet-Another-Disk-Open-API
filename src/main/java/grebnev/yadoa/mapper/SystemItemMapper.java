package grebnev.yadoa.mapper;

import grebnev.yadoa.dto.SystemItemImport;
import grebnev.yadoa.repository.SystemItemEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;

@Mapper
public interface SystemItemMapper {
    SystemItemEntity dtoToEntity(SystemItemImport dto, LocalDateTime updateDate);

    //TODO test if set null to null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntity(
            @MappingTarget SystemItemEntity currentOptionalEntity,
            SystemItemImport dto,
            LocalDateTime updateDate
    );
}
