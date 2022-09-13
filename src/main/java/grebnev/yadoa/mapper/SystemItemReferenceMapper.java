package grebnev.yadoa.mapper;

import grebnev.yadoa.controller.dto.SystemItemExport;
import grebnev.yadoa.service.model.SystemItem;
import grebnev.yadoa.service.model.SystemItemType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SystemItemReferenceMapper {
    private final SystemItemMapper mapper;

    public SystemItemReferenceMapper(@Lazy SystemItemMapper mapper) {
        this.mapper = mapper;
    }

    SystemItemType stringToType(String s) {
        return SystemItemType.valueOf(s);
    }

    List<SystemItemExport> entryToDto(Map<String, SystemItem> modelMap) {
        if (modelMap == null) return null;
        List<SystemItemExport> exports = new ArrayList<>(modelMap.size());
        for (SystemItem item : modelMap.values()) {
            SystemItemExport dto = mapper.modelToDto(item);
            exports.add(dto);
        }
        return exports;
    }

    String modelToId(SystemItem model) {
        if (model == null) return null;
        return model.getId();
    }
}
