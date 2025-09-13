package com.fleencorp.feen.oauth2.service.external.impl.external;

import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.feen.common.constant.base.ReportMessageType;
import com.fleencorp.feen.common.service.report.ReporterService;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.exception.core.Oauth2AuthorizationException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidGrantOrTokenException;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import com.fleencorp.feen.oauth2.model.response.base.Oauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.repository.Oauth2AuthorizationRepository;
import com.fleencorp.feen.oauth2.service.external.Oauth2CommonService;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.fleencorp.base.util.FleenUtil.setIfNonNull;
import static java.util.Objects.nonNull;

@Service
public class Oauth2CommonServiceImpl implements Oauth2CommonService {

  private final ReporterService reporterService;
  private final Oauth2AuthorizationRepository oauth2AuthorizationRepository;

  public Oauth2CommonServiceImpl(
    final ReporterService reporterService,
    final Oauth2AuthorizationRepository oauth2AuthorizationRepository) {
    this.reporterService = reporterService;
    this.oauth2AuthorizationRepository = oauth2AuthorizationRepository;
  }

  @Override
  public void handleTokenResponseException(final Exception ex, final String authorizationCode, final Oauth2ServiceType oauth2ServiceType, ReportMessageType reportMessageType) {
    // Generate an error message indicating the failure in verifying the authorization code
    final String errorMessage = Oauth2AuthorizationException.failedVerificationOfAuthorizationCodeMessage(ex.getMessage());
    // Send the error message to the reporting service for logging or monitoring
    reporterService.sendMessage(errorMessage, reportMessageType);

    // Check if the exception message indicates an invalid grant error
    if (Oauth2AuthorizationException.isInvalidGrant(ex.getMessage())) {
      // Throw a custom exception indicating an invalid grant or token error
      throw Oauth2InvalidGrantOrTokenException.of(authorizationCode, oauth2ServiceType);
    } else {
      // Throw a generic OAuth2 invalid authorization exception
      throw new Oauth2InvalidAuthorizationException();
    }
  }

  @Override
  public void handleException(final Exception ex, final String message, final ReportMessageType reportMessageType) {
    // Generate an error message indicating the failure in verifying the authorization code
    final String errorMessage = Oauth2AuthorizationException.failedVerificationOfAuthorizationCodeMessage(message);
    // Send the error message to the reporting service for logging or monitoring
    reporterService.sendMessage(errorMessage, reportMessageType);
    // Throw a custom exception indicating an issue with an external system
    throw new ExternalSystemException(ex.getMessage());
  }

  @Override
  public void handleExceptionForTokenRefresh(final IOException ex, final ReportMessageType reportMessageType) {
    // Format the error message for token refresh failure
    final String errorMessage = Oauth2AuthorizationException.failedTokenRefresh(ex.getMessage());
    // Send a report message with the error details
    reporterService.sendMessage(errorMessage, reportMessageType);
    // Throw an external system exception with the error message
    throw new ExternalSystemException(errorMessage);
  }

  /**
   * Updates OAuth 2.0 authorization details in the Oauth2Authorization entity.
   *
   * <p>This method updates the OAuth 2.0 authorization details in the provided Oauth2Authorization
   * entity using the data from the given Oauth2AuthorizationResponse. If both parameters are non-null,
   * it sets the access token, refresh token, access token expiration time, authorization scope,
   * and token type in the Oauth2Authorization entity.</p>
   *
   * <p>This method does not return any value; it directly modifies the state of the Oauth2Authorization
   * entity passed as a parameter.</p>
   *
   * @param oauth2Authorization         The Oauth2Authorization entity to be updated.
   * @param oauth2AuthorizationResponse The OAuth 2.0 authorization response containing updated authorization details.
   */
  public static void setUserOauth2AuthorizationFields(final Oauth2Authorization oauth2Authorization, final Oauth2AuthorizationResponse oauth2AuthorizationResponse) {
    if (nonNull(oauth2Authorization) && nonNull(oauth2AuthorizationResponse)) {
      setIfNonNull(oauth2AuthorizationResponse::getAccessToken, oauth2Authorization::setAccessToken);
      setIfNonNull(oauth2AuthorizationResponse::getRefreshToken, oauth2Authorization::setRefreshToken);
      setIfNonNull(oauth2AuthorizationResponse::getScope, oauth2Authorization::setScope);
      setIfNonNull(oauth2AuthorizationResponse::getTokenType, oauth2Authorization::setTokenType);
      oauth2Authorization.setTokenExpirationTimeInMilliseconds(oauth2AuthorizationResponse.getAccessTokenExpirationTimeInMilliseconds());
    }
  }

  /**
   * Saves the OAuth2 authorization entity if the access token has been updated.
   *
   * <p>This method compares the current access token with the one stored in the provided
   * {@link Oauth2Authorization} object. If they are different, it persists the updated
   * authorization entity to the repository.</p>
   *
   * @param oauth2Authorization the {@link Oauth2Authorization} entity containing the access token to compare.
   * @param currentAccessToken  the current access token to compare with the stored one.
   */
  @Override
  public void saveTokenIfAccessTokenUpdated(final Oauth2Authorization oauth2Authorization, final String currentAccessToken) {
    // Check if the access token has been updated
    if (oauth2Authorization.isAccessTokenNotSame(currentAccessToken)) {
      // Save the updated authorization entity to the repository
      oauth2AuthorizationRepository.save(oauth2Authorization);
    }
  }

  /**
   * Finds or creates an OAuth2 authorization for the specified member and service type.
   *
   * <p>If an existing OAuth2 authorization is found for the given {@link Member} and
   * {@link Oauth2ServiceType}, it is returned. Otherwise, a new OAuth2 authorization is created
   * for the member.</p>
   *
   * @param oauth2ServiceType the {@link Oauth2ServiceType} to create
   * @param userId the {@link Member} for whom the authorization is being retrieved or created
   * @return the existing or newly created {@link Oauth2Authorization} for the member
   */
  @Override
  public Oauth2Authorization findOauth2AuthorizationOrCreateOne(final Oauth2ServiceType oauth2ServiceType, final Long userId) {
    // Find the OAuth2 authorization for the member and service type, or create a new one if not found
    return oauth2AuthorizationRepository
      .findByMemberIdAndServiceType(userId, oauth2ServiceType)
      .orElseGet(() -> Oauth2Authorization.of(userId));
  }

}
