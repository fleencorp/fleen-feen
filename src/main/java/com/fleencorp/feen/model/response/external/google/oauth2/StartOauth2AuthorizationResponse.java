package com.fleencorp.feen.model.response.external.google.oauth2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("authorization_uri")
public class StartOauth2AuthorizationResponse {

  @JsonProperty("authorization_uri")
  private String authorizationUri;

  public static StartOauth2AuthorizationResponse of(final String authorizationUri) {
    return StartOauth2AuthorizationResponse.builder()
      .authorizationUri(authorizationUri)
      .build();
  }
}
