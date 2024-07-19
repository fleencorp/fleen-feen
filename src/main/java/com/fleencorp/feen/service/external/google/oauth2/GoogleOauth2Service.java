package com.fleencorp.feen.service.external.google.oauth2;

import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.configuration.external.google.oauth2.Oauth2Credential;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidGrantOrTokenException;
import com.fleencorp.feen.model.domain.google.oauth2.GoogleOauth2Authorization;
import com.fleencorp.feen.model.response.external.google.oauth2.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.model.response.external.google.oauth2.RefreshOauth2TokenResponse;
import com.fleencorp.feen.model.response.external.google.oauth2.StartOauth2AuthorizationResponse;
import com.fleencorp.feen.model.response.external.google.oauth2.base.Oauth2AuthorizationResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.oauth2.GoogleOauth2AuthorizationRepository;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTubeScopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.nonNull;

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
public class GoogleOauth2Service {

  private final Oauth2Credential oauth2Credential;
  private final GoogleOauth2AuthorizationRepository googleOauth2AuthorizationRepository;

  public GoogleOauth2Service(
      final Oauth2Credential oauth2Credential,
      final GoogleOauth2AuthorizationRepository googleOauth2AuthorizationRepository) {
    this.oauth2Credential = oauth2Credential;
    this.googleOauth2AuthorizationRepository = googleOauth2AuthorizationRepository;
  }

  /**
   * Starts OAuth 2.0 authentication by retrieving the authorization URI.
   *
   * <p> This method initiates the OAuth 2.0 authentication flow by obtaining the authorization URI
   * using {@link #getAuthorizationUri()}. It then constructs and returns a StartOauth2AuthorizationResponse
   * containing the retrieved authorization URI.</p>
   *
   * <p> The authorization URI is crucial for redirecting users to Google's consent screen, where they
   * can grant permissions to the application.</p>
   *
   * @return A StartOauth2AuthorizationResponse containing the OAuth 2.0 authorization URI.
   */
  public StartOauth2AuthorizationResponse startOauth2Authentication() {
    final String authorizationUri = getAuthorizationUri();
    return StartOauth2AuthorizationResponse.builder()
            .authorizationUri(authorizationUri)
            .build();
  }

  /**
   * Constructs the OAuth 2.0 authorization URI for initiating the authorization flow.
   *
   * <p> This method retrieves the configured GoogleAuthorizationCodeFlow instance using {@link #getGoogleAuthorizationCodeFlow()}.
   * If available, it constructs a GoogleAuthorizationCodeRequestUrl to generate the authorization URI.</p>
   *
   * <p> The authorization URI is built with the specified redirect URI obtained from {@link #getRedirectUri()} to ensure
   * proper redirection after user consent.</p>
   *
   * <p> If the GoogleAuthorizationCodeFlow instance is not available or any required parameters are missing, this method
   * returns null.</p>
   *
   * @return The OAuth 2.0 authorization URI, or null if unable to construct the URI.
   */
  @MeasureExecutionTime
  public String getAuthorizationUri() {
    final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = getGoogleAuthorizationCodeFlow();
    if (nonNull(googleAuthorizationCodeFlow)) {
      final GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl = googleAuthorizationCodeFlow.newAuthorizationUrl();
      googleAuthorizationCodeRequestUrl.setRedirectUri(getRedirectUri());
      return googleAuthorizationCodeRequestUrl.build();
    }
    return null;
  }

  /**
   * Verifies an OAuth 2.0 authorization code, retrieves authorization details, and saves them in the database.
   *
   * <p> This method verifies the OAuth 2.0 authorization code by calling {@link #verifyAuthorizationCode(String)}
   * with the provided authorization code. It retrieves or creates a GoogleOauth2Authorization entity for the
   * specified FleenUser and updates its authorization details with the obtained CompletedOauth2AuthorizationResponse.</p>
   *
   * <p> If the authorization code verification is successful and a CompletedOauth2AuthorizationResponse is obtained,
   * it updates the GoogleOauth2Authorization entity with the access token, refresh token, access token expiration time,
   * token type, and scope. It then saves the entity using the GoogleOauth2AuthorizationRepository and returns
   * the CompletedOauth2AuthorizationResponse.</p>
   *
   * @param authorizationCode The OAuth 2.0 authorization code obtained from the authorization callback.
   * @param authenticatedUser The FleenUser for whom the OAuth 2.0 authorization is being verified and saved.
   * @return A CompletedOauth2AuthorizationResponse containing authorization details if successful,
   *         or null if the authorization code is invalid or expired.
   */
  @MeasureExecutionTime
  public CompletedOauth2AuthorizationResponse verifyAuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(final String authorizationCode, final FleenUser authenticatedUser) {
   final CompletedOauth2AuthorizationResponse oauth2AuthorizationResponse = verifyAuthorizationCode(authorizationCode);
    final GoogleOauth2Authorization googleOauth2Authorization = googleOauth2AuthorizationRepository
            .findByMember(authenticatedUser.toMember())
            .orElseGet(() -> GoogleOauth2Authorization.builder().member(authenticatedUser.toMember()).build());

    updateOauth2Authorization(googleOauth2Authorization, oauth2AuthorizationResponse);
    googleOauth2AuthorizationRepository.save(googleOauth2Authorization);
    return oauth2AuthorizationResponse;
  }

