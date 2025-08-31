package com.fleencorp.feen.common.validator.impl;

import com.fleencorp.feen.common.validator.FoundingYear;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

import static java.util.Objects.nonNull;

public class FoundingYearValidator implements ConstraintValidator<FoundingYear, String> {

  @Override
  public boolean isValid(final String value, final ConstraintValidatorContext context) {
    if (nonNull(value)) {
      if (!value.matches("\\d{4}")) {
        return false;
      }

      final int year = Integer.parseInt(value);
      return year >= 1000 && year <= Year.now().getValue();
    }

    return true;
  }
}

