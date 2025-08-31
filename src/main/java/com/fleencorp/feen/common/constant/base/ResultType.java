package com.fleencorp.feen.common.constant.base;

import lombok.Getter;

@Getter
public enum ResultType {

  EVENT_STREAM_CREATED("Event Stream Created");

  private final String label;

  ResultType(final String label) {
    this.label = label;
  }
}
