package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing different types of streams.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum StreamTimeType implements ApiParameter {

  UPCOMING("Upcoming"),
  LIVE("Live"),
  PAST("Past");

  private final String value;

  StreamTimeType(final String value) {
    this.value = value;
  }
}
