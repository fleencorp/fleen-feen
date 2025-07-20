package com.fleencorp.feen.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * <p>The SimpleCorsFilter class is a Spring component that implements
 * the Filter interface. It is designed to handle Cross-Origin Resource
 * Sharing (CORS) by setting the necessary HTTP headers in the response.
 * The filter allows cross-origin requests from any origin and allows
 * all methods and headers.</p>
 *
 * <p>This filter is annotated with @Slf4j, which is a lombok annotation
 * for generating a logger field. It is also annotated with @Component and
 * Order(Ordered.HIGHEST_PRECEDENCE) annotation, indicating that it is a Spring
 * component and should be executed first in the filter chain to handle
 * CORS before other filters. This is particularly useful for CORS support.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Component
@Order(HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

  /**
   * <p>Handles the filtering of the request and response. It casts the
   * ServletResponse to HttpServletResponse and then sets the CORS-related
   * headers using the setHeaders method. The filter chain is then continued.</p>
   *
   * @param req The servlet request.
   * @param res The servlet response.
   * @param chain The filter chain for processing the request.
   * @throws IOException If an I/O error occurs.
   * @throws ServletException If a servlet-specific error occurs.
   */
  @Override
  public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {

    final HttpServletResponse response = (HttpServletResponse) res;
    setHeaders(response);
    chain.doFilter(req, response);
  }

  /**
   * <p>Sets the necessary CORS-related headers in the provided
   * HttpServletResponse. The headers include allowing any origin,
   * disallowing credentials, allowing all methods, and allowing all headers.</p>
   *
   * @param response The HTTP servlet response.
   */
  public static void setHeaders(final HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Credentials", "false");
    response.setHeader("Access-Control-Allow-Methods", "*");
    response.setHeader("Access-Control-Allow-Headers", "*");
  }

}