package com.fleencorp.feen.oauth2.service.external.impl.external;

import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.base.util.StringUtil;
import com.fleencorp.feen.common.aspect.MeasureExecutionTime;
import com.fleencorp.feen.common.constant.base.ReportMessageType;
import com.fleencorp.feen.common.service.report.ReporterService;
import com.fleencorp.feen.configuration.external.google.oauth2.Oauth2Credential;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.constant.Oauth2Source;
import com.fleencorp.feen.oauth2.exception.core.Oauth2AuthorizationException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidGrantOrTokenException;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import com.fleencorp.feen.oauth2.model.request.Oauth2AuthenticationRequest;
import com.fleencorp.feen.oauth2.model.response.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.model.response.RefreshOauth2TokenResponse;
import com.fleencorp.feen.oauth2.model.response.StartOauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.model.response.base.Oauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.repository.Oauth2AuthorizationRepository;
import com.fleencorp.feen.oauth2.service.external.GoogleOauth2Service;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.fleencorp.base.util.FleenUtil.setIfNonNull;
import static com.fleencorp.feen.common.constant.base.SimpleConstant.COMMA;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.SPACE;

/**
 * The {@code GoogleOauth2Service} class provides functionalities to interact with Google's OAuth 2.0 authentication.
 *
 * <p>This service handles the authentication process, token generation, and management for users
 * accessing Google services. By using this service, applications can securely access Google APIs
 * on behalf of users.</p>
 *
 * <p>This class abstracts the complexity involved in OAuth 2.0 authorization, ensuring that the
 * integration with Google services is straightforward and secure. It includes methods for
 * obtaining authorization codes, exchanging them for access tokens, refreshing tokens, and
 * revoking tokens when necessary.</p>
 *
 * <p>Note: This class requires the Google OAuth 2.0 client library to be included in the project
 * dependencies.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 *
 * @see <a href="https://developers.google.com/youtube/v3/guides/authentication">
 *   Implementing OAuth 2.0 Authorization</a>
 * @see <a href="https://developers.google.com/youtube/v3/guides/auth/server-side-web-apps">
 *   Using OAuth 2.0 for Web Server Applications</a>
 */
@Service
@Slf4j
public class GoogleOauth2ServiceImpl implements GoogleOauth2Service {

  private final Oauth2Credential oauth2Credential;
  private final Oauth2AuthorizationRepository oauth2AuthorizationRepository;
  private final ReporterService reporterService;
  private final Localizer localizer;

  /**
   * Constructs a new GoogleOauth2Service.
   *
   * @param oauth2Credential The OAuth2 credentials used for Google authentication.
   * @param oauth2AuthorizationRepository The repository for storing OAuth2 authorization data.
   * @param reporterService The service used for reporting events.
   * @param localizer The service for setting localized message to the responses
   */
  public GoogleOauth2ServiceImpl(
      final Oauth2Credential oauth2Credential,
      final Oauth2AuthorizationRepository oauth2AuthorizationRepository,
      final ReporterService reporterService,
      final Localizer localizer) {
    this.oauth2Credential = oauth2Credential;
    this.oauth2AuthorizationRepository = oauth2AuthorizationRepository;
    this.reporterService = reporterService;
    this.localizer = localizer;
  }

  /**
   * Starts OAuth 2.0 authentication by retrieving the authorization URI.
   *
   * <p>This method initiates the OAuth 2.0 authentication flow by obtaining the authorization URI
   * using {@link #getAuthorizationUri(Oauth2AuthenticationRequest)}. It then constructs and returns a StartOauth2AuthorizationResponse
   * containing the retrieved authorization URI.</p>
   *
   * <p>The authorization URI is crucial for redirecting users to Google's consent screen, where they
   * can grant permissions to the application.</p>
   *
   * @param authenticationRequest The Oauth 2.0 authentication request
   * @return A StartOauth2AuthorizationResponse containing the OAuth 2.0 authorization URI.
   */
  @Override
  public StartOauth2AuthorizationResponse startOauth2Authentication(final Oauth2AuthenticationRequest authenticationRequest) {
    final String authorizationUri = getAuthorizationUri(authenticationRequest);
    return localizer.of(StartOauth2AuthorizationResponse.of(authorizationUri));
  }

