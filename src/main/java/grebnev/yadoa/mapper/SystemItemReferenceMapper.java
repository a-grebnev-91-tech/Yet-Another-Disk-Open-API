package grebnev.yadoa.mapper;

import grebnev.yadoa.dto.SystemItemExport;
import grebnev.yadoa.model.SystemItem;
import grebnev.yadoa.model.SystemItemType;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        //        return modelMap.values().stream().map(mapper::modelToDto).collect(Collectors.toList());
    }

    String modelToId(SystemItem model) {
        if (model == null) return null;
        return model.getId();
    }
}
