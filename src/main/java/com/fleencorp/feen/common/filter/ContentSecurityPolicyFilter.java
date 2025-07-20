package com.fleencorp.feen.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * <p>ContentSecurityPolicyFilter is a Spring component that extends
 * OncePerRequestFilter to apply Content Security Policy (CSP) headers to
 * HTTP responses. CSP headers help protect web applications from various types
 * of attacks, such as Cross-Site Scripting (XSS) and data injection attacks,
 * by controlling the sources from which certain types of content can be loaded.</p>
 *
 * <p>The CSP directives are externalized using Spring's @Value annotation,
 * allowing for easy configuration and customization without modifying the Java code.
 * The values are loaded from the properties file, such as csp.properties,
 * and injected into the corresponding fields.</p>
 *
 * <p>The class includes fields for various CSP directives, such as default-src,
 * img-src, font-src, and others. The directives are concatenated
 * to form a complete CSP policy string, which is then set as the value of the
 * "Content-Security-Policy" header in the HTTP response.</p>
 *
 * <p>The class also sets other security-related headers, such as "X-Frame-Options"
 * to deny framing, "Strict-Transport-Security" to enforce HTTPS, and
 * "X-Content-Type-Options" to prevent MIME sniffing.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
@PropertySources(value = {
  @PropertySource(value = "classpath:application.properties"),
  @PropertySource(value = "classpath:properties/csp.properties")
})
public class ContentSecurityPolicyFilter extends OncePerRequestFilter {

  /** The value for the 'default-src' CSP directive. */
  @Value("${content.security.policy.default-src}")
  private String defaultSrc;

  /** The value for the 'img-src' CSP directive. */
  @Value("${content.security.policy.img-src}")
  private String imgSrc;

  /** The value for the 'font-src' CSP directive. */
  @Value("${content.security.policy.font-src}")
  private String fontSrc;

  /** The value for the 'object-src' CSP directive. */
  @Value("${content.security.policy.object-src}")
  private String objectSrc;

  /** The value for the 'media-src' CSP directive. */
  @Value("${content.security.policy.media-src}")
  private String mediaSrc;

  /** The value for the 'child-src' CSP directive. */
  @Value("${content.security.policy.child-src}")
  private String childSrc;

  /** The value for the 'script-src' CSP directive. */
  @Value("${content.security.policy.script-src}")
  private String scriptSrc;

  /** The value for the 'style-src' CSP directive. */
  @Value("${content.security.policy.style-src}")
  private String styleSrc;

  /** The value for the 'script-src-elem' CSP directive. */
  @Value("${content.security.policy.script-src-elem}")
  private String scriptSrcElem;

  /**
   * <p>Applies Content Security Policy (CSP) headers to the HTTP response. The
   * CSP directives are configured using externalized properties and injected
   * into the corresponding fields of this class. The complete CSP policy string
   * is then set as the value of the "Content-Security-Policy" header in the
   * HTTP response. Additionally, other security-related headers are set,
   * including "X-Frame-Options," "Strict-Transport-Security," and
   * "X-Content-Type-Options."</p>
   *
   * @param request The HTTP servlet request.
   * @param response The HTTP servlet response.
   * @param filterChain The filter chain for processing the request.
   * @throws ServletException If a servlet-specific error occurs.
   * @throws IOException If an I/O error occurs.
   */
  @Override
  protected void doFilterInternal(
      @NonNull final HttpServletRequest request, final HttpServletResponse response,
      final FilterChain filterChain) throws ServletException, IOException {

    final String csPolicy = "default-src " + defaultSrc + ";" +
      "img-src " + imgSrc + ";" +
      "font-src " + fontSrc + ";" +
      "object-src " + objectSrc + ";" +
      "media-src " + mediaSrc + ";" +
      "child-src " + childSrc + ";" +
      "script-src " + scriptSrc + ";" +
      "style-src " + styleSrc + ";" +
      "script-src-elem " + scriptSrcElem;

    response.setHeader("X-Frame-Options", "deny");
    response.setHeader("Strict-Transport-Security", "max-age=63072000; includeSubDomains; preload");
    response.setHeader("Content-Security-Policy", csPolicy);
    response.setHeader("X-Content-Type-Options", "nosniff");
    filterChain.doFilter(request, response);
  }
}
