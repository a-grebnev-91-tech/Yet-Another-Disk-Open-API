package grebnev.yadoa.repository;

import grebnev.yadoa.repository.entity.SystemItemEntity;
import grebnev.yadoa.service.model.SystemItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
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

    @Query(value = "SELECT DISTINCT * FROM" +
            " ((WITH RECURSIVE rec1 AS" +
            " (SELECT si1.id, si1.url, si1.updated, si1.parent, si1.type, si1.size " +
            " FROM system_items AS si1 WHERE si1.id IN (:ids)" +
            " UNION" +
            " SELECT si2.id, si2.url, si2.updated, si2.parent, si2.type, si2.size" +
            " FROM system_items AS si2" +
            " INNER JOIN rec1 ON (rec1.id = si2.parent))" +
            " SELECT * FROM rec1)" +
            " UNION ALL" +
            " (WITH RECURSIVE rec2 AS" +
            " (SELECT si3.id, si3.url, si3.updated, si3.parent, si3.type, si3.size" +
            " FROM system_items AS si3 WHERE si3.id IN (:ids)" +
            " UNION SELECT si4.id, si4.url, si4.updated, si4.parent, si4.type, si4.size" +
            " FROM system_items AS si4 INNER JOIN rec2 ON (rec2.parent = si4.id))" +
            " SELECT * FROM rec2)) AS t", nativeQuery = true)
    List<SystemItemEntity> findAllElementsInTreeByIds(@Param("ids") Collection<String> ids);

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
