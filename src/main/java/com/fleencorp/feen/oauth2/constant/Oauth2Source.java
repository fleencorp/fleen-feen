package com.fleencorp.feen.oauth2.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static java.util.Objects.nonNull;

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

  GOOGLE("Google"),
  SPOTIFY("Spotify");

  private final String value;

  Oauth2Source(final String value) {
    this.value = value;
  }

  public static Oauth2Source google() {
    return Oauth2Source.GOOGLE;
  }

  public static Oauth2Source spotify() {
    return Oauth2Source.SPOTIFY;
  }

  public static Oauth2Source byOauth2ServiceType(Oauth2ServiceType oauth2ServiceType) {
    if (nonNull(oauth2ServiceType)) {

      return switch (oauth2ServiceType) {
        case GOOGLE_CALENDAR, YOUTUBE -> Oauth2Source.GOOGLE;
        case SPOTIFY -> Oauth2Source.SPOTIFY;
      };
    }

    return null;
  }
}
