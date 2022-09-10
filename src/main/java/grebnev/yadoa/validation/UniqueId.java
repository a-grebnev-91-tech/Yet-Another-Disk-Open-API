package grebnev.yadoa.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

//todo del
@Target({METHOD, FIELD, ANNOTATION_TYPE, TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = UniqueIdValidator.class)
public @interface UniqueId {
    String message() default "Request items couldn't contain two identical IDs";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
