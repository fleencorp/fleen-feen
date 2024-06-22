package com.fleencorp.feen.model.domain.user;

import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.constant.security.profile.ProfileVerificationStatus;
import com.fleencorp.feen.constant.security.profile.ProfileVerificationType;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.fleencorp.feen.constant.security.mfa.MfaType.EMAIL;
import static com.fleencorp.feen.constant.security.mfa.MfaType.PHONE;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "member", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"email_address"}),
  @UniqueConstraint(columnNames = {"phone_number"})
})
public class Member extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "member_id", nullable = false)
  private Long memberId;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Column(name = "email_address", nullable = false, length = 150)
  private String emailAddress;

  @Column(name = "phone_number", nullable = false, length = 15)
  private String phoneNumber;

  @Column(name = "password_hash", nullable = false, length = 500)
  private String password;

  @Column(name = "profile_photo", length = 1000)
  private String profilePhoto;

  @Builder.Default
  @Column(name ="email_address_verified")
  private boolean emailAddressVerified = false;

  @Builder.Default
  @Column(name ="phone_number_verified")
  private boolean phoneNumberVerified = false;

  @Builder.Default
  @Column(name = "mfa_enabled")
  private boolean mfaEnabled = false;

  @Column(name = "mfa_secret")
  private String mfaSecret;

  @Builder.Default
  @Column(name = "mfa_type")
  @Enumerated(STRING)
  private MfaType mfaType = MfaType.NONE;

  @Column(name = "verification_status")
  @Enumerated(STRING)
  private ProfileVerificationStatus verificationStatus;

  @Column(name = "member_status")
  @Enumerated(STRING)
  private ProfileStatus memberStatus;

  @Builder.Default
  @ManyToMany(fetch = LAZY)
  @JoinTable(name = "member_role",
    joinColumns = {
      @JoinColumn(name = "member_id")
    },
    inverseJoinColumns = {
      @JoinColumn(name = "role_id")
  })
  private Set<Role> roles = new HashSet<>();

  public void clearPreCompletedSignUpRole() {
    this.roles = new HashSet<>();
  }

  public void addRole(Role role) {
    if (roles == null) {
      roles = new HashSet<>();
    }
    roles.add(role);
  }

  public void addRole(List<Role> roles) {
    if (roles != null && !roles.isEmpty()) {
      roles.forEach(this::addRole);
    }
  }

  public void verifyUser(ProfileVerificationType profileVerificationType) {
    if (profileVerificationType == ProfileVerificationType.PHONE) {
      setPhoneNumberVerified(true);
    } else if (profileVerificationType == ProfileVerificationType.EMAIL) {
      setEmailAddressVerified(true);
    }
  }

  public void verifyUserMfa(MfaType mfaType) {
    if (mfaType == PHONE) {
      verifyUser(ProfileVerificationType.PHONE);
    } else if (mfaType == EMAIL) {
      verifyUser(ProfileVerificationType.EMAIL);
    }
  }

  public void updateDetails(String firstName, String lastName) {
    if (nonNull(firstName)) {
      this.firstName = firstName;
    }
    if (nonNull(lastName)) {
      this.lastName = lastName;
    }
  }
}
