package com.fleencorp.feen.model.response.security.mfa;

import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ResendMfaVerificationCodeResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "resend.mfa.verification.code";
  }

  public static ResendMfaVerificationCodeResponse of() {
    return ResendMfaVerificationCodeResponse.builder()
        .build();
  }
}
