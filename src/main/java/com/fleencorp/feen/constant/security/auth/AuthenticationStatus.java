package com.fleencorp.feen.constant.security.auth;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enumeration representing the status of authentication.
 *
 * <p> This enum defines two statuses: {@code IN_PROGRESS} and {@code COMPLETED}.
 * Each status has an associated string value describing its state.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum AuthenticationStatus implements ApiParameter {

  IN_PROGRESS("In Progress"),
  COMPLETED("Completed");

  private final String value;

  AuthenticationStatus(String value) {
    this.value = value;
  }
}
