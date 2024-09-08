package com.fleencorp.feen.constant.external.google.youtube.base;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing the visibility status of a Google Meet event.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum YouTubeLiveBroadcastVisibility implements ApiParameter {

  PRIVATE("private"),
  PUBLIC("public"),
  UNLISTED("unlisted");

  private final String value;

  YouTubeLiveBroadcastVisibility(final String value) {
    this.value = value;
  }

  public static YouTubeLiveBroadcastVisibility of(final String value) {
    return parseEnumOrNull(value, YouTubeLiveBroadcastVisibility.class);
  }
}
