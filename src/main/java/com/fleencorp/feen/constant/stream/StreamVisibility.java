package com.fleencorp.feen.constant.stream;

import com.fleencorp.feen.constant.base.ApiParameter;
import lombok.Getter;

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
  CONFIDENTIAL("confidential"),
  UNLISTED("unlisted");

  private final String value;

  StreamVisibility(String value) {
    this.value = value;
  }
}
