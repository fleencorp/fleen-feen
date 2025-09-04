package com.fleencorp.feen.shared.member.contract;

import com.fleencorp.feen.mfa.constant.MfaType;
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
    return new IsAMember() {

      @Override
      public Long getMemberId() {
        return memberId;
      }

      @Override
      public String getUsername() {
        return "";
      }

      @Override
      public String getEmailAddress() {
        return "";
      }

      @Override
      public String getFirstName() {
        return "";
      }

      @Override
      public String getLastName() {
        return "";
      }

      @Override
      public String getFullName() {
        return "";
      }

      @Override
      public String getPhoneNumber() {
        return "";
      }

      @Override
      public String getProfilePhoto() {
        return "";
      }

      @Override
      public ProfileStatus getProfileStatus() {
        return null;
      }

      @Override
      public ProfileVerificationStatus getVerificationStatus() {
        return null;
      }

      @Override
      public String getTimezone() {
        return "";
      }

      @Override
      public boolean isMfaEnabled() {
        return false;
      }

      @Override
      public MfaType getMfaType() {
        return null;
      }

      @Override
      public String getCountry() {
        return "";
      }

      @Override
      public String getPassword() {
        return "";
      }

      @Override
      public void setMemberId(Long memberId) {

      }

      @Override
      public void setUsername(String username) {

      }

      @Override
      public void setEmailAddress(String emailAddress) {

      }

      @Override
      public void setFirstName(String firstName) {

      }

      @Override
      public void setLastName(String lastName) {

      }

      @Override
      public void setPhoneNumber(String phoneNumber) {

      }

      @Override
      public void setProfileStatus(ProfileStatus profileStatus) {

      }

      @Override
      public void setVerificationStatus(ProfileVerificationStatus profileVerificationStatus) {

      }

      @Override
      public void setTimezone(String timezone) {

      }

      @Override
      public void setMfaEnabled(boolean mfaEnabled) {

      }

      @Override
      public void setMfaType(MfaType mfaType) {

      }

      @Override
      public void setCountry(String country) {

      }

      @Override
      public void setPassword(String password) {

      }
    };
  }
}
