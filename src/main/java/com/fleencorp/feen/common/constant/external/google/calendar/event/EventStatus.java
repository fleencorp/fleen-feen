package com.fleencorp.feen.common.constant.external.google.calendar.event;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing status options for events.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum EventStatus implements ApiParameter {

  CONFIRMED("confirmed"),
  CANCELLED("cancelled");

  private final String value;

  EventStatus(final String value) {
    this.value = value;
  }

  public static String cancelled() {
    return CANCELLED.getValue();
  }
}
