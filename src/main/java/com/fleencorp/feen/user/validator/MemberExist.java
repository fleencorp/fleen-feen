package com.fleencorp.feen.user.validator;

import com.fleencorp.feen.user.validator.impl.MemberExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MemberExistValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MemberExist {

  String message() default "Member does not exist or cannot be found.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
