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

  ACTIVE("Active"),
  CANCELLED("Cancelled");

  private final String value;

  StreamStatus(final String value) {
    this.value = value;
  }
}
