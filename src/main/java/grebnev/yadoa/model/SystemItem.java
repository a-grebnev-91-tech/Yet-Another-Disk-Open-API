package grebnev.yadoa.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SystemItem {
    @EqualsAndHashCode.Include
    private final String id;
    private String url;
    @Getter(AccessLevel.NONE)
    private LocalDateTime date;
    @Setter(AccessLevel.NONE)
    private SystemItem parent;
    private final SystemItemType type;
    private Long size;
    private final List<SystemItem> children;

    public SystemItem(String id, SystemItemType type) {
        this.id = id;
        this.type = type;
        if (type.equals(SystemItemType.FOLDER)) {
            this.children = new ArrayList<>();
        } else {
            this.children = null;
        }
    }

    public void addChild(SystemItem child) {
        if (children == null) return;
        child.setParent(this);
    }

    public LocalDateTime getDate() {
        if (children != null && !children.isEmpty()) {
            LocalDateTime maxDateFromChildren = children
                    .stream()
                    .map(SystemItem::getDate)
                    .max(LocalDateTime::compareTo)
                    .get();
            if (maxDateFromChildren.isAfter(this.date)) this.date = maxDateFromChildren;
        }
        return date;
    }

    public void setParent(SystemItem parent) {
        if (parent.getChildren() != null) {
            this.parent = parent;
            parent.getChildren().add(this);
        }
    }
}
