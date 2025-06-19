package com.fleencorp.feen.oauth2.exception.core;

import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.localizer.model.exception.LocalizedException;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.function.Supplier;

import static com.fleencorp.feen.oauth2.constant.Oauth2WebKey.SERVICE_TYPE;

public class Oauth2InvalidAuthorizationException extends LocalizedException {

  @Getter
  @Setter
  private Oauth2ServiceType oauth2ServiceType;

  @Override
  public String getMessageCode() {
    return "oauth2.invalid.authorization";
  }

  @Override
  public Map<String, Object> getDetails() {
    return Map.of(SERVICE_TYPE, oauth2ServiceType);
  }

  public Oauth2InvalidAuthorizationException(final Object...params) {
    super(params);
  }

  /**
   * Creates a {@link Supplier} that produces a new instance of
   * {@link Oauth2InvalidAuthorizationException} with the specified OAuth2 service type.
   *
   * <p>The {@code oauth2ServiceType} is set on the created exception instance,
   * allowing it to provide context about the service that encountered the
   * invalid authorization error. The returned {@link Supplier} can be used
   * to lazily generate the exception when needed.</p>
   *
   * @param oauth2ServiceType the OAuth2 service type associated with the
   *                          invalid authorization exception
   * @return a {@link Supplier} that provides a new instance of
   *         {@link Oauth2InvalidAuthorizationException} with the specified
   *         service type
   */
  public static Supplier<Oauth2InvalidAuthorizationException> of(final Oauth2ServiceType oauth2ServiceType) {
    final Oauth2InvalidAuthorizationException oauth2InvalidAuthorizationException = new Oauth2InvalidAuthorizationException();
    oauth2InvalidAuthorizationException.setOauth2ServiceType(oauth2ServiceType);
    return () -> oauth2InvalidAuthorizationException;
  }

  /**
   * Creates a new instance of {@link Oauth2InvalidAuthorizationException}
   * with the specified OAuth2 service type and returns it.
   *
   * <p>This method allows for immediate instantiation of the exception with the
   * provided {@code oauth2ServiceType}, which provides context regarding the
   * service that encountered the invalid authorization error.</p>
   *
   * @param oauth2ServiceType the OAuth2 service type associated with the
   *                          invalid authorization exception
   * @return a new instance of {@link Oauth2InvalidAuthorizationException}
   *         with the specified service type
   */
  public static Oauth2InvalidAuthorizationException ofDefault(final Oauth2ServiceType oauth2ServiceType) {
    final Oauth2InvalidAuthorizationException oauth2InvalidAuthorizationException = new Oauth2InvalidAuthorizationException();
    oauth2InvalidAuthorizationException.setOauth2ServiceType(oauth2ServiceType);
    return oauth2InvalidAuthorizationException;
  }

}
