package com.fleencorp.feen.service.security;

import com.fleencorp.feen.constant.security.auth.AuthenticationStatus;
import com.fleencorp.feen.model.security.FleenUser;

public interface TokenService {

  String createAccessToken(FleenUser user);

  String createAccessToken(FleenUser user, AuthenticationStatus authenticationStatus);

  String createRefreshToken(FleenUser user);

  String createResetPasswordToken(FleenUser user);

  void saveAccessToken(String subject, String token);

  void saveRefreshToken(String subject, String token);

  void saveResetPasswordToken(String subject, String token);

  void clearResetPasswordToken(String subject);

  boolean isResetPasswordTokenExist(String subject);
}
