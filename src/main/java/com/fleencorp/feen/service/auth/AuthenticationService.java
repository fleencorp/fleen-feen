package com.fleencorp.feen.service.auth;

import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.auth.SignInDto;
import com.fleencorp.feen.model.dto.auth.SignUpDto;
import com.fleencorp.feen.model.response.auth.DataForSignUpResponse;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.security.SignOutResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface AuthenticationService {

  DataForSignUpResponse getDataForSignUp();

  SignUpResponse signUp(SignUpDto signUpDto);

  SignOutResponse signOut(FleenUser user);

  SignInResponse signIn(SignInDto signInDto);

  FleenUser initializeAuthenticationAndContext(Member member);

  Member getMemberDetails(String emailAddressOrUsername);

  void saveSignUpVerificationCodeTemporarily(String username, String verificationCode);

  void saveMfaVerificationCodeTemporarily(String username, String verificationCode);

  void sendSignUpVerificationMessage(String otpCode, VerificationType verificationType, FleenUser user);

  void saveAuthenticationTokensToRepositoryOrCache(String username, String accessToken, String refreshToken);

  void setUserTimezoneAfterAuthentication(FleenUser user);
}
