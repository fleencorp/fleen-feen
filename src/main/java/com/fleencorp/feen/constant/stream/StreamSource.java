package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing different types of streams source.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum StreamSource implements ApiParameter {

  YOUTUBE_LIVE("YouTube Live"),
  GOOGLE_MEET("Google Meet"),
  GOOGLE_MEET_LIVESTREAM("Google Meet Live Stream"),
  NONE("None");

  private final String value;

  StreamSource(final String value) {
    this.value = value;
  }

  public static StreamSource of(final String value) {
    return parseEnumOrNull(value, StreamSource.class);
  }
}
