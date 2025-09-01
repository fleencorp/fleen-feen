package com.fleencorp.feen.user.model.domain;

import com.fleencorp.base.converter.impl.security.StringCryptoConverter;
import com.fleencorp.base.util.StringUtil;
import com.fleencorp.feen.mfa.constant.MfaType;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.model.contract.UserHaveOtherDetail;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.role.model.domain.Role;
import com.fleencorp.feen.user.constant.profile.ProfileStatus;
import com.fleencorp.feen.user.constant.profile.ProfileVerificationStatus;
import com.fleencorp.feen.verification.constant.VerificationType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "member", uniqueConstraints = {
  @UniqueConstraint(columnNames = "email_address"),
  @UniqueConstraint(columnNames = "phone_number"),
  @UniqueConstraint(columnNames = "username")
})
public class Member extends FleenFeenEntity
  implements IsAMember, UserHaveOtherDetail {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "member_id", nullable = false, updatable = false, unique = true)
  private Long memberId;

  @Column(name = "first_name", nullable = false, length = 100)
  private String firstName;

  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;

  @Column(name = "username", nullable = false, unique = true, length = 50)
  private String username;

  @Column(name = "email_address", nullable = false, unique = true, length = 50)
  private String emailAddress;

  @Column(name = "phone_number", nullable = false, unique = true, length = 20)
  private String phoneNumber;

  @Column(name = "password_hash", nullable = false, length = 500)
  private String password;

  @Column(name = "profile_photo_url", length = 1000)
  private String profilePhotoUrl;

  @Column(name = "country", length = 50)
  private String country;

  @Transient
  private String timezone;

  @Transient
  private Double longitude;

  @Transient
  private Double latitude;

  @Column(name ="email_address_verified", nullable = false)
  private boolean emailAddressVerified;

  @Column(name ="phone_number_verified", nullable = false)
  private boolean phoneNumberVerified;

  @Column(name = "mfa_enabled", nullable = false)
  private boolean mfaEnabled;

  @Convert(converter = StringCryptoConverter.class)
  @Column(name = "mfa_secret")
  private String mfaSecret;

  @Enumerated(STRING)
  @Column(name = "mfa_type", nullable = false)
  private MfaType mfaType = MfaType.NONE;

  @Enumerated(STRING)
  @Column(name = "verification_status", nullable = false)
  private ProfileVerificationStatus verificationStatus = ProfileVerificationStatus.PENDING;

  @Enumerated(STRING)
  @Column(name = "profile_status", nullable = false)
  private ProfileStatus profileStatus = ProfileStatus.INACTIVE;

  @ToString.Exclude
  @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
  @JoinTable(name = "member_role", joinColumns = @JoinColumn(name = "member_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @Column(name = "is_internal", nullable = false)
  private boolean isInternal = false;

  /**
   * Retrieves the full name by concatenating the first name and last name.
   * Uses a utility method from StringUtil to handle the concatenation.
   *
   * @return A string representing the full name of the user, constructed from first and last name.
   */
  public String getFullName() {
    // Concatenate first name and last name to form the full name
    return StringUtil.getFullName(firstName, lastName);
  }

  /**
   * Checks if multi-factor authentication (MFA) is disabled.
   *
   * @return {@code true} if MFA is not enabled; {@code false} otherwise
   */
  public boolean isMfaDisabled() {
    return !mfaEnabled;
  }

  public boolean hasLatitudeAndLongitude() {
    return nonNull(getLatitude()) && nonNull(getLongitude());
  }

  /**
   * Confirms if the email address belongs to the specified origin domain and sets the user as internal if it does.
   *
   * @param originDomain the domain to check against the user's email address
   * @throws NullPointerException if {@code emailAddress} is null
   */
  public void confirmAndSetInternalUser(final String originDomain) {
    if (isInternalEmailOrEmailIsAnOriginEmail(emailAddress, originDomain)) {
      this.isInternal = true;
    }
  }

  /**
   * Checks if the provided email address is an internal email or matches the specified origin domain.
   * This method verifies whether the given email address belongs to the internal domain by checking
   * if it ends with the specified origin domain.
   *
   * @param emailAddress the email address to validate against the origin domain. This address is checked
   *                     to ensure it belongs to the specified domain.
   * @param originDomain the domain to be checked against, typically representing the internal or
   *                     organizational domain. This should be a fully qualified domain string (e.g., "@example.com").
   * @return {@code true} if the email address is non-null and ends with the origin domain, indicating
   *         that it is either an internal email or matches the origin domain; {@code false} otherwise.
   */
  public static boolean isInternalEmailOrEmailIsAnOriginEmail(final String emailAddress, final String originDomain) {
    return (nonNull(emailAddress) && emailAddress.endsWith(originDomain));
  }

  /**
   * Adds a role to the user's set of roles.
   *
   * @param role the role to add
   */
  public void addRole(final Role role) {
    if (null == roles) {
      roles = new HashSet<>(); // Initialize roles if null
    }
    roles.add(role); // Add the role to the set
  }

  /**
   * Clears the default roles assigned during sign-up by reinitializing the roles set.
   */
  public void clearDefaultRolesAssignedDuringSignUpRole() {
    roles = new HashSet<>(); // Clear roles by reinitializing the set
  }

  /**
   * Adds multiple roles to the user's set of roles.
   *
   * @param roles the list of roles to add
   */
  public void addRole(final List<Role> roles) {
    if ((nonNull(roles)) && !roles.isEmpty()) {
      roles.forEach(this::addRole); // Add each role from the list
    }
  }

  /**
   * Verifies the user based on the provided profile verification type.
   *
   * @param verificationType the type of profile verification (PHONE or EMAIL)
   */
  public void verifyUser(final VerificationType verificationType) {
    if (VerificationType.isPhone(verificationType)) {
      setPhoneNumberVerified(true); // Verify phone number
    } else if (VerificationType.isEmail(verificationType)) {
      setEmailAddressVerified(true); // Verify email address
    }
  }

  /**
   * Verifies the user for multifactor authentication (MFA) based on the provided MFA type.
   *
   * @param mfaType the type of MFA (PHONE or EMAIL)
   */
  public void verifyUserMfa(final MfaType mfaType) {
    if (MfaType.isPhone(mfaType)) {
      verifyUser(VerificationType.PHONE); // Verify using phone
    } else if (MfaType.isEmail(mfaType)) {
      verifyUser(VerificationType.EMAIL); // Verify using email
    }
  }

  /**
   * Updates the user's first name and last name if the provided values are not null.
   *
   * @param firstName the new first name
   * @param lastName  the new last name
   */
  public void updateDetails(final String firstName, final String lastName) {
    if (nonNull(firstName)) {
      this.firstName = firstName; // Update first name if not null
    }
    if (nonNull(lastName)) {
      this.lastName = lastName; // Update last name if not null
    }
  }

  /**
   * Checks if the profile status is ACTIVE and the profile verification status is APPROVED.
   *
   * @return {@code true} if the {@code profileStatus} is ACTIVE and the {@code verificationStatus} is APPROVED;
   *         {@code false} otherwise
   */
  public boolean isProfileActiveAndApproved() {
    return ProfileStatus.isActive(profileStatus) && ProfileVerificationStatus.isApproved(verificationStatus);
  }

  /**
   * Updates the user's email address and marks it as verified.
   *
   * @param emailAddress the new email address to set.
   */
  public void updateAndVerifyEmail(final String emailAddress) {
    // Set the new email address
    this.emailAddress = emailAddress;
    // Mark the email address as verified
    emailAddressVerified = true;
  }

  /**
   * Updates the user's phone number and marks it as verified.
   *
   * @param phoneNumber the new phone number to set.
   */
  public void updateAndVerifyPhone(final String phoneNumber) {
    // Set the new phone number
    this.phoneNumber = phoneNumber;
    // Mark the phone number as verified
    phoneNumberVerified = true;
  }

  /**
   * Marks the profile as inactive and sets the verification status to pending.
   *
   * <p>This method updates the profile's status to {@link ProfileStatus#INACTIVE} and
   * sets the verification status to {@link ProfileVerificationStatus#PENDING},
   * indicating that the profile is no longer active and the verification process
   * is awaiting action.</p>
   */
  public void markProfileInactiveAndPending() {
    this.profileStatus = ProfileStatus.INACTIVE;
    this.verificationStatus = ProfileVerificationStatus.PENDING;
  }

  /**
   * Marks the profile as active and sets the verification status to approved.
   *
   * <p>This method updates the profile's status to {@link ProfileStatus#ACTIVE} and
   * sets the verification status to {@link ProfileVerificationStatus#APPROVED},
   * indicating that the profile is now active and has been approved.</p>
   */
  public void markProfileActiveAndApproved() {
    this.profileStatus = ProfileStatus.ACTIVE;
    this.verificationStatus = ProfileVerificationStatus.APPROVED;
  }

  /**
   * Deletes the user's profile photo by setting the profile photo URL to null.
   */
  public void deleteProfilePhoto() {
    // Remove the profile photo by nullifying the URL
    this.profilePhotoUrl = null;
  }

  /**
   * Adds a collection of roles to the member's role set.
   * Ensures that no duplicate roles are added.
   *
   * @param roles The collection of roles to add.
   */
  public void addRoles(final Collection<Role> roles) {
    if (roles != null && !roles.isEmpty()) {
      this.roles.addAll(roles);
    }
  }

  public static Member of(final Long memberId) {
    final Member member = new Member();
    member.setMemberId(memberId);

    return member;
  }

  public static Member of(final String memberId) {
    if (nonNull(memberId)) {
      final Member member = new Member();
      member.setMemberId(Long.valueOf(memberId));

      return member;
    }
    return null;
  }
}
