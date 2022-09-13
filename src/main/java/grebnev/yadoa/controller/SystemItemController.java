package grebnev.yadoa.controller;

import grebnev.yadoa.controller.dto.SystemItemExport;
import grebnev.yadoa.controller.dto.SystemItemImportRequest;
import grebnev.yadoa.service.SystemItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SystemItemController {
    private final SystemItemService service;

    @PostMapping("/imports")
    public void addItem(@RequestBody @Valid SystemItemImportRequest request) {
        log.info("Trying to add {} item(s)", request.getItems().size());
        service.add(request);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteItem(
            @PathVariable("id") String id,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant date) {
        log.info("Trying to delete item with id {}", id);
        service.delete(id, date);
    }

    @GetMapping("/nodes/{id}")
    public SystemItemExport findById(@PathVariable("id") String id) {
        log.info("Trying to get item by id {}", id);
        return service.findById(id);
    }

    @GetMapping("/updates")
    public List<SystemItemExport> findLastUpdated(@RequestParam("date") Instant date) {
        log.info("Trying to get items updated last 24 hours before {}", date);
        return service.findLastUpdated(date);
    }
}

