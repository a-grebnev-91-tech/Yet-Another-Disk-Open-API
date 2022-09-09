package grebnev.yadoa.dto;

import grebnev.yadoa.validation.UniqueId;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SystemItemImportRequest {
    @NotNull
    @UniqueId
    private List<SystemItemImport> items;
    @NotNull
    private LocalDateTime updateDate;
}
