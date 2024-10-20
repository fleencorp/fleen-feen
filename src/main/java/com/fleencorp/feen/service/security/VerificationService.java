package com.fleencorp.feen.service.security;

import com.fleencorp.feen.exception.verification.ExpiredVerificationCodeException;
import com.fleencorp.feen.exception.verification.InvalidVerificationCodeException;
import com.fleencorp.feen.exception.verification.VerificationFailedException;
import com.fleencorp.feen.service.impl.cache.CacheService;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static java.util.Objects.nonNull;

public interface VerificationService {

  /**
   * Validates the provided verification code against the stored code.
   *
   * <p>This method checks if the verification key is null, if the key exists in the cache,
   * and if the stored code matches the provided code. It throws appropriate exceptions if
   * validation fails.</p>
   *
   * @param verificationKey the key used to retrieve the verification code from the cache
   * @param code            the verification code to validate
   * @throws VerificationFailedException    if the verification key is null
   * @throws ExpiredVerificationCodeException if the verification key does not exist in the cache
   * @throws InvalidVerificationCodeException if the stored code does not match the provided code
   */
  default void validateVerificationCode(final String verificationKey, final String code) {
    // Check if the verification key is null
    checkIsNull(verificationKey, VerificationFailedException::new);

    // Check if the verification key exists in the cache
    if (!(getCacheService().exists(verificationKey))) {
      throw ExpiredVerificationCodeException.of(code);
    }

    // Retrieve the existing code from the cache
    final String existingCode = (String) getCacheService().get(verificationKey);
    // Check if the verification key is null
    checkIsNull(existingCode, InvalidVerificationCodeException.of(code));

    // Check if the existing code is null or does not match the provided code
    if (!(nonNull(existingCode) && existingCode.equals(code))) {
      throw new InvalidVerificationCodeException(code);
    }
  }

  CacheService getCacheService();
}
