package com.fleencorp.feen.oauth2.service.external.impl.external;

import com.fleencorp.base.util.StringUtil;
import com.fleencorp.feen.adapter.spotify.SpotifyTokenAdapter;
import com.fleencorp.feen.adapter.spotify.model.response.SpotifyTokenResponse;
import com.fleencorp.feen.common.configuration.external.spotify.SpotifyAuthorizationFields;
import com.fleencorp.feen.common.configuration.external.spotify.SpotifyOauth2Credential;
import com.fleencorp.feen.common.configuration.external.spotify.SpotifyScopes;
import com.fleencorp.feen.common.constant.base.ReportMessageType;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.model.request.Oauth2AuthenticationRequest;
import com.fleencorp.feen.oauth2.model.response.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.model.response.RefreshOauth2TokenResponse;
import com.fleencorp.feen.oauth2.service.external.Oauth2CommonService;
import com.fleencorp.feen.oauth2.service.external.SpotifyOauth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static com.fleencorp.feen.common.constant.base.SimpleConstant.COMMA;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Slf4j
@Component
public class SpotifyOauth2ServiceImpl implements SpotifyOauth2Service {

  private static final String AUTH_URL = "https://accounts.spotify.com/authorize";

  private final SpotifyOauth2Credential credential;
  private final Oauth2CommonService oauth2CommonService;
  private final SpotifyTokenAdapter spotifyTokenAdapter;

  public SpotifyOauth2ServiceImpl(
      final SpotifyOauth2Credential credential,
      final Oauth2CommonService oauth2CommonService,
      final SpotifyTokenAdapter spotifyTokenAdapter) {
    this.credential = credential;
    this.oauth2CommonService = oauth2CommonService;
    this.spotifyTokenAdapter = spotifyTokenAdapter;
  }

  @Override
  public String getAuthorizationUri(Oauth2AuthenticationRequest authenticationRequest) {
    return buildAuthorizationUrl(authenticationRequest.getServiceTypeState());
  }

  @Override
  public RefreshOauth2TokenResponse refreshUserToken(String refreshToken) {
    final Oauth2ServiceType oauth2ServiceType = Oauth2ServiceType.spotify();
    final ReportMessageType reportMessageType = ReportMessageType.spotifyOauth2();

    try {
      final SpotifyTokenResponse tokenResponse = spotifyTokenAdapter.refreshAccessToken(refreshToken);
      if (nonNull(tokenResponse)) {
        final String scope = StringUtil.replaceWith(tokenResponse.getScope(), SPACE, COMMA);

        return RefreshOauth2TokenResponse.of(
          tokenResponse.getAccessToken(),
          tokenResponse.getRefreshToken(),
          tokenResponse.getExpiresInSeconds(),
          tokenResponse.getTokenType(),
          scope
        );
      }
    } catch (final Exception ex) {
      oauth2CommonService.handleTokenResponseException(ex, ex.getMessage(), oauth2ServiceType, reportMessageType);
    }

    throw FailedOperationException.of();
  }

  @Override
  public CompletedOauth2AuthorizationResponse verifyAuthorizationCode(String authorizationCode, Oauth2AuthenticationRequest authenticationRequest) {
    final Oauth2ServiceType oauth2ServiceType = authenticationRequest.getOauth2ServiceType();
    final ReportMessageType reportMessageType = ReportMessageType.spotifyOauth2();

    try {
      final SpotifyTokenResponse tokenResponse = spotifyTokenAdapter.getAccessToken(authorizationCode);
      if (nonNull(tokenResponse)) {
        final String scope = StringUtil.replaceWith(tokenResponse.getScope(), SPACE, COMMA);

        return CompletedOauth2AuthorizationResponse.of(
          tokenResponse.getAccessToken(),
          tokenResponse.getRefreshToken(),
          tokenResponse.getExpiresInSeconds(),
          tokenResponse.getTokenType(),
          scope
        );
      }
    } catch (final Exception ex) {
      oauth2CommonService.handleTokenResponseException(ex, ex.getMessage(), oauth2ServiceType, reportMessageType);
    }

    throw FailedOperationException.of();
  }

  private String buildAuthorizationUrl(String state) {
    UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(AUTH_URL);

    uri.queryParam(SpotifyAuthorizationFields.clientId(), credential.getClientId());
    uri.queryParam(SpotifyAuthorizationFields.redirectUri(), credential.getRedirectUris());

    uri.queryParam(SpotifyAuthorizationFields.responseType(), SpotifyAuthorizationFields.code());
    uri.queryParam(SpotifyAuthorizationFields.scope(), SpotifyScopes.allScopesAsEncodedString());
    uri.queryParam(SpotifyAuthorizationFields.state(), SpotifyAuthorizationFields.encode(state));

    return uri
      .build(true)
      .toUriString();
  }
}
