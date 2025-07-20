package com.fleencorp.feen.common.constant.external.google.youtube;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing different parts of a YouTube video that can be requested in API operations.
*
* <p>This enum implements {@link ApiParameter} and provides constants for video parts</p>
*
* <p>Each constant has a corresponding value used in API requests and responses.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum YouTubeVideoPart implements ApiParameter {

  CDN("cdn"),
  ID("id"),
  SNIPPET("snippet"),
  STATUS("status"),
  CONTENT_DETAILS("contentDetails");

  private final String value;

  YouTubeVideoPart(final String value) {
    this.value = value;
  }

  /**
   * Retrieves the value associated with the SNIPPET constant.
   *
   * @return the string value of SNIPPET
   */
  public static String snippet() {
    return SNIPPET.getValue();
  }

  /**
   * Retrieves the value associated with the ID constant.
   *
   * @return the string value of ID
   */
  public static String id() {
    return ID.getValue();
  }

  /**
   * Retrieves the value associated with the CONTENT_DETAILS constant.
   *
   * @return the string value of CONTENT_DETAILS
   */
  public static String contentDetails() {
    return CONTENT_DETAILS.getValue();
  }

  public static String status() {
    return STATUS.getValue();
  }

  public static String cdn() {
    return CDN.getValue();
  }
}
