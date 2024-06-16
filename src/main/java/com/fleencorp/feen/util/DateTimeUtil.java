package com.fleencorp.feen.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
public class DateTimeUtil {

  /**
   * Converts a LocalDateTime object to a Date object.
   *
   * <p>This method converts a Java 8 LocalDateTime object to a legacy Date object,
   * adjusting for the system default time zone.</p>
   *
   * @param localDateTime the LocalDateTime object to convert
   * @return a Date object representing the same date and time, or null if the input is null
   */
  public static Date toDate(LocalDateTime localDateTime) {
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
  public static long toMilliseconds(LocalDateTime dateTime, String timezone) {
    if (dateTime != null) {
      ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of(timezone));
      return zonedDateTime.toInstant().toEpochMilli();
    }
    return 0;
  }

}
