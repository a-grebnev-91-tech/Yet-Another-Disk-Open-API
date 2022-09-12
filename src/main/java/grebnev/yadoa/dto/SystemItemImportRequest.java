package grebnev.yadoa.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class SystemItemImportRequest {
    @NotNull
    private List<@NotNull @Valid SystemItemImport> items;
    @NotNull
    private Instant updateDate;
}
