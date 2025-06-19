package com.fleencorp.feen.oauth2.model.response;

import com.fleencorp.feen.oauth2.model.response.base.Oauth2AuthorizationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RefreshOauth2TokenResponse extends Oauth2AuthorizationResponse {

  public RefreshOauth2TokenResponse(final String accessToken, final String refreshToken, final String tokenType, final Long expiresIn, final String scope) {
    super(null, null, null, accessToken, refreshToken, tokenType, expiresIn, scope);
  }

  public static RefreshOauth2TokenResponse of(final String accessToken, final String refreshToken, final Long expiresIn, final String tokenType, final String scope) {
    return new RefreshOauth2TokenResponse(accessToken, refreshToken, tokenType, expiresIn, scope);
  }
}
