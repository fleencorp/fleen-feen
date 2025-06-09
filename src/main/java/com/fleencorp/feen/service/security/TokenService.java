package com.fleencorp.feen.service.security;

import com.fleencorp.feen.user.constant.auth.AuthenticationStatus;
import com.fleencorp.feen.user.security.RegisteredUser;

public interface TokenService {

  String createAccessToken(RegisteredUser user);

  String createAccessToken(RegisteredUser user, AuthenticationStatus authenticationStatus);

  String createRefreshToken(RegisteredUser user);

  String createResetPasswordToken(RegisteredUser user);

  void saveAccessToken(String subject, String token);

  void saveRefreshToken(String subject, String token);

  void saveResetPasswordToken(String subject, String token);

  void clearResetPasswordToken(String subject);

  boolean isResetPasswordTokenExist(String subject);
}
