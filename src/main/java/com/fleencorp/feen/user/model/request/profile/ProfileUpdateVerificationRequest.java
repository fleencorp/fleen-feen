package com.fleencorp.feen.user.model.request.profile;

import com.fleencorp.feen.common.constant.message.MessageRequestType;
import com.fleencorp.feen.user.model.request.authentication.SendVerificationCodeRequest;
import com.fleencorp.feen.verification.constant.VerificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.feen.common.constant.message.CommonMessageDetails.PROFILE_UPDATE_VERIFICATION;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateVerificationRequest extends SendVerificationCodeRequest {

  public static ProfileUpdateVerificationRequest of(
      final String verificationCode,
      final String firstName,
      final String lastName,
      final String emailAddress,
      final String phoneNumber,
      final VerificationType verificationType) {
    final ProfileUpdateVerificationRequest request = new ProfileUpdateVerificationRequest();
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
    return MessageRequestType.PROFILE_UPDATE_VERIFICATION;
  }

  @Override
  public String getTemplateName() {
    return PROFILE_UPDATE_VERIFICATION.getTemplateName();
  }

  @Override
  public String getMessageTitle() {
    return PROFILE_UPDATE_VERIFICATION.getMessageTitle();
  }
}
