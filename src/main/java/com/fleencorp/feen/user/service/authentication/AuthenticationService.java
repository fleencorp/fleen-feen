package com.fleencorp.feen.user.service.authentication;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.model.response.authentication.DataForSignUpResponse;
import com.fleencorp.feen.model.response.authentication.SignInResponse;
import com.fleencorp.feen.model.response.authentication.SignUpResponse;
import com.fleencorp.feen.model.response.security.SignOutResponse;
import com.fleencorp.feen.user.exception.authentication.InvalidAuthenticationException;
import com.fleencorp.feen.user.exception.user.BannedAccountException;
import com.fleencorp.feen.user.exception.user.DisabledAccountException;
import com.fleencorp.feen.user.exception.user.UserNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.dto.authentication.SignInDto;
import com.fleencorp.feen.user.model.dto.authentication.SignUpDto;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.verification.constant.VerificationType;

public interface AuthenticationService {

  DataForSignUpResponse getDataForSignUp();

  SignUpResponse signUp(SignUpDto signUpDto) throws FailedOperationException;

  SignOutResponse signOut(RegisteredUser user);

  SignInResponse signIn(SignInDto signInDto)
    throws DisabledAccountException, BannedAccountException, InvalidAuthenticationException,
      FailedOperationException;

  RegisteredUser initializeAuthenticationAndContext(Member member) throws FailedOperationException;

  Member getMemberDetails(String emailAddressOrUsername) throws UserNotFoundException;

  void saveSignUpVerificationCodeTemporarily(String username, String verificationCode);

  void saveMfaVerificationCodeTemporarily(String username, String verificationCode);

  void sendSignUpVerificationMessage(String otpCode, VerificationType verificationType, RegisteredUser user);

  void saveAuthenticationTokensToRepositoryOrCache(String username, String accessToken, String refreshToken);

  void setUserTimezoneAfterAuthentication(RegisteredUser user);
}
