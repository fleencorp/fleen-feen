package com.fleencorp.feen.constant.security.token;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enumeration representing types of tokens used in the application.
 *
 * <p> This enum defines different types of tokens:
 * {@code ACCESS_TOKEN}, {@code REFRESH_TOKEN}, and {@code RESET_PASSWORD_TOKEN}.
 * Each token type has an associated string value indicating its purpose.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum TokenType implements ApiParameter {

  ACCESS_TOKEN("ACCESS_TOKEN"),
  REFRESH_TOKEN("REFRESH_TOKEN"),
  RESET_PASSWORD_TOKEN("RESET_PASSWORD_TOKEN");

  private final String value;

  TokenType(final String value) {
    this.value = value;
  }
}
