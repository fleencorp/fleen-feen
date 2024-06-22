package com.fleencorp.feen.constant.external.google.calendar.event;

import com.fleencorp.feen.constant.base.ApiParameter;
import lombok.Getter;

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

  EventVisibility(String value) {
    this.value = value;
  }
}
