package com.fleencorp.feen.constant.external;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing types of external systems used within the application.
 *
 * <p>Each type corresponds to a specific external system integration.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ExternalSystemType implements ApiParameter {

  YOUTUBE("YouTube"),
  GOOGLE_CALENDAR("Google Calendar"),
  CALENDAR_EVENT("Calendar Event");

  private final String value;

  ExternalSystemType(String value) {
    this.value = value;
  }
}
