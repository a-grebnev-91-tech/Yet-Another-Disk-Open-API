package grebnev.yadoa.validation;

import grebnev.yadoa.dto.SystemItemImport;
import grebnev.yadoa.dto.SystemItemImportRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UniqueIdValidator implements ConstraintValidator<UniqueId, List<SystemItemImport>> {

    @Override
    public boolean isValid(List<SystemItemImport> systemItemImports, ConstraintValidatorContext constraintValidatorContext) {
        if (systemItemImports == null) {
            return true;
        }
        Set<String> distinctIds = systemItemImports.stream().map(SystemItemImport::getId).collect(Collectors.toSet());
        return distinctIds.size() == systemItemImports.size();
    }
}
