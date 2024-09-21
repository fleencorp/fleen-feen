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

  ACCEPTED("accepted"),
  DECLINED("declined"),
  NEEDS_ACTION("needsAction"),
  TENTATIVE("tentative");

  private final String value;

  /**
   * Constructor for EventAttendeeDecisionToJoin.
   *
   * @param value the value representing the attendee's decision.
   */
  EventAttendeeDecisionToJoin(String value) {
    this.value = value;
  }

  /**
   * Retrieves the value of the accepted decision.
   *
   * @return the string "accepted".
   */
  public static String accepted() {
    return ACCEPTED.getValue();
  }
}
