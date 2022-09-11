package grebnev.yadoa.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SystemItem {
    private String id;
    private String url;
    private LocalDateTime date;
    private String parentId;
    private SystemItemType type;
    private Long size;
    private List<SystemItem> children;
}
