package com.fleencorp.feen.shared.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fleencorp.feen.user.constant.profile.ProfileStatus;
import com.fleencorp.feen.user.constant.profile.ProfileVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenPayload {

  private Long userId;
  private String username;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String country;
  private String timezone;
  private String sub;
  private String status;
  private String verificationStatus;
  private String[] authorities;
  private String profilePhoto;

  public ProfileStatus getProfileStatus() {
    return ProfileStatus.of(status);
  }

  public ProfileVerificationStatus getProfileVerificationStatus() {
    return ProfileVerificationStatus.of(verificationStatus);
  }
}
