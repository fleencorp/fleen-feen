package com.fleencorp.feen.model.request.auth;

import com.fleencorp.feen.constant.security.verification.VerificationType;
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
public class ForgotPasswordRequest extends ProfileRequest {

  private String verificationCode;
  private VerificationType verificationType;

  public static ForgotPasswordRequest of(String verificationCode, String firstName, String lastName, String emailAddress,
      String phoneNumber, VerificationType verificationType) {
    return ForgotPasswordRequest.builder()
        .verificationCode(verificationCode)
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(emailAddress)
        .phoneNumber(phoneNumber)
        .verificationType(verificationType)
        .build();
  }
}
