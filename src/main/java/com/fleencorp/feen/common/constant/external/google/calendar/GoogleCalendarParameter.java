package com.fleencorp.feen.common.constant.external.google.calendar;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing various parameters related to Google Calendar integration.
 *
 * <p>Each enum constant corresponds to a specific configuration or URL used in the
 * Google Calendar API, such as the base URL for Google Meet links or other calendar-related values.</p>
 *
 * <p>These parameters are used throughout the application when interacting with
 * Google Calendar or Google Meet services.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum GoogleCalendarParameter implements ApiParameter {

  GOOGLE_MEET_BASE_URL("https://meet.google.com/");

  private final String value;

  GoogleCalendarParameter(final String value) {
    this.value = value;
  }

  /**
   * Retrieves the base URL for Google Meet from the Google Calendar parameters.
   *
   * <p>This method fetches the Google Meet URL as defined in the application's
   * {@link GoogleCalendarParameter} enum configuration.
   *
   * @return the base Google Meet URL as a String.
   */
  public static String googleMeetLink() {
    return GoogleCalendarParameter.GOOGLE_MEET_BASE_URL.getValue();
  }
}
