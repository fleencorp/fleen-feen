package com.fleencorp.feen.model.response.authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResendSignUpVerificationCodeResponse extends LocalizedResponse {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "resend.sign.up.verification.code";
  }

  public static ResendSignUpVerificationCodeResponse of() {
    return new ResendSignUpVerificationCodeResponse();
  }
}
