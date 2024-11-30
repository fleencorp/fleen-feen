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

  ACTIVE("Active", "stream.status.active", "stream.status.active.2", "stream.status.active.3"),
  CANCELED("Canceled", "stream.status.canceled", "stream.status.canceled.2", "stream.status.canceled.3");

  private final String value;
  private final String messageCode;
  private final String messageCode2;
  private final String messageCode3;

  StreamStatus(
      final String value,
      final String messageCode,
      final String messageCode2,
      final String messageCode3) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
    this.messageCode3 = messageCode3;
  }
}
