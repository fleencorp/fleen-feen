package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing different types of stream status.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum StreamStatus implements ApiParameter {

  ACTIVE("Active", "stream.status.active"),
  CANCELED("Canceled", "stream.status.canceled"),;

  private final String value;
  private final String messageCode;

  StreamStatus(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }
}
