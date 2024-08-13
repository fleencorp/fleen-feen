package com.fleencorp.feen.model.response.security.mfa;

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
  "message",
})
public class ConfirmMfaSetupResponse {

  @Builder.Default
  @JsonProperty("message")
  private String message = "Mfa setup and confirmation successful";

  public static ConfirmMfaSetupResponse of() {
    return new ConfirmMfaSetupResponse();
  }
}
