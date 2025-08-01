package com.fleencorp.feen.common.constant.external.google.youtube;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing privacy statuses for live broadcasts on YouTube.
*
* <p>This enum implements {@link ApiParameter} and provides constants for three privacy statuses:</p>
*
* <p>Each constant has a corresponding value used in API requests and responses.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum LiveBroadcastPrivacyStatus implements ApiParameter {

  PRIVATE("private"),
  PUBLIC("public"),
  UNLISTED("unlisted");

  private final String value;

  LiveBroadcastPrivacyStatus(final String value) {
    this.value = value;
  }

  public static LiveBroadcastPrivacyStatus of(final String value) {
    return parseEnumOrNull(value, LiveBroadcastPrivacyStatus.class);
  }
}
