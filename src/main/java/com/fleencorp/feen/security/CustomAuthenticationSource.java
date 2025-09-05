package com.fleencorp.feen.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * <p>The {@link CustomAuthenticationSource} class is a Spring Security {@link AuthenticationDetailsSource}
 * implementation that provides custom authentication details for web-based authentication.</p>
 *
 * <p>This class is used to build {@link WebAuthenticationDetails} based on the information available in
 * the {@link HttpServletRequest} object.</p>
 *
 * <p>This class is typically instantiated and managed by the Spring framework, which ensures
 * thread safety by handling the bean lifecycle.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 * @see AuthenticationDetailsSource
 * @see HttpServletRequest
 * @see WebAuthenticationDetails
 */
@NoArgsConstructor
@Component
public class CustomAuthenticationSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

  /**
   * <p>Builds and returns WebAuthenticationDetails based on the information available in
   * the provided {@link HttpServletRequest} object.</p>
   *
   * @param request The {@link HttpServletRequest} object containing the details of the current request.
   * @return The {@link WebAuthenticationDetails} containing custom authentication details.
   */
  @Override
  public WebAuthenticationDetails buildDetails(final HttpServletRequest request) {
    return null;
  }
}
