package grebnev.yadoa.service;

import grebnev.yadoa.exception.NotFoundException;
import grebnev.yadoa.mapper.SystemItemMapper;
import grebnev.yadoa.repository.entity.SystemItemEntity;
import grebnev.yadoa.service.model.SystemItem;
import grebnev.yadoa.service.model.SystemItemType;

import javax.validation.ValidationException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class ItemsHierarchy {
    private final Map<String, SystemItem> leavesById;
    private final Map<String, SystemItem> rootsById;

    private ItemsHierarchy(
            Map<String, SystemItem> leavesById,
            Map<String, SystemItem> rootsById
    ) {
        this.leavesById = leavesById;
        this.rootsById = rootsById;
    }

    public static HierarchyMaker getMaker() {
        return new HierarchyMaker();
    }

    public void addAll(Map<String, SystemItem> itemsFromReq) {
        for (Map.Entry<String, SystemItem> entry : itemsFromReq.entrySet()) {
            String newId = entry.getKey();
            SystemItem newItem = entry.getValue();
            Optional<SystemItem> existing = getExisting(newId);
            if (existing.isPresent()) {
                validateExistingOrThrow(newItem, existing.get(), itemsFromReq);
                updateExisting(existing.get(), newItem);
                if (parentIsChanged(newItem, existing.get())) {
                    move(existing.get(), newItem, itemsFromReq);
                }
            } else {
                if (newItem.getParentId() == null) {
                    rootsById.put(newId, newItem);
                } else {
                    checkParentOrThrow(itemsFromReq, newItem.getParentId());
                    leavesById.put(newId, newItem);
                    assignParent(newItem, itemsFromReq);
                }
            }
        }
    }

    public List<String> deleteById(String id, Instant date) {
        Optional<SystemItem> maybeExisting = getExisting(id);
        if (maybeExisting.isPresent()) {
            SystemItem existing = maybeExisting.get();
            Optional<SystemItem> parent = existing.getParent();
            parent.ifPresent(systemItem -> {
                systemItem.setDate(date);
                systemItem.removeChild(existing);
            });
            List<String> idsToDelete = getIdsToDelete(existing.getChildren());
            idsToDelete.add(id);
            rootsById.remove(id);
            idsToDelete.forEach(leavesById.keySet()::remove);
            return idsToDelete;
        } else {
            return Collections.emptyList();
        }
    }

    public List<SystemItem> getAll() {
        List<SystemItem> result = new ArrayList<>(rootsById.size() + leavesById.size());
        result.addAll(rootsById.values());
        result.addAll(leavesById.values());
        return result;
    }

    private List<String> getIdsToDelete(Map<String, SystemItem> children) {
        List<String> idsToDelete = new ArrayList<>();
        if (children != null && !children.isEmpty()) {
            for (SystemItem child : children.values()) {
                idsToDelete.addAll(getIdsToDelete(child.getChildren()));
            }
            idsToDelete.addAll(children.keySet());
        }
        return idsToDelete;
    }

    public SystemItem getRootById(String id) {
        SystemItem root = rootsById.get(id);
        if (root == null) throw new NotFoundException(String.format("There are no item with id %s", id));
        return root;
    }

    private void assignParent(SystemItem newItem, Map<String, SystemItem> itemsFromReq) {
        assignParent(newItem, newItem, itemsFromReq);
    }

    private void move(SystemItem existing, SystemItem newItem, Map<String, SystemItem> itemsFromReq) {
        Optional<SystemItem> oldParent = existing.getParent();
        if (oldParent.isPresent()) {
            oldParent.get().removeChild(existing);
            oldParent.get().setDate(existing.getDate());
        }
        oldParent.ifPresent(systemItem -> systemItem.removeChild(existing));
        if (newItem.getParentId() == null) {
            existing.setParent(null);
            rootsById.put(existing.getId(), existing);
        } else {
            assignParent(existing, newItem, itemsFromReq);
        }
    }

    private void assignParent(SystemItem existing, SystemItem newItem, Map<String, SystemItem> itemsFromReq) {
        Optional<SystemItem> maybeNewParent = getExisting(newItem.getParentId());
        SystemItem newParent;
        if (maybeNewParent.isEmpty()) {
            newParent = itemsFromReq.get(newItem.getParentId());
        } else {
            newParent = maybeNewParent.get();
        }
        newParent.addChild(existing);
        existing.setParent(newParent);
    }

    private void updateExisting(SystemItem existing, SystemItem newItem) {
        existing.setDate(newItem.getDate());
        existing.setSize(newItem.getSize());
        existing.setUrl(newItem.getUrl());
    }

    private boolean parentIsChanged(SystemItem newItem, SystemItem existing) {
        return !Objects.equals(newItem.getParentId(), existing.getParentId());
    }

    private void validateExistingOrThrow(SystemItem newItem, SystemItem existing, Map<String, SystemItem> itemsFromReq) {
        if (newItem.getType() != existing.getType()) throw new ValidationException("Type could not be changed");
        if (newItem.getParentId() == null) return;
        String newParentId = newItem.getParentId();
        if (Objects.equals(newParentId, existing.getParentId())) return;
        checkParentOrThrow(itemsFromReq, newParentId);
    }

    private void checkParentOrThrow(Map<String, SystemItem> itemsFromReq, String newParentId) {
        Optional<SystemItem> maybeParent = getExisting(newParentId);
        if (maybeParent.isEmpty()) maybeParent = Optional.ofNullable(itemsFromReq.get(newParentId));
        if (maybeParent.isEmpty()) throw new ValidationException("Parent for item isn't exist");
        if (SystemItemType.FILE.equals(maybeParent.get().getType()))
            throw new ValidationException("File could not be parent");
    }

    private Optional<SystemItem> getExisting(String id) {
        SystemItem result = rootsById.get(id);
        if (result == null) result = leavesById.get(id);
        return Optional.ofNullable(result);
    }

    public static class HierarchyMaker {
        private HierarchyMaker() {
        }

        public ItemsHierarchy makeByEntities(List<SystemItemEntity> allElementsInTreeByIds, SystemItemMapper mapper) {
            Map<String, SystemItem> itemMap = allElementsInTreeByIds
                    .stream()
                    .collect(Collectors.toMap(SystemItemEntity::getId, mapper::entityToModel));
            return getItemsHierarchy(itemMap);
        }

        private ItemsHierarchy getItemsHierarchy(Map<String, SystemItem> itemMap) {
            Map<String, SystemItem> rootsById = findRoots(itemMap);
            Map<String, SystemItem> leavesById = new HashMap<>(itemMap.size());
            Map<String, SystemItem> rootsByLeafId = new HashMap<>(itemMap.size());
            initLeavesMaps(itemMap, rootsById, leavesById, rootsByLeafId);
            return new ItemsHierarchy(leavesById, rootsById);
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

        private Map<String, SystemItem> findRoots(Map<String, SystemItem> itemMap) {
            Map<String, SystemItem> resultMap = new HashMap<>(itemMap.size());
            Set<String> idsToRemove = new HashSet<>();
            for (SystemItem item : itemMap.values()) {
                String id = item.getId();
                String parentId = item.getParentId();
                if (parentId == null || !itemMap.containsKey(parentId)) {
                    resultMap.put(id, item);
                    idsToRemove.add(id);
                }
            }
            itemMap.entrySet().removeIf(entry -> idsToRemove.contains(entry.getKey()));
            return resultMap;
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
    }
}
