package grebnev.yadoa.service;

import grebnev.yadoa.controller.dto.SystemItemImport;
import grebnev.yadoa.mapper.SystemItemMapper;
import grebnev.yadoa.repository.entity.SystemItemEntity;
import grebnev.yadoa.service.model.SystemItem;
import grebnev.yadoa.service.model.SystemItemType;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

public class ItemsHierarchy {
    private final Map<String, SystemItem> rootsByLeafId;
    private final Map<String, SystemItem> leavesById;
    private final Map<String, SystemItem> rootsById;

    private ItemsHierarchy(
            Map<String, SystemItem> rootsByLeafId,
            Map<String, SystemItem> leavesById,
            Map<String, SystemItem> rootsById
    ) {
        this.rootsByLeafId = rootsByLeafId;
        this.leavesById = leavesById;
        this.rootsById = rootsById;
    }

    public static HierarchyFactory getBuilder() {
        return new HierarchyFactory();
    }

    public Set<String> getAllIds() {
        return null;
    }

    public void addAll(ItemsHierarchy requestHierarchy) {
        return;
    }

    public List<SystemItemEntity> getAllEntities() {
        return null;
    }

    public static class HierarchyFactory {
        private HierarchyFactory() {
        }

        public ItemsHierarchy makeByEntities(List<SystemItemEntity> allElementsInTreeByIds, SystemItemMapper mapper) {
            Map<String, SystemItem> itemMap = allElementsInTreeByIds
                    .stream()
                    .collect(Collectors.toMap(SystemItemEntity::getId, mapper::entityToModel));
            return getItemsHierarchy(itemMap);
        }

        public ItemsHierarchy makeByImport(List<SystemItemImport> itemsImport, SystemItemMapper mapper) {
            Map<String, SystemItem> itemMap = itemsImport
                    .stream()
                    .collect(Collectors.toMap(SystemItemImport::getId, mapper::dtoToModel));
            return getItemsHierarchy(itemMap);
        }

        private ItemsHierarchy getItemsHierarchy(Map<String, SystemItem> itemMap) {
            Map<String, SystemItem> rootsById = findRoots(itemMap);
            Map<String, SystemItem> leavesById = new HashMap<>(itemMap.size() - rootsById.size());
            Map<String, SystemItem> rootsByLeafId = new HashMap<>(itemMap.size() - rootsById.size());
            initLeavesMaps(itemMap, rootsById, leavesById, rootsByLeafId);
            return new ItemsHierarchy(rootsByLeafId, leavesById, rootsById);
        }

        private void initLeavesMaps(
                Map<String, SystemItem> items,
                Map<String, SystemItem> rootsById,
                Map<String, SystemItem> leavesById,
                Map<String, SystemItem> rootsByLeafId
        ) {
            items.values().forEach((item) -> {
                String parentId = item.getParentId();
                SystemItem parent = items.get(parentId);
                if (parent == null) {
                    parent = rootsById.get(parentId);
                    rootsByLeafId.put(item.getId(), parent);
                }
                makeConnection(parent, item);
                leavesById.put(item.getId(), item);
            });
            Set<String> notAssignedToRoot = new HashSet<>(leavesById.keySet());
            notAssignedToRoot.removeAll(rootsByLeafId.keySet());
            for (String leafId : notAssignedToRoot) {
                SystemItem curLeaf = leavesById.get(leafId);
                String rootId = getRootByItem(curLeaf, rootsById);
                rootsByLeafId.put(leafId, rootsById.get(rootId));
            }
        }

        private String getRootByItem(SystemItem curLeaf, Map<String, SystemItem> rootsById) {
            String parentId = curLeaf.getParentId();
            if (rootsById.containsKey(parentId)) return parentId;
            else return getRootByItem(curLeaf.getParent().get(), rootsById);
        }

        private void makeConnection(SystemItem parent, SystemItem child) {
            if (parent.getType().equals(SystemItemType.FILE)) throw new ValidationException("File couldn't be parent");
            child.setParent(parent);
            parent.addChild(child);
        }

        private Map<String, SystemItem> findRoots(Map<String, SystemItem> itemMap) {
            Map<String, SystemItem> resultMap = new HashMap<>(itemMap.size());
            itemMap.entrySet().removeIf(entry -> {
                SystemItem item = entry.getValue();
                String parentId = item.getParentId();
                if (parentId == null || !itemMap.containsKey(parentId)) {
                    resultMap.put(item.getId(), item);
                    return true;
                } else {
                    return false;
                }
            });
            return resultMap;
        }
    }
}
