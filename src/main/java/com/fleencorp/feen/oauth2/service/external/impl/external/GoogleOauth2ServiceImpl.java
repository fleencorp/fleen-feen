package com.fleencorp.feen.oauth2.service.external.impl.external;

import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.base.util.StringUtil;
import com.fleencorp.feen.common.aspect.MeasureExecutionTime;
import com.fleencorp.feen.common.configuration.external.google.oauth2.GoogleOauth2Credential;
import com.fleencorp.feen.common.constant.base.ReportMessageType;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidGrantOrTokenException;
import com.fleencorp.feen.oauth2.model.request.Oauth2AuthenticationRequest;
import com.fleencorp.feen.oauth2.model.response.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.model.response.RefreshOauth2TokenResponse;
import com.fleencorp.feen.oauth2.service.external.GoogleOauth2Service;
import com.fleencorp.feen.oauth2.service.external.Oauth2CommonService;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
@Component
public class GoogleOauth2ServiceImpl implements GoogleOauth2Service {

  private final Oauth2CommonService oauth2CommonService;
  private final GoogleOauth2Credential oauth2Credential;

  public GoogleOauth2ServiceImpl(
    final Oauth2CommonService oauth2CommonService,
    final GoogleOauth2Credential oauth2Credential) {
    this.oauth2Credential = oauth2Credential;
    this.oauth2CommonService = oauth2CommonService;
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
  @Override
  public String getAuthorizationUri(final Oauth2AuthenticationRequest authenticationRequest) {
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

    throw FailedOperationException.of();
  }

  @Override
  public CompletedOauth2AuthorizationResponse verifyAuthorizationCode(final String authorizationCode, final Oauth2AuthenticationRequest authenticationRequest) {
    // Exchange the authorization code for an OAuth2 token response
    final TokenResponse tokenResponse = exchangeAuthorizationCode(authorizationCode, authenticationRequest);

    if (nonNull(tokenResponse)) {
      final String scope = StringUtil.replaceWith(tokenResponse.getScope(), SPACE, COMMA); // Replace space with comma in scope;

      return CompletedOauth2AuthorizationResponse.of(
        tokenResponse.getAccessToken(),
        tokenResponse.getRefreshToken(),
        tokenResponse.getExpiresInSeconds(),
        tokenResponse.getTokenType(),
        scope
      );
    }

    throw FailedOperationException.of();
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
      throw FailedOperationException.of();
    }

    final Oauth2ServiceType oauth2ServiceType = authenticationRequest.getOauth2ServiceType();
    final ReportMessageType reportMessageType = ReportMessageType.googleOauth2();

    try {
      // Request a new token using the authorization code and execute the request
      return googleAuthorizationCodeFlow.newTokenRequest(authorizationCode)
        .setRedirectUri(getRedirectUri())
        .execute();
    } catch (final TokenResponseException ex) {
      // Handle exceptions during the token response process
      oauth2CommonService.handleTokenResponseException(ex, authorizationCode, oauth2ServiceType, reportMessageType);
    } catch (final IOException ex) {
      // Handle IO exceptions during the token exchange process
      oauth2CommonService.handleException(ex, ex.getMessage(), reportMessageType);
    }

    // Return an empty response
    throw FailedOperationException.of();
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
  @Override
  public RefreshOauth2TokenResponse refreshUserToken(final String refreshToken) {
    final GoogleTokenResponse tokenResponse = refreshAccessToken(refreshToken);

    if (nonNull(tokenResponse)) {
      return RefreshOauth2TokenResponse.of(
        tokenResponse.getAccessToken(),
        tokenResponse.getRefreshToken(),
        tokenResponse.getExpiresInSeconds(),
        tokenResponse.getTokenType(),
        tokenResponse.getScope()
      );
    }

    throw FailedOperationException.of();
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
      oauth2CommonService.handleExceptionForTokenRefresh(ex, ReportMessageType.googleOauth2());
    }

    throw FailedOperationException.of();
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
      oauth2Credential.getClientSecret()
    );
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
}
