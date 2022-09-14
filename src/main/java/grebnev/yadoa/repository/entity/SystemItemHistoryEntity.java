package grebnev.yadoa.repository.entity;

import grebnev.yadoa.service.model.SystemItemType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "history")
public class SystemItemHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "item_id")
    private String itemId;
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
