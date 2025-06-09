package com.fleencorp.feen.user.service.impl;

import com.fleencorp.base.service.EmailService;
import com.fleencorp.base.service.PhoneService;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.user.exception.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.projection.MemberInfoSelect;
import com.fleencorp.feen.user.model.projection.MemberProfileStatusSelect;
import com.fleencorp.feen.user.model.projection.MemberUpdateSelect;
import com.fleencorp.feen.model.response.common.EmailAddressExistsResponse;
import com.fleencorp.feen.model.response.common.PhoneNumberExistsResponse;
import com.fleencorp.feen.model.response.member.MemberInfoResponse;
import com.fleencorp.feen.model.response.member.MemberProfileStatusResponse;
import com.fleencorp.feen.model.response.member.MemberUpdateInfoResponse;
import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveMemberInfoResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveMemberUpdateInfoResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveProfileStatusResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.user.repository.MemberRepository;
import com.fleencorp.feen.user.repository.UserProfileRepository;
import com.fleencorp.feen.service.auth.PasswordService;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.feen.user.service.MemberService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.fleencorp.feen.service.impl.common.CacheKeyService.*;

/**
 * Implementation of the {@link MemberService}, {@link EmailService}, and {@link PhoneService} interfaces.
 *This class provides functionalities for managing members, including operations related to email and phone services.
 *
 * @author Yusuf Àlàmú Musa
 * @version 1.0
 */
