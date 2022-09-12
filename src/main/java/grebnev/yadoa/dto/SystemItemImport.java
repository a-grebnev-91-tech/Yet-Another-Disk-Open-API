package grebnev.yadoa.dto;

import grebnev.yadoa.validation.ValidItemType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class SystemItemImport {
    @NotBlank
    private String id;
    @Size(max = 255)
    private String url;
    private String parentId;
    @ValidItemType
    private String type;
    private Long size;
}
