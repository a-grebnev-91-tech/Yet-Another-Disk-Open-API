package grebnev.yadoa.dto;

import grebnev.yadoa.validation.ValidItemImport;
import grebnev.yadoa.validation.ValidType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class SystemItemImport {
    @NotBlank
    private String id;
    @Max(255)
    private String url;
    private String parentId;
    @ValidType
    private String type;
    private Long size;
}
