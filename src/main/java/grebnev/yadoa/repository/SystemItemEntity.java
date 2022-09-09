package grebnev.yadoa.repository;

import grebnev.yadoa.service.SystemItemType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "system_items")
public class SystemItemEntity {
    @Id
    private String id;
    @Column(name = "url")
    private String url;
    @Column(name = "date")
    private LocalDateTime date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private SystemItemEntity parent;
    @JoinColumn(name = "type")
    private SystemItemType type;
    @JoinColumn(name = "size")
    private Long size;
}
