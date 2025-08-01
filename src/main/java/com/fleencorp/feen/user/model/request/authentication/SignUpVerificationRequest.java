package com.fleencorp.feen.user.model.request.authentication;


import com.fleencorp.feen.common.constant.message.MessageRequestType;
import com.fleencorp.feen.verification.constant.VerificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.common.constant.message.CommonMessageDetails.SIGN_UP_VERIFICATION;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class SignUpVerificationRequest extends SendVerificationCodeRequest {

  public static SignUpVerificationRequest of(final String verificationCode, final String firstName, final String lastName, final String emailAddress,
      final String phoneNumber, final VerificationType verificationType) {
    return SignUpVerificationRequest.builder()
        .verificationCode(verificationCode)
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(emailAddress)
        .phoneNumber(phoneNumber)
        .verificationType(verificationType)
        .build();
  }

  @Override
  public MessageRequestType getRequestType() {
    return MessageRequestType.SIGNUP_VERIFICATION;
  }

  @Override
  public String getTemplateName() {
    return SIGN_UP_VERIFICATION.getTemplateName();
  }

  @Override
  public String getMessageTitle() {
    return SIGN_UP_VERIFICATION.getMessageTitle();
  }
}
