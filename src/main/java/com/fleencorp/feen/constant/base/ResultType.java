package com.fleencorp.feen.constant.base;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum ResultType implements ApiParameter {

  EVENT_STREAM_CREATED("Event Stream Created");

  private final String value;

  ResultType(String value) {
    this.value = value;
  }
}
