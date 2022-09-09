package grebnev.yadoa.dto;

import grebnev.yadoa.validation.ValidItemImport;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ValidItemImport
public class SystemItemImport {
    @NotBlank
    private String id;
    private String url;
    private String parentId;
    private String type;
    private Long size;
}
