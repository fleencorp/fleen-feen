package com.fleencorp.feen.model.response.auth;

import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ResendSignUpVerificationCodeResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "resend.sign.up.verification.code";
  }

  public static ResendSignUpVerificationCodeResponse of() {
    return ResendSignUpVerificationCodeResponse.builder()
        .build();
  }
}
