package com.fleencorp.feen.model.domain.user;

import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.constant.security.profile.ProfileVerificationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

  @DisplayName("Non empty member")
  @Test
  void create_member_with_fields() {
    Member member = new Member();
    member.setMemberId(1L);
    member.setFirstName("test");
    member.setLastName("doe");
    member.setEmailAddress("test@email.com");
    member.setPassword("password");
    member.setPhoneNumber("01111234");
    member.setProfilePhotoUrl("profile_photo_url");
    member.setCountry("Kenya");
    member.setEmailAddressVerified(false);
    member.setPhoneNumberVerified(false);
    member.setMfaEnabled(false);
    member.setMfaSecret("secret");
    member.setMfaType(MfaType.NONE);
    member.setVerificationStatus(ProfileVerificationStatus.PENDING);
    member.setProfileStatus(ProfileStatus.INACTIVE);
    member.setRoles(Set.of());

    assertEquals(1L, member.getMemberId());
    assertEquals("test", member.getFirstName());
    assertEquals("doe", member.getLastName());
    assertEquals("test@email.com", member.getEmailAddress());
    assertEquals("Kenya", member.getCountry());
    assertEquals("secret", member.getMfaSecret());
  }

  @DisplayName("Non-null member with empty fields")
  @Test
  void create_nonnull_member() {
    final Member member = new Member();
    assertNotNull(member);
  }

  @DisplayName("Null member")
  @Test
  void create_null_member() {
    final Member member = null;
    assertNull(member);
  }

  @DisplayName("Empty Member")
  @Test
  void create_empty_member() {
    final Member member = new Member();
    member.setMemberId(null);
    assertNull(member.getEmailAddress());
    assertNull(member.getMemberId());
    assertNull(member.getMfaSecret());
  }

  @DisplayName("Members are equal")
  @Test
  void ensure_same_members_are_equal() {
//    MEMBER1
    Member member1 = new Member();
    member1.setMemberId(1L);
    member1.setFirstName("test");
    member1.setLastName("doe");
    member1.setEmailAddress("test@email.com");
    member1.setPassword("password");
    member1.setPhoneNumber("01111234");
    member1.setProfilePhotoUrl("profile_photo_url");
    member1.setCountry("Kenya");
    member1.setEmailAddressVerified(false);
    member1.setPhoneNumberVerified(false);
    member1.setMfaEnabled(false);
    member1.setMfaSecret("secret");
    member1.setMfaType(MfaType.NONE);
    member1.setVerificationStatus(ProfileVerificationStatus.PENDING);
    member1.setProfileStatus(ProfileStatus.INACTIVE);
    member1.setRoles(Set.of());
//    MEMBER2
    Member member2 = new Member();
    member2.setMemberId(1L);
    member2.setFirstName("test");
    member2.setLastName("doe");
    member2.setEmailAddress("test@email.com");
    member2.setPassword("password");
    member2.setPhoneNumber("01111234");
    member2.setProfilePhotoUrl("profile_photo_url");
    member2.setCountry("Kenya");
    member2.setEmailAddressVerified(false);
    member2.setPhoneNumberVerified(false);
    member2.setMfaEnabled(false);
    member2.setMfaSecret("secret");
    member2.setMfaType(MfaType.NONE);
    member2.setVerificationStatus(ProfileVerificationStatus.PENDING);
    member2.setProfileStatus(ProfileStatus.INACTIVE);
    member2.setRoles(Set.of());

    assertEquals(member1.getMemberId(), member2.getMemberId());
    assertEquals(member1.getFirstName(), member2.getFirstName());
    assertEquals(member1.getLastName(), member2.getLastName());
    assertEquals(member1.getEmailAddress(), member2.getEmailAddress());
    assertEquals(member1.getPassword(), member2.getPassword());
    assertEquals(member1.getPhoneNumber(), member2.getPhoneNumber());
    assertEquals(member1.getProfilePhotoUrl(), member2.getProfilePhotoUrl());
    assertEquals(member1.getCountry(), member2.getCountry());
    assertEquals(member1.getVerificationStatus(), member2.getVerificationStatus());
    assertEquals(member1.getProfileStatus(), member2.getProfileStatus());
  }

  @DisplayName("Members are not equal")
  @Test
  void ensure_members_are_not_equal() {
//    MEMBER1
    Member member1 = new Member();
    member1.setMemberId(1L);
    member1.setFirstName("test1");
    member1.setLastName("doe1");
    member1.setEmailAddress("test1@email.com");
    member1.setPassword("password1");
    member1.setPhoneNumber("011112341");
    member1.setProfilePhotoUrl("profile_photo_url1");
    member1.setCountry("Kenya1");
    member1.setEmailAddressVerified(false);
    member1.setPhoneNumberVerified(false);
    member1.setMfaEnabled(false);
    member1.setMfaSecret("secret");
    member1.setMfaType(MfaType.NONE);
    member1.setVerificationStatus(ProfileVerificationStatus.PENDING);
    member1.setProfileStatus(ProfileStatus.INACTIVE);
    member1.setRoles(Set.of());
//    MEMBER2
    Member member2 = new Member();
    member2.setMemberId(2L);
    member2.setFirstName("test");
    member2.setLastName("doe");
    member2.setEmailAddress("test@email.com");
    member2.setPassword("password");
    member2.setPhoneNumber("01111234");
    member2.setProfilePhotoUrl("profile_photo_url");
    member2.setCountry("Kenya");
    member2.setEmailAddressVerified(false);
    member2.setPhoneNumberVerified(false);
    member2.setMfaEnabled(false);
    member2.setMfaSecret("secret");
    member2.setMfaType(MfaType.NONE);
    member2.setVerificationStatus(ProfileVerificationStatus.PENDING);
    member2.setProfileStatus(ProfileStatus.INACTIVE);
    member2.setRoles(Set.of());

    assertNotEquals(member1.getMemberId(), member2.getMemberId());
    assertNotEquals(member1.getFirstName(), member2.getFirstName());
    assertNotEquals(member1.getLastName(), member2.getLastName());
    assertNotEquals(member1.getEmailAddress(), member2.getEmailAddress());
    assertNotEquals(member1.getPassword(), member2.getPassword());
    assertNotEquals(member1.getPhoneNumber(), member2.getPhoneNumber());
    assertNotEquals(member1.getProfilePhotoUrl(), member2.getProfilePhotoUrl());
    assertNotEquals(member1.getCountry(), member2.getCountry());
  }
}