package com.fleencorp.feen.verification.service.impl;

import com.fleencorp.feen.common.service.impl.cache.CacheService;
import com.fleencorp.feen.configuration.security.properties.TokenDurationProperties;
import com.fleencorp.feen.service.security.TokenService;
import com.fleencorp.feen.user.constant.authentication.AuthenticationStatus;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

import static com.fleencorp.base.util.datetime.DateTimeUtil.toHours;
import static com.fleencorp.feen.common.service.impl.cache.CacheKeyService.*;
import static com.fleencorp.feen.user.constant.token.TokenType.*;
import static java.time.Duration.ofHours;

/**
 * Implementation of {@link TokenService} that handles token generation and storage.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {

  private final CacheService cacheService;
  private final TokenUtil tokenUtil;
  private final TokenDurationProperties tokenDurationProperties;

  /**
   * Constructs a TokenServiceImpl with the necessary dependencies.
   *
   * @param cacheService    the service used for caching tokens
   * @param tokenUtil       utility for generating and managing tokens
   * @param tokenDurationProperties properties related to token generation and validation
   */
  public TokenServiceImpl(
      final CacheService cacheService,
      final TokenUtil tokenUtil,
      final TokenDurationProperties tokenDurationProperties) {
    this.cacheService = cacheService;
    this.tokenUtil = tokenUtil;
    this.tokenDurationProperties = tokenDurationProperties;
  }

  /**
   * Create a access token that can be used to perform actions on the application or through the API.
   *
   * @param user the authenticated user
   * @return the token to use to access the application or API features
   */
  @Override
  public String createAccessToken(final RegisteredUser user) {
    return tokenUtil.generateAccessToken(user, ACCESS_TOKEN, null);
  }

  /**
   * Create a access token with an authentication status that can be used to perform actions on the application or through the API.
   *
   * @param user the authenticated user
   * @return the token to use to access the application or API features
   */
  @Override
  public String createAccessToken(final RegisteredUser user, final AuthenticationStatus authenticationStatus) {
    return tokenUtil.generateAccessToken(user, ACCESS_TOKEN, authenticationStatus);
  }

  /**
   * <p>Create a refresh token that can be used to obtain new access token to perform actions on the application or through the API.</p>
   *
   * @param user the authenticated user
   * @return the token to use to obtain new access token.
   */
  @Override
  public String createRefreshToken(final RegisteredUser user) {
    return tokenUtil.generateRefreshToken(user, REFRESH_TOKEN, null);
  }

  /**
   * Creates a reset password token for the given user.
   *
   * <p>Uses the tokenUtil to generate a reset password token specific to the user.</p>
   *
   * @param user the user for whom the reset password token is created
   * @return String the generated reset password token
   */
  @Override
  public String createResetPasswordToken(final RegisteredUser user) {
    return tokenUtil.generateResetPasswordToken(user, RESET_PASSWORD_TOKEN, null);
  }

  /**
   * Save the authentication access token.
   *
   * @param subject the user's identifier to associate with the access token
   * @param token the user's token to validate during the requests and process of using the application
   */
  @Override
  public void saveAccessToken(final String subject, final String token) {
    final Duration duration = Duration.ofHours(tokenDurationProperties.getAccessToken());
    cacheService.set(getAccessTokenCacheKey(subject), token, duration);
  }

  /**
   * Save the authentication refresh token that can only be used once to get a new access token.
   *
   * @param subject the user's identifier to associate with the refresh token
   * @param token the user's token to validate during request and use to get a new token
   */
  @Override
  public void saveRefreshToken(final String subject, final String token) {
    final Duration duration = Duration.ofHours(tokenDurationProperties.getRefreshToken());
    cacheService.set(getRefreshTokenCacheKey(subject), token, duration);
  }

  /**
   * Saves the reset password token in the cache for the specified subject.
   *
   * @param subject the subject (username or email) for which the token is saved
   * @param token   the reset password token to be saved
   */
  @Override
  public void saveResetPasswordToken(final String subject, final String token) {
    final Duration duration = ofHours(toHours(new Date(), tokenUtil.getExpirationDateFromToken(token)));
    cacheService.set(getResetPasswordTokenCacheKey(subject), token, duration);
  }

  /**
   * Clears the reset password token for the specified subject.
   *
   * <p>This method removes the reset password token associated with the given subject
   * from the cache by deleting the corresponding cache entry.</p>
   *
   * @param subject the identifier for which the reset password token is to be cleared.
   */
  @Override
  public void clearResetPasswordToken(final String subject) {
    cacheService.delete(getResetPasswordTokenCacheKey(subject));
  }

  /**
   * Checks if a reset password token exists for the specified subject.
   *
   * <p>This method verifies whether a reset password token associated with the given subject
   * is present in the cache by checking the existence of the corresponding cache entry.</p>
   *
   * @param subject the identifier for which to check the existence of a reset password token.
   */
  @Override
  public boolean isResetPasswordTokenExist(final String subject) {
    return cacheService.exists(getResetPasswordTokenCacheKey(subject));
  }

}
