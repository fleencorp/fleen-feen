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

  /**
   * Returns the {@link Oauth2ServiceType} for YouTube.
   *
   * <p>This method provides a static way to access the {@link Oauth2ServiceType} enumeration value
   * corresponding to YouTube. It is used to identify or categorize OAuth2 services related to YouTube.</p>
   *
   * @return the {@link Oauth2ServiceType} representing YouTube.
   */
  public static Oauth2ServiceType youTube() {
    return Oauth2ServiceType.YOUTUBE;
  }

  /**
   * Returns the {@link Oauth2ServiceType} for Google Calendar.
   *
   * <p>This method provides a static way to access the {@link Oauth2ServiceType} enumeration value
   * corresponding to Google Calendar. It is used to identify or categorize OAuth2 services related to Google Calendar.</p>
   *
   * @return the {@link Oauth2ServiceType} representing Google Calendar.
   */
  public static Oauth2ServiceType googleCalendar() {
    return Oauth2ServiceType.GOOGLE_CALENDAR;
  }

  /**
   * Checks if the given {@link Oauth2ServiceType} is YouTube.
   *
   * <p>This method compares the provided {@code oauth2ServiceType} with the YouTube enumeration value
   * to determine if they are the same. It is used for validation or decision-making processes where
   * identifying a YouTube service type is necessary.</p>
   *
   * @param oauth2ServiceType the {@link Oauth2ServiceType} to check.
   * @return {@code true} if the given {@code oauth2ServiceType} is YouTube; {@code false} otherwise.
   */
  public static boolean isYoutube(final Oauth2ServiceType oauth2ServiceType) {
    return oauth2ServiceType == YOUTUBE;
  }

  /**
   * Checks if the given {@link Oauth2ServiceType} is Google Calendar.
   *
   * <p>This method compares the provided {@code oauth2ServiceType} with the Google Calendar enumeration value
   * to determine if they are the same. It is used for validation or decision-making processes where
   * identifying a Google Calendar service type is necessary.</p>
   *
   * @param oauth2ServiceType the {@link Oauth2ServiceType} to check.
   * @return {@code true} if the given {@code oauth2ServiceType} is Google Calendar; {@code false} otherwise.
   */
  public static boolean isGoogleCalendar(final Oauth2ServiceType oauth2ServiceType) {
    return oauth2ServiceType == GOOGLE_CALENDAR;
  }

}
