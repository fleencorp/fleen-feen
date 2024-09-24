package com.fleencorp.feen.model.domain.user;

import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.constant.security.profile.ProfileVerificationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

  @DisplayName("Create empty member")
  @Test
  void create_empty_member() {
    // GIVEN
    final Member member = new Member();

    // ASSERT
    assertNotNull(member);
  }

  @DisplayName("Create a member null")
  @Test
  void create_null_member() {
    // GIVEN
    final Member member = null;

    // ASSERT
    assertNull(member);
  }

  @DisplayName("Ensure Member Id is not null")
  @Test
  void ensure_member_id_is_not_null() {
    // GIVEN
    Member member = new Member();
    member.setMemberId(1L);

    // ASSERT
    assertNotNull(member.getMemberId());
  }
  @DisplayName("Ensure Member Id is null")
  @Test
  void ensure_member_id_is_null() {
    // GIVEN
    Member member = new Member();
    member.setMemberId(null);

    // ASSERT
    assertNull(member.getMemberId());
  }
  @DisplayName("Ensure Member Ids is equal")
  @Test
  void ensure_member_ids_are_equal() {
    // GIVEN
    Member member1 = new Member();
    member1.setMemberId(1L);

    Member member2 = new Member();
    member2.setMemberId(1L);

    // ASSERT
    assertEquals(member1.getMemberId(), member2.getMemberId());
  }
  @DisplayName("Ensure Member Ids are not equal")
  @Test
  void ensure_member_ids_are_not_equal() {
    // GIVEN
    Member member1 = new Member();
    member1.setMemberId(1L);

    Member member2 = new Member();
    member2.setMemberId(2L);

    // ASSERT
    assertNotEquals(member1.getMemberId(), member2.getMemberId());
  }

  @DisplayName("Ensure first name is not null")
  @Test
  void ensure_first_name_is_not_null() {
    // GIVEN
    Member member = new Member();
    member.setFirstName("John");

    // ASSERT
    assertNotNull(member.getFirstName());
  }

  @DisplayName("Ensure first name is null")
  @Test
  void ensure_first_name_is_null() {
    // GIVEN
    Member member = new Member();
    member.setFirstName(null);

    // ASSERT
    assertNull(member.getFirstName());
  }

  @DisplayName("Ensure first names are equal")
  @Test
  void ensure_first_names_are_equal() {
    // GIVEN
    final String firstName = "John";
    Member member = new Member();
    member.setFirstName("John");

    // ASSERT
    assertEquals(member.getFirstName(), firstName);
  }

  @DisplayName("Ensure first name are not equal")
  @Test
  void ensure_first_names_are_not_equal() {
    // GIVEN
    String firstName = "John";
    Member member = new Member();
    member.setFirstName("Conor");

    // ASSERT
    assertNotEquals(member.getFirstName(), firstName);
  }

  @DisplayName("Ensure last name is not null")
  @Test
  void ensure_last_name_is_not_null() {
    // GIVEN
    Member member = new Member();
    member.setLastName("test");

    // ASSERT
    assertNotNull(member.getLastName());
  }
  @DisplayName("Ensure last name is null")
  @Test
  void ensure_lastname_is_null() {
//    GIVEN
    Member member = new Member();
    member.setLastName(null);
//    ASSERT
    assertNull(member.getLastName());
  }
  @DisplayName("Ensure last name is equal")
  @Test
  void ensure_lastname_is_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setLastName("test1");

    Member member2 = new Member();
    member2.setLastName("test1");
//    ASSERT
    assertEquals(member1.getLastName(), member2.getLastName());
  }
  @DisplayName("Ensure last name is not equal")
  @Test
  void ensure_lastname_is_not_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setLastName("test1");

    Member member2 = new Member();
    member2.setLastName("test");
