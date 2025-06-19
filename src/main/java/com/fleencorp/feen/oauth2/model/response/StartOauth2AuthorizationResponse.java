package com.fleencorp.feen.oauth2.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "authorization_uri"
})
public class StartOauth2AuthorizationResponse extends LocalizedResponse {

  @JsonProperty("authorization_uri")
  private String authorizationUri;

  @Override
  public String getMessageCode() {
    return "start.oauth2.authorization";
  }

  public static StartOauth2AuthorizationResponse of(final String authorizationUri) {
    return StartOauth2AuthorizationResponse.builder()
      .authorizationUri(authorizationUri)
      .build();
  }
}
