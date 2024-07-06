package com.fleencorp.feen.model.domain.google.oauth2;

import com.fleencorp.feen.converter.impl.StringCryptoConverter;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "google_oauth2_authorization")
public class GoogleOauth2Authorization extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "google_oauth2_authorization_id", nullable = false, updatable = false, unique = true)
  private Long googleOauthAuthorizationId;

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

  @OneToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false, unique = true)
  private Member member;

}
