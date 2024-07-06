package com.fleencorp.feen.constant.stream;

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

  INSTANT("Instant"),
  SCHEDULED("Scheduled");

  private final String value;

  StreamCreationType(final String value) {
    this.value = value;
  }

}
