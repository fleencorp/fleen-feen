package com.fleencorp.feen.service.auth;

import com.fleencorp.feen.model.dto.auth.*;
import com.fleencorp.feen.model.response.auth.DataForSignUpResponse;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.security.ChangePasswordResponse;
import com.fleencorp.feen.model.response.security.ForgotPasswordResponse;
import com.fleencorp.feen.model.response.security.SignOutResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface AuthenticationService {

  DataForSignUpResponse getDataForSignUp();

  SignUpResponse signUp(SignUpDto signUpDto);

  SignUpResponse completeSignUp(CompleteSignUpDto completeSignUpDto, FleenUser user);

  SignOutResponse signOut(FleenUser user);

  SignInResponse signIn(SignInDto signInDto);

  ForgotPasswordResponse forgotPassword(ForgotPasswordDto forgotPasswordDto);

  ChangePasswordResponse changePassword(ChangePasswordDto changePasswordDto, FleenUser user);
}
