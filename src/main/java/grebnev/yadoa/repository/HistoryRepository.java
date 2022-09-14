package grebnev.yadoa.repository;

import grebnev.yadoa.repository.entity.SystemItemHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface HistoryRepository extends JpaRepository<SystemItemHistoryEntity, Long> {
    @Query(value = "SELECT id, item_id, url, updated, parent, type, size" +
            " FROM history" +
            " WHERE item_id = :itemId AND (updated >= :dateStart AND updated < :dateEnd)", nativeQuery = true)
    List<SystemItemHistoryEntity> findHistoryByItemId(String itemId, Instant dateStart, Instant dateEnd);

    @Query(value = "SELECT id, item_id, url, updated, parent, type, size" +
            " FROM history" +
            " WHERE item_id = :itemId AND updated >= :dateStart", nativeQuery = true)
    List<SystemItemHistoryEntity> findHistoryByItemId(String itemId, Instant dateStart);
}
