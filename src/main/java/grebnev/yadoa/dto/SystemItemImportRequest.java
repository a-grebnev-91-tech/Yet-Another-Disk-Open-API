package grebnev.yadoa.dto;

import grebnev.yadoa.validation.ValidItemImport;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class SystemItemImportRequest {
    @NotNull
    @ValidItemImport
    private List<SystemItemImport> items;
    //TODO добавить валидацию что дата в стандарте
    @NotNull
    private Instant updateDate;
}
