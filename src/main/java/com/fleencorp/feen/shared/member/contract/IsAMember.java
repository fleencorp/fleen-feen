package com.fleencorp.feen.shared.member.contract;

import com.fleencorp.feen.mfa.constant.MfaType;
import com.fleencorp.feen.user.constant.profile.ProfileStatus;
import com.fleencorp.feen.user.constant.profile.ProfileVerificationStatus;

public interface IsAMember {

  Long getMemberId();

  default String getUsername() {
    return null;
  }

  default String getEmailAddress() {
    return null;
  }

  default String getFirstName() {
    return null;
  }

  default String getLastName() {
    return null;
  }

  default String getFullName() {
    return null;
  }

  default String getPhoneNumber() {
    return null;
  }

  default ProfileStatus getProfileStatus() {
    return null;
  }

  default ProfileVerificationStatus getVerificationStatus() {
    return null;
  }

  default String getTimezone() {
    return null;
  }

  default boolean isMfaEnabled() {
    return false;
  }

  default MfaType getMfaType() {
    return null;
  }

  default String getCountry() {
    return null;
  }

  default String getPassword() {
    return null;
  }

  void setMemberId(Long memberId);

  void setUsername(String username);

  void setEmailAddress(String emailAddress);

  void setFirstName(String firstName);

  void setLastName(String lastName);

  void setPhoneNumber(String phoneNumber);

  void setProfileStatus(ProfileStatus profileStatus);

  void setVerificationStatus(ProfileVerificationStatus profileVerificationStatus);

  void setTimezone(String timezone);

  void setMfaEnabled(boolean mfaEnabled);

  void setMfaType(MfaType mfaType);

  void setCountry(String country);

  void setPassword(String password);
}
