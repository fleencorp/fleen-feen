package com.fleencorp.feen.security.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * <p>The CustomAuthenticationProvider class is a Spring Security AuthenticationProvider
 * implementation that performs custom authentication logic.</p>
 *
 * <p>This class is responsible for authenticating users based on their email address and password.
 * It uses a provided UserDetailsService to load user details and a PasswordEncoder to verify the password.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 * @see AuthenticationProvider
 * @see UserDetailsService
 * @see PasswordEncoder
 */
@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;

  /**
   * <p>Constructs a CustomAuthenticationProvider with the specified UserDetailsService
   * and PasswordEncoder.</p>
   *
   * @param userDetailsService The service used to load user details.
   * @param passwordEncoder    The encoder used to verify passwords.
   */
  public CustomAuthenticationProvider(@Lazy final UserDetailsService userDetailsService,
                                      @Lazy final PasswordEncoder passwordEncoder) {
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * <p>Attempts to authenticate the user based on the provided Authentication object.</p>
   *
   * @param authentication The authentication request object.
   * @return A fully authenticated object including credentials.
   * @throws AuthenticationException If authentication fails.
   */
  @Override
  public Authentication authenticate(final Authentication authentication) {
    final String emailAddress = authentication.getName();
    final String password = authentication.getCredentials().toString();

    final UserDetails user = userDetailsService.loadUserByUsername(emailAddress);
    if (passwordEncoder.matches(password, user.getPassword())) {
      return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
    return null;
  }

  /**
   * <p>Indicates whether this AuthenticationProvider implementation supports the
   * specified authentication token type.</p>
   *
   * @param authentication The class of the Authentication object to check.
   * @return true if the implementation can process the indicated token type.
   */
  @Override
  public boolean supports(final Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}
