package grebnev.yadoa.validation;

import grebnev.yadoa.dto.SystemItemImport;
import grebnev.yadoa.dto.SystemItemImportRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//TODO del
public class UniqueIdValidator implements ConstraintValidator<UniqueId, List<SystemItemImport>> {
    @Override
    public boolean isValid(List<SystemItemImport> systemItemImports, ConstraintValidatorContext constraintValidatorContext) {
return  false;
    }
}
