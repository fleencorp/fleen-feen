package com.fleencorp.feen.model.request.mfa;

import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.model.request.verification.ResendVerificationCodeRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MfaVerificationRequest extends ResendVerificationCodeRequest {

  private VerificationType verificationType;

  public static MfaVerificationRequest of(String verificationCode, String firstName, String lastName, String emailAddress,
      String phoneNumber, VerificationType verificationType) {
    return MfaVerificationRequest.builder()
        .verificationCode(verificationCode)
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(emailAddress)
        .phoneNumber(phoneNumber)
        .verificationType(verificationType)
        .build();
  }
}
