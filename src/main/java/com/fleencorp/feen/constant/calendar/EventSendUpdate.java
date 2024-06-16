package com.fleencorp.feen.constant.calendar;

import com.fleencorp.feen.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing options for sending updates for events.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum EventSendUpdate implements ApiParameter {

  EXTERNAL_ONLY("externalOnly"),
  ALL("all"),
  NONE("none");

  private final String value;

  EventSendUpdate(String value) {
    this.value = value;
  }
}
