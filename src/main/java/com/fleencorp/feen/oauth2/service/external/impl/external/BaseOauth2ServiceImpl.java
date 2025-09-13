package com.fleencorp.feen.oauth2.service.external.impl.external;

import com.fleencorp.feen.common.aspect.MeasureExecutionTime;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.constant.Oauth2Source;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import com.fleencorp.feen.oauth2.model.request.Oauth2AuthenticationRequest;
import com.fleencorp.feen.oauth2.model.response.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.model.response.RefreshOauth2TokenResponse;
import com.fleencorp.feen.oauth2.model.response.StartOauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.model.response.base.Oauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.repository.Oauth2AuthorizationRepository;
import com.fleencorp.feen.oauth2.service.external.BaseOauth2Service;
import com.fleencorp.feen.oauth2.service.external.GoogleOauth2Service;
import com.fleencorp.feen.oauth2.service.external.Oauth2CommonService;
import com.fleencorp.feen.oauth2.service.external.SpotifyOauth2Service;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fleencorp.base.util.FleenUtil.setIfNonNull;
import static com.fleencorp.feen.oauth2.service.external.impl.external.Oauth2CommonServiceImpl.setUserOauth2AuthorizationFields;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class BaseOauth2ServiceImpl implements BaseOauth2Service {

  private final GoogleOauth2Service googleOauth2Service;
  private final Oauth2CommonService oauth2CommonService;
  private final SpotifyOauth2Service spotifyOauth2Service;
  private final Oauth2AuthorizationRepository oauth2AuthorizationRepository;
  private final Localizer localizer;

  public BaseOauth2ServiceImpl(
      final GoogleOauth2Service googleOauth2Service,
      final Oauth2CommonService oauth2CommonService,
      final SpotifyOauth2Service spotifyOauth2Service,
      final Oauth2AuthorizationRepository oauth2AuthorizationRepository,
      final Localizer localizer) {
    this.googleOauth2Service = googleOauth2Service;
    this.oauth2CommonService = oauth2CommonService;
    this.spotifyOauth2Service = spotifyOauth2Service;
    this.oauth2AuthorizationRepository = oauth2AuthorizationRepository;
    this.localizer = localizer;
  }

  @Override
  public StartOauth2AuthorizationResponse startOauth2Authentication(final Oauth2AuthenticationRequest authenticationRequest) {
    final Oauth2ServiceType oauth2ServiceType = authenticationRequest.getOauth2ServiceType();

    final String authorizationUri = switch (oauth2ServiceType) {
      case SPOTIFY -> spotifyOauth2Service.getAuthorizationUri(authenticationRequest);
      case GOOGLE_CALENDAR, YOUTUBE -> googleOauth2Service.getAuthorizationUri(authenticationRequest);
    };

    final StartOauth2AuthorizationResponse startOauth2AuthorizationResponse = StartOauth2AuthorizationResponse.of(authorizationUri);
    return localizer.of(startOauth2AuthorizationResponse);
  }

  @Override
  @Transactional
  @MeasureExecutionTime
  public CompletedOauth2AuthorizationResponse verifyAuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(final String authorizationCode, final Oauth2AuthenticationRequest authenticationRequest, final RegisteredUser user) {
    final CompletedOauth2AuthorizationResponse oauth2AuthorizationResponse = verifyAuthorizationCode(authorizationCode, authenticationRequest);
    final Oauth2ServiceType oauth2ServiceType = authenticationRequest.getOauth2ServiceType();
    final Oauth2Source oauth2Source = Oauth2Source.byOauth2ServiceType(oauth2ServiceType);
    final Oauth2Authorization oauth2Authorization = oauth2CommonService.findOauth2AuthorizationOrCreateOne(oauth2ServiceType, user.getId());

    setUserOauth2AuthorizationFields(oauth2Authorization, oauth2AuthorizationResponse);
    oauth2Authorization.updateServiceTypeAndSource(oauth2ServiceType, oauth2Source);
    oauth2AuthorizationRepository.save(oauth2Authorization);

    return localizer.of(oauth2AuthorizationResponse);
  }

  protected RefreshOauth2TokenResponse refreshUserAccessToken(final Oauth2AuthenticationRequest authenticationRequest, final Long userId) {
    RefreshOauth2TokenResponse oauth2TokenResponse;
    final Oauth2ServiceType oauth2ServiceType = authenticationRequest.getOauth2ServiceType();

    oauth2TokenResponse = switch (oauth2ServiceType) {
      case GOOGLE_CALENDAR, YOUTUBE -> googleOauth2Service.refreshUserToken(authenticationRequest.getRefreshToken());
      case SPOTIFY -> spotifyOauth2Service.refreshUserToken(authenticationRequest.getRefreshToken());
    };

    saveAndUpdateUserOauth2Credential(authenticationRequest, userId, oauth2TokenResponse);

    if (nonNull(oauth2TokenResponse)) {
      return oauth2TokenResponse;
    }

    throw Oauth2InvalidAuthorizationException.ofDefault(oauth2ServiceType);
  }

  private void saveAndUpdateUserOauth2Credential(Oauth2AuthenticationRequest authRequest, Long userId, RefreshOauth2TokenResponse tokenResponse) {
    // If the token response is not null, proceed with updating the authorization
    if (nonNull(tokenResponse)) {
      // Retrieve the OAuth2 authorization for the user
      final Oauth2Authorization oauth2Authorization = getOauth2Authorization(authRequest, userId);
      // Update the authorization with the new token information
      setUserOauth2AuthorizationFields(oauth2Authorization, tokenResponse);
      // Save the updated authorization in the repository
      oauth2AuthorizationRepository.save(oauth2Authorization);
    }

    throw FailedOperationException.of();
  }

  protected Oauth2Authorization getOauth2Authorization(final Oauth2AuthenticationRequest authenticationRequest, final Long userId) {
    if (authenticationRequest.isOauth2AuthorizationPresent()) {
      return authenticationRequest.getOauth2Authorization();
    } else if (authenticationRequest.isOauth2ServiceTypePresent()) {
      return oauth2CommonService.findOauth2AuthorizationOrCreateOne(authenticationRequest.getOauth2ServiceType(), userId);
    } else {
      return Oauth2Authorization.of(userId);
    }
  }


  protected CompletedOauth2AuthorizationResponse verifyAuthorizationCode(final String authorizationCode, final Oauth2AuthenticationRequest authenticationRequest) {
    return switch (authenticationRequest.getOauth2ServiceType()) {
      case SPOTIFY -> spotifyOauth2Service.verifyAuthorizationCode(authorizationCode, authenticationRequest);
      case GOOGLE_CALENDAR, YOUTUBE -> googleOauth2Service.verifyAuthorizationCode(authorizationCode, authenticationRequest);
    };
  }

  @Override
  @Transactional
  public Oauth2Authorization validateAccessTokenExpiryTimeOrRefreshToken(final Oauth2ServiceType oauth2ServiceType, final Long userId) {
    final Oauth2Authorization oauth2Authorization = oauth2AuthorizationRepository.findByMemberIdAndServiceType(userId, oauth2ServiceType)
      .orElseThrow(Oauth2InvalidAuthorizationException.of(oauth2ServiceType));

    final String currentAccessToken = oauth2Authorization.getAccessToken();

    validateAccessTokenExpiryTimeOrRefreshToken(oauth2Authorization, oauth2ServiceType, userId);
    oauth2CommonService.saveTokenIfAccessTokenUpdated(oauth2Authorization, currentAccessToken);

    return oauth2Authorization;
  }

  protected void validateAccessTokenExpiryTimeOrRefreshToken(final Oauth2Authorization oauth2Authorization, final Oauth2ServiceType oauth2ServiceType, final Long userId) {
    final Oauth2AuthenticationRequest authenticationRequest = Oauth2AuthenticationRequest.of(oauth2ServiceType);
    authenticationRequest.setRefreshToken(oauth2Authorization.getRefreshToken());
    authenticationRequest.setOauth2Authorization(oauth2Authorization);

    if (oauth2Authorization.isAccessTokenExpired()) {
      final Oauth2AuthorizationResponse refreshOauth2TokenResponse = refreshUserAccessToken(authenticationRequest, userId);

      setIfNonNull(refreshOauth2TokenResponse::getAccessToken, oauth2Authorization::setAccessToken);
      setIfNonNull(refreshOauth2TokenResponse::getRefreshToken, oauth2Authorization::setRefreshToken);
    }
  }
}
