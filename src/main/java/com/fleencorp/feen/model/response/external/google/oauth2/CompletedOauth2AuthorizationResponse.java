package com.fleencorp.feen.model.response.external.google.oauth2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fleencorp.feen.model.response.external.google.oauth2.base.Oauth2AuthorizationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompletedOauth2AuthorizationResponse extends Oauth2AuthorizationResponse {

  @Override
  public String getMessageCode() {
    return "completed.oauth2.authorization";
  }

  public static CompletedOauth2AuthorizationResponse of(final String accessToken, final String refreshToken, final Long expiresIn, final String tokenType, final String scope) {
    return CompletedOauth2AuthorizationResponse.builder()
      .accessToken(accessToken)
      .refreshToken(refreshToken)
      .accessTokenExpirationTimeInSeconds(expiresIn)
      .tokenType(tokenType)
      .scope(scope)
      .build();
  }
}
