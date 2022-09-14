package grebnev.yadoa.controller.dto;

import grebnev.yadoa.service.model.SystemItemType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class SystemItemHistoryUnit {
    private String id;
    private String url;
    private String parentId;
    private SystemItemType type;
    private Long size;
    private Instant date;
}
