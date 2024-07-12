package com.fleencorp.feen.config.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>JwtAuthenticationEntryPoint is a class that implements the
 * AuthenticationEntryPoint interface for handling authentication
 * entry point for JWT (JSON Web Token) based authentication. It is used to
 * commence the authentication process when an unauthenticated user attempts
 * to access a secured resource.It is responsible for delegating the handling of
 *  * authentication exceptions to a configured HandlerExceptionResolver.</p>
 *  <br/>
 *
 * <p>This class is annotated with @Component, indicating that it is a
 * Spring component and should be automatically detected and registered during
 * the component scanning process.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private final HandlerExceptionResolver resolver;

  /**
   * <p>Constructs a new JwtAuthenticationEntryPoint instance with the
   * specified HandlerExceptionResolver.</p>
   *
   * @param resolver The handler for resolving authentication exceptions.
   */
  public JwtAuthenticationEntryPoint(
      @Lazy @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
    this.resolver = resolver;
  }

  /**
   * <p>Commences the authentication process when an unauthenticated user attempts
   * to access a secured resource. It delegates the handling of the authentication
   * exception to the configured HandlerExceptionResolver.</p>
   *
   * @param request The HTTP servlet request.
   * @param response The HTTP servlet response.
   * @param authException The authentication exception that occurred.
   */
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
    resolver.resolveException(request, response, null, authException);
  }
}
