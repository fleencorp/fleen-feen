package com.fleencorp.feen.model.request.auth;

import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.model.request.verification.SendVerificationCodeRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.constant.message.CommonMessageDetails.FORGOT_PASSWORD;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ForgotPasswordRequest extends SendVerificationCodeRequest {

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

  @Override
  public String getTemplateName() {
    return FORGOT_PASSWORD.getTemplateName();
  }

  @Override
  public String getMessageTitle() {
    return FORGOT_PASSWORD.getMessageTitle();
  }
}