//    ASSERT
    assertNotEquals(member1.getLastName(), member2.getLastName());
  }

  @DisplayName("Ensure email address is not null")
  @Test
  void ensure_emailaddress_is_not_null() {
//    GIVEN
    Member member = new Member();
    member.setEmailAddress("test@gmail.com");
//    ASSERT
    assertNotNull(member.getEmailAddress());
  }
  @DisplayName("Ensure email address is null")
  @Test
  void ensure_emailaddress_is_null() {
//    GIVEN
    Member member = new Member();
    member.setEmailAddress(null);
//    ASSERT
    assertNull(member.getEmailAddress());
  }
  @DisplayName("Ensure email address is equal")
  @Test
  void ensure_emailaddress_is_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setEmailAddress("test1@gmail.com");

    Member member2 = new Member();
    member2.setEmailAddress("test1@gmail.com");
//    ASSERT
    assertEquals(member1.getEmailAddress(), member2.getEmailAddress());
  }
  @DisplayName("Ensure email address is not equal")
  @Test
  void ensure_emailaddress_is_not_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setEmailAddress("test1@gmail.com");

    Member member2 = new Member();
    member2.setEmailAddress("test@gmail.com");
//    ASSERT
    assertNotEquals(member1.getEmailAddress(), member2.getEmailAddress());
  }

  @DisplayName("Ensure phone number is not null")
  @Test
  void ensure_phonenumber_is_not_null() {
//    GIVEN
    Member member = new Member();
    member.setPhoneNumber("123456789");
//    ASSERT
    assertNotNull(member.getPhoneNumber());
  }
  @DisplayName("Ensure phone number is null")
  @Test
  void ensure_phonenumber_is_null() {
//    GIVEN
    Member member = new Member();
    member.setPhoneNumber(null);
//    ASSERT
    assertNull(member.getPhoneNumber());
  }
  @DisplayName("Ensure phone number is equal")
  @Test
  void ensure_phonenumber_is_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setPhoneNumber("123456789");

    Member member2 = new Member();
    member2.setPhoneNumber("123456789");
//    ASSERT
    assertEquals(member1.getPhoneNumber(), member2.getPhoneNumber());
  }
  @DisplayName("Ensure phone number is not equal")
  @Test
  void ensure_phonenumber_is_not_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setPhoneNumber("123456789");

    Member member2 = new Member();
    member2.setPhoneNumber("123456799");
//    ASSERT
    assertNotEquals(member1.getPhoneNumber(), member2.getPhoneNumber());
  }

  @DisplayName("Ensure password is not null")
  @Test
  void ensure_password_is_not_null() {
//    GIVEN
    Member member = new Member();
    member.setPassword("123456");
//    ASSERT
    assertNotNull(member.getPassword());
  }
  @DisplayName("Ensure password is null")
  @Test
  void ensure_password_is_null() {
//    GIVEN
    Member member = new Member();
    member.setPassword(null);
//    ASSERT
    assertNull(member.getPassword());
  }
  @DisplayName("Ensure password is equal")
  @Test
  void ensure_password_is_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setPassword("123456");

    Member member2 = new Member();
    member2.setPassword("123456");
//    ASSERT
    assertEquals(member1.getPassword(), member2.getPassword());
  }
  @DisplayName("Ensure password is not equal")
  @Test
  void ensure_password_is_not_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setPassword("123456");

    Member member2 = new Member();
    member2.setPassword("12345");
//    ASSERT
    assertNotEquals(member1.getPassword(), member2.getPassword());
  }

  @DisplayName("Ensure password is not null")
  @Test
  void ensure_profilephotourl_is_not_null() {
//    GIVEN
    Member member = new Member();
    member.setProfilePhotoUrl("profilephotourl");
//    ASSERT
    assertNotNull(member.getProfilePhotoUrl());
  }
  @DisplayName("Ensure profile photo url is null")
  @Test
  void ensure_profilephotourl_is_null() {
//    GIVEN
    Member member = new Member();
    member.setProfilePhotoUrl(null);
//    ASSERT
    assertNull(member.getProfilePhotoUrl());
  }
  @DisplayName("Ensure profilephotourl is equal")
  @Test
  void ensure_profilephotourl_is_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setProfilePhotoUrl("profilephotourl");

    Member member2 = new Member();
    member2.setProfilePhotoUrl("profilephotourl");
