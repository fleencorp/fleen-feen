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

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "google_oauth_authorization")
public class GoogleOauth2Authorization extends FleenFeenEntity {

  @Id
  @Column(name = "google_oauth_authorization_id")
  private Long googleOauthAuthorizationId;

  @Column(name = "access_token")
  @Convert(converter = StringCryptoConverter.class)
  private String accessToken;

  @Column(name = "refresh_token")
  @Convert(converter = StringCryptoConverter.class)
  private String refreshToken;

  @Column(name = "token_expiration_time_in_milliseconds")
  private Long tokenExpirationTimeInMilliseconds;

  @Column(name = "authorization_scope")
  private String authorizationScope;

  @Column(name = "token_type")
  private String tokenType;

  @OneToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id")
  private Member member;

}
