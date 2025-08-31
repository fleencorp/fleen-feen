package com.fleencorp.feen.user.model.request.authentication;

import com.fleencorp.feen.common.constant.message.MessageRequestType;
import com.fleencorp.feen.verification.constant.VerificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.feen.common.constant.message.CommonMessageDetails.FORGOT_PASSWORD;

@Getter
@Setter
@NoArgsConstructor
public class ForgotPasswordRequest extends SendVerificationCodeRequest {

  public static ForgotPasswordRequest of(
      final String verificationCode,
      final String firstName,
      final String lastName,
      final String emailAddress,
      final String phoneNumber,
      final VerificationType verificationType) {
    final ForgotPasswordRequest request = new ForgotPasswordRequest();
    request.setVerificationCode(verificationCode);
    request.setFirstName(firstName);
    request.setLastName(lastName);
    request.setEmailAddress(emailAddress);
    request.setPhoneNumber(phoneNumber);
    request.setVerificationType(verificationType);

    return request;
  }

  @Override
  public MessageRequestType getRequestType() {
    return MessageRequestType.FORGOT_PASSWORD;
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
