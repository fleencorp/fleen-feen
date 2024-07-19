package com.fleencorp.feen.service.impl.security;

import com.fleencorp.feen.configuration.security.properties.TokenProperties;
import com.fleencorp.feen.constant.security.auth.AuthenticationStatus;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.feen.service.security.TokenService;
import com.fleencorp.feen.util.security.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

import static com.fleencorp.base.util.datetime.DateTimeUtil.toHours;
import static com.fleencorp.feen.constant.security.token.TokenType.*;
import static com.fleencorp.feen.service.impl.common.CacheKeyService.*;
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
  private final TokenProperties tokenProperties;

  /**
   * Constructs a TokenServiceImpl with the necessary dependencies.
   *
   * @param cacheService    the service used for caching tokens
   * @param tokenUtil       utility for generating and managing tokens
   * @param tokenProperties properties related to token generation and validation
   */
  public TokenServiceImpl(
      CacheService cacheService,
      TokenUtil tokenUtil,
      TokenProperties tokenProperties) {
    this.cacheService = cacheService;
    this.tokenUtil = tokenUtil;
    this.tokenProperties = tokenProperties;
  }

  /**
   * Create a access token that can be used to perform actions on the application or through the API.
   *
   * @param user the authenticated user
   * @return the token to use to access the application or API features
   */
  @Override
  public String createAccessToken(FleenUser user) {
    return tokenUtil.generateAccessToken(user, ACCESS_TOKEN, null);
  }

  /**
   * Create a access token with an authentication status that can be used to perform actions on the application or through the API.
   *
   * @param user the authenticated user
   * @return the token to use to access the application or API features
   */
  @Override
  public String createAccessToken(FleenUser user, AuthenticationStatus authenticationStatus) {
    return tokenUtil.generateAccessToken(user, ACCESS_TOKEN, authenticationStatus);
  }

  /**
   * <p>Create a refresh token that can be used to obtain new access token to perform actions on the application or through the API.</p>
   * <br/>
   *
   * @param user the authenticated user
   * @return the token to use to obtain new access token.
   */
  @Override
  public String createRefreshToken(FleenUser user) {
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
  public String createResetPasswordToken(FleenUser user) {
    return tokenUtil.generateResetPasswordToken(user, RESET_PASSWORD_TOKEN, null);
  }

  /**
   * <p>Save the authentication access token.</p>
   * <br/>
   *
   * @param subject the user's identifier to associate with the access token
   * @param token the user's token to validate during the requests and process of using the application
   */
  @Override
  public void saveAccessToken(String subject, String token) {
    Duration duration = Duration.ofHours(tokenProperties.getAccessToken());
    cacheService.set(getAccessTokenCacheKey(subject), token, duration);
  }

  /**
   * Save the authentication refresh token that can only be used once to get a new access token.
   *
   * @param subject the user's identifier to associate with the refresh token
   * @param token the user's token to validate during request and use to get a new token
   */
  @Override
  public void saveRefreshToken(String subject, String token) {
    Duration duration = Duration.ofHours(tokenProperties.getRefreshToken());
    cacheService.set(getRefreshTokenCacheKey(subject), token, duration);
  }

  /**
   * Saves the reset password token in the cache for the specified subject.
   *
   * @param subject the subject (username or email) for which the token is saved
   * @param token   the reset password token to be saved
   */
  @Override
  public void saveResetPasswordToken(String subject, String token) {
    Duration duration = ofHours(toHours(new Date(), tokenUtil.getExpirationDateFromToken(token)));
    cacheService.set(getResetPasswordTokenCacheKey(subject), token, duration);
  }

}
