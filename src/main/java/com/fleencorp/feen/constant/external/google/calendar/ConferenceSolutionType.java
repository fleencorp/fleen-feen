package com.fleencorp.feen.constant.external.google.calendar;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing types of conference solutions for events.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum ConferenceSolutionType implements ApiParameter {

  EVENT_HANGOUT("eventHangout"),
  EVENT_NAMED_HANGOUT("eventNamedHangout"),
  HANGOUTS_MEET("hangoutsMeet");

  private final String value;

  ConferenceSolutionType(final String value) {
    this.value = value;
  }

  public static ConferenceSolutionType getDefault() {
    return ConferenceSolutionType.HANGOUTS_MEET;
  }
}