  /**
   * Constructs the OAuth 2.0 authorization URI for initiating the authorization flow.
   *
   * <p>This method retrieves the configured GoogleAuthorizationCodeFlow instance using {@link #getGoogleAuthorizationCodeFlow(Oauth2AuthenticationRequest)}.
   * If available, it constructs a GoogleAuthorizationCodeRequestUrl to generate the authorization URI.</p>
   *
   * <p>The authorization URI is built with the specified redirect URI obtained from {@link #getRedirectUri()} to ensure
   * proper redirection after user consent.</p>
   *
   * <p>If the GoogleAuthorizationCodeFlow instance is not available or any required parameters are missing, this method
   * returns null.</p>
   *
   * @param authenticationRequest The Oauth 2.0 authentication request
   * @return The OAuth 2.0 authorization URI, or null if unable to construct the URI.
   */
  @MeasureExecutionTime
  protected String getAuthorizationUri(final Oauth2AuthenticationRequest authenticationRequest) {
    // Obtain the Google authorization code flow based on the authentication request
    final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = getGoogleAuthorizationCodeFlow(authenticationRequest);
    if (nonNull(googleAuthorizationCodeFlow)) {
      // Create a new authorization URL using the code flow
      final GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl = googleAuthorizationCodeFlow.newAuthorizationUrl();
      // Set the state parameter with the service type in the URL
      googleAuthorizationCodeRequestUrl.setState(authenticationRequest.getServiceTypeState());
      // Set the redirect URI for the authorization request
      googleAuthorizationCodeRequestUrl.setRedirectUri(getRedirectUri());
      // Build and return the complete authorization URL
      return googleAuthorizationCodeRequestUrl.build();
    }
    return null;
  }

  /**
   * Verifies an OAuth 2.0 authorization code, retrieves authorization details, and saves them in the database.
   *
   * <p>This method verifies the OAuth 2.0 authorization code by calling {@link #verifyAuthorizationCode(String, Oauth2AuthenticationRequest)}
   * with the provided authorization code. It retrieves or creates a Oauth2Authorization entity for the
   * specified FleenUser and updates its authorization details with the obtained CompletedOauth2AuthorizationResponse.</p>
   *
   * <p>If the authorization code verification is successful and a CompletedOauth2AuthorizationResponse is obtained,
   * it updates the Oauth2Authorization entity with the access token, refresh token, access token expiration time,
   * token type, and scope. It then saves the entity using the Oauth2AuthorizationRepository and returns
   * the CompletedOauth2AuthorizationResponse.</p>
   *
   * @param authorizationCode The OAuth 2.0 authorization code obtained from the authorization callback.
   * @param authenticationRequest The Oauth 2.0 authentication request
   * @param user The FleenUser for whom the OAuth 2.0 authorization is being verified and saved.
   * @return A CompletedOauth2AuthorizationResponse containing authorization details if successful,
   *         or null if the authorization code is invalid or expired.
   */
  @Override
  @Transactional
  @MeasureExecutionTime
  public CompletedOauth2AuthorizationResponse verifyAuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(final String authorizationCode, final Oauth2AuthenticationRequest authenticationRequest, final RegisteredUser user) {
    // Verify the authorization code and obtain the completed OAuth2 authorization response
    final CompletedOauth2AuthorizationResponse oauth2AuthorizationResponse = verifyAuthorizationCode(authorizationCode, authenticationRequest);
    // Convert the user to a member instance
    final Member member = user.toMember();
    // Get service type associated with authorization and authorization code
    final Oauth2ServiceType oauth2ServiceType = authenticationRequest.getOauth2ServiceType();
    // Retrieve the OAuth2 authorization for the member and service type, or create a new one if not found
    final Oauth2Authorization oauth2Authorization = findOauth2AuthorizationOrCreateOne(oauth2ServiceType, member);

    // Update the OAuth2 authorization with the details from the authorization response
    updateOauth2Authorization(oauth2Authorization, oauth2AuthorizationResponse);
    // Set the service type and source for the authorization
    oauth2Authorization.updateServiceTypeAndSource(oauth2ServiceType, Oauth2Source.google());
    // Save the updated OAuth2 authorization in the repository
    oauth2AuthorizationRepository.save(oauth2Authorization);
    // Return a localized response of the oauth2 authorization
    return localizer.of(oauth2AuthorizationResponse);
  }

