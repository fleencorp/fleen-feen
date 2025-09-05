package com.fleencorp.feen.shared.member.query.mapper;

import com.fleencorp.feen.mfa.constant.MfaType;
import com.fleencorp.feen.shared.member.model.MemberData;
import com.fleencorp.feen.user.constant.profile.ProfileStatus;
import com.fleencorp.feen.user.constant.profile.ProfileVerificationStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class MemberQueryMapper implements RowMapper<MemberData> {

  private MemberQueryMapper() {}

  public static MemberQueryMapper of() {
    return new MemberQueryMapper();
  }

  @Override
  public MemberData mapRow(ResultSet rs, int rowNum) throws SQLException {
    final MemberData member = new MemberData();

    Long memberId = rs.getLong("memberId");
    String country = rs.getString("country");
    String username = rs.getString("username");
    String firstName = rs.getString("firstName");
    String lastName = rs.getString("lastName");
    String fullName = rs.getString("fullName");

    String password = rs.getString("password");
    String phoneNumber = rs.getString("phoneNumber");
    String emailAddress = rs.getString("emailAddress");
    String profilePhoto = rs.getString("profilePhoto");

    boolean mfaEnabled = rs.getBoolean("mfaEnabled");
    boolean phoneNumberVerified = rs.getBoolean("phoneNumberVerified");
    boolean emailAddressVerified = rs.getBoolean("emailAddressVerified");

    String mfaTypeValue = rs.getString("mfaType");
    String profileStatusValue = rs.getString("profileStatus");
    String profileVerificationStatusValue = rs.getString("profileVerificationStatus");

    MfaType mfaType = MfaType.of(mfaTypeValue);
    ProfileStatus profileStatus = ProfileStatus.of(profileStatusValue);
    ProfileVerificationStatus profileVerificationStatus = ProfileVerificationStatus.of(profileVerificationStatusValue);

    member.setMemberId(memberId);
    member.setCountry(country);
    member.setUsername(username);
    member.setFirstName(firstName);
    member.setLastName(lastName);
    member.setFullName(fullName);

    member.setPassword(password);
    member.setPhoneNumber(phoneNumber);
    member.setEmailAddress(emailAddress);
    member.setProfilePhoto(profilePhoto);

    member.setMfaEnabled(mfaEnabled);
    member.setPhoneNumberVerified(phoneNumberVerified);
    member.setEmailAddressVerified(emailAddressVerified);

    member.setMfaType(mfaType);
    member.setProfileStatus(profileStatus);
    member.setVerificationStatus(profileVerificationStatus);

    return member;
  }

}
