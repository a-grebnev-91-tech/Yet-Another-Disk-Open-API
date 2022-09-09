package grebnev.yadoa.repository;

import java.util.List;
import java.util.Optional;

public interface SystemItemRepository {
    List<SystemItemEntity> findAllById(List<String> curId);

    Optional<SystemItemEntity> findById(String curId);

    SystemItemEntity save(SystemItemEntity entity);
}
