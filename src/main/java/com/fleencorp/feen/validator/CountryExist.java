package com.fleencorp.feen.validator;

import com.fleencorp.feen.validator.impl.CountryExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CountryExistValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CountryExist {

  String message() default "Country does not exist or cannot be found.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
