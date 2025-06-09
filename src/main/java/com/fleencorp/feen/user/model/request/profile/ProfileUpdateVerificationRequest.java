package com.fleencorp.feen.user.model.request.profile;

import com.fleencorp.feen.constant.message.MessageRequestType;
import com.fleencorp.feen.user.constant.verification.VerificationType;
import com.fleencorp.feen.user.model.request.authentication.SendVerificationCodeRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.constant.message.CommonMessageDetails.PROFILE_UPDATE_VERIFICATION;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateVerificationRequest extends SendVerificationCodeRequest {

  public static ProfileUpdateVerificationRequest of(final String verificationCode, final String firstName, final String lastName, final String emailAddress,
      final String phoneNumber, final VerificationType verificationType) {
    return ProfileUpdateVerificationRequest.builder()
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
