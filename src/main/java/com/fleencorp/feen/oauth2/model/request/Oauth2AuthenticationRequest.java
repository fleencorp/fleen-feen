package com.fleencorp.feen.oauth2.model.request;

import com.fleencorp.feen.common.configuration.external.spotify.SpotifyScopes;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidScopeException;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.youtube.YouTubeScopes;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.fleencorp.feen.oauth2.constant.Oauth2WebKey.SERVICE_TYPE;
import static java.util.Objects.nonNull;

@Slf4j
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Oauth2AuthenticationRequest {

  public static final String OAUTH_2_SERVICE_TYPE_KEY = SERVICE_TYPE;
  private Oauth2ServiceType oauth2ServiceType;
  private Collection<String> scopes;
  private String refreshToken;
  private Oauth2Authorization oauth2Authorization;

  /**
   * Retrieves the state of the OAuth2 service type in a formatted string.
   *
   * <p>This method checks if the {@code oauth2ServiceType} is non-null. If it is,
   * it returns a string representation of the service type in the format
   * {@code SERVICE_TYPE=value}, where {@code value} is the lowercase name
   * of the {@code oauth2ServiceType}. If the {@code oauth2ServiceType} is null,
   * it returns an empty string.</p>
   *
   * @return a formatted string representing the OAuth2 service type state, or an
   *         empty string if the service type is not set
   */
  public String getServiceTypeState() {
    return nonNull(oauth2ServiceType)
      ? SERVICE_TYPE.concat("=").concat(oauth2ServiceType.name().toLowerCase())
      : "";
  }


  public static Oauth2AuthenticationRequest of(final Oauth2ServiceType oauth2ServiceType) {
    if (nonNull(oauth2ServiceType)) {
      return switch (oauth2ServiceType) {
        case GOOGLE_CALENDAR -> getGoogleCalendarOauth2AuthenticationRequest();
        case SPOTIFY -> getSpotifyOauth2AuthenticationRequest();
        case YOUTUBE -> getYouTubeOauth2AuthenticationRequest();
      };
    }

    throw new Oauth2InvalidScopeException();
  }

  public static Oauth2AuthenticationRequest getGoogleCalendarOauth2AuthenticationRequest() {
    final Oauth2AuthenticationRequest authRequest = new Oauth2AuthenticationRequest();
    authRequest.setOauth2ServiceType(Oauth2ServiceType.googleCalendar());
    authRequest.setScopes(getCalendarScopes());

    return authRequest;
  }

  public static Oauth2AuthenticationRequest getSpotifyOauth2AuthenticationRequest() {
    Collection<String> scopes = List.of(SpotifyScopes.allScopesAsString());

    final Oauth2AuthenticationRequest authRequest = new Oauth2AuthenticationRequest();
    authRequest.setOauth2ServiceType(Oauth2ServiceType.spotify());
    authRequest.setScopes(scopes);

    return authRequest;
  }

  public static Oauth2AuthenticationRequest getYouTubeOauth2AuthenticationRequest() {
    final Oauth2AuthenticationRequest authRequest = new Oauth2AuthenticationRequest();
    authRequest.setOauth2ServiceType(Oauth2ServiceType.youTube());
    authRequest.setScopes(getYouTubeScopes());

    return authRequest;
  }

  private static Set<String> getCalendarScopes() {
    return CalendarScopes.all();
  }

  private static Set<String> getYouTubeScopes() {
    return YouTubeScopes.all();
  }

  public boolean isOauth2AuthorizationPresent() {
    return nonNull(oauth2Authorization);
  }

  public boolean isOauth2ServiceTypePresent() {
    return nonNull(oauth2ServiceType);
  }
}
