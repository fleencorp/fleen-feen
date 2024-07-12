package com.fleencorp.feen.model.request.auth;

import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.model.request.verification.ResendVerificationCodeRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ResendSignUpVerificationCodeRequest extends ResendVerificationCodeRequest {

  public static ResendSignUpVerificationCodeRequest of(String verificationCode, String firstName, String lastName, String emailAddress,
      String phoneNumber, VerificationType verificationType) {
    return ResendSignUpVerificationCodeRequest.builder()
        .verificationCode(verificationCode)
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(emailAddress)
        .phoneNumber(phoneNumber)
        .verificationType(verificationType)
        .build();
  }

}
