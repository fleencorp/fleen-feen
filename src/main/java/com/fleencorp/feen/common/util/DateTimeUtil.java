package com.fleencorp.feen.common.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static java.util.Objects.nonNull;

/**
 * Utility class for date and time operations.
 *
 * <p>This class provides static methods to handle conversions between Java 8 LocalDateTime
 * and Google Calendar API DateTime objects, as well as other date and time utilities.</p>
 *
 * <p>Methods in this class ensure proper handling of time zones and date-time conversions,
 * making it easier to work with date and time representations in different contexts.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
public final class DateTimeUtil {

  private DateTimeUtil() {}

  /**
   * Converts a LocalDateTime object to a Date object.
   *
   * <p>This method converts a Java 8 LocalDateTime object to a legacy Date object,
   * adjusting for the system default time zone.</p>
   *
   * @param localDateTime the LocalDateTime object to convert
   * @return a Date object representing the same date and time, or null if the input is null
   */
  public static Date toDate(final LocalDateTime localDateTime) {
    if (nonNull(localDateTime)) {
      return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    return null;
  }

  /**
   * Converts a LocalDateTime object to milliseconds since epoch in a specified timezone.
   *
   * <p>This method converts a Java 8 LocalDateTime object to milliseconds since epoch,
   * adjusting for the specified timezone.</p>
   *
   * @param dateTime the LocalDateTime object to convert
   * @param timezone the timezone in which to interpret the LocalDateTime
   * @return the number of milliseconds since epoch corresponding to the given LocalDateTime,
   *         or 0 if the input dateTime is null
   */
  public static long toMilliseconds(final LocalDateTime dateTime, final String timezone) {
    if (dateTime != null) {
      final ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of(timezone));
      return zonedDateTime.toInstant().toEpochMilli();
    }
    return 0;
  }

  /**
   * Retrieves the timezone abbreviation for a given LocalDateTime and timezone.
   *
   * @param localDateTime the date and time to be converted.
   * @param timezone the timezone ID to be used for conversion (e.g., "America/New_York").
   * @return the abbreviation of the timezone (e.g., "EST", "PST").
   */
  public static String getTimezoneAbbreviation(final LocalDateTime localDateTime, final String timezone) {
    // Convert LocalDateTime to ZonedDateTime using the provided timezone
    final ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(timezone));
    // Create a DateTimeFormatter that includes the short timezone (abbreviation)
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("zzz");

    // Format ZonedDateTime to get the timezone abbreviation
    return zonedDateTime.format(formatter);
  }

  /**
   * Retrieves the GMT offset for a given LocalDateTime and timezone in the format "(UTC+HH:mm)".
   *
   * @param localDateTime the date and time to be converted.
   * @param timezone the timezone ID to be used for conversion (e.g., "Asia/Dubai").
   * @return the GMT offset formatted as "(UTC+HH:mm)".
   */
  public static String getGmtOffset(final LocalDateTime localDateTime, final String timezone) {
    // Convert LocalDateTime to ZonedDateTime using the provided timezone
    final ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(timezone));
    // Get the offset from UTC
    final ZoneOffset offset = zonedDateTime.getOffset();

    // Calculate hours and minutes from total seconds
    // 3600 is 1 hour in seconds
    final int oneHour = 3600;
    final int totalSeconds = offset.getTotalSeconds();
    final int hours = totalSeconds / oneHour;
    final int minutes = Math.abs((totalSeconds % oneHour) / 60);

    // Format the offset as a string
    final String offsetString = String.format("UTC%s%02d:%02d",
      hours >= 0 ? "+" : "-",
      Math.abs(hours),
      minutes);

    return "(" + offsetString + ")";
  }

  /**
   * Converts a LocalDateTime from one timezone to another.
   *
   * @param dateTime the {@link LocalDateTime} to be converted.
   * @param fromTimezone the original timezone of the dateTime.
   * @param toTimezone the target timezone to which the dateTime should be converted.
   * @return the {@link LocalDateTime} in the target timezone.
   */
  public static LocalDateTime convertToTimezone(final LocalDateTime dateTime, final String fromTimezone, final String toTimezone) {
    if (nonNull(dateTime) && nonNull(fromTimezone) && nonNull(toTimezone)) {
      // Convert the LocalDateTime to ZonedDateTime in the original timezone
      final ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of(fromTimezone));
      // Convert the ZonedDateTime to the target timezone
      final ZonedDateTime targetZonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of(toTimezone));
      // Return the converted LocalDateTime in the target timezone
      return targetZonedDateTime.toLocalDateTime();
    }
    return LocalDateTime.now();
  }


}