//    ASSERT
    assertEquals(member1.getProfilePhotoUrl(), member2.getProfilePhotoUrl());
  }
  @DisplayName("Ensure profilephotourl is not equal")
  @Test
  void ensure_profilephotourl_is_not_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setProfilePhotoUrl("profilephotourl");

    Member member2 = new Member();
    member2.setProfilePhotoUrl("profilephotourl1");
//    ASSERT
    assertNotEquals(member1.getProfilePhotoUrl(), member2.getProfilePhotoUrl());
  }

  @DisplayName("Ensure country is not null")
  @Test
  void ensure_country_is_not_null() {
//    GIVEN
    Member member = new Member();
    member.setCountry("Kenya");
//    ASSERT
    assertNotNull(member.getCountry());
  }
  @DisplayName("Ensure country is null")
  @Test
  void ensure_country_is_null() {
//    GIVEN
    Member member = new Member();
    member.setCountry(null);
//    ASSERT
    assertNull(member.getCountry());
  }
  @DisplayName("Ensure country is equal")
  @Test
  void ensure_country_is_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setCountry("Kenya");

    Member member2 = new Member();
    member2.setCountry("Kenya");
//    ASSERT
    assertEquals(member1.getCountry(), member2.getCountry());
  }
  @DisplayName("Ensure country is not equal")
  @Test
  void ensure_country_is_not_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setCountry("Kenya");

    Member member2 = new Member();
    member2.setCountry("Nigeria");
//    ASSERT
    assertNotEquals(member1.getCountry(), member2.getCountry());
  }

  @DisplayName("Ensure MfaSecret is not null")
  @Test
  void ensure_mfasecret_is_not_null() {
//    GIVEN
    Member member = new Member();
    member.setMfaSecret("secret");
//    ASSERT
    assertNotNull(member.getMfaSecret());
  }
  @DisplayName("Ensure MfaSecret is null")
  @Test
  void ensure_mfasecret_is_null() {
//    GIVEN
    Member member = new Member();
    member.setMfaSecret(null);
//    ASSERT
    assertNull(member.getMfaSecret());
  }
  @DisplayName("Ensure MfaSecret is equal")
  @Test
  void ensure_mfasecret_is_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setMfaSecret("secret");

    Member member2 = new Member();
    member2.setMfaSecret("secret");
//    ASSERT
    assertEquals(member1.getMfaSecret(), member2.getMfaSecret());
  }
  @DisplayName("Ensure mfasecret is not equal")
  @Test
  void ensure_mfasecret_is_not_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setMfaSecret("secret");

    Member member2 = new Member();
    member2.setMfaSecret("secret1");
//    ASSERT
    assertNotEquals(member1.getMfaSecret(), member2.getMfaSecret());
  }

  @DisplayName("Ensure MfaType is not null")
  @Test
  void ensure_mfatype_is_not_null() {
//    GIVEN
    Member member = new Member();
    member.setMfaType(MfaType.NONE);
//    ASSERT
    assertNotNull(member.getMfaType());
  }

  @DisplayName("Ensure MfaType is null")
  @Test
  void ensure_mfatype_is_null() {
//    GIVEN
    Member member = new Member();
    member.setMfaType(null);
//    ASSERT
    assertNull(member.getMfaType());
  }
  @DisplayName("Ensure MfaType is equal")
  @Test
  void ensure_mfatype_is_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setMfaType(MfaType.NONE);

    Member member2 = new Member();
    member2.setMfaType(MfaType.NONE);
//    ASSERT
    assertEquals(member1.getMfaType(), member2.getMfaType());
  }
  @DisplayName("Ensure mfaType is not equal")
  @Test
  void ensure_mfatype_is_not_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setMfaType(MfaType.NONE);

    Member member2 = new Member();
    member2.setMfaType(MfaType.EMAIL);
