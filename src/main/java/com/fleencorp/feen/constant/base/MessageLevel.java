package com.fleencorp.feen.constant.base;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum MessageLevel implements ApiParameter {

  ERROR("Error"),
  INFO("Info"),
  WARN("Warn");

  private final String value;

  MessageLevel(String value) {
    this.value = value;
  }
}
