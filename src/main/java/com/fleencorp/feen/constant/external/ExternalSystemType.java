package com.fleencorp.feen.constant.external;

import com.fleencorp.feen.constant.base.ApiParameter;
import lombok.Getter;

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
