package com.fleencorp.feen.mfa.model.request;

import com.fleencorp.feen.common.constant.message.MessageRequestType;
import com.fleencorp.feen.user.model.request.authentication.SendVerificationCodeRequest;
import com.fleencorp.feen.verification.constant.VerificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.feen.common.constant.message.CommonMessageDetails.MFA_VERIFICATION;

@Getter
@Setter
@NoArgsConstructor
public class MfaVerificationRequest extends SendVerificationCodeRequest {

  public static MfaVerificationRequest of(
      final String verificationCode,
      final String firstName,
      final String lastName,
      final String emailAddress,
      final String phoneNumber,
      final VerificationType verificationType) {
    final MfaVerificationRequest request = new MfaVerificationRequest();
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
    return MessageRequestType.MFA_VERIFICATION;
  }

  @Override
  public String getTemplateName() {
    return MFA_VERIFICATION.getTemplateName();
  }

  @Override
  public String getMessageTitle() {
    return MFA_VERIFICATION.getMessageTitle();
  }
}
