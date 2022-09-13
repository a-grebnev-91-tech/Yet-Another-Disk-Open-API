package grebnev.yadoa.service;

import grebnev.yadoa.controller.dto.SystemItemImport;
import grebnev.yadoa.mapper.SystemItemMapper;
import grebnev.yadoa.repository.entity.SystemItemEntity;
import grebnev.yadoa.service.model.SystemItem;
import grebnev.yadoa.service.model.SystemItemType;

import javax.validation.ValidationException;
import java.time.Instant;
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

    public List<SystemItem> getAll() {
        List<SystemItem> result = new ArrayList<>(rootsById.size() + leavesById.size());
        result.addAll(rootsById.values());
        result.addAll(leavesById.values());
        return result;
    }

    public Set<String> getAllIds() {
        Set<String> ids = new HashSet<>(rootsById.size() + leavesById.size());
        ids.addAll(rootsById.keySet());
        ids.addAll(leavesById.keySet());
        return ids;
    }

    public void addAll(Map<String, SystemItem> itemsFromReq) {
        for (Map.Entry<String, SystemItem> entry : itemsFromReq.entrySet()) {
            String newId = entry.getKey();
            SystemItem newItem = entry.getValue();
            Optional<SystemItem> existed = getExisted(newId);
            if (existed.isPresent()) {
                validateExistedOrThrow(newItem, existed.get(), itemsFromReq);
                updateExisted(existed.get(), newItem);
                if (parentIsChanged(newItem, existed.get())) {
                    move(existed.get(), newItem, itemsFromReq);
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

    private void assignParent(SystemItem newItem, Map<String, SystemItem> itemsFromReq) {
        assignParent(newItem, newItem, itemsFromReq);
    }

    private void move(SystemItem existed, SystemItem newItem, Map<String, SystemItem> itemsFromReq) {
        Optional<SystemItem> oldParent = existed.getParent();
        oldParent.ifPresent(systemItem -> systemItem.removeChild(existed));
        if (newItem.getParentId() == null) {
            existed.setParent(null);
            rootsById.put(existed.getId(), existed);
        } else {
            assignParent(existed, newItem, itemsFromReq);
        }
    }

    private void assignParent(SystemItem existed, SystemItem newItem, Map<String, SystemItem> itemsFromReq) {
        Optional<SystemItem> maybeNewParent = getExisted(newItem.getParentId());
        SystemItem newParent;
        if (maybeNewParent.isEmpty()) {
            newParent = itemsFromReq.get(newItem.getParentId());
        } else {
            newParent = maybeNewParent.get();
        }
        newParent.addChild(existed);
        existed.setParent(newParent);
    }

    private void updateExisted(SystemItem existed, SystemItem newItem) {
        existed.setDate(newItem.getDate());
        existed.setSize(newItem.getSize());
        existed.setUrl(newItem.getUrl());
    }

    private boolean parentIsChanged(SystemItem newItem, SystemItem existed) {
        return !Objects.equals(newItem.getParentId(), existed.getParentId());
    }

    private void validateExistedOrThrow(SystemItem newItem, SystemItem existed, Map<String, SystemItem> itemsFromReq) {
        if (newItem.getType() != existed.getType()) throw new ValidationException("Type could not be changed");
        if (newItem.getParentId() == null) return;
        String newParentId = newItem.getParentId();
        if (Objects.equals(newParentId, existed.getParentId())) return;
        checkParentOrThrow(itemsFromReq, newParentId);
    }

    private void checkParentOrThrow(Map<String, SystemItem> itemsFromReq, String newParentId) {
        Optional<SystemItem> maybeParent = getExisted(newParentId);
        if (maybeParent.isEmpty()) maybeParent = Optional.ofNullable(itemsFromReq.get(newParentId));
        if (maybeParent.isEmpty()) throw new ValidationException("Parent for item isn't exist");
        if (SystemItemType.FILE.equals(maybeParent.get().getType()))
            throw new ValidationException("File could not be parent");
    }

    private Optional<SystemItem> getExisted(String id) {
        SystemItem result = rootsById.get(id);
        if (result == null) result = leavesById.get(id);
        return Optional.ofNullable(result);
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

        public ItemsHierarchy makeByImport(List<SystemItemImport> itemsImport, Instant date, SystemItemMapper mapper) {
//            Map<String, SystemItem> itemMap = new HashMap<>(itemsImport.size());
//            for (SystemItemImport itemImport : itemsImport) {
//                SystemItem item = mapper.dtoToModel(itemImport);
//                String id = item.getId();
//                itemMap.put(id, item);
//            }
            Map<String, SystemItem> itemMap = itemsImport
                    .stream()
                    .collect(Collectors.toMap(SystemItemImport::getId, dto -> mapper.dtoToModel(dto, date)));
            return getItemsHierarchy(itemMap);
        }

        private ItemsHierarchy getItemsHierarchy(Map<String, SystemItem> itemMap) {
            Map<String, SystemItem> rootsById = findRoots(itemMap);
            Map<String, SystemItem> leavesById = new HashMap<>(itemMap.size());
            Map<String, SystemItem> rootsByLeafId = new HashMap<>(itemMap.size());
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
    }
}
