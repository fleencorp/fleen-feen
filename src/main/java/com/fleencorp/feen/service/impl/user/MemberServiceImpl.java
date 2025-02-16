package com.fleencorp.feen.service.impl.user;

import com.fleencorp.base.service.EmailService;
import com.fleencorp.base.service.PhoneService;
import com.fleencorp.feen.configuration.external.aws.s3.S3BucketNames;
import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.event.publisher.ProfileRequestPublisher;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.member.MemberNotFoundException;
import com.fleencorp.feen.exception.user.profile.BannedAccountException;
import com.fleencorp.feen.exception.user.profile.DisabledAccountException;
import com.fleencorp.feen.mapper.user.UserMapper;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.info.user.ProfileStatusInfo;
import com.fleencorp.feen.model.projection.MemberInfoSelect;
import com.fleencorp.feen.model.projection.MemberProfileStatusSelect;
import com.fleencorp.feen.model.projection.MemberUpdateSelect;
import com.fleencorp.feen.model.request.profile.ProfileUpdateVerificationRequest;
import com.fleencorp.feen.model.response.common.EmailAddressExistsResponse;
import com.fleencorp.feen.model.response.common.EmailAddressNotExistsResponse;
import com.fleencorp.feen.model.response.common.PhoneNumberExistsResponse;
import com.fleencorp.feen.model.response.common.PhoneNumberNotExistsResponse;
import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import com.fleencorp.feen.model.response.user.profile.RetrieveMemberInfoResponse;
import com.fleencorp.feen.model.response.user.profile.RetrieveMemberUpdateInfoResponse;
import com.fleencorp.feen.model.response.user.profile.RetrieveProfileStatusResponse;
import com.fleencorp.feen.model.response.user.profile.UpdateProfileStatusResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.repository.user.UserProfileRepository;
import com.fleencorp.feen.service.auth.PasswordService;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.feen.service.impl.external.aws.s3.StorageService;
import com.fleencorp.feen.service.security.VerificationService;
import com.fleencorp.feen.service.user.MemberService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
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
  private final CountryService countryService;
  private final StorageService storageService;
  private final VerificationService verificationService;
  private final MemberRepository memberRepository;
  private final UserProfileRepository userProfileRepository;
  private final Localizer localizer;
  private final PasswordEncoder passwordEncoder;
  private final ProfileRequestPublisher profileRequestPublisher;
  private final UserMapper userMapper;
  private final S3BucketNames s3BucketNames;

  /**
   * Constructs a new instance of {@code MemberServiceImpl} with the specified dependencies.
   *
   * <p>This constructor initializes the service with the required components to perform member-related
   * operations, including caching, country-related services, file storage, verification,
   * repository access, localization, password encoding, profile publishing, and user mapping.</p>
   *
   * @param cacheService            the service used for caching member data
   * @param countryService          the service providing country-related operations
   * @param storageService          the service for handling file storage operations
   * @param verificationService     the service responsible for verifying members
   * @param memberRepository        the repository for managing member-related database operations
   * @param userProfileRepository   the repository for handling user profile data access
   * @param localizer               the service for handling localization of responses
   * @param passwordEncoder         the encoder for handling password hashing and verification
   * @param profileRequestPublisher the publisher for handling profile request messages
   * @param userMapper              the mapper for transforming user data between models
   * @param s3BucketNames           the configuration class for managing S3 bucket names
   */
  public MemberServiceImpl(
      final CacheService cacheService,
      final CountryService countryService,
      final StorageService storageService,
      final VerificationService verificationService,
      final MemberRepository memberRepository,
      final UserProfileRepository userProfileRepository,
      final Localizer localizer,
      final PasswordEncoder passwordEncoder,
      final ProfileRequestPublisher profileRequestPublisher,
      final UserMapper userMapper,
      final S3BucketNames s3BucketNames) {
    this.cacheService = cacheService;
    this.countryService = countryService;
    this.storageService = storageService;
    this.verificationService = verificationService;
    this.memberRepository = memberRepository;
    this.userProfileRepository = userProfileRepository;
    this.localizer = localizer;
    this.passwordEncoder = passwordEncoder;
    this.profileRequestPublisher = profileRequestPublisher;
    this.userMapper = userMapper;
    this.s3BucketNames = s3BucketNames;
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
  public EntityExistsResponse verifyMemberEmailAddressExists(final String emailAddress) {
    // Check if the email address exist
    final boolean exists = isEmailAddressExist(emailAddress);
    // Return a localized response of the status
    return exists
      ? localizer.of(EmailAddressExistsResponse.of(true))
      : localizer.of(EmailAddressNotExistsResponse.of(false));
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
  public EntityExistsResponse verifyMemberPhoneNumberExists(final String phoneNumber) {
    final boolean exists = isPhoneNumberExist(phoneNumber);
    return exists
      ? localizer.of(PhoneNumberExistsResponse.of(true))
      : localizer.of(PhoneNumberNotExistsResponse.of(false));
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
  public RetrieveMemberInfoResponse getMemberInfo(final FleenUser user) {
    // Retrieve member information from the user profile repository
    final MemberInfoSelect info = userProfileRepository.findInfoByMember(user.toMember())
      .orElseThrow(FailedOperationException::new);

    // Return the localized response containing the retrieved member information
    return localizer.of(RetrieveMemberInfoResponse.of(info));
  }

  /**
   * Retrieves the update information for the specified member.
   *
   * @param user the {@link FleenUser} whose member update information is being retrieved.
   * @return a localized {@link RetrieveMemberUpdateInfoResponse} containing the member's update information.
   * @throws FailedOperationException if no update information is found for the given member.
   */
  @Override
  public RetrieveMemberUpdateInfoResponse getMemberUpdateInfo(final FleenUser user) {
    // Retrieve member update information from the user profile repository
    final MemberUpdateSelect info = userProfileRepository.findByMember(user.toMember())
      .orElseThrow(FailedOperationException::new);

    // Return the localized response containing the retrieved member update information
    return localizer.of(RetrieveMemberUpdateInfoResponse.of(info));
  }

  /**
   * Retrieves the profile verification status for the specified user.
   *
   * @param user the {@link FleenUser} whose profile status is being retrieved.
   * @return a localized {@link RetrieveProfileStatusResponse} containing the member's profile verification status.
   * @throws FailedOperationException if the profile status for the given member is not found.
   */
  @Override
  public RetrieveProfileStatusResponse getProfileStatus(final FleenUser user) {
    // Retrieve the profile status from the user profile repository
    final MemberProfileStatusSelect verificationStatus = userProfileRepository.findStatusByMember(user.toMember())
      .orElseThrow(FailedOperationException::new);

    // Return the localized response containing the retrieved profile status
    return localizer.of(RetrieveProfileStatusResponse.of(verificationStatus));
  }

  /**
   * Updates the country information for the specified member.
   *
   * @param member the {@link Member} whose country is to be updated.
   * @param countryCode the country code used to retrieve the country's details.
   * @throws FailedOperationException if the provided member or country code is null.
   */
  protected void updateUserCountry(final Member member, final String countryCode) {
    // Throw an exception if the provided member is null
    checkIsNull(List.of(member, countryCode), FailedOperationException::new);

    // Retrieve country details based on the provided country code
    final Country country = countryService.getCountryByCode(countryCode);
    // Set the member's country to the retrieved country name
    member.setCountry(country.getTitle());
  }

  /**
   * Updates the profile status of the specified user based on the provided profile status.
   *
   * @param profileStatus the {@link ProfileStatus} to be set for the user.
   * @param user the {@link FleenUser} whose profile status is being updated.
   * @return a localized {@link UpdateProfileStatusResponse} indicating the result of the operation.
   * @throws FailedOperationException if the user cannot be found in the member repository.
   */
  protected UpdateProfileStatusResponse updateProfileStatus(final ProfileStatus profileStatus, final FleenUser user) {
    // Retrieve the member associated with the user's email address
    final Member member = memberRepository.findByEmailAddress(user.getEmailAddress())
      .orElseThrow(FailedOperationException::new);
    // Check and validate if account is banned or disabled
    checkIfProfileIsBannedOrDisabled(member);
    // Status to update
    ProfileStatus newProfileStatus = profileStatus;

    // Check if the profile status is inactive
    if (ProfileStatus.isInactive(member.getProfileStatus()) || ProfileStatus.isActive(member.getProfileStatus())) {
      // Update the profile status if currently inactive
      userProfileRepository.updateProfileStatus(user.toMember(), profileStatus);
    } else {
      newProfileStatus = member.getProfileStatus();
    }

    // Retrieve the profile status info
    final ProfileStatusInfo profileStatusInfo = userMapper.toProfileStatusInfo(newProfileStatus);
    // Return a localized response
    return localizer.of(UpdateProfileStatusResponse.of(profileStatusInfo));
  }

  /**
   * Retrieves a {@link Member} based on the provided email address.
   *
   * @param emailAddress the email address of the member to be retrieved.
   * @return the {@link Member} associated with the given email address.
   * @throws FailedOperationException if no member is found with the specified email address.
   */
  protected Member findMember(final String emailAddress) {
    // Attempt to find the member by the provided email address
    return memberRepository.findByEmailAddress(emailAddress)
      .orElseThrow(FailedOperationException::new);
  }

  /**
   * Creates a {@link ProfileUpdateVerificationRequest} with the provided OTP and user information.
   *
   * @param otp the one-time password (OTP) for verification.
   * @param verificationType the {@link VerificationType} indicating the type of verification (email or phone).
   * @param user the {@link FleenUser} containing the user's details.
   * @return a new instance of {@link ProfileUpdateVerificationRequest} populated with the provided details.
   */
  public ProfileUpdateVerificationRequest createProfileUpdateVerificationRequest(final String otp, final VerificationType verificationType, final FleenUser user) {
    // Create and return a ProfileUpdateVerificationRequest with the user's information and OTP
    return ProfileUpdateVerificationRequest
      .of(otp, user.getFirstName(), user.getLastName(), user.getEmailAddress(), user.getPhoneNumber(), verificationType);
  }

  /**
   * Saves the verification code for updating the member's email address or phone number
   * based on the specified verification type.
   *
   * @param verificationType the {@link VerificationType} indicating whether the code is for email or phone verification.
   * @param member the {@link Member} associated with the verification code.
   * @param code the verification code to be saved.
   */
  protected void saveUpdateEmailOrPhoneVerificationCode(final VerificationType verificationType, final Member member, final String code) {
    // Check if the verification type is for email
    if (VerificationType.isEmail(verificationType)) {
      // Save the OTP for email verification
      saveUpdateEmailOtp(member.getEmailAddress(), code);
    } // Check if the verification type is for phone
    else if (VerificationType.isPhone(verificationType)) {
      // Save the OTP for phone verification
      saveUpdatePhoneNumberOtp(member.getEmailAddress(), code);
    }
  }

  /**
   * Save the update email otp.
   *
   * @param subject the user's identifier to associate with the otp
   * @param otp a random code associated with the user's identifier during the update email process
   */
  protected void saveUpdateEmailOtp(final String subject, final String otp) {
    cacheService.set(getUpdateEmailCacheKey(subject), otp, Duration.ofMinutes(3));
  }

  /**
   * Save the update phone number otp.
   *
   * @param subject the user's identifier to associate with the otp
   * @param otp a random code associated with the user's identifier during the update phone number process
   */
  public void saveUpdatePhoneNumberOtp(final String subject, final String otp) {
    cacheService.set(getUpdatePhoneNumberCacheKey(subject), otp, Duration.ofMinutes(3));
  }


  /**
   * Remove the user's update email otp after successful update of the user's email address.
   *
   * @param username the user's identifier associated with the update email otp or code
   */
  protected void clearUpdateEmailAddressOtp(final String username) {
    cacheService.delete(getUpdateEmailCacheKey(username));
  }

  /**
   * Remove the user's update phone otp after successful update of the user's phone number.
   *
   * @param username the user's identifier associated with the update phone otp or code
   */
  protected void clearUpdatePhoneNumberOtp(final String username) {
    cacheService.delete(getUpdatePhoneNumberCacheKey(username));
  }

  /**
   * Checks if the specified member's profile is banned or disabled.
   *
   * @param member The member whose profile status is to be checked.
   * @throws FailedOperationException if the provided member is null.
   * @throws DisabledAccountException if the member's profile status is disabled.
   * @throws BannedAccountException if the member's profile status is banned.
   */
  protected void checkIfProfileIsBannedOrDisabled(final Member member) {
    checkIsNull(member, FailedOperationException::new);

    // Validate that the member object is not null
    if (ProfileStatus.isDisabled(member.getProfileStatus())) {
      // Profile is disabled
      throw new DisabledAccountException();
    } else if (ProfileStatus.isBanned(member.getProfileStatus())) {
      // Profile is banned
      throw new BannedAccountException();
    }
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