  /**
   * Refreshes a user's OAuth 2.0 access token and updates the authorization details in the database.
   *
   * <p> This method refreshes the user's OAuth 2.0 access token using the provided refresh token
   * by calling the {@link #refreshUserToken(String)} method. It retrieves or creates a GoogleOauth2Authorization
   * entity for the given FleenUser, updates its authorization details with the refreshed token response,
   * and saves the entity using the GoogleOauth2AuthorizationRepository.</p>
   *
   * <p> If the token refresh is successful and a RefreshOauth2TokenResponse is obtained, it updates
   * the GoogleOauth2Authorization entity with the refreshed access token, refresh token, access token
   * expiration time, token type, and scope. It then returns the RefreshOauth2TokenResponse.</p>
   *
   * @param refreshToken The OAuth 2.0 refresh token used to obtain a new access token.
   * @param fleenUser The FleenUser for whom the OAuth 2.0 authorization is being refreshed.
   * @return A RefreshOauth2TokenResponse containing the refreshed OAuth 2.0 access token and other details.
   */
  public RefreshOauth2TokenResponse refreshUserAccessToken(final String refreshToken, final FleenUser fleenUser) {
    final RefreshOauth2TokenResponse oauth2TokenResponse = refreshUserToken(refreshToken);
    final GoogleOauth2Authorization googleOauth2Authorization = googleOauth2AuthorizationRepository
            .findByMember(fleenUser.toMember())
            .orElseGet(() -> GoogleOauth2Authorization.builder().member(fleenUser.toMember()).build());

      updateOauth2Authorization(googleOauth2Authorization, oauth2TokenResponse);
      googleOauth2AuthorizationRepository.save(googleOauth2Authorization);

    return oauth2TokenResponse;
  }

  /**
   * Updates OAuth 2.0 authorization details in the GoogleOauth2Authorization entity.
   *
   * <p> This method updates the OAuth 2.0 authorization details in the provided GoogleOauth2Authorization
   * entity using the data from the given Oauth2AuthorizationResponse. If both parameters are non-null,
   * it sets the access token, refresh token, access token expiration time, authorization scope,
   * and token type in the GoogleOauth2Authorization entity.</p>
   *
   * <p> This method does not return any value; it directly modifies the state of the GoogleOauth2Authorization
   * entity passed as a parameter.</p>
   *
   * @param googleOauth2Authorization The GoogleOauth2Authorization entity to be updated.
   * @param oauth2AuthorizationResponse The OAuth 2.0 authorization response containing updated authorization details.
   */
  private void updateOauth2Authorization(final GoogleOauth2Authorization googleOauth2Authorization, final Oauth2AuthorizationResponse oauth2AuthorizationResponse) {
    if (nonNull(googleOauth2Authorization) && nonNull(oauth2AuthorizationResponse)) {
      googleOauth2Authorization.setAccessToken(oauth2AuthorizationResponse.getAccessToken());
      googleOauth2Authorization.setRefreshToken(oauth2AuthorizationResponse.getRefreshToken());
      googleOauth2Authorization.setTokenExpirationTimeInMilliseconds(TimeUnit.SECONDS.toMillis(oauth2AuthorizationResponse.getAccessTokenExpirationTimeInSeconds()));
      googleOauth2Authorization.setScope(oauth2AuthorizationResponse.getScope());
      googleOauth2Authorization.setTokenType(oauth2AuthorizationResponse.getTokenType());
    }
  }

