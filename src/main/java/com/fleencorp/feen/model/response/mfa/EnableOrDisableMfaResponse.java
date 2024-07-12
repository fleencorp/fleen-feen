package com.fleencorp.feen.model.response.mfa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnableOrDisableMfaResponse {

  @Builder.Default
  @JsonProperty("message")
  private String message = "Mfa status updated successfully";

  public static EnableOrDisableMfaResponse of() {
    return new EnableOrDisableMfaResponse();
  }
}
