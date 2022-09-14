package grebnev.yadoa.service;

import grebnev.yadoa.controller.dto.*;
import grebnev.yadoa.exception.NotFoundException;
import grebnev.yadoa.mapper.HierarchyMakerMapper;
import grebnev.yadoa.mapper.SystemItemMapper;
import grebnev.yadoa.repository.HistoryRepository;
import grebnev.yadoa.repository.entity.SystemItemHistoryEntity;
import grebnev.yadoa.service.model.SystemItem;
import grebnev.yadoa.repository.entity.SystemItemEntity;
import grebnev.yadoa.repository.SystemItemRepository;
import grebnev.yadoa.validation.ItemValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SystemItemService {
    private final SystemItemRepository itemRepository;
    private final HistoryRepository historyRepository;
    private final SystemItemMapper mapper;
    private final HierarchyMakerMapper hierarchyMakerMapper;
    private final ItemValidator validator;

    public void add(SystemItemImportRequest request) {
        if (validator.isInvalid(request.getItems())) {
            throw new ValidationException("Request is invalid");
        }
        Set<String> idsFromRequest = new HashSet<>(request.getItems().size() * 2);
        Map<String, SystemItem> itemsFromReq = request.getItems()
                .stream()
                .peek(itemImport -> {
                    if (itemImport.getParentId() != null) idsFromRequest.add(itemImport.getParentId());
                    idsFromRequest.add(itemImport.getId());
                })
                .collect(Collectors.toMap(SystemItemImport::getId, dto -> mapper.dtoToModel(dto, request.getUpdateDate())));
        ItemsHierarchy existing = ItemsHierarchy
                .getMaker()
                .makeByEntities(itemRepository.findAllElementsInTreeByIds(idsFromRequest), mapper);
        existing.addAll(itemsFromReq);
        List<SystemItem> itemsToSave = existing.getAll();
        itemRepository.saveAll(mapper.modelsToEntities(itemsToSave));
    }

    public void delete(String id, Instant date) {
        Optional<SystemItemEntity> maybeEntity = itemRepository.findById(id);
        throwIfNotFound(id, maybeEntity);
        ItemsHierarchy existing =
                ItemsHierarchy.getMaker().makeByEntities(itemRepository.findAllElementsInTreeByIds(List.of(id)), mapper);
        List<String> idsToDelete = existing.deleteById(id, date);
        itemRepository.deleteAllById(idsToDelete);
        List<SystemItem> entities = existing.getAll();
        itemRepository.saveAll(mapper.modelsToEntities(entities));
    }

    public SystemItemExport findById(String id) {
        List<SystemItemRepository.LeveledSystemItemEntity> items = itemRepository.findAllElementsByRoot(id);
        if (items.size() == 0) throw new NotFoundException(String.format("Item with id %s isn't exist", id));
        SystemItem root = hierarchyMakerMapper.getHierarchy(items);
        return mapper.modelToDto(root);
    }

    public SystemItemHistoryResponse findHistory(String id, Instant dateStart, Instant dateEnd) {
        Optional<SystemItemEntity> maybeItem = itemRepository.findById(id);
        throwIfNotFound(id, maybeItem);
        List<SystemItemHistoryEntity> historyElements;
        if (dateStart == null) {
            dateStart = Instant.ofEpochMilli(0);
        }
        if (dateEnd == null) {
            historyElements = historyRepository.findHistoryByItemId(id, dateStart);
        } else {
            historyElements = historyRepository.findHistoryByItemId(id, dateStart, dateEnd);
        }
        List<SystemItemHistoryUnit> historyUnits =  historyElements
                .stream()
                .map(mapper::historyEntityToHistoryUnit)
                .collect(Collectors.toList());
        return new SystemItemHistoryResponse(historyUnits);
    }

    public SystemItemHistoryResponse findLastUpdated(Instant to) {
        int secondsInDay = 24 * 60 * 60;
        Instant from = to.minusSeconds(secondsInDay);
        List<SystemItemEntity> entities = itemRepository.findLastUpdated(from, to);
        List<SystemItemHistoryUnit> historyUnits =  entities
                .stream()
                .map(mapper::entityToHistoryUnit)
                .collect(Collectors.toList());
        return new SystemItemHistoryResponse(historyUnits);
    }

    private void throwIfNotFound(String id, Optional<SystemItemEntity> existing) {
        if (existing.isEmpty()) throw new NotFoundException(String.format("Item with id %s isn't exist", id));
    }

    //todo remove
    private Optional<SystemItemEntity> findParentById(String childId) {
        Optional<SystemItemEntity> maybeChild = itemRepository.findById(childId);
        if (maybeChild.isPresent()) {
            String parentId = maybeChild.get().getParentId();
            if (parentId != null) {
                return itemRepository.findById(parentId);
            } else {
                return Optional.empty();
            }
        } else {
            throw new NotFoundException(String.format("Entity with id %s isn't exits", childId));
        }
    }
}
