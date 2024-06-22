package com.fleencorp.feen.util.datetime;

/**
 * Utility class for date and time format patterns.
 *
 * <p> This class defines static constants for various date and time format patterns
 * commonly used in applications for parsing, formatting, and displaying dates and times.</p>
 *
 * <p> These constants can be used to standardize date and time representations across
 * different parts of the application.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class DateFormatUtil {

  public static final String DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";
  public static final String DATE = "yyyy-MM-dd";
  public static final String TIME = "[H:]mm";
  public static final String DATE_TIME_WITH_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  public static final String DATE_TIME_NO_SECONDS = "yyyy-MM-dd'T'HH:mm";
  public static final String DATE_TIME_NO_PT_SECONDS = "yyyy-MM-dd HH:mm";
}
