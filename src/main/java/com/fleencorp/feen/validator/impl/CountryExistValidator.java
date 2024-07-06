package com.fleencorp.feen.validator.impl;

import com.fleencorp.feen.service.CountryService;
import com.fleencorp.feen.validator.CountryExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

/**
* Validator class that checks if a given country ID exists using the {@link CountryService}.
* Implements the {@link ConstraintValidator} interface for the {@link CountryExist} annotation.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Component
public class CountryExistValidator implements ConstraintValidator<CountryExist, String> {

  private final CountryService service;

  /**
  * Constructs a new CountryExistValidator with the specified {@link CountryService}.
  *
  * @param service the CountryService to use for checking if a country exists
  */
  public CountryExistValidator(CountryService service) {
    this.service = service;
  }

  /**
  * Initializes the validator. This method is a no-op for this validator.
  *
  * @param constraintAnnotation the annotation instance for a given constraint declaration
  */
  @Override
  public void initialize(CountryExist constraintAnnotation) {}

  /**
  * Checks if the given country ID is valid by verifying its existence using the {@link CountryService}.
  *
  * <p>If the country ID is not null, the method attempts to parse it into a Long and checks if the country
  * exists using the CountryService. If the parsing fails or the country does not exist, the method returns false.
  * If the country ID is null, the method returns true, indicating that null values are considered valid.</p>
  *
  * @param countryId the country ID to validate
  * @param context context in which the constraint is evaluated
  * @return true if the country ID exists, false otherwise
  */
  @Override
  public boolean isValid(String countryId, ConstraintValidatorContext context) {
    if (nonNull(countryId)) {
      try {
        return service.isCountryExists(Long.parseLong(countryId));
      } catch (Exception ignored) {}
      return false;
    }
    return true;
  }
}