  /**
   * Refreshes a user's OAuth 2.0 access token using the provided refresh token.
   *
   * <p> This method attempts to refresh the user's OAuth 2.0 access token by calling the
   * {@link #refreshAccessToken(String)} method with the provided refresh token.</p>
   *
   * <p> If the token refresh is successful and a GoogleTokenResponse is obtained, it constructs
   * and returns a RefreshOauth2TokenResponse containing the refreshed access token, refresh token,
   * access token expiration time in seconds, token type, and scope.</p>
   *
   * <p> If the token refresh fails or returns null, indicating an invalid or expired refresh token,
   * this method returns null.</p>
   *
   * @param refreshToken The OAuth 2.0 refresh token used to obtain a new access token.
   * @return A RefreshOauth2TokenResponse containing the refreshed OAuth 2.0 access token and other details,
   *         or null if the refresh token is invalid or expired.
   */
  @MeasureExecutionTime
  public RefreshOauth2TokenResponse refreshUserToken(final String refreshToken) {
    final GoogleTokenResponse googleTokenResponse = refreshAccessToken(refreshToken);
    if (nonNull(googleTokenResponse)) {
      return RefreshOauth2TokenResponse.builder()
          .accessToken(googleTokenResponse.getAccessToken())
          .refreshToken(googleTokenResponse.getRefreshToken())
          .accessTokenExpirationTimeInSeconds(googleTokenResponse.getExpiresInSeconds())
          .tokenType(googleTokenResponse.getTokenType())
          .scope(googleTokenResponse.getScope())
          .build();
    }
    return null;
  }

  /**
   * Verifies an OAuth 2.0 authorization code and retrieves authorization details.
   *
   * <p> This method exchanges the provided OAuth 2.0 authorization code for an access token and
   * other authorization details using the {@link #exchangeAuthorizationCode(String)} method.</p>
   *
   * <p> If the token exchange is successful and a TokenResponse is obtained, it constructs and
   * returns a CompletedOauth2AuthorizationResponse containing the access token, refresh token,
   * token type, access token expiration time in seconds, and scope.</p>
   *
   * <p> If the token exchange fails or returns null, indicating an invalid or expired authorization
   * code, this method returns null.</p>
   *
   * @param authorizationCode The OAuth 2.0 authorization code obtained from the authorization callback.
   * @return A CompletedOauth2AuthorizationResponse containing authorization details if successful,
   *         or null if the authorization code is invalid or expired.
   */
  public CompletedOauth2AuthorizationResponse verifyAuthorizationCode(final String authorizationCode) {
    final TokenResponse tokenResponse = exchangeAuthorizationCode(authorizationCode);
    if (nonNull(tokenResponse)) {
      return CompletedOauth2AuthorizationResponse.builder()
          .accessToken(tokenResponse.getAccessToken())
          .refreshToken(tokenResponse.getRefreshToken())
          .tokenType(tokenResponse.getTokenType())
          .accessTokenExpirationTimeInSeconds(tokenResponse.getExpiresInSeconds())
          .scope(tokenResponse.getScope())
          .build();
    }
    return null;
  }

  /**
   * Exchanges an OAuth 2.0 authorization code for an access token and possibly a refresh token.
   *
   * <p> This method uses the configured GoogleAuthorizationCodeFlow instance to exchange the
   * provided authorization code for an OAuth 2.0 access token. It sets the redirect URI as part
   * of the token request to ensure secure communication.</p>
   *
   * <p> If successful, it returns a TokenResponse containing the OAuth 2.0 access token, refresh token,
   * and other token details. If an error occurs during the token exchange process, specific exceptions
   * are caught and handled:</p>
   *
   * @param authorizationCode The OAuth 2.0 authorization code obtained from the authorization callback.
   * @return A TokenResponse containing the OAuth 2.0 access token and optionally a refresh token.
   * @throws Oauth2InvalidGrantOrTokenException If the token request fails due to "invalid_grant".
   * @throws Oauth2InvalidAuthorizationException If the token request fails due to other authorization errors.
   * @throws ExternalSystemException If an I/O error occurs during the token exchange process.
   */
  @MeasureExecutionTime
  public TokenResponse exchangeAuthorizationCode(final String authorizationCode) {
    final GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow = getGoogleAuthorizationCodeFlow();
    try {
      if (nonNull(googleAuthorizationCodeFlow)) {
        return googleAuthorizationCodeFlow.newTokenRequest(authorizationCode)
            .setRedirectUri(getRedirectUri())
            .execute();
      }
    } catch (final TokenResponseException ex) {
        log.error(ex.getMessage());
      if (ex.getMessage().contains("invalid_grant")) {
        throw new Oauth2InvalidGrantOrTokenException(authorizationCode);
      }
      throw new Oauth2InvalidAuthorizationException();
    } catch (final IOException ex) {
        log.error(ex.getMessage());
      throw new ExternalSystemException(ex.getMessage());
    }
    return null;
  }

