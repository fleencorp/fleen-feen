package com.fleencorp.feen.constant.share;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum BlockStatus implements ApiParameter {

  BLOCKED("Blocked"),
  UNBLOCK("Unblocked");

  private final String value;

  BlockStatus(final String value) {
    this.value = value;
  }
}
