package com.fleencorp.feen.model.request.mfa;

import com.fleencorp.feen.model.request.verification.ResendVerificationCodeRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ResendMfaVerificationCodeRequest extends ResendVerificationCodeRequest {

  public static ResendMfaVerificationCodeRequest of(String verificationCode, String firstName, String lastName, String emailAddress,
      String phoneNumber) {
    return ResendMfaVerificationCodeRequest.builder()
        .verificationCode(verificationCode)
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(emailAddress)
        .phoneNumber(phoneNumber)
        .build();
  }
}
