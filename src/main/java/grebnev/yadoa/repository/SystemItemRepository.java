package grebnev.yadoa.repository;

import java.util.List;
import java.util.Optional;

public interface SystemItemRepository {
    List<SystemItemEntity> findAllByIdIn(List<String> id);

    Optional<SystemItemEntity> findById(String curId);

    SystemItemEntity save(SystemItemEntity entity);
}