@Slf4j
@Service
public class MemberServiceImpl implements MemberService,
  EmailService, PasswordService, PhoneService {

  private final CacheService cacheService;
  private final MemberRepository memberRepository;
  private final UserProfileRepository userProfileRepository;
  private final Localizer localizer;
  private final PasswordEncoder passwordEncoder;

  /**
   * Constructs a new instance of {@code MemberServiceImpl} with the specified dependencies.
   *
   * <p>This constructor initializes the service with the required components to perform member-related
   * operations, including caching, country-related services, file storage, verification,
   * repository access, localization, password encoding, profile publishing, and user mapping.</p>
   *
   * @param cacheService            the service used for caching member data
   * @param memberRepository        the repository for managing member-related database operations
   * @param userProfileRepository   the repository for handling user profile data access
   * @param localizer               the service for handling localization of responses
   * @param passwordEncoder         the encoder for handling password hashing and verification
   */
  public MemberServiceImpl(
      final CacheService cacheService,
      final MemberRepository memberRepository,
      final UserProfileRepository userProfileRepository,
      final Localizer localizer,
      final PasswordEncoder passwordEncoder) {
    this.cacheService = cacheService;
    this.memberRepository = memberRepository;
    this.userProfileRepository = userProfileRepository;
    this.localizer = localizer;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Retrieves the password encoder instance.
   *
   * @return the {@link PasswordEncoder} used for encoding passwords.
   */
  @Override
  public PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }

  /**
   * Checks if a member's email address exists in the system.
   *
   * <p>This method verifies the existence of the given email address
   * by calling {@link #isEmailAddressExist(String)}. Depending on whether
   * the email address exists or not, it returns a localized response
   * indicating the result.
   *
   * @param emailAddress the email address to check for existence
   * @return an {@link EntityExistsResponse} containing a localized response
   *         indicating whether the email address exists or not
   */
  @Override
  public EmailAddressExistsResponse verifyMemberEmailAddressExists(final String emailAddress) {
    // Check if the email address exist
    final boolean exists = isEmailAddressExist(emailAddress);
    // Create the response
    final EmailAddressExistsResponse emailAddressExistsResponse = EmailAddressExistsResponse.of(exists);
    // Return a localized response of the status
    return localizer.of(emailAddressExistsResponse);
  }

  /**
   * Checks if a member with the specified email address exists in the repository.
   *
   * @param emailAddress the email address to check.
   * @return {@code true} if a member with the specified email address exists, {@code false} otherwise.
   */
  @Override
  public boolean isEmailAddressExist(final String emailAddress) {
    return memberRepository.existsByEmailAddress(emailAddress);
  }

  /**
   * Checks if a member's phone number exists in the system.
   *
   * <p>This method verifies the existence of the given phone number
   * by calling {@link #isPhoneNumberExist(String)}. Based on the existence
   * of the phone number, it returns a localized response indicating
   * the result.
   *
   * @param phoneNumber the phone number to check for existence
   * @return an {@link EntityExistsResponse} containing a localized response
   *         indicating whether the phone number exists or not
   */
  @Override
  public PhoneNumberExistsResponse verifyMemberPhoneNumberExists(final String phoneNumber) {
    // Check if the phone number exist
    final boolean exists = isPhoneNumberExist(phoneNumber);
    // Create the response
    final PhoneNumberExistsResponse phoneNumberExistsResponse = PhoneNumberExistsResponse.of(exists);
    // Return a localized response of the status
    return localizer.of(phoneNumberExistsResponse);
  }

  /**
   * Checks if a member with the specified phone number exists in the repository.
   *
   * @param phoneNumber the phone number to check.
   * @return {@code true} if a member with the specified phone number exists, {@code false} otherwise.
   */
  @Override
  public boolean isPhoneNumberExist(final String phoneNumber) {
    return memberRepository.existsByPhoneNumber(phoneNumber);
  }

  /**
   * Checks if a member with the specified ID exists in the repository.
   *
   * @param memberId the ID of the member to check.
   * @return {@code true} if the member exists, {@code false} otherwise.
   */
  @Override
  public boolean isIdExists(final Long memberId) {
    return memberRepository.existsByMemberId(memberId);
  }

  /**
   * Checks whether a username already exists in the repository.
   *
   * <p>This method checks the member repository to determine if a member with the given username already exists.
   * It returns {@code true} if the username exists and {@code false} otherwise.</p>
   *
   * @param username the username to check for existence
   * @return {@code true} if the username exists, {@code false} otherwise
   */
  @Override
  public boolean isUsernameExist(final String username) {
    return memberRepository.existsByUsername(username);
  }

  /**
   * Finds a member by their unique identifier.
   *
   * <p>This method retrieves a member from the repository using the provided member ID. If no member is found
   * with the specified ID, a {@link MemberNotFoundException} is thrown.</p>
   *
   * @param memberId The unique identifier of the member to be retrieved.
   * @return The {@link Member} associated with the given member ID.
   * @throws MemberNotFoundException if no member is found with the specified ID.
   */
  @Override
  public Member findMember(final Long memberId) throws MemberNotFoundException {
    // Retrieve the member by ID and throw an exception if not found
    return memberRepository.findById(memberId)
      .orElseThrow(MemberNotFoundException.of(memberId));
  }

  /**
   * Retrieves the member information for the specified user.
   *
   * @param user the {@link FleenUser} whose member information is being retrieved.
   * @return a localized {@link RetrieveMemberInfoResponse} containing the member's information.
   * @throws FailedOperationException if no member information is found for the given user.
   */
  @Override
  public RetrieveMemberInfoResponse getMemberInfo(final FleenUser user) throws FailedOperationException {
    // Retrieve member information from the user profile repository
    final MemberInfoSelect info = userProfileRepository.findInfoByMember(user.toMember())
      .orElseThrow(FailedOperationException::new);
    // Convert to a response
    final MemberInfoResponse memberInfoResponse = MemberInfoResponse.of(
      info.getMemberId(),
      info.getFirstName(),
      info.getLastName(),
      info.getProfilePhoto(),
      info.getCountry()
    );
    // Create the response
    final RetrieveMemberInfoResponse retrieveMemberInfoResponse = RetrieveMemberInfoResponse.of(memberInfoResponse);
    // Return the localized response containing the retrieved member information
    return localizer.of(retrieveMemberInfoResponse);
  }

  /**
   * Retrieves the update information for the specified member.
   *
   * @param user the {@link FleenUser} whose member update information is being retrieved.
   * @return a localized {@link RetrieveMemberUpdateInfoResponse} containing the member's update information.
   * @throws FailedOperationException if no update information is found for the given member.
   */
  @Override
  public RetrieveMemberUpdateInfoResponse getMemberUpdateInfo(final FleenUser user) throws FailedOperationException {
    // Retrieve member update information from the user profile repository
    final MemberUpdateSelect info = userProfileRepository.findByMember(user.toMember())
      .orElseThrow(FailedOperationException::new);
    // Convert to response
    final MemberUpdateInfoResponse memberUpdateInfoResponse = MemberUpdateInfoResponse.of(
      info.getMemberId(),
      info.getFirstName(),
      info.getLastName(),
      info.getEmailAddress(),
      info.getPhoneNumber(),
      info.getCountry()
    );

    // Create the response
    final RetrieveMemberUpdateInfoResponse retrieveMemberUpdateInfoResponse = RetrieveMemberUpdateInfoResponse.of(memberUpdateInfoResponse);
    // Return the localized response containing the retrieved member update information
    return localizer.of(retrieveMemberUpdateInfoResponse);
  }

  /**
   * Retrieves the profile verification status for the specified user.
   *
   * @param user the {@link FleenUser} whose profile status is being retrieved.
   * @return a localized {@link RetrieveProfileStatusResponse} containing the member's profile verification status.
   * @throws FailedOperationException if the profile status for the given member is not found.
   */
  @Override
  public RetrieveProfileStatusResponse getProfileStatus(final FleenUser user) throws FailedOperationException {
    // Retrieve the profile status from the user profile repository
    final MemberProfileStatusSelect verificationStatus = userProfileRepository.findStatusByMember(user.toMember())
      .orElseThrow(FailedOperationException::new);
    // Convert to response
    final MemberProfileStatusResponse memberProfileStatusResponse = MemberProfileStatusResponse.of(verificationStatus.getProfileStatus());
    // Create the response
    final RetrieveProfileStatusResponse retrieveProfileStatusResponse = RetrieveProfileStatusResponse.of(memberProfileStatusResponse);
    // Return the localized response containing the retrieved profile status
    return localizer.of(retrieveProfileStatusResponse);
  }

  /**
   * Clears authentication tokens for the specified user.
   *
   * @param username the username of the user
   */
  @Override
  @Async
  public void clearAuthenticationTokens(final String username) {
    // Retrieve the associated token cache keys
    final String accessTokenCacheKeyKey = getAccessTokenCacheKey(username);
    final String refreshTokenCacheKeyKey = getRefreshTokenCacheKey(username);
    final String resetPasswordTokenCacheKey = getResetPasswordTokenCacheKey(username);

    // Delete access token from cache if it exists
    cacheService.existsAndDelete(accessTokenCacheKeyKey);
    // Delete reset password token from cache if it exists
    cacheService.existsAndDelete(resetPasswordTokenCacheKey);
    // Delete refresh token from cache if it exists
    cacheService.existsAndDelete(refreshTokenCacheKeyKey);
  }

}
