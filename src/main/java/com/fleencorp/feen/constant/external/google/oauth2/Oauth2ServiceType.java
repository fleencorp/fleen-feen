package com.fleencorp.feen.constant.external.google.oauth2;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
 * Enum representing different types of OAuth2 services.
 * Each enum constant holds the name of the service it represents.
 *
 * <p>This enum is used to define the OAuth2 services that are supported by the application,
 * such as Google Calendar and YouTube.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum Oauth2ServiceType implements ApiParameter {

  GOOGLE_CALENDAR("GoogleCalendar"),
  YOUTUBE("YouTube");

  private final String value;

  Oauth2ServiceType(final String value) {
    this.value = value;
  }

  /**
   * Converts a string value into its corresponding Oauth2Scope enum constant.
   *
   * @param value the string value representing an OAuth2 scope.
   * @return the cor1responding Oauth2Scope enum constant, or null if no matching constant is found.
   */
  public static Oauth2ServiceType of(final String value) {
    return parseEnumOrNull(value, Oauth2ServiceType.class);
  }
}
