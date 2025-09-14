package com.fleencorp.feen.shared.member.contract;

import com.fleencorp.feen.mfa.constant.MfaType;
import com.fleencorp.feen.shared.member.model.MemberData;
import com.fleencorp.feen.user.constant.profile.ProfileStatus;
import com.fleencorp.feen.user.constant.profile.ProfileVerificationStatus;

public interface IsAMember {

  Long getMemberId();

  String getUsername();

  String getEmailAddress();

  String getFirstName();

  String getLastName();

  String getFullName();

  String getPhoneNumber();

  String getProfilePhoto();

  ProfileStatus getProfileStatus();

  ProfileVerificationStatus getVerificationStatus();

  String getTimezone();

  boolean isMfaEnabled();

  MfaType getMfaType();

  String getCountry();

  String getPassword();

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

  static IsAMember defaultInstance(final Long memberId) {
    final MemberData memberData = new MemberData();
    memberData.setMemberId(memberId);

    return memberData;
  }
}
