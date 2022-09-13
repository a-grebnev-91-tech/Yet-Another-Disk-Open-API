package grebnev.yadoa.validation;

import grebnev.yadoa.controller.dto.SystemItemImport;
import grebnev.yadoa.service.model.SystemItemType;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemValidator {

    public boolean isInvalid(List<SystemItemImport> items) {
        if (isRecurringId(items)) return true;
        for (SystemItemImport anImport : items) {
            if (!isItemInvalid(anImport)) return true;
        }
        return false;
    }

    private boolean isRecurringId(List<SystemItemImport> imports) {
        Set<String> distinctIds = imports.stream().map(SystemItemImport::getId).collect(Collectors.toSet());
        return distinctIds.size() != imports.size();
    }

    private boolean isItemInvalid(SystemItemImport systemItemImport) {
        String type = systemItemImport.getType();
        if (!EnumUtils.isValidEnum(SystemItemType.class, type)) return true;

        SystemItemType curType = SystemItemType.valueOf(type);
        switch (curType) {
            case FILE:
                return isInvalidFail(systemItemImport);
            case FOLDER:
                return isInvalidFolder(systemItemImport);
            default:
                return true;
        }
    }

    private boolean isInvalidFail(SystemItemImport systemItemImport) {
        Long size = systemItemImport.getSize();
        String url = systemItemImport.getUrl();
        if (size == null) return true;
        if (size <= 0) return true;
        if (url == null) return true;
        return url.isBlank();
    }

    private boolean isInvalidFolder(SystemItemImport systemItemImport) {
        Long size = systemItemImport.getSize();
        String url = systemItemImport.getUrl();
        return size != null && url != null;
    }
}

