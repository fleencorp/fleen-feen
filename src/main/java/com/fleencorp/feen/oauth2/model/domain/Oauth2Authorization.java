package com.fleencorp.feen.oauth2.model.domain;

import com.fleencorp.base.converter.impl.security.StringCryptoConverter;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.constant.Oauth2Source;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "oauth2_authorization")
public class Oauth2Authorization extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "oauth2_authorization_id", nullable = false, updatable = false, unique = true)
  private Long oauth2AuthorizationId;

  @Convert(converter = StringCryptoConverter.class)
  @Column(name = "access_token")
  private String accessToken;

  @Convert(converter = StringCryptoConverter.class)
  @Column(name = "refresh_token")
  private String refreshToken;

  @Column(name = "token_expiration_time_in_milliseconds")
  private Long tokenExpirationTimeInMilliseconds;

  @Column(name = "scope")
  private String scope;

  @Column(name = "token_type")
  private String tokenType;

  @Enumerated(STRING)
  @Column(name = "oauth2_service_type", updatable = false, nullable = false)
  private Oauth2ServiceType serviceType;

  @Enumerated(STRING)
  @Column(name = "oauth2_source", updatable = false, nullable = false)
  private Oauth2Source oauth2Source;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  public static Oauth2Authorization of(final Member member) {
    final Oauth2Authorization oauth2Authorization = new Oauth2Authorization();
    oauth2Authorization.setMember(member);

    return oauth2Authorization;
  }

  /**
   * Updates the OAuth2 service type and source for the current session.
   *
   * <p>This method sets the {@code serviceType} and {@code oauth2Source} for the session, updating
   * the OAuth2 service provider type and the source of the authentication.</p>
   *
   * @param serviceType the type of OAuth2 service (e.g., Google, Facebook)
   * @param oauth2Source the source of the OAuth2 authentication
   */
  public void updateServiceTypeAndSource(final Oauth2ServiceType serviceType, final Oauth2Source oauth2Source) {
    this.serviceType = serviceType;
    this.oauth2Source = oauth2Source;
  }

  /**
   * Checks if the access token has expired.
   *
   * <p>This method compares the current system time with the token's expiration time to determine
   * if the access token is expired.</p>
   *
   * @return {@code true} if the access token is expired, {@code false} otherwise
   */
  public boolean isAccessTokenExpired() {
    return System.currentTimeMillis() >= tokenExpirationTimeInMilliseconds;
  }

  /**
   * Checks if the provided access token differs from the stored one.
   *
   * <p>This method compares the new access token with the currently stored access token. It returns
   * {@code true} if the stored token is either {@code null} or different from the new one.</p>
   *
   * @param newAccessToken the new access token to compare with the stored one
   * @return {@code true} if the tokens are different, {@code false} if they are the same
   */
  public boolean isAccessTokenNotSame(final String newAccessToken) {
    return isNull(accessToken) || !(accessToken.equals(newAccessToken));
  }

}
