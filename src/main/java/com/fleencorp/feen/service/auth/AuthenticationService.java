package com.fleencorp.feen.service.auth;

import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.user.UserNotFoundException;
import com.fleencorp.feen.exception.user.profile.BannedAccountException;
import com.fleencorp.feen.exception.user.profile.DisabledAccountException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.model.dto.auth.SignInDto;
import com.fleencorp.feen.model.dto.auth.SignUpDto;
import com.fleencorp.feen.model.response.auth.DataForSignUpResponse;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.security.SignOutResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface AuthenticationService {

  DataForSignUpResponse getDataForSignUp();

  SignUpResponse signUp(SignUpDto signUpDto) throws FailedOperationException;

  SignOutResponse signOut(FleenUser user);

  SignInResponse signIn(SignInDto signInDto)
    throws DisabledAccountException, BannedAccountException, InvalidAuthenticationException,
      FailedOperationException;

  FleenUser initializeAuthenticationAndContext(Member member) throws FailedOperationException;

  Member getMemberDetails(String emailAddressOrUsername) throws UserNotFoundException;

  void saveSignUpVerificationCodeTemporarily(String username, String verificationCode);

  void saveMfaVerificationCodeTemporarily(String username, String verificationCode);

  void sendSignUpVerificationMessage(String otpCode, VerificationType verificationType, FleenUser user);

  void saveAuthenticationTokensToRepositoryOrCache(String username, String accessToken, String refreshToken);

  void setUserTimezoneAfterAuthentication(FleenUser user);
}
