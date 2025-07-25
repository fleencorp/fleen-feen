package com.fleencorp.feen.mfa.model.response;

import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResendMfaVerificationCodeResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "resend.mfa.verification.code";
  }

  public static ResendMfaVerificationCodeResponse of() {
    return new ResendMfaVerificationCodeResponse();
  }
}
