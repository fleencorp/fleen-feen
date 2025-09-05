package com.fleencorp.feen.common.filter;

import com.fleencorp.base.service.EmailService;
import com.fleencorp.feen.common.service.impl.cache.CacheService;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.shared.security.TokenPayload;
import com.fleencorp.feen.user.exception.authentication.InvalidAuthenticationException;
import com.fleencorp.feen.user.exception.authentication.InvalidAuthenticationTokenException;
import com.fleencorp.feen.user.util.TokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static com.fleencorp.base.constant.base.SecurityConstant.AUTH_HEADER_PREFIX;
import static com.fleencorp.feen.common.constant.message.ResponseMessage.UNKNOWN;
import static com.fleencorp.feen.common.service.impl.cache.CacheKeyService.getAccessTokenCacheKey;
import static com.fleencorp.feen.common.util.common.LoggingUtil.logIfEnabled;
import static com.fleencorp.feen.user.util.UserAuthoritiesUtil.isAuthorityWhitelisted;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


/**
 * <p>The JwtAuthenticationFilter class is a Spring component that extends
 * OncePerRequestFilter. It is responsible for processing JWT (JSON Web Token)
 * authentication in the incoming HTTP requests. The filter extracts the JWT
 * from the Authorization header, validates it, and sets the authentication
 * information in the SecurityContextHolder if the token is valid.</p>
 *
 * <p>This filter is annotated with @Slf4j for Lombok-generated logging, and
 * {@link Component} to indicate that it is a Spring component and can be automatically
 * discovered and registered in the Spring context. It is designed to be executed
 * once per request, making use of OncePerRequestFilter.</p>
 *
 * <p>The JwtAuthenticationFilter has dependencies on JwtUtil, CacheService,
 * MemberService, and HandlerExceptionResolver. These dependencies are injected
 * through the constructor, making the filter configurable and loosely coupled.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenUtil tokenUtil;
  private final CacheService cacheService;
  private final EmailService emailService;
  private final HandlerExceptionResolver resolver;

  /**
   * Constructs a JwtAuthenticationFilter with the required dependencies.
   *
   * @param tokenUtil The utility class for handling JWT operations.
   * @param cacheService The service for caching and retrieving data.
   * @param emailService The service for interacting with emails.
   * @param handlerExceptionResolver The resolver for handling exceptions during filter execution.
   */
  public JwtAuthenticationFilter(
      final TokenUtil tokenUtil,
      final CacheService cacheService,
      @Lazy final EmailService emailService,
      @Lazy @Qualifier("handlerExceptionResolver") final HandlerExceptionResolver handlerExceptionResolver) {
    this.tokenUtil = tokenUtil;
    this.cacheService = cacheService;
    this.emailService = emailService;
    this.resolver = handlerExceptionResolver;
  }

  /**
   * Performs JWT token extraction, validation, and sets authentication details for incoming requests.
   *
   * <p>This method extracts a JWT token from the request, validates its format and content,
   * retrieves necessary user details from the token, and sets up authentication if valid.</p>
   *
   * <p>If the token is missing or invalid, the method proceeds with the filter chain without authentication.</p>
   *
   * @param request      HTTP servlet request containing the JWT token.
   * @param response     HTTP servlet response for handling filter results.
   * @param filterChain  Filter chain to proceed with after token validation.
   * @throws InvalidAuthenticationTokenException if the authentication token is invalid or does not exist in the repository
   */
  @Override
  protected void doFilterInternal(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response, @NonNull final FilterChain filterChain) {
    try {
      // Extract and validate JWT token
      final String token = extractAndValidateJwtToken(request);
      final UsernamePasswordAuthenticationToken unauthenticatedUser = new UsernamePasswordAuthenticationToken(RegisteredUser.of(), null);

      if (isNull(token)) {
        log.info("Step 1");
        SecurityContextHolder.getContext().setAuthentication(unauthenticatedUser);
        log.info("Step 2");
        filterChain.doFilter(request, response);
        return;
      }

      log.info("Step 3");
      // Retrieve email address from the JWT token
      final String emailAddress = getEmailAddressFromToken(token);
      if (!StringUtils.isNotEmpty(emailAddress)) {
        log.info("Step 4");
        SecurityContextHolder.getContext().setAuthentication(unauthenticatedUser);
        log.info("Step 5");
        filterChain.doFilter(request, response);
        return;
      }

      log.info("Step 6");
      // Validate the JWT token and set authentication details
      final boolean validationSuccessful = handleJwtTokenValidation(token, request);
      log.info("Step 7");
      if (!validationSuccessful) {
        log.info("Step 8");
        throw new InvalidAuthenticationTokenException();
      }

      log.info("Step 9");
      // Continue with the filter chain
      filterChain.doFilter(request, response);
    } catch (final InvalidAuthenticationTokenException | IOException | ServletException ex) {
      handleException(request, response, ex);
    }
  }

  /**
   * Handles validation and authentication setup for JWT tokens.
   *
   * <p>This method validates the provided JWT token, checks its validity against cached tokens,
   * and sets up authentication details in the security context if authentication conditions are met.</p>
   *
   * <p>If authentication is successful and conditions allow, it sets the authentication token
   * in the security context for further request processing.</p>
   *
   * @param token   JWT token extracted from the request.
   * @param request HTTP servlet request associated with the token validation.
   * @return {@code true} if JWT token validation and authentication succeed, {@code false} otherwise.
   */
  private boolean handleJwtTokenValidation(final String token, final HttpServletRequest request) {
    try {
      log.info("Step 1-1");
      if (isAuthenticationEmpty()) {
        log.info("Step 1-2");
        final UserDetails userDetails = extractUserDetailsFromToken(token);
        log.info("Step 1-3");
        final String key = getAccessTokenCacheKey(userDetails.getUsername());
        log.info("Step 1-4");
        final String savedToken = getTokenFromCache(key);

        log.info("Step 1-5");
        if (isTokenValid(token, userDetails)) {
          log.info("Step 1-6");
          final UsernamePasswordAuthenticationToken authentication = createAuthenticationToken(request, userDetails);

          log.info("Step 1-7");
          // Set authentication in SecurityContextHolder based on conditions
          // Extract checks for if user is existing in the record even if the token is valid
          if (shouldSetAuthentication(key, savedToken, userDetails) && isEmailExists(userDetails)) {
            log.info("Step 1-8");
            SecurityContextHolder.getContext().setAuthentication(authentication);
          } else {
            log.info("Step 1-9");
            return false;
          }
        }
      }
    } catch (final RuntimeException ex) {
      // Log any exceptions as errors
      logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
    }
    log.info("Step 1-10");
    return true;
  }

  /**
   * Checks whether the email associated with the given user details already exists.
   *
   * <p>This method retrieves the email address from the provided user details and checks
   * if it already exists using the {@code emailService}.</p>
   *
   * @param userDetails the user details containing the email to check
   * @return {@code true} if the email exists, {@code false} otherwise
   */
  private boolean isEmailExists(final UserDetails userDetails) {
    return emailService.isEmailAddressExist(userDetails.getUsername());
  }

  /**
   * Validates if the provided JWT token is valid for the given user details.
   *
   * <p>This method delegates the validation process to the {@link TokenUtil} instance,
   * which checks if the token matches the user details and is not expired.</p>
   *
   * @param token       JWT token to be validated.
   * @param userDetails User details to validate against the token.
   * @return {@code true} if the token is valid for the given user details, {@code false} otherwise.
   */
  private boolean isTokenValid(final String token, final UserDetails userDetails) {
    return tokenUtil.isTokenValid(token, userDetails);
  }

  /**
   * Checks if the current authentication context is empty.
   *
   * <p>This method verifies if the {@link SecurityContextHolder} does not contain
   * an authenticated {@link org.springframework.security.core.Authentication} object.</p>
   *
   * @return {@code true} if the authentication context is empty, {@code false} otherwise.
   */
  public boolean isAuthenticationEmpty() {
    return SecurityContextHolder.getContext().getAuthentication() == null;
  }

  /**
   * Retrieves a token from the cache for the given key.
   *
   * <p>This method fetches the token associated with the specified key from the cache.</p>
   *
   * @param key The key used to retrieve the token from the cache.
   * @return The token associated with the provided key, or {@code null} if the key does not exist in the cache.
   */
  private String getTokenFromCache(final String key) {
    return (String) cacheService.get(key);
  }

  /**
   * Creates an {@code UsernamePasswordAuthenticationToken} for the given user details.
   *
   * <p>This method generates an authentication token using the provided {@code UserDetails} and
   * sets the details using the {@code HttpServletRequest}.</p>
   *
   * @param request The {@code HttpServletRequest} from which to build the authentication details.
   * @param userDetails The {@code UserDetails} of the authenticated user.
   * @return The created {@code UsernamePasswordAuthenticationToken}.
   */
  private UsernamePasswordAuthenticationToken createAuthenticationToken(
      final HttpServletRequest request, final UserDetails userDetails) {
    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
      userDetails,
      null,
      userDetails.getAuthorities());
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    return authentication;
  }

  /**
   * Extracts user details from the provided token.
   *
   * <p>This method converts the token into a {@code TokenPayload} using the {@code tokenUtil},
   * and then creates a {@code FleenUser} from the token details.</p>
   *
   * @param token The token from which to extract user details.
   * @return The {@code UserDetails} extracted from the token.
   */
  private UserDetails extractUserDetailsFromToken(final String token) {
    final TokenPayload details = tokenUtil.convertTokenMapToPayload(token);
    return RegisteredUser.fromToken(details);
  }

  /**
   * Extracts and validates the JWT token from the request's Authorization header.
   *
   * <p>This method retrieves the Authorization header from the given HTTP request,
   * checks if it starts with the expected prefix, and extracts the JWT token if valid.</p>
   *
   * @param request The HttpServletRequest object from which the Authorization header is extracted.
   * @return The extracted JWT token, or {@code null} if the header is missing or invalid.
   */
  private String extractAndValidateJwtToken(final HttpServletRequest request) {
    // Retrieve Authorization header from the request
    final String header = request.getHeader(AUTHORIZATION);

    // Check if the header is missing or doesn't start with the expected prefix
    if (isNull(header) || !startsWithIgnoreCase(header, AUTH_HEADER_PREFIX)) {
      return null;
    }

    // Extract and return the JWT token from the header
    final int index = AUTH_HEADER_PREFIX.length() + 1;
    return header.substring(index);
  }

  /**
   * Retrieves the email address from the provided token, handling any exceptions that occur during the process.
   *
   * <p>This method uses the {@code tokenUtil} to extract the email address (username) from the token.
   * If an exception occurs, it logs the error and throws an {@code InvalidAuthenticationTokenException}.</p>
   *
   * @param token     The token from which to retrieve the email address.
   * @return The email address extracted from the token.
   * @throws InvalidAuthenticationTokenException if there is an error extracting the email address from the token.
   */
  private String getEmailAddressFromToken(final String token) {
    try {
      log.info("Step 2-1");
      return tokenUtil.getUsernameFromToken(token);
    } catch (final IllegalArgumentException | ExpiredJwtException | MalformedJwtException | SignatureException ex) {
      // Log the error
      logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
      log.info("Step 2-2");
      throw new InvalidAuthenticationTokenException();
    }
  }

  /**
   * Determines whether authentication should be set based on the provided key, saved token, and user details.
   *
   * <p>This method checks if the given key exists in the cache and if the saved token is non-null.
   * Additionally, it checks if the user's authorities are whitelisted.</p>
   *
   * @param key         The cache key to check for existence.
   * @param savedToken  The saved token to check for non-nullity.
   * @param userDetails The user details containing the authorities to check against the whitelist.
   * @return {@code true} if authentication should be set, {@code false} otherwise.
   */
  private boolean shouldSetAuthentication(final String key, final String savedToken, final UserDetails userDetails) {
    return (cacheService.exists(key) && nonNull(savedToken)) ||
      isAuthorityWhitelisted(userDetails.getAuthorities());
  }

  /**
   * Handles exceptions that occur during authentication processing.
   *
   * <p>Logs the exception and resolves the appropriate response based on the type of exception.
   * If the exception is an instance of {@code InvalidAuthenticationTokenException}, it resolves it
   * using the specified resolver. Otherwise, it creates a new {@code InvalidAuthenticationException}
   * and resolves it.</p>
   *
   * @param request  The {@code HttpServletRequest} in which the exception occurred.
   * @param response The {@code HttpServletResponse} to send the error response.
   * @param ex       The exception that occurred during the authentication process.
   */
  private void handleException(final HttpServletRequest request, final HttpServletResponse response, final Exception ex) {
    logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
log.info("Step 3-1");
    if (ex instanceof InvalidAuthenticationTokenException) {
      resolver.resolveException(request, response, null, ex);
      log.info("Step 3-2");
      return;
    }

    log.info("Step 3-3");
    final InvalidAuthenticationException exception = new InvalidAuthenticationException(UNKNOWN);
    log.info("Step 3-4");
    resolver.resolveException(request, response, null, exception);
  }
}
