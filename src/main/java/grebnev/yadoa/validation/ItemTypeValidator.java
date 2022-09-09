package grebnev.yadoa.validation;

import grebnev.yadoa.service.SystemItemType;
import org.apache.commons.lang3.EnumUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

//TODO del
public class ItemTypeValidator implements ConstraintValidator<ValidItemType, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return false;
        }
        return EnumUtils.isValidEnum(SystemItemType.class, s);
    }
}