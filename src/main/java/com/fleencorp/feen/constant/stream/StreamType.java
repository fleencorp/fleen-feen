package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing the types of streams available in the system.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum StreamType implements ApiParameter {

  EVENT("Event", "stream.type.event"),
  LIVE_STREAM("Live Stream", "stream.type.live.stream");

  private final String value;
  private final String messageCode;

  StreamType(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }
}
