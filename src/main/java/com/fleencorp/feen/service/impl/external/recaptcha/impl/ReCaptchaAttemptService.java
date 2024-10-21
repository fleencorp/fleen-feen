package com.fleencorp.feen.service.impl.external.recaptcha.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * ReCaptchaAttemptService is used to limit the number of attempts a user can against some application APIs
 * for security purpose and to prevent DDOS attacks
 *
 * @author Yusuf Alamu Musa
 */
@Component
public class ReCaptchaAttemptService {
  private static final int MAX_ATTEMPT = 3;
  private final LoadingCache<String, Integer> attemptsCache;

  /**
   * When the user performs authentication like login.html; in order to protect the application from attacks like DDOS or even bots,
   * a record is saved that contains uniquely identifiable information of the client like an IP address, and It's checked to make
   * sure that the user is not allowed to make unauthorized calls until after a period of time.
   */
  public ReCaptchaAttemptService() {
    attemptsCache = CacheBuilder.newBuilder()
      .expireAfterWrite(5, TimeUnit.MINUTES).build(new CacheLoader<>() {
        @NotNull
        @Override
        public Integer load(@NotNull final String key) {
          return 0;
        }
      });
  }

  /**
   * Record a successful attempt of the user or client to perform reCaptcha verification and
   * clear or reset the value of any previous reCaptcha attempt to the default value.
   * @param key the cache key that uniquely identifies a user or client
   */
  public void reCaptchaSucceeded(final String key) {
    attemptsCache.invalidate(key);
  }

  /**
   * Record a failed attempt of the user or client to perform reCaptcha verification
   * @param key the cache key that uniquely identifies a user or client
   */
  public void reCaptchaFailed(final String key) {
    int attempts = attemptsCache.getUnchecked(key);
    attempts++;
    attemptsCache.put(key, attempts);
  }

  /**
   * This checks to see if the user has reached exceeded the number of available reCaptcha attempts it can make
   * @param key the cache key that uniquely identifies a user or client
   * @return true or false if the attempts has or has not been exceeded
   */
  public boolean isBlocked(final String key) {
    return attemptsCache.getUnchecked(key) >= MAX_ATTEMPT;
  }
}
