package com.fleencorp.feen.model.response.security.mfa;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResendMfaVerificationCodeResponse extends FleenFeenResponse {

  @Builder.Default
  @JsonProperty("message")
  protected String message = "Verification code sent successfully";

  public static ResendMfaVerificationCodeResponse of() {
    return ResendMfaVerificationCodeResponse.builder()
        .build();
  }
}
