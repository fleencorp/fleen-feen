package com.fleencorp.feen.model.response.security;

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
@JsonPropertyOrder({
  "access_token"
})
public class InitiatePasswordChangeResponse {

  @JsonProperty("access_token")
  public String accessToken;

  public static InitiatePasswordChangeResponse of(String accessToken) {
    return InitiatePasswordChangeResponse.builder()
        .accessToken(accessToken)
        .build();
  }
}
