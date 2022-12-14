package grebnev.yadoa.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = ItemTypeValidator.class)
public @interface ValidItemType {
    String message() default "Item type should be valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
