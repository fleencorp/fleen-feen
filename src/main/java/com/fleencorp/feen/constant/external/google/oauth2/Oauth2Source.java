package com.fleencorp.feen.constant.external.google.oauth2;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing different OAuth2 sources.
 * Each enum constant holds the name of the OAuth2 provider it represents.
 *
 * <p>This enum is used to identify the source of OAuth2 authentication,
 * with Google being the currently supported provider.</p>
 *
 */
@Getter
public enum Oauth2Source implements ApiParameter {

  GOOGLE("Google");

  private final String value;

  Oauth2Source(final String value) {
    this.value = value;
  }
}
