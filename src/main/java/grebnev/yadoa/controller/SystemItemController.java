package grebnev.yadoa.controller;

import grebnev.yadoa.dto.SystemItemImportRequest;
import grebnev.yadoa.service.SystemItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SystemItemController {
    private final SystemItemService sevice;

    @PostMapping("/imports")
    public void addItem(@RequestBody @Valid SystemItemImportRequest request) {
        log.info("Trying to add {} items", request.getItems().size());
        sevice.add(request);
    }
}

