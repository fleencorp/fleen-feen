package com.fleencorp.feen.validator.impl;

import com.fleencorp.feen.model.dto.stream.base.CreateStreamDto;
import com.fleencorp.feen.validator.StreamDuration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static java.util.Objects.nonNull;

/**
 * Custom validator to check the duration between the start and end date-time of a stream.
 * This validator ensures that the duration between the start and end time does not exceed 24 hours.
 *
 * <p>It is applied at the class level to validate the {@link CreateStreamDto} and checks that the
 * difference between the start and end times is within the allowed 24-hour limit.</p>
 *
 * @author Yusuf Alàmú Musa
 * @version 1.0
 */
public class StreamDurationValidator implements ConstraintValidator<StreamDuration, CreateStreamDto> {

  /**
   * Validates whether the duration between the start and end date-times of the stream is no more than 24 hours.
   *
   * @param dto the {@link CreateStreamDto} object containing the start and end date-times to validate
   * @param context the {@link ConstraintValidatorContext} used to add custom violation messages if necessary
   * @return {@code true} if the duration between the start and end date-time is less than or equal to 24 hours,
   *         {@code false} otherwise
   */
  @Override
  public boolean isValid(final CreateStreamDto dto, final ConstraintValidatorContext context) {
    if (nonNull(dto)) {
      try {
        // Retrieve the actual start and end date times
        final LocalDateTime start = dto.getStartDateTime();
        final LocalDateTime end = dto.getEndDateTime();

        // Calculate the duration between start and end date-times
        final Duration duration = Duration.between(start, end);

        // Validate that the duration does not exceed 24 hours
        return duration.toHours() <= 24;
      } catch (final DateTimeParseException | NullPointerException ignored) {
        return false;
      }
    }
    return true;
  }
}

