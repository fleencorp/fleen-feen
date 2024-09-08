package com.fleencorp.feen.model.response.external.google.oauth2;

import com.fleencorp.feen.model.response.external.google.oauth2.base.Oauth2AuthorizationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class RefreshOauth2TokenResponse extends Oauth2AuthorizationResponse {

  public static RefreshOauth2TokenResponse of(final String accessToken, final String refreshToken, final Long expiresIn, final String tokenType, final String scope) {
    return RefreshOauth2TokenResponse.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .accessTokenExpirationTimeInSeconds(expiresIn)
      .tokenType(tokenType)
      .scope(scope)
      .build();
  }
}
