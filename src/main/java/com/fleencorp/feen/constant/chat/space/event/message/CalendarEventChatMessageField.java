package com.fleencorp.feen.constant.chat.space.event.message;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Represents fields for calendar event chat messages in the application.
 *
 * <p>This enum defines various constants used in calendar event chat messages, including icons,
 * event details, and other relevant fields.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum CalendarEventChatMessageField implements ApiParameter {

  BOOKMARK_ICON("BOOKMARK"),
  CLOCK_ICON("CLOCK"),
  DATE("Date"),
  EVENT_DETAILS("Event Details"),
  JOIN_EVENT("Join Event"),
  ROBOT_IMAGE("https://developers.google.com/chat/images/quickstart-app-avatar.png"),
  TIMEZONE("Timezone");

  private final String value;

  CalendarEventChatMessageField(final String value) {
    this.value = value;
  }

  /**
   * Returns the value representing the "Join Event" field.
   *
   * @return The value of the "Join Event" field.
   */
  public static String joinEvent() {
    return JOIN_EVENT.getValue();
  }

  /**
   * Returns the value representing the "Event Details" field.
   *
   * @return The value of the "Event Details" field.
   */
  public static String eventDetails() {
    return EVENT_DETAILS.getValue();
  }

  /**
   * Returns the value representing the bookmark icon.
   *
   * @return The value of the bookmark icon.
   */
  public static String bookmarkIcon() {
    return BOOKMARK_ICON.getValue();
  }

  /**
   * Returns the value representing the clock icon.
   *
   * @return The value of the clock icon.
   */
  public static String clockIcon() {
    return CLOCK_ICON.getValue();
  }

  /**
   * Returns the value representing the robot image URL.
   *
   * @return The value of the robot image URL.
   */
  public static String robotImage() {
    return ROBOT_IMAGE.getValue();
  }
}
