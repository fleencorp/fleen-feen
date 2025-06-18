package com.fleencorp.feen.oauth2.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fleencorp.feen.oauth2.model.response.base.Oauth2AuthorizationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompletedOauth2AuthorizationResponse extends Oauth2AuthorizationResponse {

  public CompletedOauth2AuthorizationResponse(final String accessToken, final String refreshToken, final String tokenType, final Long expiresIn, final String scope) {
    super(null, null, null, accessToken, refreshToken, tokenType, expiresIn, scope);
  }

  @Override
  public String getMessageCode() {
    return "completed.oauth2.authorization";
  }

  public static CompletedOauth2AuthorizationResponse of(final String accessToken, final String refreshToken, final Long expiresIn, final String tokenType, final String scope) {
    return new CompletedOauth2AuthorizationResponse(accessToken, refreshToken, tokenType, expiresIn, scope);
  }
}
