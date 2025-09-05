package com.fleencorp.feen.shared.member.model;

import com.fleencorp.feen.mfa.constant.MfaType;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.user.constant.profile.ProfileStatus;
import com.fleencorp.feen.user.constant.profile.ProfileVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberData implements IsAMember {

  private Long memberId;
  private String username;
  private String emailAddress;
  private String firstName;
  private String lastName;
  private String fullName;
  private String phoneNumber;
  private String profilePhoto;
  private String timezone;
  private boolean mfaEnabled;
  private String country;
  private String password;
  private boolean phoneNumberVerified;
  private boolean emailAddressVerified;
  private MfaType mfaType;
  private ProfileStatus profileStatus;
  private ProfileVerificationStatus verificationStatus;

  public static MemberData empty() {
    return new MemberData();
  }
}
