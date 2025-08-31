package com.fleencorp.feen.mfa.constant;

import lombok.Getter;

@Getter
public enum MfaSetupStatus {

  COMPLETE("Complete"),
  IN_PROGRESS("In progress");

  private final String label;

  MfaSetupStatus(final String label) {
    this.label = label;
  }
}
