package com.fleencorp.feen.constant.external.google.youtube.base;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

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

  YouTubeLiveBroadcastVisibility(String value) {
    this.value = value;
  }
}