  /**
   * Refreshes an OAuth 2.0 access token using the provided refresh token.
   *
   * <p> This method sends a request to Google's token endpoint to refresh the OAuth 2.0 access token
   * using the given refresh token. It constructs a GoogleRefreshTokenRequest with the necessary
   * parameters, including transport, JSON factory, client credentials, and the refresh token.</p>
   *
   * <p> If successful, it returns a GoogleTokenResponse containing the new access token and optionally
   * a new refresh token. If an error occurs during the token refresh process, an ExternalSystemException
   * is thrown, logged with details of the error for troubleshooting.</p>
   *
   * @param refreshToken The OAuth 2.0 refresh token used to obtain a new access token.
   * @return A GoogleTokenResponse containing the refreshed OAuth 2.0 access token.
   * @throws ExternalSystemException If an error occurs while refreshing the access token.
   */
  @MeasureExecutionTime
  public GoogleTokenResponse refreshAccessToken(final String refreshToken) {
    try {
      final GoogleRefreshTokenRequest refreshTokenRequest = new GoogleRefreshTokenRequest(
              GoogleOauth2Service.getTransport(), GoogleOauth2Service.getJsonFactory(), refreshToken,
              oauth2Credential.getClientId(), oauth2Credential.getClientSecret());
      return refreshTokenRequest.execute();
    } catch (final IOException ex) {
      final String errorMessage = String
              .format("An error occurred while refreshing token with %s in refreshAccessToken() of %s. Reason: %s",
                refreshToken,
                ex.getClass().getName(),
                ex.getMessage());
        log.error(errorMessage);
      throw new ExternalSystemException(errorMessage);
    }
  }

  /**
   * Constructs a GoogleAuthorizationCodeFlow.Builder for configuring OAuth 2.0 authorization flow settings.
   *
   * <p> This method creates a builder instance for setting up the OAuth 2.0 authorization flow,
   * including transport, JSON factory, client secrets, and scopes required for accessing Google APIs.</p>
   *
   * <p> The builder is configured with access type "offline", allowing the application to receive
   * refresh tokens for long-term access to user data. The approval prompt is set to "force",
   * ensuring that users are prompted to grant access every time.</p>
   *
   * @return A GoogleAuthorizationCodeFlow.Builder instance configured with OAuth 2.0 authorization settings.
   */
  public GoogleAuthorizationCodeFlow.Builder getAuthorizationCodeFlowBuilder() {
    return new GoogleAuthorizationCodeFlow.Builder(GoogleOauth2Service.getTransport(), GoogleOauth2Service.getJsonFactory(), getClientSecrets(), getScopes())
            .setAccessType("offline")
            .setApprovalPrompt("force");
  }

  /**
   * Retrieves an instance of GoogleAuthorizationCodeFlow configured with OAuth 2.0 settings.
   *
   * <p> This method builds and returns a GoogleAuthorizationCodeFlow instance configured using
   * the builder obtained from {@link #getAuthorizationCodeFlowBuilder()}.</p>
   *
   * <p> The returned GoogleAuthorizationCodeFlow encapsulates the OAuth 2.0 authorization flow
   * settings, including transport, JSON factory, client secrets, scopes, access type, and approval prompt.</p>
   *
   * @return A GoogleAuthorizationCodeFlow instance configured with OAuth 2.0 authorization settings.
   */
  public GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow() {
    return getAuthorizationCodeFlowBuilder().build();
  }

  /**
   * Retrieves the OAuth 2.0 client secrets associated with the current OAuth 2.0 credentials.
   *
   * <p> This method converts the OAuth 2.0 credentials into GoogleClientSecrets object, which contains
   * client identifier and client secret. These credentials are used to authenticate and authorize
   * access to Google APIs.</p>
   *
   * <p> It is crucial to keep the client secrets secure and never expose them in client-side code
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
   * <p> This method returns the redirect URI from the OAuth 2.0 credentials, which is used to
   * redirect users back to the application after authentication and authorization.</p>
   *
   * <p> The redirect URI must be registered with the OAuth 2.0 provider (e.g., Google) to ensure
   * secure communication and to prevent unauthorized redirection attempts.</p>
   *
   * @return The OAuth 2.0 redirect URI configured in the current OAuth 2.0 credentials.
   */
  private String getRedirectUri() {
    return oauth2Credential.getRedirectUriList().get(0);
  }

  /**
   * Retrieves the OAuth 2.0 scopes required for interacting with YouTube APIs.
   *
   * <p> This method returns a set of strings representing all available scopes for YouTube APIs.</p>
   *
   * <p> The scopes define the level of access and permissions granted to the application when
   * interacting with user data through YouTube services.</p>
   *
   * @return A {@code Set<String>} containing OAuth 2.0 scopes required for YouTube APIs.
   */
  private Set<String> getScopes() {
    return YouTubeScopes.all();
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
