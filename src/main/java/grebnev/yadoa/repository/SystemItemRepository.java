package grebnev.yadoa.repository;

import grebnev.yadoa.entity.SystemItemEntity;
import grebnev.yadoa.model.SystemItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SystemItemRepository extends JpaRepository<SystemItemEntity, String> {
 //not work//WITH RECURSIVE elements AS (SELECT si1.id, si1.url, si1.update_date, si1.parent_id, si1.type, si1.size FROM system_items AS si1 WHERE si1.id = '111-111' UNION SELECT si2.id, si2.url, si2.update_date, si2.parent_id, si2.type, si2.size  FROM system_items AS si2 INNER JOIN system_items AS si ON (si.parent_id = si2.id)) SELECT * FROM ELEMENTS;
    //WITH RECURSIVE elements AS (SELECT si1.id, si1.url, si1.update_date, si1.parent_id, si1.type, si1.size FROM system_items AS si1 WHERE si1.id = '111-111' UNION SELECT si2.id, si2.url, si2.update_date, si2.parent_id, si2.type, si2.size  FROM system_items AS si2 INNER JOIN system_items AS si ON (si.id = si2.parent_id)) SELECT * FROM ELEMENTS;
    @Query(value = " WITH RECURSIVE elements AS (" +
            " SELECT si1.id, si1.url, si1.update_date, si1.parent_id, si1.type, si1.size, 0 AS level" +
            " FROM system_items AS si1 WHERE si1.id = :rootId" +
            " UNION" +
            " SELECT si2.id, si2.url, si2.update_date, si2.parent_id, si2.type, si2.size, elements.level + 1 AS level" +
            "  FROM system_items AS si2" +
            " INNER JOIN elements ON (elements.id = si2.parent_id)" +
            ") SELECT * FROM ELEMENTS", nativeQuery = true)
    List<LeveledSystemItemEntity> findAllElementsByRoot(@Param("rootId") String rootId);

    interface LeveledSystemItemEntity {
        String getId();
        String getUrl();
        LocalDateTime getUpdateDate();
        String getParentId();
        SystemItemType getType();
        Long getSize();
        Integer getLevel();
    }
}
