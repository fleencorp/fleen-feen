package com.fleencorp.feen.mfa.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing the status of MFA (Multi-Factor Authentication) setup.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum MfaSetupStatus implements ApiParameter {

  COMPLETE("Complete"),
  IN_PROGRESS("In progress");

  private final String value;

  MfaSetupStatus(final String value) {
    this.value = value;
  }
}
