package com.fleencorp.feen.model.security;

import com.fleencorp.base.util.StringUtil;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.constant.security.profile.ProfileVerificationStatus;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.domain.user.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.fleencorp.feen.util.security.UserAuthoritiesUtil.buildAuthorities;
import static java.util.Arrays.asList;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FleenUser implements UserDetails {

  @Serial
  private static final long serialVersionUID = 1L;

  private Long id;
  private String emailAddress;
  private String phoneNumber;
  private String password;
  private String firstName;
  private String lastName;
  private String profilePhoto;
  private String country;
  private String timezone;
  private ProfileStatus profileStatus;
  private ProfileVerificationStatus verificationStatus;
  private boolean mfaEnabled;
  private MfaType mfaType;

  @Setter
  private Collection<? extends GrantedAuthority> authorities;

  public String getFullName() {
    return StringUtil.getFullName(firstName, lastName);
  }

  public static FleenUser of(final Long userId) {
    return FleenUser.builder()
        .id(userId)
        .build();
  }

  /**
   * Constructs a FleenUser object from a Member entity.
   *
   * @param member The Member entity from which to construct the FleenUser.
   * @return A FleenUser object populated with data from the Member entity.
   */
  public static FleenUser fromMember(final Member member) {
    // Extract roles from the Member entity and build authorities
    final List<String> roles = member.getRoles()
      .stream()
      .map(Role::getCode)
      .toList();
    final List<GrantedAuthority> authorities = buildAuthorities(roles);

    // Build a FleenUser object from the Member entity data
    final FleenUser user = builder()
      .id(member.getMemberId())
      .emailAddress(member.getEmailAddress())
      .phoneNumber(member.getPhoneNumber())
      .password(member.getPassword())
      .authorities(authorities)
      .profileStatus(member.getProfileStatus())
      .country(member.getCountry())
      .build();

    // Set additional properties on the FleenUser object
    user.setFirstName(member.getFirstName());
    user.setLastName(member.getLastName());
    user.setProfilePhoto(member.getProfilePhotoUrl());
    user.setMfaEnabled(member.isMfaEnabled());
    user.setMfaType(member.getMfaType());
    user.setVerificationStatus(member.getVerificationStatus());
    return user;
  }

  /**
   * Constructs a basic FleenUser object from a Member entity with minimal information.
   *
   * @param member The Member entity from which to construct the basic FleenUser.
   * @return A basic FleenUser object populated with minimal data from the Member entity.
   */
  public static FleenUser fromMemberBasic(final Member member) {
    return FleenUser.builder()
      .id(member.getMemberId())
      .firstName(member.getFirstName())
      .lastName(member.getLastName())
      .emailAddress(member.getEmailAddress())
      .phoneNumber(member.getPhoneNumber())
      .password(member.getPassword())
      .build();
  }

  /**
   * Constructs a FleenUser object from a JwtTokenDetails object.
   *
   * @param details The JwtTokenDetails object from which to construct the FleenUser.
   * @return A FleenUser object populated with data from the JwtTokenDetails.
   */
  public static FleenUser fromToken(final TokenPayload details) {
    // Build authorities from token details
    final List<GrantedAuthority> authorities = buildAuthorities(asList(details.getAuthorities()));

    // Build a FleenUser object from the JwtTokenDetails data
    return FleenUser.builder()
      .firstName(details.getFirstName())
      .lastName(details.getLastName())
      .emailAddress(details.getSub())
      .phoneNumber(details.getPhoneNumber())
      .authorities(authorities)
      .id(details.getUserId())
      .profileStatus(details.getProfileStatus())
      .verificationStatus(details.getProfileVerificationStatus())
      .profilePhoto(details.getProfilePhoto())
      .country(details.getCountry())
      .timezone(details.getTimezone())
      .build();
  }

  /**
   * Converts authorities to roles.
   *
   * @return A list of Role objects representing the authorities of the FleenUser.
   */
  public List<Role> authoritiesToRoles() {
    return authorities
      .stream()
      .filter(Objects::nonNull)
      .map(authority -> Role.of(authority.getAuthority()))
      .filter(Objects::nonNull)
      .toList();
  }

  public Member toMember() {
    return Member.builder()
      .memberId(id)
      .profilePhotoUrl(profilePhoto)
      .firstName(firstName)
      .lastName(lastName)
      .emailAddress(emailAddress)
      .phoneNumber(phoneNumber)
      .profileStatus(profileStatus)
      .verificationStatus(verificationStatus)
      .build();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getUsername() {
    return emailAddress;
  }
}
