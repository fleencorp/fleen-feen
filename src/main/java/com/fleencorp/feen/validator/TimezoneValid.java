package com.fleencorp.feen.validator;

import com.fleencorp.feen.validator.impl.TimezoneValidValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimezoneValidValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimezoneValid {

  String message() default "Timezone does not exist or cannot be found.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
