package com.fleencorp.feen.oauth2.exception.core;

import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.Map;

import static com.fleencorp.feen.oauth2.constant.Oauth2WebKey.SERVICE_TYPE;

public class Oauth2InvalidGrantOrTokenException extends LocalizedException {

  private Oauth2ServiceType oauth2ServiceType;

  @Override
  public String getMessageCode() {
    return "oauth2.invalid.grant.or.token";
  }

  @Override
  public Map<String, Object> getDetails() {
    return Map.of(SERVICE_TYPE, oauth2ServiceType);
  }

  public Oauth2InvalidGrantOrTokenException(final Object...params) {
    super(params);
  }

  /**
   * Creates a new instance of {@link Oauth2InvalidGrantOrTokenException}
   * with the specified authorization code and OAuth2 service type.
   *
   * <p>This method allows for the instantiation of the exception while providing
   * context regarding the invalid grant or token error, which includes the
   * authorization code that caused the issue. The service type can be used to
   * indicate which OAuth2 service encountered the error.</p>
   *
   * @param authorizationCode the authorization code that was invalid, leading
   *                          to this exception
   * @param oauth2ServiceType the OAuth2 service type associated with the
   *                          invalid grant or token error
   * @return a new instance of {@link Oauth2InvalidGrantOrTokenException}
   *         with the specified authorization code and service type
   */
  public static Oauth2InvalidGrantOrTokenException of(final Object authorizationCode, final Oauth2ServiceType oauth2ServiceType) {
    final Oauth2InvalidGrantOrTokenException oauth2InvalidGrantOrTokenException = new Oauth2InvalidGrantOrTokenException(authorizationCode);
    oauth2InvalidGrantOrTokenException.oauth2ServiceType = oauth2ServiceType;
    return oauth2InvalidGrantOrTokenException;
  }
}
