package com.fleencorp.feen.model.request.auth;

import com.fleencorp.feen.constant.security.profile.ProfileVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompletedUserSignUpRequest extends ProfileRequest {

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
}
