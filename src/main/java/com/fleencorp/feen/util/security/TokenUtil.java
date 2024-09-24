package com.fleencorp.feen.util.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleencorp.feen.configuration.security.properties.TokenProperties;
import com.fleencorp.feen.constant.security.auth.AuthenticationStatus;
import com.fleencorp.feen.constant.security.token.TokenType;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.model.security.TokenPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;

import static com.fleencorp.base.util.datetime.DateTimeUtil.durationToMilliseconds;
import static com.fleencorp.feen.constant.security.token.TokenClaimField.*;
import static com.fleencorp.feen.util.security.UserAuthoritiesUtil.getRefreshTokenAuthorities;
import static com.fleencorp.feen.util.security.UserAuthoritiesUtil.getResetPasswordAuthorities;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

/**
 * <p>JwtUtil is a utility class for handling JSON Web Tokens (JWT) in the Fleen application.
 * Provides methods for token generation, validation, and extraction of user details.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Component
@Getter
@Setter
@PropertySources({
  @PropertySource("classpath:application.properties")
})
public class TokenUtil {

  private final ObjectMapper mapper;
  private final String jwtIssuer;
  private final String jwtSecret;
  private final TokenProperties tokenProperties;

  public TokenUtil(
      final ObjectMapper objectMapper,
      @Value("${jwt.issuer}") final String jwtIssuer,
      @Value("${jwt.secret}") final String jwtSecret, final TokenProperties tokenProperties) {
    this.mapper = objectMapper;
    this.jwtIssuer = jwtIssuer;
    this.jwtSecret = jwtSecret;
    this.tokenProperties = tokenProperties;
  }

  /**
   * Retrieves the username associated with the JWT token.
   *
   * <p>This method extracts the subject claim from the JWT token, which typically represents the username
   * or identifier of the token holder.</p>
   *
   * @param token The JWT token from which to retrieve the username.
   * @return The username associated with the JWT token.
   * @throws io.jsonwebtoken.JwtException If an error occurs during JWT parsing or validation.
   */
  public String getUsernameFromToken(final String token) {
    return getClaimFromToken(token, Claims::getSubject);
  }

  /**
   * Retrieves the expiration date of the JWT token.
   *
   * <p>This method extracts the expiration claim from the JWT token, indicating the date and time when
   * the token is set to expire.</p>
   *
   * @param token The JWT token from which to retrieve the expiration date.
   * @return The expiration date of the JWT token.
   * @throws io.jsonwebtoken.JwtException If an error occurs during JWT parsing or validation.
   */
  public Date getExpirationDateFromToken(final String token) {
    return getClaimFromToken(token, Claims::getExpiration);
  }

  /**
   * Retrieves a specific claim from the JWT token using the provided resolver function.
   *
   * <p>This method extracts the claims from the JWT token and applies the resolver function to retrieve
   * a specific claim value.</p>
   *
   * @param token          The JWT token from which to retrieve claims.
   * @param claimsResolver The function to resolve and extract a specific claim from the token's claims.
   * @param <T>            The type of the claim value.
   * @return The resolved claim value from the token.
   * @throws io.jsonwebtoken.JwtException If an error occurs during JWT parsing or validation.
   */
  public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
    final Claims claims = getClaimsFromToken(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Retrieves a specific claim value from the JWT token based on the provided key.
   *
   * <p>This method extracts the claims from the JWT token and retrieves the value associated with
   * the specified key.</p>
   *
   * @param token The JWT token from which to retrieve claims.
   * @param key   The key of the claim whose value is to be retrieved.
   * @return The value of the claim associated with the specified key, or {@code null} if the claim does not exist.
   * @throws io.jsonwebtoken.JwtException If an error occurs during JWT parsing or validation.
   */
  public Object getClaim(final String token, final String key) {
    final Claims claims = getClaimsFromToken(token);
    return claims.get(key);
  }

  /**
   * Decodes the JWT secret from Base64 encoding.
   */
  private byte[] getJwtSecretDecodedFromBase64() {
    return Base64
        .getDecoder()
        .decode(jwtSecret);
  }

  /**
   * Converts the decoded JWT secret into a SecretKey for HMAC-SHA512 encryption.
   */
  private SecretKey convertJwtSecretToSecretKey() {
    return new SecretKeySpec(getJwtSecretDecodedFromBase64(), "HmacSHA512");
  }

  /**
   * Retrieves the signing key for HMAC-SHA algorithms using the decoded JWT secret.
   */
  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(getJwtSecretDecodedFromBase64());
  }

  /**
   * Retrieves claims from a JWT token.
   *
   * <p>This method parses and verifies the JWT token using a secret key.
   * It extracts and returns all claims (payload) embedded within the token.</p>
   *
   * @param token The JWT token string from which claims are to be extracted.
   * @return A {@link Claims} object containing all claims parsed from the JWT token.
   * @throws io.jsonwebtoken.JwtException if an error occurs during parsing and validation of token
   */
  private Claims getClaimsFromToken(final String token) {
    return Jwts
        .parser()
        .verifyWith(convertJwtSecretToSecretKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * Retrieves and returns a copy of the token payload as a mutable map.
   */
  public Map<String, Object> createTokenMapFromClaims(final String token) {
    return new HashMap<>(getClaimsFromToken(token));
  }

  /**
   * Converts a token's claims map into a TokenPayload object using ObjectMapper.
   */
  public TokenPayload convertTokenMapToPayload(final String token) {
    final Map<String, Object> claims = getClaimsFromToken(token);
    return mapper.convertValue(claims, TokenPayload.class);
  }

  /**
   * Checks if the given JWT token has expired.
   *
   * <p>This method extracts the expiration date from the token and compares it with the current date
   * to determine if the token has expired.</p>
   *
   * @param token The JWT token to be checked for expiration.
   * @return {@code true} if the token is expired, {@code false} otherwise.
   */
  public boolean isTokenExpired(final String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  /**
   * Validates if the given JWT token matches the username in the UserDetails and is not expired.
   *
   * <p>This method verifies if the username extracted from the token matches the username in the UserDetails
   * and checks if the token has not expired.</p>
   *
   * @param token   The JWT token to be validated.
   * @param details The UserDetails containing user information for validation.
   * @return {@code true} if the token is valid for the UserDetails, {@code false} otherwise.
   */
  public boolean isTokenValid(final String token, final UserDetails details) {
    final String username = getUsernameFromToken(token);
    return (nonNull(username) && username.equalsIgnoreCase(details.getUsername()) && !isTokenExpired(token));
  }

  /**
   * Generates an access token for the specified user.
   *
   * <p>This method generates a JWT access token for the given {@code FleenUser}.
   * It creates a fresh set of claims, sets basic user details, authorities, token type,
   * and authentication status, and then generates the token with a specified expiration duration.</p>
   *
   * @param user The {@code FleenUser} for whom the token is generated.
   * @param tokenType The type of token being generated (e.g., ACCESS_TOKEN).
   * @param authenticationStatus The authentication status to be set in the token.
   * @return A JWT access token as a {@code String}.
   */
  public String generateAccessToken(final FleenUser user, final TokenType tokenType, final AuthenticationStatus authenticationStatus) {
    final Map<String, Object> claims = getFreshClaims();
    setBasicDetails(claims, user.getId(), authoritiesToList(user.getAuthorities()));
    setUserDetails(user, claims);
    setTokenType(claims, tokenType);
    setAuthenticationStatus(claims, authenticationStatus);

    final long tokenExpirationInMilliseconds = durationToMilliseconds(Duration.ofHours(tokenProperties.getAccessToken()));
    return createToken(user.getUsername(), claims, tokenExpirationInMilliseconds);
  }

  /**
   * Generates a refresh token for the specified user.
   *
   * <p>This method generates a JWT refresh token for the given {@code FleenUser}.
   * It utilizes the provided {@code TokenType} and {@code AuthenticationStatus}, along with
   * predefined authorities and a duration specified by the refresh token properties.</p>
   *
   * @param user The {@code FleenUser} for whom the token is generated.
   * @param tokenType The type of token being generated (e.g., REFRESH_TOKEN).
   * @param authenticationStatus The authentication status to be set in the token.
   * @return A JWT refresh token as a {@code String}.
   */
  public String generateRefreshToken(final FleenUser user, final TokenType tokenType, final AuthenticationStatus authenticationStatus) {
    return generateToken(user, tokenType, authenticationStatus, getRefreshTokenAuthorities(), Duration.ofHours(tokenProperties.getRefreshToken()));
  }

  /**
   * Generates a reset password token for the specified user.
   *
   * <p>This method generates a JWT reset password token for the given {@code FleenUser}.
   * It utilizes the provided {@code TokenType} and {@code AuthenticationStatus}, along with
   * predefined authorities and a duration specified by the reset password token properties.</p>
   *
   * @param user The {@code FleenUser} for whom the token is generated.
   * @param tokenType The type of token being generated (e.g., RESET_PASSWORD_TOKEN).
   * @param authenticationStatus The authentication status to be set in the token.
   * @return A JWT reset password token as a {@code String}.
   */
  public String generateResetPasswordToken(final FleenUser user, final TokenType tokenType, final AuthenticationStatus authenticationStatus) {
    return generateToken(user, tokenType, authenticationStatus, getResetPasswordAuthorities(), Duration.ofHours(tokenProperties.getResetPasswordToken()));
  }

  /**
   * Generates a token for the given user with specified token type, authentication status, authorities, and duration.
   *
   * <p>This method creates a JWT token for the user. It sets basic details, token type, authentication status,
   * and expiration duration in the claims map, then signs and returns the token.</p>
   *
   * @param user The {@code FleenUser} object containing user details.
   * @param tokenType The type of the token, as specified by the {@code TokenType} enum.
   * @param authenticationStatus The authentication status, as specified by the {@code AuthenticationStatus} enum.
   * @param authorities A list of granted authorities associated with the user.
   * @param duration The duration for which the token is valid.
   * @return A signed JWT token as a {@code String}.
   */
  public String generateToken(final FleenUser user, final TokenType tokenType, final AuthenticationStatus authenticationStatus, final List<GrantedAuthority> authorities, final Duration duration) {
    final Map<String, Object> claims = getFreshClaims();
    setBasicDetails(claims, user.getId(), authoritiesToList(authorities));

    setTokenType(claims, tokenType);
    setAuthenticationStatus(claims, authenticationStatus);

    final long tokenExpirationInMilliseconds = durationToMilliseconds(duration);
    return createToken(user.getUsername(), claims, tokenExpirationInMilliseconds);
  }

  /**
   * Creates and returns a new, empty claims map.
   *
   * <p>This method initializes a fresh {@code HashMap} to store JWT claims.
   * It can be used as a starting point for adding various token-related claims.</p>
   *
   * @return A new, empty {@code Map<String, Object>} for JWT claims.
   */
  public Map<String, Object> getFreshClaims() {
    return new HashMap<>();
  }

  /**
   * Sets the token type in the claims map.
   *
   * @param claims The map containing token claims.
   * @param tokenType The TokenType enum representing the type of token.
   */
  public void setTokenType(final Map<String, Object> claims, final TokenType tokenType) {
    if (nonNull(claims) && nonNull(tokenType)) {
      claims.put(TOKEN_TYPE.getValue(), tokenType.getValue());
    }
  }

  /**
   * Sets the authentication status in the claims map.
   *
   * @param claims The map containing token claims.
   * @param authenticationStatus The AuthenticationStatus enum representing the status of authentication.
   */
  public void setAuthenticationStatus(final Map<String, Object> claims, final AuthenticationStatus authenticationStatus) {
    if (nonNull(claims) && nonNull(authenticationStatus)) {
      claims.put(AUTHENTICATION_STATUS_KEY.getValue(), authenticationStatus);
    }
  }

  /**
   * Creates a JWT token with specified subject, claims, and expiration period.
   *
   * @param subject The subject of the token.
   * @param claims  The claims to be included in the token payload.
   * @param expirationPeriod The expiration period of the token in milliseconds.
   * @return A JWT token string.
   */
  public String createToken(final String subject, final Map<String, Object> claims, final long expirationPeriod) {
    return Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuer(jwtIssuer)
        .issuedAt(new Date(currentTimeMillis()))
        .expiration(new Date(currentTimeMillis() + expirationPeriod))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * Sets user details in the claims map.
   *
   * <p>This method adds various user details, such as first name, last name, email address, phone number,
   * profile photo, and profile status, to the provided claims map. Each detail is associated with a
   * specific key from the {@code TokenClaimField} enum.</p>
   *
   * @param user The {@code FleenUser} object containing user details to be added to the claims.
   * @param claims The map of claims to which the user details will be added.
   */
  public void setUserDetails(final FleenUser user, final Map<String, Object> claims) {
    if (nonNull(claims) && nonNull(user)) {
      claims.put(FIRST_NAME.getValue(), user.getFirstName());
      claims.put(LAST_NAME.getValue(), user.getLastName());
      claims.put(COUNTRY.getValue(), user.getCountry());
      claims.put(EMAIL_ADDRESS.getValue(), user.getEmailAddress());
      claims.put(PHONE_NUMBER.getValue(), user.getPhoneNumber());
      claims.put(PROFILE_PHOTO.getValue(), user.getProfilePhoto());
      claims.put(STATUS.getValue(), user.getProfileStatus());
      claims.put(VERIFICATION_STATUS.getValue(), user.getVerificationStatus());
      claims.put(TIMEZONE.getValue(), user.getTimezone());
    }
  }

  /**
   * Sets basic details in the claims map.
   *
   * <p>This method adds the user ID and authorities to the provided claims map. The user ID is
   * associated with the {@code CLAIMS_USER_ID_KEY}, and the authorities are associated with the
   * {@code CLAIMS_AUTHORITY_KEY}.</p>
   *
   * @param claims The map of claims to which the user ID and authorities will be added.
   * @param userId The user ID to be added to the claims.
   * @param authorities The array of authorities to be added to the claims.
   */
  private void setBasicDetails(final Map<String, Object> claims, final Long userId, final String[] authorities) {
    if (nonNull(claims) && nonNull(userId)) {
      claims.put(USER_ID.getValue(), userId);
    }
    if (nonNull(claims) && nonNull(authorities)) {
      claims.put(AUTHORITIES.getValue(), authorities);
    }
  }

  /**
   * Converts a collection of granted authorities to a list of authority names.
   *
   * <p>This method takes a collection of GrantedAuthority objects, extracts the authority names
   * from each GrantedAuthority, and returns them as an array of strings.</p>
   *
   * @param authorities The collection of GrantedAuthority objects.
   * @return An array of strings representing the authority names.
   */
  public String[] authoritiesToList(final Collection<? extends GrantedAuthority> authorities) {
    if (nonNull(authorities)) {
      return authorities
          .stream()
          .map(GrantedAuthority::getAuthority)
          .toArray(String[]::new);
    }
    return new String[0];
  }
}
