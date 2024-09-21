package com.fleencorp.feen.constant.external.google.calendar.event;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing the decision of an event attendee to join.
 * Currently supports only the "ACCEPTED" decision.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum EventAttendeeDecisionToJoin implements ApiParameter {

  ACCEPTED("accepted");

  private final String value;

  EventAttendeeDecisionToJoin(String value) {
    this.value = value;
  }

  public static String accepted() {
    return ACCEPTED.getValue();
  }
}
