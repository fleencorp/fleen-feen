package com.fleencorp.feen.constant.security.auth;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum AuthenticationStage implements ApiParameter {

  NONE("None"),
  PRE_VERIFICATION("Pre Verification"),
  MFA_VERIFICATION("Multi Factor or Two FA or Pre Authentication");

  private final String value;

  AuthenticationStage(final String value) {
    this.value = value;
  }
}