//    ASSERT
    assertNotEquals(member1.getMfaType(), member2.getMfaType());
  }

  @DisplayName("Ensure VerificationStatus is not null")
  @Test
  void ensure_verificationstatus_is_not_null() {
//    GIVEN
    Member member = new Member();
    member.setVerificationStatus(ProfileVerificationStatus.PENDING);
//    ASSERT
    assertNotNull(member.getVerificationStatus());
  }

  @DisplayName("Ensure VerificationStatus is null")
  @Test
  void ensure_verificationstatus_is_null() {
//    GIVEN
    Member member = new Member();
    member.setVerificationStatus(null);
//    ASSERT
    assertNull(member.getVerificationStatus());
  }
  @DisplayName("Ensure VerificationStatus is equal")
  @Test
  void ensure_verificationstatus_is_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setVerificationStatus(ProfileVerificationStatus.PENDING);

    Member member2 = new Member();
    member2.setVerificationStatus(ProfileVerificationStatus.PENDING);
//    ASSERT
    assertEquals(member1.getVerificationStatus(), member2.getVerificationStatus());
  }
  @DisplayName("Ensure VerificationStatus is not equal")
  @Test
  void ensure_verificationstatus_is_not_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setVerificationStatus(ProfileVerificationStatus.PENDING);

    Member member2 = new Member();
    member2.setVerificationStatus(ProfileVerificationStatus.APPROVED);
//    ASSERT
    assertNotEquals(member1.getVerificationStatus(), member2.getVerificationStatus());
  }

  @DisplayName("Ensure ProfileStatus is not null")
  @Test
  void ensure_profilestatus_is_not_null() {
//    GIVEN
    Member member = new Member();
    member.setProfileStatus(ProfileStatus.INACTIVE);
//    ASSERT
    assertNotNull(member.getProfileStatus());
  }

  @DisplayName("Ensure ProfileStatus is null")
  @Test
  void ensure_profilestatus_is_null() {
//    GIVEN
    Member member = new Member();
    member.setProfileStatus(null);
//    ASSERT
    assertNull(member.getProfileStatus());
  }
  @DisplayName("Ensure ProfileStatus is equal")
  @Test
  void ensure_profilestatus_is_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setProfileStatus(ProfileStatus.INACTIVE);

    Member member2 = new Member();
    member2.setProfileStatus(ProfileStatus.INACTIVE);
//    ASSERT
    assertEquals(member1.getProfileStatus(), member2.getProfileStatus());
  }
  @DisplayName("Ensure ProfileStatus is not equal")
  @Test
  void ensure_profilestatus_is_not_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setProfileStatus(ProfileStatus.INACTIVE);

    Member member2 = new Member();
    member2.setProfileStatus(ProfileStatus.ACTIVE);
//    ASSERT
    assertNotEquals(member1.getProfileStatus(), member2.getProfileStatus());
  }

  @DisplayName("Ensure Roles is not null")
  @Test
  void ensure_roles_is_not_null() {
//    GIVEN
    Member member = new Member();
    member.setRoles(Set.of(Role.of(1L)));
//    ASSERT
    assertNotNull(member.getRoles());
  }

  @DisplayName("Ensure Roles is null")
  @Test
  void ensure_roles_is_null() {
//    GIVEN
    Member member = new Member();
    member.setRoles(null);
//    ASSERT
    assertNull(member.getRoles());
  }
  @DisplayName("Ensure Roles is equal")
  @Test
  void ensure_roles_is_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setRoles(Set.of(Role.of(1L)));

    Member member2 = new Member();
    member2.setRoles(Set.of(Role.of(1L)));
    Role role1 = member1.getRoles().iterator().next();
    Role role2 = member2.getRoles().iterator().next();
//    ASSERT
    assertEquals(role1.getRoleId(), role2.getRoleId());
  }
  @DisplayName("Ensure Roles is not equal")
  @Test
  void ensure_roles_are_not_equal() {
//    GIVEN
    Member member1 = new Member();
    member1.setRoles(Set.of(Role.of(1L)));;

    Member member2 = new Member();
    member2.setRoles(Set.of(Role.of(2L)));;
//    ASSERT
    assertNotEquals(member1.getRoles(), member2.getRoles());
  }
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

}