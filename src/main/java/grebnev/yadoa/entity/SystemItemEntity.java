package grebnev.yadoa.entity;

import grebnev.yadoa.model.SystemItemType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "system_items")
public class SystemItemEntity {
    @Id
    private String id;
    @Column(name = "url")
    private String url;
    @Column(name = "updated")
    private Instant date;
    @Column(name = "parent")
    private String parentId;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SystemItemType type;
    @Column(name = "size")
    private Long size;
}
