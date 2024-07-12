package com.fleencorp.feen.service.auth;

import com.fleencorp.feen.model.dto.auth.*;
import com.fleencorp.feen.model.dto.mfa.ConfirmMfaVerificationCodeDto;
import com.fleencorp.feen.model.dto.mfa.ResendMfaVerificationCodeDto;
import com.fleencorp.feen.model.response.auth.ResendSignUpVerificationCodeResponse;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.mfa.ResendMfaVerificationCodeResponse;
import com.fleencorp.feen.model.response.security.ChangePasswordResponse;
import com.fleencorp.feen.model.response.security.ForgotPasswordResponse;
import com.fleencorp.feen.model.response.security.InitiatePasswordChangeResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface AuthenticationService {

  SignUpResponse signUp(SignUpDto signUpDto);

  SignUpResponse completeSignUp(CompleteSignUpDto completeSignUpDto, FleenUser user);

  ResendSignUpVerificationCodeResponse resendSignUpVerificationCode(ResendSignUpVerificationCodeDto resendSignUpVerificationCodeDto, FleenUser user);

  ResendMfaVerificationCodeResponse resendMfaVerificationCode(ResendMfaVerificationCodeDto resendMfaVerificationCodeDto, FleenUser user);

  void signOut(FleenUser user);

  SignInResponse verifyMfaVerificationCodeAndAuthenticateUser(ConfirmMfaVerificationCodeDto confirmMfaVerificationCodeDto, FleenUser user);

  SignInResponse signIn(SignInDto signInDto);

  ForgotPasswordResponse forgotPassword(ForgotPasswordDto forgotPasswordDto);

  InitiatePasswordChangeResponse validateResetPasswordCode(ResetPasswordDto resetPasswordDto, FleenUser user);

  ChangePasswordResponse changePassword(ChangePasswordDto changePasswordDto, FleenUser user);
}
