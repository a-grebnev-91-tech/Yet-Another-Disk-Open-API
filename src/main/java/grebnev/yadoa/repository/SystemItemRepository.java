package grebnev.yadoa.repository;

import grebnev.yadoa.repository.entity.SystemItemEntity;
import grebnev.yadoa.repository.entity.SystemItemHistoryEntity;
import grebnev.yadoa.service.model.SystemItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface SystemItemRepository extends JpaRepository<SystemItemEntity, String> {
    @Query(value = " WITH RECURSIVE elements AS (" +
            " SELECT si1.id, si1.url, si1.updated, si1.parent, si1.type, si1.size" +
            " FROM system_items AS si1 WHERE si1.id = :rootId" +
            " UNION" +
            " SELECT si2.id, si2.url, si2.updated, si2.parent, si2.type, si2.size" +
            "  FROM system_items AS si2" +
            " INNER JOIN elements ON (elements.id = si2.parent)" +
            ") SELECT * FROM ELEMENTS", nativeQuery = true)
    List<SystemItemEntity> findAllElementsByRoot(@Param("rootId") String rootId);

    @Query(value = " WITH RECURSIVE rec1 AS" +
            " (SELECT si1.id, si1.url, si1.updated, si1.parent, si1.type, si1.size" +
            " FROM system_items AS si1 WHERE si1.id IN" +
            " (WITH RECURSIVE rec2 AS" +
            " (SELECT si3.id, si3.parent FROM system_items AS si3" +
            " WHERE si3.id IN (:ids)" +
            " UNION" +
            " SELECT si4.id, si4.parent FROM system_items AS si4" +
            " INNER JOIN rec2 ON (rec2.parent = si4.id))" +
            " SELECT id FROM rec2 WHERE parent IS NULL)" +
            " UNION" +
            " SELECT si2.id, si2.url, si2.updated, si2.parent, si2.type, si2.size" +
            " FROM system_items AS si2" +
            " INNER JOIN rec1 ON (rec1.id = si2.parent))" +
            " SELECT * FROM rec1;", nativeQuery = true)
    List<SystemItemEntity> findAllElementsInTreeByIds(@Param("ids") Collection<String> ids);

    @Query(value = "SELECT * FROM system_items" +
            " WHERE type = 'FILE'" +
            " AND (updated BETWEEN :from AND :to)", nativeQuery = true)
    List<SystemItemEntity> findLastUpdated(@Param("from") Instant from,@Param("to") Instant to);
}
