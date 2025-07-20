package com.fleencorp.feen.common.constant.external.google.youtube.base;

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

  APPLICATION_OCTET_STREAM("application/octet-stream"),
  LIVE_STREAMING_BASE_LINK("https://www.youtube.com/watch?v="),
  BRANDING_SETTINGS("brandingSettings"),
  EN("en"),
  IMAGE_CONTENT_TYPE("image/*"),
  US("US"),
  VIDEO_CONTENT_TYPE("video/*");

  private final String value;

  YouTubeParameter(final String value) {
    this.value = value;
  }

  /**
   * Retrieves the base link for live streaming.
   *
   * @return the live streaming base link as a string
   */
  public static String liveStreamLink() {
    return LIVE_STREAMING_BASE_LINK.getValue();
  }
}