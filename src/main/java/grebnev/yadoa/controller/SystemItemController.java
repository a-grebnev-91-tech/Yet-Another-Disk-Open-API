package grebnev.yadoa.controller;

import grebnev.yadoa.dto.SystemItemImportRequest;
import grebnev.yadoa.service.SystemItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;

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

//    @DeleteMapping("/delete/{id}")
//    public void deleteItem(
//            @PathVariable("id") String id,
//            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date) {
//        log.info("Trying to delete item with id {}", id);
//        service.delete(id, date);
//    }

    @DeleteMapping("/delete/{id}")
    public void deleteItem(
            @PathVariable("id") String id,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        log.info("Trying to delete item with id {}", id);
        service.delete(id, date);
    }
}