  /**
   * Refreshes a user's OAuth 2.0 access token and updates the authorization details in the database.
   *
   * <p>This method refreshes the user's OAuth 2.0 access token using the provided refresh token
   * by calling the {@link #refreshUserToken(String)} method. It retrieves or creates a Oauth2Authorization
   * entity for the given FleenUser, updates its authorization details with the refreshed token response,
   * and saves the entity using the Oauth2AuthorizationRepository.</p>
   *
   * <p>If the token refresh is successful and a RefreshOauth2TokenResponse is obtained, it updates
   * the Oauth2Authorization entity with the refreshed access token, refresh token, access token
   * expiration time, token type, and scope. It then returns the RefreshOauth2TokenResponse.</p>
   *
   * @param authenticationRequest The Oauth 2.0 authentication request
   * @param user The FleenUser for whom the OAuth 2.0 authorization is being refreshed.
   * @return A RefreshOauth2TokenResponse containing the refreshed OAuth 2.0 access token and other details.
   */
  protected RefreshOauth2TokenResponse refreshUserAccessToken(final Oauth2AuthenticationRequest authenticationRequest, final RegisteredUser user) {
    // Attempt to refresh the user's OAuth2 token using the provided refresh token
    final RefreshOauth2TokenResponse oauth2TokenResponse = refreshUserToken(authenticationRequest.getRefreshToken());
    // If the token response is not null, proceed with updating the authorization
    if (nonNull(oauth2TokenResponse)) {
      // Convert the user to a member instance
      final Member member = user.toMember();
      // Retrieve the OAuth2 authorization for the user
      final Oauth2Authorization oauth2Authorization = getOauth2Authorization(authenticationRequest, member);
      // Update the authorization with the new token information
      updateOauth2Authorization(oauth2Authorization, oauth2TokenResponse);
      // Save the updated authorization in the repository
      oauth2AuthorizationRepository.save(oauth2Authorization);
      // Return the response
      return oauth2TokenResponse;
    }
    // Throw an exception if the token response is null
    throw Oauth2InvalidAuthorizationException.ofDefault(authenticationRequest.getOauth2ServiceType());
  }

