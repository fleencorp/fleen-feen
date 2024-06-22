package com.fleencorp.feen.util.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.fleencorp.feen.constant.security.role.RoleType.*;

/**
 * Utility class for managing Fleen authorities.
 */
public class UserAuthoritiesUtil {

  /**
   * Prefix for all roles.
   */
  public static final String ROLE_PREFIX = "ROLE_";

  /**
   * Retrieves authorities for pre-verified users.
   *
   * @return A list of granted authorities for pre-verified users.
   */
  public static List<GrantedAuthority> getUserPreVerifiedAuthorities() {
    return List.of(new SimpleGrantedAuthority(ROLE_PREFIX.concat(PRE_VERIFIED_USER.name())));
  }

  /**
   * Retrieves authorities for refresh token users.
   *
   * @return A list of granted authorities for refresh token users.
   */
  public static List<GrantedAuthority> getRefreshTokenAuthorities() {
    return List.of(new SimpleGrantedAuthority(ROLE_PREFIX.concat(REFRESH_TOKEN_USER.name())));
  }

  /**
   * Retrieves authorities for pre-authenticated users.
   *
   * @return A list of granted authorities for pre-authenticated users.
   */
  public static List<GrantedAuthority> getPreAuthenticatedAuthorities() {
    return List.of(new SimpleGrantedAuthority(ROLE_PREFIX.concat(PRE_AUTHENTICATED_USER.name())));
  }

  /**
   * Retrieves authorities for reset password users.
   *
   * @return A list of granted authorities for reset password users.
   */
  public static List<GrantedAuthority> getResetPasswordAuthorities() {
    return List.of(new SimpleGrantedAuthority(ROLE_PREFIX.concat(RESET_PASSWORD_USER.name())));
  }

  /**
   * Builds authorities from a list of role names.
   *
   * @param roles The list of role names.
   * @return A list of granted authorities built from the provided role names.
   */
  public static List<GrantedAuthority> buildAuthorities(List<String> roles) {
    return roles
      .stream()
      .filter(Objects::nonNull)
      .map(role -> new SimpleGrantedAuthority(role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX.concat(role)))
      .collect(Collectors.toList());
  }

  /**
   * Checks if any of the provided authorities are allow-listed.
   *
   * @param authorities The collection of authorities to check.
   * @return {@code true} if any of the authorities are allow-listed, otherwise {@code false}.
   */
  public static boolean isAuthorityWhitelisted(Collection<? extends GrantedAuthority> authorities) {
    List<String> whitelistedAuthorities = List.of(RESET_PASSWORD_USER.name(), REFRESH_TOKEN_USER.name());
    return authorities
      .stream()
      .filter(Objects::nonNull)
      .map(role -> role.getAuthority().replace(ROLE_PREFIX, ""))
      .anyMatch(whitelistedAuthorities::contains);
  }
}

