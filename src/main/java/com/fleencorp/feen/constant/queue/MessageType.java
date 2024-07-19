package com.fleencorp.feen.constant.queue;


import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum MessageType implements ApiParameter {

  VERIFICATION("Verification"),
  STREAM_EVENT("Stream Event");

  private final String value;

  MessageType(String value) {
    this.value = value;
  }
}
