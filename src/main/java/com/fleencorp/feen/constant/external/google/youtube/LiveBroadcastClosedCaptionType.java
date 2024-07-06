package com.fleencorp.feen.constant.external.google.youtube;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing different types of closed captions for live broadcasts.
*
* <p> This enum implements {@link ApiParameter} and provides constants for three types of closed captions:</p>
*
* <p>Each constant has a corresponding value used in API requests and responses.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum LiveBroadcastClosedCaptionType implements ApiParameter {

  CLOSED_CAPTIONS_DISABLED("closedCaptionsDisabled"),
  CLOSED_CAPTIONS_HTTP_POST("closedCaptionsHttpPost"),
  CLOSED_CAPTIONS_EMBEDDED("closedCaptionsEmbedded");

  private final String value;

  LiveBroadcastClosedCaptionType(final String value) {
    this.value = value;
  }
}
