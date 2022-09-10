package grebnev.yadoa.mapper;

import grebnev.yadoa.repository.SystemItemEntity;
import grebnev.yadoa.repository.SystemItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

//TODO delete
public class SystemItemValidItemMapper {
    Map<String, SystemItemEntity> parentEntitiesToCreate;
    Map<String, SystemItemEntity> otherEntitiesToCreate;
    Map<String, SystemItemEntity> existingEntities;
    PriorityQueue<SystemItemEntity> queueToSave;

    public SystemItemValidItemMapper(SystemItemRepository repository) {

    }
}
