package com.fleencorp.feen.model.request.auth;

import com.fleencorp.feen.constant.security.profile.ProfileVerificationStatus;
import com.fleencorp.feen.model.request.message.MessageRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.fleencorp.feen.constant.message.CommonMessageDetails.SIGN_UP_COMPLETED;
import static com.fleencorp.feen.constant.message.MessageTemplateField.PROFILE_VERIFICATION_STATUS;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompletedUserSignUpRequest extends MessageRequest {

  private ProfileVerificationStatus profileVerificationStatus;

  public static CompletedUserSignUpRequest of(String firstName, String lastName, String emailAddress,
      String phoneNumber, ProfileVerificationStatus profileVerificationStatus) {
    return CompletedUserSignUpRequest.builder()
        .firstName(firstName)
        .lastName(lastName)
        .emailAddress(emailAddress)
        .phoneNumber(phoneNumber)
        .profileVerificationStatus(profileVerificationStatus)
        .build();
  }

  public Map<String, Object> toMessagePayload() {
    Map<String, Object> payload = new HashMap<>(super.toMessagePayload());
    payload.put(PROFILE_VERIFICATION_STATUS.getValue(), profileVerificationStatus);

    return payload;
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