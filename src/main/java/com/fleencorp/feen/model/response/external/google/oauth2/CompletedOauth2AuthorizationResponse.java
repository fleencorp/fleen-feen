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
}
