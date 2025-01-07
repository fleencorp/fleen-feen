package com.fleencorp.feen.model.response.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "access_token"
})
public class InitiatePasswordChangeResponse extends ApiResponse {

  @JsonProperty("access_token")
  public String accessToken;

  @Override
  public String getMessageCode() {
    return "initiate.password.change";
  }

  public static InitiatePasswordChangeResponse of(final String accessToken) {
    return InitiatePasswordChangeResponse.builder()
        .accessToken(accessToken)
        .build();
  }
}
