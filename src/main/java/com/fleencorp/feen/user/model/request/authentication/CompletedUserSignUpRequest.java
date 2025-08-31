package com.fleencorp.feen.user.model.request.authentication;

import com.fleencorp.feen.chat.space.model.request.external.message.MessageRequest;
import com.fleencorp.feen.common.constant.message.MessageRequestType;
import com.fleencorp.feen.user.constant.profile.ProfileVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.fleencorp.feen.common.constant.message.CommonMessageDetails.SIGN_UP_COMPLETED;
import static com.fleencorp.feen.common.constant.message.MessageTemplateField.PROFILE_VERIFICATION_STATUS;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompletedUserSignUpRequest extends MessageRequest {

  private ProfileVerificationStatus profileVerificationStatus;

  public static CompletedUserSignUpRequest of(
      final String firstName,
      final String lastName,
      final String emailAddress,
      final String phoneNumber,
      final ProfileVerificationStatus profileVerificationStatus) {
    final CompletedUserSignUpRequest request = new CompletedUserSignUpRequest();
    request.setFirstName(firstName);
    request.setLastName(lastName);
    request.setEmailAddress(emailAddress);
    request.setPhoneNumber(phoneNumber);
    request.setProfileVerificationStatus(profileVerificationStatus);

    return request;
  }

  @Override
  public Map<String, Object> toMessagePayload() {
    final Map<String, Object> payload = new HashMap<>(super.toMessagePayload());
    payload.put(PROFILE_VERIFICATION_STATUS.getValue(), profileVerificationStatus);

    return payload;
  }

  @Override
  public MessageRequestType getRequestType() {
    return MessageRequestType.COMPLETED_USER_SIGNUP;
  }

  @Override
  public String getTemplateName() {
    return SIGN_UP_COMPLETED.getTemplateName();
  }

  @Override
  public String getMessageTitle() {
    return SIGN_UP_COMPLETED.getMessageTitle();
  }


}
