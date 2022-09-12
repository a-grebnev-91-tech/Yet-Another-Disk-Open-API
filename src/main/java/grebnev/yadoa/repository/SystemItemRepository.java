package grebnev.yadoa.repository;

import grebnev.yadoa.entity.SystemItemEntity;
import grebnev.yadoa.model.SystemItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SystemItemRepository extends JpaRepository<SystemItemEntity, String> {
    @Query(value = " WITH RECURSIVE elements AS (" +
            " SELECT si1.id, si1.url, si1.updated, si1.parent, si1.type, si1.size, 0 AS level" +
            " FROM system_items AS si1 WHERE si1.id = :rootId" +
            " UNION" +
            " SELECT si2.id, si2.url, si2.updated, si2.parent, si2.type, si2.size, elements.level + 1 AS level" +
            "  FROM system_items AS si2" +
            " INNER JOIN elements ON (elements.id = si2.parent)" +
            ") SELECT * FROM ELEMENTS", nativeQuery = true)
    List<LeveledSystemItemEntity> findAllElementsByRoot(@Param("rootId") String rootId);

    @Query(value = "SELECT * FROM system_items" +
            " WHERE type = 'FILE'" +
            " AND (updated BETWEEN :from AND :to)", nativeQuery = true)
    List<SystemItemEntity> findLastUpdated(@Param("from") Instant from,@Param("to") Instant to);

    interface LeveledSystemItemEntity {
        String getId();

        String getUrl();

        Instant getUpdated();

        String getParent();

        SystemItemType getType();

        Long getSize();

        Integer getLevel();
    }
}
