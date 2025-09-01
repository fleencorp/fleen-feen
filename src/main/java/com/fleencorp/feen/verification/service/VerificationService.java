package com.fleencorp.feen.verification.service;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.common.service.impl.cache.CacheService;
import com.fleencorp.feen.mfa.model.dto.ConfirmMfaVerificationCodeDto;
import com.fleencorp.feen.mfa.model.dto.ResendMfaVerificationCodeDto;
import com.fleencorp.feen.mfa.model.response.ResendMfaVerificationCodeResponse;
import com.fleencorp.feen.model.response.authentication.ResendSignUpVerificationCodeResponse;
import com.fleencorp.feen.model.response.authentication.SignInResponse;
import com.fleencorp.feen.model.response.authentication.SignUpResponse;
import com.fleencorp.feen.model.response.security.ChangePasswordResponse;
import com.fleencorp.feen.model.response.security.ForgotPasswordResponse;
import com.fleencorp.feen.model.response.security.InitiatePasswordChangeResponse;
import com.fleencorp.feen.user.exception.authentication.InvalidAuthenticationException;
import com.fleencorp.feen.user.exception.user.UserNotFoundException;
import com.fleencorp.feen.user.model.dto.authentication.*;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.verification.exception.core.*;

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

  SignUpResponse completeSignUp(CompleteSignUpDto completeSignUpDto, RegisteredUser user) throws AlreadySignedUpException, VerificationFailedException, FailedOperationException;

  ResendSignUpVerificationCodeResponse resendSignUpVerificationCode(ResendSignUpVerificationCodeDto resendSignUpVerificationCodeDto, RegisteredUser user) throws AlreadySignedUpException, FailedOperationException;

  ResendMfaVerificationCodeResponse resendMfaVerificationCode(ResendMfaVerificationCodeDto resendMfaVerificationCodeDto, RegisteredUser user);

  SignInResponse verifyMfaVerificationCodeAndAuthenticateUser(ConfirmMfaVerificationCodeDto confirmMfaCodeDto, RegisteredUser user);

  InitiatePasswordChangeResponse verifyResetPasswordCode(ResetPasswordDto resetPasswordDto) throws UserNotFoundException, ResetPasswordCodeInvalidException;

  ForgotPasswordResponse forgotPassword(ForgotPasswordDto forgotPasswordDto) throws UserNotFoundException;

  ChangePasswordResponse changePassword(ChangePasswordDto changePasswordDto, RegisteredUser user) throws UserNotFoundException, InvalidAuthenticationException;
}
