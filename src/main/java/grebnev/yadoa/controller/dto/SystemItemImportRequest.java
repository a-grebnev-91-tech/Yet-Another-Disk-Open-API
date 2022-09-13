package grebnev.yadoa.controller.dto;

import grebnev.yadoa.validation.UniqueId;
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
    @UniqueId
    private List<@NotNull @Valid SystemItemImport> items;
    @NotNull
    private Instant updateDate;
}
