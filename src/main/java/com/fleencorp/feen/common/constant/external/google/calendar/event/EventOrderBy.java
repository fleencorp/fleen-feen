package com.fleencorp.feen.common.constant.external.google.calendar.event;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing options for sending updates for events.
*
* @author Yusuf Alamu
* @version 1.0
*/
@Getter
public enum EventOrderBy implements ApiParameter {

  START_TIME("startTime"),
  UPDATED("updated");

  private final String value;

  EventOrderBy(final String value) {
    this.value = value;
  }
}
