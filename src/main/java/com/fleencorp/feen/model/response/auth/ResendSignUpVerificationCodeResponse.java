package com.fleencorp.feen.model.response.auth;

import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResendSignUpVerificationCodeResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "resend.sign.up.verification.code";
  }

  public static ResendSignUpVerificationCodeResponse of() {
    return new ResendSignUpVerificationCodeResponse();
  }
}
