package com.fleencorp.feen.model.request.mfa;

import com.fleencorp.feen.constant.security.verification.VerificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.constant.message.CommonMessageDetails.MFA_SETUP;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class MfaSetupVerificationRequest extends MfaVerificationRequest {

  public static MfaSetupVerificationRequest of(String verificationCode, String firstName, String lastName, String emailAddress,
                                          String phoneNumber, VerificationType verificationType) {
    return MfaSetupVerificationRequest.builder()
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
    return MFA_SETUP.getTemplateName();
  }

  @Override
  public String getMessageTitle() {
    return MFA_SETUP.getMessageTitle();
  }
}
