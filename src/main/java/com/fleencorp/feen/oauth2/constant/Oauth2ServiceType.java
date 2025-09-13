package com.fleencorp.feen.oauth2.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum Oauth2ServiceType implements ApiParameter {

  GOOGLE_CALENDAR("GoogleCalendar"),
  SPOTIFY("Spotify"),
  YOUTUBE("YouTube");

  private final String value;

  Oauth2ServiceType(final String value) {
    this.value = value;
  }

  public static Oauth2ServiceType of(final String value) {
    return parseEnumOrNull(value, Oauth2ServiceType.class);
  }

  public static Oauth2ServiceType googleCalendar() {
    return Oauth2ServiceType.GOOGLE_CALENDAR;
  }

  public static Oauth2ServiceType spotify() {
    return Oauth2ServiceType.SPOTIFY;
  }

  public static Oauth2ServiceType youTube() {
    return Oauth2ServiceType.YOUTUBE;
  }

  public static boolean isGoogleCalendar(final Oauth2ServiceType oauth2ServiceType) {
    return oauth2ServiceType == GOOGLE_CALENDAR;
  }

  public static boolean isSpotify(final Oauth2ServiceType oauth2ServiceType) {
    return oauth2ServiceType == SPOTIFY;
  }

  public static boolean isYoutube(final Oauth2ServiceType oauth2ServiceType) {
    return oauth2ServiceType == YOUTUBE;
  }

}
