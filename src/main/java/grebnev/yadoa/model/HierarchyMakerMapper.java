package grebnev.yadoa.model;

import grebnev.yadoa.repository.SystemItemRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HierarchyMakerMapper {
    private Map<Integer, Map<String, SystemItemRepository.LeveledSystemItemEntity>> leveledEntityMap;
    private Map<Integer, Map<String, SystemItem>> leveledModelMap;

    public SystemItem getHierarchy(List<SystemItemRepository.LeveledSystemItemEntity> leveledEntities) {
        fillLeveledMaps(leveledEntities);
        SystemItem rootItem = makeHierarchy();
        return rootItem;
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
        for (int level = leveledModelMap.size() - 1; level > 0; level--) {
            Map<String, SystemItem> curLevel = leveledModelMap.get(level);
            Map<String, SystemItem> upperLevel = leveledModelMap.get(level - 1);
            for (SystemItem child : curLevel.values()) {
                String parentId = leveledEntityMap.get(level).get(child.getId()).getParentId();
                SystemItem parent = upperLevel.get(parentId);
                makeConnection(parent, child);
                if (level == 1) {
                    rootId = parentId;
                }
            }
        }
        return leveledModelMap.get(0).get(rootId);
    }

    private void makeConnection(SystemItem parent, SystemItem child) {
        child.setParent(parent);
        parent.addChild(child);
    }

    private SystemItem mapEntityToModel(SystemItemRepository.LeveledSystemItemEntity leveledEntity) {
        SystemItem item = new SystemItem(leveledEntity.getId(), leveledEntity.getType());
        item.setDate(leveledEntity.getUpdateDate());
        item.setSize(leveledEntity.getSize());
        item.setUrl(leveledEntity.getUrl());
        return item;
    }
//TODO delete

//    public SystemItem getHierarchy(List<SystemItemRepository.LeveledSystemItemEntity> leveledEntities) {
//        Map<Integer, Map<String, SystemItem>> leveledModelMap = getLeveledMap(leveledEntities);
//        SystemItem rootItem = makeHierarchy(leveledModelMap);
//        return rootItem;
//    }
//
//    private SystemItem makeHierarchy(
//            Map<Integer, Map<String, SystemItem>> leveledModelMap
//    ) {
//        for (int i = leveledModelMap.size() - 1; i > 0; i--) {
//            Map<String, SystemItem> curLevel = leveledModelMap.get(i);
//            Map<String, SystemItem> upperLevel = leveledModelMap.get(i - 1);
//            for (SystemItem child : curLevel.values()) {
//                SystemItem parent = upperLevel.get(child.getParent());
//                makeConnection()
//            }
//        }
//    }
//
//    private Map<Integer, Map<String, SystemItem>> getLeveledMap(
//            List<SystemItemRepository.LeveledSystemItemEntity> leveledEntities
//    ) {
//        Map<Integer, Map<String, SystemItem>> leveledMap = new HashMap<>();
//        for (SystemItemRepository.LeveledSystemItemEntity leveledEntity : leveledEntities) {
//            SystemItem item = mapEntityToModel(leveledEntity);
//            int level = leveledEntity.getLevel();
//            Map<String, SystemItem> curLevelMap = leveledMap.get(level);
//            if (curLevelMap == null) {
//                curLevelMap = new HashMap<>();
//            }
//            curLevelMap.put(item.getId(), item);
//        }
//        return leveledMap;
//    }
//
//    private SystemItem mapEntityToModel(SystemItemRepository.LeveledSystemItemEntity leveledEntity) {
//        SystemItem item = new SystemItem(leveledEntity.getId(), leveledEntity.getType());
//        item.setDate(leveledEntity.getUpdateDate());
//        item.setSize(leveledEntity.getSize());
//        item.setUrl(leveledEntity.getUrl());
//        return item;
//    }
}
