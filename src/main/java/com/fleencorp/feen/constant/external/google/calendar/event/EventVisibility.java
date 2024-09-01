package com.fleencorp.feen.constant.external.google.calendar.event;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing visibility options for events.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum EventVisibility implements ApiParameter {

  PUBLIC("public"),
  PRIVATE("private");

  private final String value;

  EventVisibility(final String value) {
    this.value = value;
  }

  public static EventVisibility of(final String value) {
    return parseEnumOrNull(value, EventVisibility.class);
  }
}
