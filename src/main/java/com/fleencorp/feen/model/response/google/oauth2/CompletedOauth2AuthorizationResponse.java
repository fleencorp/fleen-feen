package com.fleencorp.feen.model.response.google.oauth2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fleencorp.feen.model.response.google.oauth2.base.Oauth2AuthorizationResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class CompletedOauth2AuthorizationResponse extends Oauth2AuthorizationResponse {
}
