package com.fleencorp.feen.model.response.google.oauth2;

import com.fleencorp.feen.model.response.google.oauth2.base.Oauth2AuthorizationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class RefreshOauth2TokenResponse extends Oauth2AuthorizationResponse {
}
