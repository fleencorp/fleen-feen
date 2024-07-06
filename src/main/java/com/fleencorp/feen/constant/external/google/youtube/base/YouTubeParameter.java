package com.fleencorp.feen.constant.external.google.youtube.base;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing YouTube parameters used in the application.
* Implements the {@link ApiParameter} interface.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum YouTubeParameter implements ApiParameter {

  LIVE_STREAMING_BASE_LINK("https://www.youtube.com/watch?v="),
  US("US");

  private final String value;

  YouTubeParameter(String value) {
    this.value = value;
  }
}