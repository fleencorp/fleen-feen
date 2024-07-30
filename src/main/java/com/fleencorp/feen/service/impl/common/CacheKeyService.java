package com.fleencorp.feen.service.impl.common;

import com.fleencorp.base.constant.base.CacheKeyConstant;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import lombok.extern.slf4j.Slf4j;

import static com.fleencorp.feen.constant.security.mfa.MfaType.EMAIL;

@Slf4j
public class CacheKeyService {

  public static String getMfaSetupCacheKey(final String username, final MfaType mfaType) {
    return mfaType == EMAIL ? getEmailMfaSetupCacheKey(username) : getPhoneMfaSetupCacheKey(username);
  }

  /**
   * <p>Prefix a user's identifier with a predefined key used to save an authentication token like JWT.</p>
   * <br/>
   *
   * @param username a user identifier found on the system or is to be registered on the system
   * @return a string concatenation of a predefined prefix and the user's identifier
   */
  public static String getAccessTokenCacheKey(final String username) {
    return CacheKeyConstant.AUTH_ACCESS_TOKEN_CACHE_PREFIX.concat(username);
  }

  /**
   * <p>Prefix a user's identifier with a predefined key used to save an authentication refresh token like JWT.</p>
   * <br/>
   *
   * @param username a user identifier found on the system or is to be registered on the system
   * @return a string concatenation of a predefined prefix and the user's identifier
   */
  public static String getRefreshTokenCacheKey(final String username) {
    return CacheKeyConstant.AUTH_REFRESH_TOKEN_CACHE_PREFIX.concat(username);
  }

  /**
   * Generates a cache key for storing reset password tokens based on the username.
   *
   * @param username the username for which the cache key is generated
   * @return String the generated cache key for reset password tokens
   */
  public static String getResetPasswordTokenCacheKey(final String username) {
    return CacheKeyConstant.RESET_PASSWORD_TOKEN_CACHE_PREFIX.concat(username);
  }

  /**
   * <p>Prefix a user's identifier with a predefined key used to save a pre-verification token or OTP or code.</p>
   * <br/>
   *
   * @param username a user identifier found on the system or is to be registered on the system
   * @return a string concatenation of a predefined prefix and the user's identifier
   */
  public static String getSignUpVerificationCacheKey(final String username) {
    return CacheKeyConstant.SIGN_UP_VERIFICATION_PREFIX.concat(username);
  }

  /**
   * <p>Prefix a user's identifier with a predefined key used to save a pre-authentication token or OTP or code.</p>
   * <br/>
   *
   * @param username a user identifier found on the system or is to be registered on the system
   * @return a string concatenation of a predefined prefix and the user's identifier
   */
  public static String getMfaAuthenticationCacheKey(final String username) {
    return CacheKeyConstant.MFA_AUTHENTICATION_PREFIX.concat(username);
  }

  /**
   * <p>Prefix a user's identifier with a predefined key used to save a reset password token or OTP or code.</p>
   * <br/>
   *
   * @param username a user identifier found on the system or is to be registered on the system
   * @return a string concatenation of a predefined prefix and the user's identifier
   */
  public static String getResetPasswordCacheKey(final String username) {
    return CacheKeyConstant.RESET_PASSWORD_CACHE_PREFIX.concat(username);
  }

  /**
   * <p>Prefix a user's identifier with a predefined key used to save a MFA setup token or OTP or code.</p>
   * <br/>
   *
   * @param username a user identifier found on the system or is to be registered on the system
   * @return a string concatenation of a predefined prefix and the user's identifier
   */
  public static String getEmailMfaSetupCacheKey(final String username) {
    return CacheKeyConstant.MFA_SETUP_EMAIL_CACHE_PREFIX.concat(username);
  }

  /**
   * <p>Prefix a user's identifier with a predefined key used to save a MFA setup token or OTP or code.</p>
   * <br/>
   *
   * @param username a user identifier found on the system or is to be registered on the system
   * @return a string concatenation of a predefined prefix and the user's identifier
   */
  public static String getPhoneMfaSetupCacheKey(final String username) {
    return CacheKeyConstant.MFA_SETUP_PHONE_CACHE_PREFIX.concat(username);
  }
}
