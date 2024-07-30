package com.fleencorp.feen.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.fleencorp.feen.filter.SimpleCorsFilter.setHeaders;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpMethod.OPTIONS;

/**
 * <p>The OptionsFilter class is a Spring component that extends OncePerRequestFilter.
 * It is designed to handle HTTP OPTIONS requests, providing a mechanism to handle
 * pre-flight requests, typically associated with Cross-Origin Resource Sharing (CORS) policy.
 * The filter intercepts OPTIONS requests for a specific URL pattern ("/api/**" by default)
 * and sets the required headers to allow cross-origin requests from a web client.</p>
 * <br/>
 *
 * <p>This filter is annotated with @Component and @Order(Ordered.HIGHEST_PRECEDENCE),
 * indicating that it is a Spring component and should be executed first in the filter
 * chain to handle OPTIONS requests before other filters. This is particularly useful
 * for CORS support.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
@Order(HIGHEST_PRECEDENCE)
public class OptionsFilter extends OncePerRequestFilter {

  private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

  // The URL pattern for which OPTIONS requests are intercepted.
  private static final String URL_PATTERN = "/api/**";


  /**
   * <p>Handles HTTP OPTIONS requests for a specific URL pattern ("/api/**" by default).
   * If the request method is OPTIONS and the request URI matches the configured URL pattern,
   * the filter sets the required headers for CORS support and responds with a status of
   * HttpServletResponse.SC_OK. Otherwise, the filter continues the filter chain.</p>
   *
   * @param request The HTTP servlet request.
   * @param response The HTTP servlet response.
   * @param filterChain The filter chain for processing the request.
   * @throws ServletException If a servlet-specific error occurs.
   * @throws IOException If an I/O error occurs.
   */
  @Override
  protected void doFilterInternal(
      final HttpServletRequest request,
      @NonNull final HttpServletResponse response,
      @NonNull final FilterChain filterChain)  throws ServletException, IOException {

    if (OPTIONS.name().equals(request.getMethod())
      && PATH_MATCHER.match(URL_PATTERN, request.getRequestURI())) {
      setHeaders(response);
      response.setStatus(SC_OK);
      return;
    }

    // Continue with the filter chain for non-OPTIONS requests
    filterChain.doFilter(request, response);
  }
}
