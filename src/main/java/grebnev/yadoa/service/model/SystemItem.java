package grebnev.yadoa.service.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SystemItem {
    @EqualsAndHashCode.Include
    private final String id;
    private String url;
    @Getter(AccessLevel.NONE)
    private Instant date;
    @Setter(AccessLevel.NONE)
    private String parentId;
    @Setter(AccessLevel.NONE)
    private Optional<SystemItem> parent = Optional.empty();
    private final SystemItemType type;
    @Getter(AccessLevel.NONE)
    private Long size;
    @Getter(AccessLevel.NONE)
    private final Map<String, SystemItem> children;

    public SystemItem(String id, SystemItemType type) {
        this.id = id;
        this.type = type;
        if (type.equals(SystemItemType.FOLDER)) {
            this.children = new HashMap<>();
        } else {
            this.children = null;
        }
    }

    public void addChild(SystemItem child) {
        if (children == null || child == null) return;
        children.put(child.getId(), child);
    }

    public Map<String, SystemItem> getChildren() {
        if (children == null) return null;
        return Map.copyOf(children);
    }

    public Instant getDate() {
        if (children != null && !children.isEmpty()) {
            Instant maxDateFromChildren = children.values()
                    .stream()
                    .map(SystemItem::getDate)
                    .max(Instant::compareTo)
                    .get();
            if (maxDateFromChildren.isAfter(this.date)) this.date = maxDateFromChildren;
        }
        return date;
    }

    public Long getSize() {
        if (this.type.equals(SystemItemType.FILE)) {
            return size;
        } else {
            String id = this.getId();
            return children.values().stream()
                    .map(SystemItem::getSize)
                    .filter(Objects::nonNull)
                    .reduce(0L, Long::sum);
        }
    }

    public void setParentId(String parentId) {
        if (this.parent.isEmpty()) this.parentId = parentId;
    }

    public void setParent(SystemItem parent) {
        if (parent == null) {
            this.parent = Optional.empty();
            this.parentId = null;
        } else {
            if (parent.getType().equals(SystemItemType.FOLDER)) {
                this.parent = Optional.of(parent);
                this.parentId = parent.getId();
            }
        }
    }

    public void removeChild(SystemItem child) {
        children.remove(child.getId());
    }
}
