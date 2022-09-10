package grebnev.yadoa.validation;

import grebnev.yadoa.dto.SystemItemImport;
import grebnev.yadoa.service.SystemItemType;
import org.apache.commons.lang3.EnumUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemImportValidator implements ConstraintValidator<ValidItemImport, List<SystemItemImport>> {
    @Override
    public boolean isValid(
            List<SystemItemImport> imports,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        if (imports == null) return false;
        if (isRepeatingId(imports)) return false;
        for (SystemItemImport anImport : imports) {
            if (!isValid(anImport)) return false;
        }
        return true;
    }

    private boolean isRepeatingId(List<SystemItemImport> imports) {
        Set<String> distinctIds = imports.stream().map(SystemItemImport::getId).collect(Collectors.toSet());
        return distinctIds.size() != imports.size();
    }

    private boolean isValid(SystemItemImport systemItemImport) {
        if (systemItemImport == null) return false;

        String id = systemItemImport.getId();
        if (id == null) return false;
        if (id.isBlank()) return false;

        String type = systemItemImport.getType();
        if (type == null) return false;
        if (!EnumUtils.isValidEnum(SystemItemType.class, type)) return false;

        SystemItemType curType = SystemItemType.valueOf(type);
        switch (curType) {
            case FILE:
                return isValidFile(systemItemImport);
            case FOLDER:
                return isValidFolder(systemItemImport);
            default:
                return false;
        }
    }

    private boolean isValidFile(SystemItemImport systemItemImport) {
        Long size = systemItemImport.getSize();
        String url = systemItemImport.getUrl();
        if (size == null) return false;
        if (size <= 0) return false;
        if (url == null) return false;
        if (url.isBlank()) return false;
        return url.length() <= 255;
    }

    private boolean isValidFolder(SystemItemImport systemItemImport) {
        Long size = systemItemImport.getSize();
        String url = systemItemImport.getUrl();
        return size == null && url == null;
    }
}
