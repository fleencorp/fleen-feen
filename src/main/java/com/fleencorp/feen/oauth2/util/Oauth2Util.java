package com.fleencorp.feen.oauth2.util;

import com.fleencorp.base.util.StringUtil;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidScopeException;
import com.fleencorp.feen.oauth2.model.request.Oauth2AuthenticationRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;


/**
 * Utility class providing helper methods and functionalities related to OAuth2 authentication and authorization.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
public final class Oauth2Util {

  private Oauth2Util() {}

  /**
   * Validates the OAuth2 scope provided in the states string and returns the corresponding {@link Oauth2ServiceType} enum.
   *
   * <p>This method first converts the provided states string into a map using {@link #getStatesMap(String)}.
   * It then delegates the validation to {@link #validateOauth2ScopeAndReturn(Map)} to obtain the corresponding
   * {@link Oauth2ServiceType} enum.</p>
   *
   * @param statesStr A string containing the states information, including the OAuth2 scope.
   * @return The validated {@link Oauth2ServiceType} enum.
   * @throws Oauth2InvalidScopeException if the OAuth2 scope is invalid or null.
   */
  public static Oauth2ServiceType validateOauth2ScopeAndReturn(final String statesStr) {
    final Map<String, String> statesMap = getStatesMap(statesStr);
    return validateOauth2ScopeAndReturn(statesMap);
  }

  /**
   * Validates the OAuth2 scope provided in the states map and returns the corresponding Oauth2Scope enum.
   *
   * <p>This method retrieves the OAuth2 scope string from the provided map using the key defined by
   * {@link Oauth2AuthenticationRequest#OAUTH_2_SERVICE_TYPE_KEY}. It then converts this string to an Oauth2Scope
   * enum using {@link Oauth2ServiceType#of(String)}.</p>
   *
   * <p>If either the scope string or the resulting Oauth2Scope is null, an {@link Oauth2InvalidScopeException}
   * is thrown.</p>
   *
   * @param states A map containing state information, including the OAuth2 scope.
   * @return The validated {@link Oauth2ServiceType} enum.
   * @throws Oauth2InvalidScopeException if the OAuth2 scope is invalid or null.
   */
  public static Oauth2ServiceType validateOauth2ScopeAndReturn(final Map<String, String> states) {
    final String oauth2ServiceTypeStr = states.get(Oauth2AuthenticationRequest.OAUTH_2_SERVICE_TYPE_KEY);
    final Oauth2ServiceType oauth2ServiceType = Oauth2ServiceType.of(oauth2ServiceTypeStr);

    checkIsNullAny(List.of(oauth2ServiceTypeStr, oauth2ServiceType), Oauth2InvalidScopeException::new);
    return oauth2ServiceType;
  }

  /**
   * Converts a string representation of states into a map where the key is the state name
   * and the value is its corresponding value.
   *
   * @param statesStr the string containing state information, typically formatted as key-value pairs.
   * @return a map of state names to their corresponding values.
   */
  public static Map<String, String> getStatesMap(final String statesStr) {
    return StringUtil.strToMap(statesStr);
  }

  public static Oauth2AuthenticationRequest toOauth2AuthenticationRequest(final Oauth2ServiceType oauth2ServiceType) {
    return Oauth2AuthenticationRequest.of(oauth2ServiceType);
  }
}
