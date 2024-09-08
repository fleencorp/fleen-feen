package com.fleencorp.feen.model.request;

import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.exception.google.oauth2.InvalidOauth2ScopeException;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.youtube.YouTubeScopes;
import lombok.*;

import java.util.Set;

import static com.fleencorp.feen.constant.external.google.oauth2.Oauth2WebKey.SERVICE_TYPE;
import static java.util.Objects.nonNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Oauth2AuthenticationRequest {

  public static final String oauth2ServiceTypeKey = SERVICE_TYPE;
  private Oauth2ServiceType oauth2ServiceType;
  private Set<String> scopes;
  private String refreshToken;
  private Oauth2Authorization oauth2Authorization;

  public String getServiceTypeState() {
    return nonNull(oauth2ServiceType)
      ? SERVICE_TYPE.concat("=").concat(oauth2ServiceType.name().toLowerCase())
      : "";
  }

  /**
   * Creates an {@link Oauth2AuthenticationRequest} based on the specified OAuth2 scope.
   *
   * <p>This method generates an OAuth2 authentication request specific to the given scope. If the scope is
   * {@link Oauth2ServiceType#GOOGLE_CALENDAR}, it returns a request configured for Google Calendar. If the scope is
   * {@link Oauth2ServiceType#YOUTUBE}, it returns a request configured for YouTube. If the scope is not recognized,
   * an {@link InvalidOauth2ScopeException} is thrown.</p>
   *
   * @param oauth2ServiceType The OAuth2 scope for which the authentication request should be created.
   * @return A configured {@link Oauth2AuthenticationRequest} for the given scope.
   * @throws InvalidOauth2ScopeException If the provided scope is not valid or recognized.
   */
  public static Oauth2AuthenticationRequest of(final Oauth2ServiceType oauth2ServiceType) {
    if (nonNull(oauth2ServiceType)) {
      if (Oauth2ServiceType.isGoogleCalendar(oauth2ServiceType)) {
        return getGoogleCalendarOauth2AuthenticationRequest();
      } else if (Oauth2ServiceType.isYoutube(oauth2ServiceType)) {
        return getYouTubeOauth2AuthenticationRequest();
      }
    }
    throw new InvalidOauth2ScopeException();
  }

  /**
   * Creates an {@link Oauth2AuthenticationRequest} for Google Calendar OAuth2 authentication.
   *
   * <p>This method sets the OAuth2 scope to {@link Oauth2ServiceType#GOOGLE_CALENDAR} and includes all necessary scopes
   * for Google Calendar API access.</p>
   *
   * @return A configured {@link Oauth2AuthenticationRequest} for Google Calendar.
   */
  public static Oauth2AuthenticationRequest getGoogleCalendarOauth2AuthenticationRequest() {
    return Oauth2AuthenticationRequest.builder()
      .oauth2ServiceType(Oauth2ServiceType.googleCalendar())
      .scopes(getCalendarScopes())
      .build();
  }

  /**
   * Creates an {@link Oauth2AuthenticationRequest} for YouTube OAuth2 authentication.
   *
   * <p>This method sets the OAuth2 scope to {@link Oauth2ServiceType#YOUTUBE} and includes all necessary scopes
   * for YouTube API access.</p>
   *
   * @return A configured {@link Oauth2AuthenticationRequest} for YouTube.
   */
  public static Oauth2AuthenticationRequest getYouTubeOauth2AuthenticationRequest() {
    return Oauth2AuthenticationRequest.builder()
      .oauth2ServiceType(Oauth2ServiceType.youTube())
      .scopes(getYouTubeScopes())
      .build();
  }

  /**
   * Retrieves the OAuth 2.0 scopes required for interacting with Calendar APIs.
   *
   * <p>This method returns a set of strings representing all available scopes for Calendar APIs.</p>
   *
   * <p>The scopes define the level of access and permissions granted to the application when
   * interacting with user data through Calendar services.</p>
   *
   * @return A {@code Set<String>} containing OAuth 2.0 scopes required for Calendar APIs.
   */
  private static Set<String> getCalendarScopes() {
    return CalendarScopes.all();
  }

  /**
   * Retrieves the OAuth 2.0 scopes required for interacting with YouTube APIs.
   *
   * <p>This method returns a set of strings representing all available scopes for YouTube APIs.</p>
   *
   * <p>The scopes define the level of access and permissions granted to the application when
   * interacting with user data through YouTube services.</p>
   *
   * @return A {@code Set<String>} containing OAuth 2.0 scopes required for YouTube APIs.
   */
  private static Set<String> getYouTubeScopes() {
    return YouTubeScopes.all();
  }
}
