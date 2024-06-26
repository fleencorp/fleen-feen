package com.fleencorp.feen.constant.stream;

import com.fleencorp.feen.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing different types of streams.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum StreamType implements ApiParameter {

  YOUTUBE_LIVE("YouTube Live"),
  GOOGLE_MEET("Google Meet"),
  GOOGLE_MEET_LIVESTREAM("Google Meet Live Stream");

  private final String value;

  StreamType(String value) {
    this.value = value;
  }
}
