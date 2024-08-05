package com.fleencorp.feen.validator.impl;

import com.fleencorp.feen.validator.TimezoneValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.ZoneId;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;

/**
* Validator class that checks if a given timezone string is valid.
* Implements the {@link ConstraintValidator} interface for the {@link TimezoneValid} annotation.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public class TimezoneValidValidator implements ConstraintValidator<TimezoneValid, String> {

  /**
  * Constructs a new TimezoneValidValidator.
  */
  public TimezoneValidValidator() {}

  /**
  * Initializes the validator. This method is a no-op for this validator.
  *
  * @param constraintAnnotation the annotation instance for a given constraint declaration
  */
  @Override
  public void initialize(final TimezoneValid constraintAnnotation) {}

  /**
  * Checks if the given timezone string is valid by verifying its existence in the available zone IDs.
  *
  * <p>If the timezone string is not null, the method checks if the string is contained within the
  * set of available timezone IDs. If the timezone string is null, the method returns true, indicating
  * that null values are considered valid.</p>
  *
  * @param timezone the timezone string to validate
  * @param context context in which the constraint is evaluated
  * @return true if the timezone string is valid or null, false otherwise
  */
  @Override
  public boolean isValid(final String timezone, final ConstraintValidatorContext context) {
    return !nonNull(timezone) || TimezoneValidValidator.getTimezones().contains(timezone.toLowerCase());
  }

  /**
  * Retrieves a set of all available timezones, converted to lowercase.
  *
  * <p>This method fetches the available zone IDs from the {@link ZoneId} class, converts each ID to lowercase,
  * and collects them into a {@link Set}. The resulting set contains all the timezone IDs in lowercase format,
  * which can be used for case-insensitive comparisons.</p>
  *
  * @return a {@link Set} of all available timezone IDs in lowercase
  */
  public static Set<String> getTimezones() {
    return getAvailableTimezones().stream()
            .map(String::toLowerCase)
            .collect(toSet());
  }

  /**
   * Retrieves a set of all available timezones
   *
   * <p>This method fetches the available zone IDs from the {@link ZoneId} class, converts each ID to lowercase,
   * and collects them into a {@link Set}. The resulting set contains all the timezone IDs in lowercase format,
   * which can be used for case-insensitive comparisons.</p>
   *
   * @return a {@link Set} of all available timezone IDs in lowercase
   */
  public static Set<String> getAvailableTimezones() {
    return ZoneId.getAvailableZoneIds();
  }
}