  /**
   * Retrieves or creates an {@link Oauth2Authorization} based on the provided {@link Oauth2AuthenticationRequest}
   * and {@link Member}.
   *
   * <p>If the {@code authenticationRequest} contains a non-null {@link Oauth2Authorization}, it is returned directly.
   * Otherwise, if the request specifies an OAuth2 service type, an existing authorization is retrieved from the
   * {@code oauth2AuthorizationRepository} based on the {@link Member} and service type. If no matching authorization
   * is found, a new {@link Oauth2Authorization} is created. If neither an authorization nor service type is provided,
   * the method attempts to retrieve an existing authorization for the {@link Member}; if none is found, a new one is created.</p>
   *
   * @param authenticationRequest the authentication request containing OAuth2 details.
   * @param member the member associated with the authorization.
   * @return an {@link Oauth2Authorization} instance based on the provided request and member.
   */
  protected Oauth2Authorization getOauth2Authorization(final Oauth2AuthenticationRequest authenticationRequest, final Member member) {
    // Check if the request already contains an OAuth2 authorization
    if (authenticationRequest.isOauth2AuthorizationPresent()) {
      return authenticationRequest.getOauth2Authorization();
    } else if (authenticationRequest.isOauth2ServiceTypePresent()) {
      // If no authorization is provided, check for a specific OAuth2 service type and retrieve the authorization if available
      return findOauth2AuthorizationOrCreateOne(authenticationRequest.getOauth2ServiceType(), member);
    } else {
      // If neither an authorization nor a service type is provided, find or create a general OAuth2 authorization for the member
      return Oauth2Authorization.of(member);
    }
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
  private void updateOauth2Authorization(final Oauth2Authorization oauth2Authorization, final Oauth2AuthorizationResponse oauth2AuthorizationResponse) {
    if (nonNull(oauth2Authorization) && nonNull(oauth2AuthorizationResponse)) {
      setIfNonNull(oauth2AuthorizationResponse::getAccessToken, oauth2Authorization::setAccessToken);
      setIfNonNull(oauth2AuthorizationResponse::getRefreshToken, oauth2Authorization::setRefreshToken);
      setIfNonNull(oauth2AuthorizationResponse::getScope, oauth2Authorization::setScope);
      setIfNonNull(oauth2AuthorizationResponse::getTokenType, oauth2Authorization::setTokenType);
      oauth2Authorization.setTokenExpirationTimeInMilliseconds(oauth2AuthorizationResponse.getAccessTokenExpirationTimeInMilliseconds());
    }
  }

  /**
   * Refreshes a user's OAuth 2.0 access token using the provided refresh token.
   *
   * <p>This method attempts to refresh the user's OAuth 2.0 access token by calling the
   * {@link #refreshAccessToken(String)} method with the provided refresh token.</p>
   *
   * <p>If the token refresh is successful and a GoogleTokenResponse is obtained, it constructs
   * and returns a RefreshOauth2TokenResponse containing the refreshed access token, refresh token,
   * access token expiration time in seconds, token type, and scope.</p>
   *
   * <p>If the token refresh fails or returns null, indicating an invalid or expired refresh token,
   * this method returns null.</p>
   *
   * @param refreshToken The OAuth 2.0 refresh token used to obtain a new access token.
   * @return A RefreshOauth2TokenResponse containing the refreshed OAuth 2.0 access token and other details,
   *         or null if the refresh token is invalid or expired.
   */
  @MeasureExecutionTime
  protected RefreshOauth2TokenResponse refreshUserToken(final String refreshToken) {
    final GoogleTokenResponse googleTokenResponse = refreshAccessToken(refreshToken);
    if (nonNull(googleTokenResponse)) {
      return RefreshOauth2TokenResponse.of(googleTokenResponse.getAccessToken(), googleTokenResponse.getRefreshToken(), googleTokenResponse.getExpiresInSeconds(),
          googleTokenResponse.getTokenType(), googleTokenResponse.getScope());
    }
    return null;
  }

  /**
   * Verifies an OAuth 2.0 authorization code and retrieves authorization details.
   *
   * <p>This method exchanges the provided OAuth 2.0 authorization code for an access token and
   * other authorization details using the {@link #exchangeAuthorizationCode(String, Oauth2AuthenticationRequest)} method.</p>
   *
   * <p>If the token exchange is successful and a TokenResponse is obtained, it constructs and
   * returns a CompletedOauth2AuthorizationResponse containing the access token, refresh token,
   * token type, access token expiration time in seconds, and scope.</p>
   *
   * <p>If the token exchange fails or returns null, indicating an invalid or expired authorization
   * code, this method returns null.</p>
   *
   * @param authorizationCode The OAuth 2.0 authorization code obtained from the authorization callback.
   * @param authenticationRequest The Oauth 2.0 authentication request
   * @return A CompletedOauth2AuthorizationResponse containing authorization details if successful,
   *         or null if the authorization code is invalid or expired.
   */
  protected CompletedOauth2AuthorizationResponse verifyAuthorizationCode(final String authorizationCode, final Oauth2AuthenticationRequest authenticationRequest) {
    // Exchange the authorization code for an OAuth2 token response
    final TokenResponse tokenResponse = exchangeAuthorizationCode(authorizationCode, authenticationRequest);

    // If the token response is not null, create and return the CompletedOauth2AuthorizationResponse with the token details
    if (nonNull(tokenResponse)) {
      return CompletedOauth2AuthorizationResponse.of(
        tokenResponse.getAccessToken(),
        tokenResponse.getRefreshToken(),
        tokenResponse.getExpiresInSeconds(),
        tokenResponse.getTokenType(),
        StringUtil.replaceWith(tokenResponse.getScope(), SPACE, COMMA) // Replace space with comma in scope
      );
    }

    // Return null if the token response is null
    return null;
  }


  /**
   * Exchanges an OAuth 2.0 authorization code for an access token and possibly a refresh token.
   *
   * <p>This method uses the configured GoogleAuthorizationCodeFlow instance to exchange the
   * provided authorization code for an OAuth 2.0 access token. It sets the redirect URI as part
   * of the token request to ensure secure communication.</p>
   *
   * <p>If successful, it returns a TokenResponse containing the OAuth 2.0 access token, refresh token,
   * and other token details. If an error occurs during the token exchange process, specific exceptions
   * are caught and handled:</p>
   *
   * @param authorizationCode The OAuth 2.0 authorization code obtained from the authorization callback.
   * @param authenticationRequest The Oauth 2.0 authentication request
   * @return A TokenResponse containing the OAuth 2.0 access token and optionally a refresh token.
   * @throws Oauth2InvalidGrantOrTokenException If the token request fails due to "invalid_grant".
   * @throws Oauth2InvalidAuthorizationException If the token request fails due to other authorization errors.
   * @throws ExternalSystemException If an I/O error occurs during the token exchange process.
   */
  @MeasureExecutionTime
  protected TokenResponse exchangeAuthorizationCode(final String authorizationCode, final Oauth2AuthenticationRequest authenticationRequest) {
    // Retrieve the GoogleAuthorizationCodeFlow for the OAuth2 service
    final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = getGoogleAuthorizationCodeFlow(authenticationRequest);
    // Return null if the authorization code flow is not available
    if (isNull(googleAuthorizationCodeFlow)) {
      return null;
    }

    try {
      // Request a new token using the authorization code and execute the request
      return googleAuthorizationCodeFlow.newTokenRequest(authorizationCode)
        .setRedirectUri(getRedirectUri())
        .execute();
    } catch (final TokenResponseException ex) {
      // Handle exceptions during the token response process
      handleTokenResponseException(ex, authorizationCode, authenticationRequest);
    } catch (final IOException ex) {
      // Handle IO exceptions during the token exchange process
      handleIOException(ex, ex.getMessage());
    }

    // Return an empty response
    return null;
  }

  /**
   * Handles {@link TokenResponseException} during OAuth2 token exchange processes.
   *
   * <p>This method processes the {@link TokenResponseException} that occurs during the token exchange
   * phase of OAuth2 authentication. It sends an error message through the {@code reporterService}
   * and determines whether the exception is due to an invalid grant. If so, an {@link Oauth2InvalidGrantOrTokenException}
   * is thrown; otherwise, it throws an {@link Oauth2InvalidAuthorizationException}.</p>
   *
   * @param ex the {@link TokenResponseException} that occurred
   * @param authorizationCode the OAuth2 authorization code involved in the request
   * @param authenticationRequest the {@link Oauth2AuthenticationRequest} containing details of the OAuth2 service and request
   */
  protected void handleTokenResponseException(final TokenResponseException ex, final String authorizationCode, final Oauth2AuthenticationRequest authenticationRequest) {
    // Generate an error message indicating the failure in verifying the authorization code
    final String errorMessage = Oauth2AuthorizationException.failedVerificationOfAuthorizationCodeMessage(ex.getMessage());
    // Send the error message to the reporting service for logging or monitoring
    reporterService.sendMessage(errorMessage, ReportMessageType.googleOauth2());

    // Check if the exception message indicates an invalid grant error
    if (Oauth2AuthorizationException.isInvalidGrant(ex.getMessage())) {
      // Throw a custom exception indicating an invalid grant or token error
      throw Oauth2InvalidGrantOrTokenException.of(authorizationCode, authenticationRequest.getOauth2ServiceType());
    } else {
      // Throw a generic OAuth2 invalid authorization exception
      throw new Oauth2InvalidAuthorizationException();
    }
  }

  /**
   * Handles {@link IOException} during OAuth2 authorization processes.
   *
   * <p>This method processes the provided {@link IOException} by generating a detailed error message
   * related to the failure of authorization code verification. It sends a notification through the
   * {@code reporterService} and then throws an {@link ExternalSystemException}.</p>
   *
   * @param ex the {@link IOException} that occurred
   * @param message the custom message to include in the error report
   */
  protected void handleIOException(final IOException ex, final String message) {
    // Generate an error message indicating the failure in verifying the authorization code
    final String errorMessage = Oauth2AuthorizationException.failedVerificationOfAuthorizationCodeMessage(message);
    // Send the error message to the reporting service for logging or monitoring
    reporterService.sendMessage(errorMessage, ReportMessageType.googleOauth2());
    // Throw a custom exception indicating an issue with an external system
    throw new ExternalSystemException(ex.getMessage());
  }

  /**
   * Refreshes an OAuth 2.0 access token using the provided refresh token.
   *
   * <p>This method sends a request to Google's token endpoint to refresh the OAuth 2.0 access token
   * using the given refresh token. It constructs a GoogleRefreshTokenRequest with the necessary
   * parameters, including transport, JSON factory, client credentials, and the refresh token.</p>
   *
   * <p>If successful, it returns a GoogleTokenResponse containing the new access token and optionally
   * a new refresh token. If an error occurs during the token refresh process, an ExternalSystemException
   * is thrown, logged with details of the error for troubleshooting.</p>
   *
   * @param refreshToken The OAuth 2.0 refresh token used to obtain a new access token.
   * @return A GoogleTokenResponse containing the refreshed OAuth 2.0 access token.
   * @throws ExternalSystemException If an error occurs while refreshing the access token.
   */
  @MeasureExecutionTime
  protected GoogleTokenResponse refreshAccessToken(final String refreshToken) {
    try {
      final GoogleRefreshTokenRequest refreshTokenRequest = createRefreshTokenRequest(refreshToken);
      return refreshTokenRequest.execute();
    } catch (final IOException ex) {
      handleIOExceptionForTokenRefresh(ex);
    }
    return null;
  }

  /**
   * Creates a refresh token request for OAuth2 authentication.
   *
   * <p>This method generates a {@link GoogleRefreshTokenRequest} using the provided refresh token
   * and the stored OAuth2 credentials, including the client ID and client secret. It uses the
   * appropriate transport and JSON factory for making the request to refresh the token.</p>
   *
   * @param refreshToken the refresh token to be used for authentication and token refresh
   * @return a {@link GoogleRefreshTokenRequest} initialized with the provided refresh token and OAuth2 credentials
   */
  protected GoogleRefreshTokenRequest createRefreshTokenRequest(final String refreshToken) {
    return new GoogleRefreshTokenRequest(
      getTransport(),
      getJsonFactory(),
      refreshToken,
      oauth2Credential.getClientId(),
      oauth2Credential.getClientSecret());
  }

  /**
   * Handles an {@link IOException} that occurs during the Google OAuth2 token refresh process.
   *
   * <p>This method captures any {@link IOException} thrown while attempting to refresh the OAuth2
   * token using the provided refresh token, logs the error, and sends a report message. It then throws
   * an {@link ExternalSystemException} with the error message.</p>
   *
   * @param ex the {@link IOException} encountered during the token refresh process
   */
  protected void handleIOExceptionForTokenRefresh(final IOException ex) {
    // Format the error message for token refresh failure
    final String errorMessage = Oauth2AuthorizationException.failedTokenRefresh(ex.getMessage());
    // Send a report message with the error details
    reporterService.sendMessage(errorMessage, ReportMessageType.googleOauth2());
    // Throw an external system exception with the error message
    throw new ExternalSystemException(errorMessage);
  }

  /**
   * Constructs a GoogleAuthorizationCodeFlow.Builder for configuring OAuth 2.0 authorization flow settings.
   *
   * <p>This method creates a builder instance for setting up the OAuth 2.0 authorization flow,
   * including transport, JSON factory, client secrets, and scopes required for accessing Google APIs.</p>
   *
   * <p>The builder is configured with access type "offline", allowing the application to receive
   * refresh tokens for long-term access to user data. The approval prompt is set to "force",
   * ensuring that users are prompted to grant access every time.</p>
   *
   * @param authenticationRequest The Oauth 2.0 authentication request
   * @return A GoogleAuthorizationCodeFlow.Builder instance configured with OAuth 2.0 authorization settings.
   */
  protected GoogleAuthorizationCodeFlow.Builder getAuthorizationCodeFlowBuilder(final Oauth2AuthenticationRequest authenticationRequest) {
    return new GoogleAuthorizationCodeFlow.Builder(getTransport(), getJsonFactory(), getClientSecrets(),
            authenticationRequest.getScopes())
            .setAccessType("offline")
            .setApprovalPrompt("force");
  }

  /**
   * Retrieves an instance of GoogleAuthorizationCodeFlow configured with OAuth 2.0 settings.
   *
   * <p>This method builds and returns a GoogleAuthorizationCodeFlow instance configured using
   * the builder obtained from {@link #getAuthorizationCodeFlowBuilder(Oauth2AuthenticationRequest)}.</p>
   *
   * <p>The returned GoogleAuthorizationCodeFlow encapsulates the OAuth 2.0 authorization flow
   * settings, including transport, JSON factory, client secrets, scopes, access type, and approval prompt.</p>
   *
   * @param authenticationRequest The Oauth 2.0 authentication request
   * @return A GoogleAuthorizationCodeFlow instance configured with OAuth 2.0 authorization settings.
   */
  protected GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow(final Oauth2AuthenticationRequest authenticationRequest) {
    return getAuthorizationCodeFlowBuilder(authenticationRequest)
      .build();
  }

  /**
   * Retrieves the OAuth 2.0 client secrets associated with the current OAuth 2.0 credentials.
   *
   * <p>This method converts the OAuth 2.0 credentials into GoogleClientSecrets object, which contains
   * client identifier and client secret. These credentials are used to authenticate and authorize
   * access to Google APIs.</p>
   *
   * <p>It is crucial to keep the client secrets secure and never expose them in client-side code
   * or public repositories to prevent unauthorized access to your application.</p>
   *
   * @return The OAuth 2.0 client secrets as GoogleClientSecrets object.
   */
  private GoogleClientSecrets getClientSecrets() {
    return oauth2Credential.toGoogleClientSecrets();
  }

  /**
   * Retrieves the OAuth 2.0 redirect URI configured for the current OAuth 2.0 credentials.
   *
   * <p>This method returns the redirect URI from the OAuth 2.0 credentials, which is used to
   * redirect users back to the application after authentication and authorization.</p>
   *
   * <p>The redirect URI must be registered with the OAuth 2.0 provider (e.g., Google) to ensure
   * secure communication and to prevent unauthorized redirection attempts.</p>
   *
   * @return The OAuth 2.0 redirect URI configured in the current OAuth 2.0 credentials.
   */
  private String getRedirectUri() {
    return oauth2Credential.getRedirectUriList().getFirst();
  }

  /**
   * Retrieves an instance of the HTTP transport for making requests.
   * This method creates and returns a new instance of the NetHttpTransport class,
   * which is used for making HTTP requests to external services, such as the YouTube API.
   *
   * @return An instance of the HTTP transport.
   */
  public static NetHttpTransport getTransport() {
    return new NetHttpTransport();
  }

  /**
   * Retrieves the JSON factory for creating JSON-related objects.
   * This method returns an instance of the JSON factory, specifically
   * the default instance provided by the Gson library.
   * The JSON factory is used for creating JSON-related objects such as
   * JSON parsers and generators.
   *
   * @return The JSON factory instance.
   */
  public static JsonFactory getJsonFactory() {
    return GsonFactory.getDefaultInstance();
  }

  /**
   * Validates the expiry time of the access token associated with the specified OAuth2 service type and user.
   * If the access token has expired, it attempts to refresh the token. The method will then save the updated
   * OAuth2 authorization details if a new token has been generated.
   *
   * @param oauth2ServiceType The type of the OAuth2 service (e.g., Google Calendar) for which the authorization
   *                          details are being validated.
   * @param user              The user whose OAuth2 authorization details are to be validated.
   * @return The updated {@link Oauth2Authorization} entity containing the authorization details for the specified
   *         OAuth2 service and user.
   * @throws Oauth2InvalidAuthorizationException if no valid OAuth2 authorization details are found for the
   *         specified user and service type.
   */
  @Override
  @Transactional
  public Oauth2Authorization validateAccessTokenExpiryTimeOrRefreshToken(final Oauth2ServiceType oauth2ServiceType, final RegisteredUser user) {
    // Retrieve user oauth2 authorization details associated with Google Calendar
    final Oauth2Authorization oauth2Authorization = oauth2AuthorizationRepository.findByMemberAndServiceType(user.toMember(), oauth2ServiceType)
      .orElseThrow(Oauth2InvalidAuthorizationException.of(oauth2ServiceType));

    final String currentAccessToken = oauth2Authorization.getAccessToken();
    // Check access token expiry date and refresh access token if it is expired
    validateAccessTokenExpiryTimeOrRefreshToken(oauth2Authorization, oauth2ServiceType, user);
    // Save Oauth2Authorization if new token has been set
    saveTokenIfAccessTokenUpdated(oauth2Authorization, currentAccessToken);
    // Return the oauth2 authorization
    return oauth2Authorization;
  }

  /**
   * Validates the expiration time of the OAuth2 access token and refreshes it if necessary.
   *
   * <p>This method checks if the access token associated with the provided {@link Oauth2Authorization} has expired.
   * If the token is expired, it attempts to refresh the access token using the stored refresh token. The refreshed
   * access token and refresh token (if available) are then updated in the {@link Oauth2Authorization} object.</p>
   *
   * @param oauth2Authorization the {@link Oauth2Authorization} containing the current access and refresh tokens
   * @param oauth2ServiceType the type of OAuth2 service being validated, e.g., Google, Facebook
   * @param user the authenticated user for whom the token validation and refresh are being performed
   */
  protected void validateAccessTokenExpiryTimeOrRefreshToken(final Oauth2Authorization oauth2Authorization, final Oauth2ServiceType oauth2ServiceType, final RegisteredUser user) {
    // Create a new authentication request for the specified OAuth2 service type
    final Oauth2AuthenticationRequest authenticationRequest = Oauth2AuthenticationRequest.of(oauth2ServiceType);
    // Set the refresh token in the authentication request
    authenticationRequest.setRefreshToken(oauth2Authorization.getRefreshToken());
    // Set the existing OAuth2 authorization in the authentication request
    authenticationRequest.setOauth2Authorization(oauth2Authorization);

    // Check if the access token has expired
    if (oauth2Authorization.isAccessTokenExpired()) {
      // Refresh the access token using the refresh token
      final Oauth2AuthorizationResponse refreshOauth2TokenResponse = refreshUserAccessToken(authenticationRequest, user);
      // Update the access token if the refresh response contains a new one
      setIfNonNull(refreshOauth2TokenResponse::getAccessToken, oauth2Authorization::setAccessToken);
      // Update the refresh token if the refresh response contains a new one
      setIfNonNull(refreshOauth2TokenResponse::getRefreshToken, oauth2Authorization::setRefreshToken);
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
  protected void saveTokenIfAccessTokenUpdated(final Oauth2Authorization oauth2Authorization, final String currentAccessToken) {
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
   * @param member the {@link Member} for whom the authorization is being retrieved or created
   * @return the existing or newly created {@link Oauth2Authorization} for the member
   */
  protected Oauth2Authorization findOauth2AuthorizationOrCreateOne(final Oauth2ServiceType oauth2ServiceType, final Member member) {
    // Find the OAuth2 authorization for the member and service type, or create a new one if not found
    return oauth2AuthorizationRepository
      .findByMemberAndServiceType(member, oauth2ServiceType)
      .orElseGet(() -> Oauth2Authorization.of(member));
  }

}
