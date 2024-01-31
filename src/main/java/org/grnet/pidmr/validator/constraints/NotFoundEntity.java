package org.grnet.pidmr.validator.constraints;


import org.grnet.pidmr.repository.Repository;
import org.grnet.pidmr.validator.validators.NotFoundEntityValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotFoundEntityValidator.class)
@Documented
public @interface NotFoundEntity {

    String message() default "Not founded:";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    Class<? extends Repository<?,?>> repository();
}
