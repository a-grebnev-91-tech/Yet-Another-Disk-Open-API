package grebnev.yadoa.controller.dto;

import grebnev.yadoa.service.model.SystemItemType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class SystemItemExport {
    private String id;
    private String url;
    private Instant date;
    private String parentId;
    private SystemItemType type;
    private Long size;
    private List<SystemItemExport> children;
}
