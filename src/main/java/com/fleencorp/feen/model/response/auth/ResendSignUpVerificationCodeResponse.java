package com.fleencorp.feen.model.response.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ResendSignUpVerificationCodeResponse extends FleenFeenResponse {

  @Builder.Default
  @JsonProperty("message")
  protected String message = "Verification code sent successfully";

  public static ResendSignUpVerificationCodeResponse of() {
    return ResendSignUpVerificationCodeResponse.builder()
        .build();
  }
}
