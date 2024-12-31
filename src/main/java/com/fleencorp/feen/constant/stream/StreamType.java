package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

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

  public static StreamType of(final String value) {
    return parseEnumOrNull(value, StreamType.class);
  }

  public static boolean isEvent(final StreamType streamType) {
    return streamType == EVENT;
  }

  public static boolean isLiveStream(final StreamType streamType) {
    return streamType == LIVE_STREAM;
  }

  public static String event() {
    return EVENT.name();
  }

  public static String liveStream() {
    return LIVE_STREAM.name();
  }
}
