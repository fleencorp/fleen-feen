package com.fleencorp.feen.stream.constant.core;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing different duration or creation of streams.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum StreamCreationType implements ApiParameter {

  INSTANT("Instant", "stream.creation.type.instant"),
  SCHEDULED("Scheduled", "stream.creation.type.scheduled");

  private final String value;
  private final String messageCode;

  StreamCreationType(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

}
