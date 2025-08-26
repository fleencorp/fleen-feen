package com.fleencorp.feen.common.validator;

import com.fleencorp.feen.common.validator.impl.FoundingYearValidator;
import com.slack.api.webhook.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = FoundingYearValidator.class)
@Documented
public @interface FoundingYear {

  String message() default "Invalid founding year";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

