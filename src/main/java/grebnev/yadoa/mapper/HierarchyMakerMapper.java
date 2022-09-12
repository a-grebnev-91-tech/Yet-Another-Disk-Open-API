package grebnev.yadoa.mapper;

import grebnev.yadoa.exception.NotFoundException;
import grebnev.yadoa.model.SystemItem;
import grebnev.yadoa.repository.SystemItemRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HierarchyMakerMapper {
    private Map<Integer, Map<String, SystemItemRepository.LeveledSystemItemEntity>> leveledEntityMap;
    private Map<Integer, Map<String, SystemItem>> leveledModelMap;

    public SystemItem getHierarchy(List<SystemItemRepository.LeveledSystemItemEntity> leveledEntities) {
        if (leveledEntities == null) throw new IllegalArgumentException("Leveled entities list couldn't be null");
        fillLeveledMaps(leveledEntities);
        return makeHierarchy();
    }

    private void addEntityToLevel(SystemItemRepository.LeveledSystemItemEntity leveledEntity, int level) {
        Map<String, SystemItemRepository.LeveledSystemItemEntity> curLevelMap
                = leveledEntityMap.get(level);
        if (curLevelMap == null) {
            curLevelMap = new HashMap<>();
        }
        curLevelMap.put(leveledEntity.getId(), leveledEntity);
        leveledEntityMap.put(level, curLevelMap);
    }

    private void addModelToLevel(SystemItem item, int level) {
        Map<String, SystemItem> curLevelMap = leveledModelMap.get(level);
        if (curLevelMap == null) {
            curLevelMap = new HashMap<>();
        }
        curLevelMap.put(item.getId(), item);
        leveledModelMap.put(level, curLevelMap);
    }

    private void fillLeveledMaps(List<SystemItemRepository.LeveledSystemItemEntity> leveledEntities) {
        leveledEntityMap = new HashMap<>();
        leveledModelMap = new HashMap<>();
        for (SystemItemRepository.LeveledSystemItemEntity leveledEntity : leveledEntities) {
            SystemItem item = mapEntityToModel(leveledEntity);
            int curLevel = leveledEntity.getLevel();
            addEntityToLevel(leveledEntity, curLevel);
            addModelToLevel(item, curLevel);
        }
    }

    private SystemItem makeHierarchy() {
        String rootId = null;
        if (leveledModelMap.size() > 1) {
            for (int level = leveledModelMap.size() - 1; level > 0; level--) {
                Map<String, SystemItem> curLevel = leveledModelMap.get(level);
                Map<String, SystemItem> upperLevel = leveledModelMap.get(level - 1);
                for (SystemItem child : curLevel.values()) {
                    String parentId = leveledEntityMap.get(level).get(child.getId()).getParent();
                    SystemItem parent = upperLevel.get(parentId);
                    makeConnection(parent, child);
                    if (level == 1) {
                        rootId = parentId;
                    }
                }
            }
            return leveledModelMap.get(0).get(rootId);
        } else if (leveledModelMap.size() == 1) {
            return leveledModelMap.values().stream().findFirst().get().values().stream().findFirst().get();
        } else {
            throw new NotFoundException("Item with corresponded id isn't exist");
        }
    }

    private void makeConnection(SystemItem parent, SystemItem child) {
        child.setParent(parent);
        parent.addChild(child);
    }

    private SystemItem mapEntityToModel(SystemItemRepository.LeveledSystemItemEntity leveledEntity) {
        SystemItem item = new SystemItem(leveledEntity.getId(), leveledEntity.getType());
        item.setDate(leveledEntity.getUpdated());
        item.setSize(leveledEntity.getSize());
        item.setUrl(leveledEntity.getUrl());
        return item;
    }
}
