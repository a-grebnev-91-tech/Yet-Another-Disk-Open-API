package grebnev.yadoa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSystemItemRepository extends JpaRepository<SystemItemEntity, String>, SystemItemRepository {
}
