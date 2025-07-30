package com.fleencorp.feen.common.validator;

import com.fleencorp.feen.common.validator.impl.StreamDurationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = StreamDurationValidator.class)
public @interface StreamDuration {

  String message() default "The stream duration should not exceed 24 hours.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

