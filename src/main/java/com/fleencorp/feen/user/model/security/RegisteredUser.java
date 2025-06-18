package com.fleencorp.feen.user.model.security;

import com.fleencorp.base.util.StringUtil;
import com.fleencorp.feen.mfa.constant.MfaType;
import com.fleencorp.feen.role.model.domain.Role;
import com.fleencorp.feen.user.constant.profile.ProfileStatus;
import com.fleencorp.feen.user.constant.profile.ProfileVerificationStatus;
import com.fleencorp.feen.user.model.domain.Member;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.fleencorp.feen.user.util.UserAuthoritiesUtil.buildAuthorities;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredUser implements UserDetails {

  @Serial
  private static final long serialVersionUID = 1L;

  private Long id;
  private String displayName;
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

  public static RegisteredUser of(final Long userId) {
    final RegisteredUser user = new RegisteredUser();
    user.setId(userId);

    return user;
  }

  public static RegisteredUser of() {
    return new RegisteredUser();
  }

  /**
   * Constructs a FleenUser object from a Member entity.
   *
   * @param member The Member entity from which to construct the FleenUser.
   * @return A FleenUser object populated with data from the Member entity.
   */
  public static RegisteredUser fromMember(final Member member) {
    // Extract roles from the Member entity and build authorities
    final List<String> roles = member.getRoles()
      .stream()
      .map(Role::getCode)
      .toList();
    final List<GrantedAuthority> authorities = buildAuthorities(roles);

    // Build a FleenUser object from the Member entity data
    final RegisteredUser user = builder()
      .id(member.getMemberId())
      .authorities(authorities)
      .country(member.getCountry())
      .displayName(member.getUsername())
      .password(member.getPassword())
      .phoneNumber(member.getPhoneNumber())
      .emailAddress(member.getEmailAddress())
      .profileStatus(member.getProfileStatus())
      .build();

    // Set additional properties on the FleenUser object
    user.setFirstName(member.getFirstName());
    user.setLastName(member.getLastName());
    user.setMfaType(member.getMfaType());
    user.setMfaEnabled(member.isMfaEnabled());
    user.setProfilePhoto(member.getProfilePhotoUrl());
    user.setVerificationStatus(member.getVerificationStatus());

    return user;
  }

  /**
   * Constructs a basic FleenUser object from a Member entity with minimal information.
   *
   * @param member The Member entity from which to construct the basic FleenUser.
   * @return A basic FleenUser object populated with minimal data from the Member entity.
   */
  public static RegisteredUser fromMemberBasic(final Member member) {
    final RegisteredUser user = new RegisteredUser();
    user.setId(member.getMemberId());
    user.setFirstName(member.getFirstName());
    user.setLastName(member.getLastName());
    user.setDisplayName(member.getUsername());
    user.setPassword(member.getPassword());
    user.setPhoneNumber(member.getPhoneNumber());
    user.setEmailAddress(member.getEmailAddress());

    return user;
  }

  /**
   * Constructs a FleenUser object from a JwtTokenDetails object.
   *
   * @param details The JwtTokenDetails object from which to construct the FleenUser.
   * @return A FleenUser object populated with data from the JwtTokenDetails.
   */
  public static RegisteredUser fromToken(final TokenPayload details) {
    // Build authorities from token details
    final List<GrantedAuthority> authorities = buildAuthorities(asList(details.getAuthorities()));

    // Build a FleenUser object from the JwtTokenDetails data
    final RegisteredUser user = new RegisteredUser();
    user.setId(details.getUserId());
    user.setAuthorities(authorities);
    user.setCountry(details.getCountry());
    user.setEmailAddress(details.getSub());
    user.setTimezone(details.getTimezone());
    user.setFirstName(details.getFirstName());
    user.setLastName(details.getLastName());
    user.setDisplayName(details.getUsername());
    user.setPhoneNumber(details.getPhoneNumber());
    user.setProfilePhoto(details.getProfilePhoto());
    user.setProfileStatus(details.getProfileStatus());
    user.setVerificationStatus(details.getProfileVerificationStatus());

    return user;
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
    if (isNull(getId())) {
      return null;
    }

    final Member member = new Member();
    member.setMemberId(getId());
    member.setUsername(getDisplayName());
    member.setPassword(getPassword());
    member.setCountry(getCountry());
    member.setMfaType(getMfaType());
    member.setTimezone(getTimezone());
    member.setLastName(getLastName());
    member.setFirstName(getFirstName());
    member.setMfaEnabled(isMfaEnabled());
    member.setPhoneNumber(getPhoneNumber());
    member.setEmailAddress(getEmailAddress());
    member.setProfileStatus(getProfileStatus());
    member.setVerificationStatus(getVerificationStatus());

    return member;
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
