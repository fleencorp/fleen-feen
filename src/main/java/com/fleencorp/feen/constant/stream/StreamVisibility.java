package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing the visibility status of a stream.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum StreamVisibility implements ApiParameter {

  PRIVATE("private"),
  PUBLIC("public"),
  PROTECTED("protected");

  private final String value;

  StreamVisibility(final String value) {
    this.value = value;
  }

  public static StreamVisibility of(final String value) {
    return parseEnumOrNull(value, StreamVisibility.class);
  }
}
