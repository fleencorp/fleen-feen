package com.fleencorp.feen.constant.base;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * The {@code ResultType} enum represents different types of results
 * that can occur in the application.
 *
 * <p>Each type is associated with a specific string value that describes the result.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ResultType implements ApiParameter {

  EVENT_STREAM_CREATED("Event Stream Created");

  private final String value;

  /**
   * Constructs a new {@code ResultType} with the specified value.
   *
   * @param value the string value associated with the result type
   */
  ResultType(String value) {
    this.value = value;
  }
}
