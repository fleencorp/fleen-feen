package com.fleencorp.feen.service.auth;

import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface PasswordService {

  default String createEncodedPassword(final String rawPassword) {
    return getPasswordEncoder().encode(rawPassword);
  }

  /**
   * Encodes or hashes the user's password and sets it on the member object.
   *
   * <p> This default method takes a {@link Member} object and a plain text password as input.
   * It creates an encoded or hashed version of the password using the {@link #createEncodedPassword(String)} method
   * and sets this encoded password on the member object.</p>
   *
   * @param member The {@link Member} object whose password is to be encoded or hashed.
   * @param password The plain text password to be encoded or hashed.
   */
  default void encodeOrHashUserPassword(final Member member, final String password) {
    final String encodedPassword = createEncodedPassword(password);
    member.setPassword(encodedPassword);
  }

  PasswordEncoder getPasswordEncoder();
}
