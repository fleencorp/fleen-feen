package com.fleencorp.feen.service.impl.user;

import com.fleencorp.base.service.EmailService;
import com.fleencorp.base.service.PhoneService;
import com.fleencorp.feen.configuration.external.aws.s3.S3BucketNames;
import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.event.model.base.PublishMessageRequest;
import com.fleencorp.feen.event.publisher.ProfileRequestPublisher;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.UnableToCompleteOperationException;
import com.fleencorp.feen.exception.user.profile.*;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.user.profile.*;
import com.fleencorp.feen.model.projection.MemberInfoSelect;
import com.fleencorp.feen.model.projection.MemberProfileStatusSelect;
import com.fleencorp.feen.model.projection.MemberUpdateSelect;
import com.fleencorp.feen.model.request.profile.ProfileUpdateVerificationRequest;
import com.fleencorp.feen.model.response.common.EmailAddressExistsResponse;
import com.fleencorp.feen.model.response.common.EmailAddressNotExistsResponse;
import com.fleencorp.feen.model.response.common.PhoneNumberExistsResponse;
import com.fleencorp.feen.model.response.common.PhoneNumberNotExistsResponse;
import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import com.fleencorp.feen.model.response.user.profile.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.repository.user.UserProfileRepository;
import com.fleencorp.feen.service.auth.PasswordService;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.feen.service.impl.external.aws.S3Service;
import com.fleencorp.feen.service.security.VerificationService;
import com.fleencorp.feen.service.user.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static com.fleencorp.base.util.ExceptionUtil.checkIsFalse;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.feen.service.impl.common.CacheKeyService.getUpdateEmailCacheKey;
import static com.fleencorp.feen.service.impl.common.CacheKeyService.getUpdatePhoneNumberCacheKey;
import static com.fleencorp.feen.service.security.OtpService.getRandomSixDigitOtp;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link MemberService}, {@link EmailService}, and {@link PhoneService} interfaces.
 *This class provides functionalities for managing members, including operations related to email and phone services.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class MemberServiceImpl implements MemberService,
  EmailService, PasswordService, PhoneService, VerificationService {

  private final CacheService cacheService;
  private final CountryService countryService;
  private final S3Service s3Service;
  private final MemberRepository memberRepository;
  private final UserProfileRepository userProfileRepository;
  private final LocalizedResponse localizedResponse;
  private final PasswordEncoder passwordEncoder;
  private final ProfileRequestPublisher profileRequestPublisher;
  private final S3BucketNames s3BucketNames;

  /**
   * Constructs a new instance of {@code MemberServiceImpl} with the specified member repository.
   *
   * @param memberRepository the repository used to manage member entities.
   */
  public MemberServiceImpl(
      final CacheService cacheService,
      final CountryService countryService,
      final S3Service s3Service,
      final MemberRepository memberRepository,
      final UserProfileRepository userProfileRepository,
      final LocalizedResponse localizedResponse,
      final PasswordEncoder passwordEncoder,
      final ProfileRequestPublisher profileRequestPublisher,
      final S3BucketNames s3BucketNames) {
    this.cacheService = cacheService;
    this.countryService = countryService;
    this.s3Service = s3Service;
    this.memberRepository = memberRepository;
    this.userProfileRepository = userProfileRepository;
    this.localizedResponse = localizedResponse;
    this.passwordEncoder = passwordEncoder;
    this.profileRequestPublisher = profileRequestPublisher;
    this.s3BucketNames = s3BucketNames;
  }

  /**
   * Retrieves the cache service instance.
   *
   * @return the {@link CacheService} instance used for managing cache operations.
   */
  @Override
  public CacheService getCacheService() {
    return cacheService;
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
  public EntityExistsResponse isMemberEmailAddressExists(String emailAddress) {
    boolean exists = isEmailAddressExist(emailAddress);
    return exists
      ? localizedResponse.of(EmailAddressExistsResponse.of(true))
      : localizedResponse.of(EmailAddressNotExistsResponse.of(false));
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
  public EntityExistsResponse isMemberPhoneNumberExists(String phoneNumber) {
    boolean exists = isPhoneNumberExist(phoneNumber);
    return exists
      ? localizedResponse.of(PhoneNumberExistsResponse.of(true))
      : localizedResponse.of(PhoneNumberNotExistsResponse.of(false));
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
    return localizedResponse.of(RetrieveMemberInfoResponse.of(info));
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
    return localizedResponse.of(RetrieveMemberUpdateInfoResponse.of(info));
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
    return localizedResponse.of(RetrieveProfileStatusResponse.of(verificationStatus));
  }

  /**
   * Updates the password for the specified user.
   *
   * @param updatePasswordDto the {@link UpdatePasswordDto} containing the old and new password information.
   * @param user the {@link FleenUser} whose password is being updated.
   * @return a localized {@link UpdatePasswordResponse} indicating the result of the password update operation.
   * @throws UpdatePasswordFailedException if the old password does not match or if the user is not found.
   */
  @Override
  @Transactional
  public UpdatePasswordResponse updatePassword(final UpdatePasswordDto updatePasswordDto, final FleenUser user) {
    // Retrieve the member associated with the user's email address
    final Member member = memberRepository.findByEmailAddress(user.getEmailAddress())
      .orElseThrow(UpdatePasswordFailedException::new);

    // Extract the new password from the DTO and encode it
    final String newPassword = updatePasswordDto.getPassword();
    final String hashedOrEncodedPassword = createEncodedPassword(newPassword);

    // Check if the old password matches the stored password
    if (passwordEncoder.matches(updatePasswordDto.getOldPassword(), member.getPassword())) {
      // Update the password if the old password matches
      userProfileRepository.updatePassword(user.toMember(), hashedOrEncodedPassword);
    } else {
      // Throw an exception if the old password does not match
      throw new UpdatePasswordFailedException();
    }

    // Return the response indicating successful password update
    return localizedResponse.of(UpdatePasswordResponse.of());
  }

  /**
   * Updates the profile information for the specified user.
   *
   * @param updateProfileInfoDto the {@link UpdateProfileInfoDto} containing the user's new profile details such as country, first name, and last name.
   * @param user the {@link FleenUser} whose profile information is being updated.
   * @return a localized {@link UpdateProfileInfoResponse} indicating the result of the profile update operation.
   * @throws UpdateProfileInfoFailedException if the user is not found in the repository.
   */
  @Override
  @Transactional
  public UpdateProfileInfoResponse updateInfo(final UpdateProfileInfoDto updateProfileInfoDto, final FleenUser user) {
    // Retrieve the member associated with the user's email address
    final Member member = memberRepository.findByEmailAddress(user.getEmailAddress())
      .orElseThrow(UpdateProfileInfoFailedException::new);

    // Update the user's country information
    updateUserCountry(member, updateProfileInfoDto.getCountryCode());
    // Update the user's first and last name
    member.updateDetails(updateProfileInfoDto.getFirstName(), updateProfileInfoDto.getLastName());
    // Return the response indicating successful profile update
    return localizedResponse.of(UpdateProfileInfoResponse.of());
  }

  /**
   * Updates the country information for the specified member.
   *
   * @param member the {@link Member} whose country is to be updated.
   * @param countryCode the country code used to retrieve the country's details.
   * @throws UnableToCompleteOperationException if the provided member or country code is null.
   */
  protected void updateUserCountry(final Member member, final String countryCode) {
    // Throw an exception if the provided member is null
    checkIsNull(List.of(member, countryCode), UnableToCompleteOperationException::new);

    // Retrieve country details based on the provided country code
    final Country country = countryService.getCountryByCode(countryCode);
    // Set the member's country to the retrieved country name
    member.setCountry(country.getTitle());
  }

  /**
   * Sends a verification code for updating the user's email address or phone number.
   *
   * @param updateEmailAddressOrPhoneNumberDto the {@link UpdateEmailAddressOrPhoneNumberDto} containing the details for the update request.
   * @param user the {@link FleenUser} initiating the verification process.
   * @return a localized {@link SendUpdateEmailOrPhoneVerificationCodeResponse} indicating the result of the operation.
   * @throws IllegalArgumentException if the verification type is invalid.
   */
  @Override
  @Transactional
  public SendUpdateEmailOrPhoneVerificationCodeResponse sendUpdateEmailAddressOrPhoneNumberVerificationCode(final UpdateEmailAddressOrPhoneNumberDto updateEmailAddressOrPhoneNumberDto, final FleenUser user) {
    // Retrieve the verification type from the DTO
    final VerificationType verificationType = updateEmailAddressOrPhoneNumberDto.getActualVerificationType();
    // Find the member associated with the user's email address
    final Member member = findMember(user.getEmailAddress());

    // Generate a random six-digit OTP (One-Time Password)
    final String code = getRandomSixDigitOtp();
    // Create a profile update verification request with the generated code
    final ProfileUpdateVerificationRequest profileUpdateVerificationRequest = createProfileUpdateVerificationRequest(code, verificationType, user);
    // Publish the message to the profile request publisher
    profileRequestPublisher.publishMessage(PublishMessageRequest.of(profileUpdateVerificationRequest));

    // Save the generated verification code for the member
    saveUpdateEmailOrPhoneVerificationCode(verificationType, member, code);
    // Return the response indicating successful sending of the verification code
    return localizedResponse.of(SendUpdateEmailOrPhoneVerificationCodeResponse.of());
  }

  /**
   * Updates the email address for the specified user after verifying the provided code.
   *
   * @param updateEmailAddressDto the {@link ConfirmUpdateEmailAddressDto} containing the new email address and verification code.
   * @param user the {@link FleenUser} whose email address is being updated.
   * @return a localized {@link UpdateEmailAddressResponse} indicating the result of the operation.
   * @throws FailedOperationException if the user cannot be found or if the verification fails.
   * @throws EmailAddressAlreadyExistsException if the new email address is already in use by another member.
   */
  @Override
  @Transactional
  public UpdateEmailAddressResponse updateEmailAddress(final ConfirmUpdateEmailAddressDto updateEmailAddressDto, final FleenUser user) {
    // Get the current user's email address
    final String username = user.getEmailAddress();
    // Generate the cache key for the email update verification
    final String verificationKey = getUpdateEmailCacheKey(username);
    // Retrieve the verification code from the DTO
    final String code = updateEmailAddressDto.getVerificationCode();

    // Validate the provided verification code
    validateVerificationCode(verificationKey, code);
    // Retrieve the member associated with the user's email address
    final Member member = findMember(user.getEmailAddress());

    // Check if the new email address is already associated with another member
    memberRepository.findEmailOfMember(updateEmailAddressDto.getEmailAddress())
      .ifPresent(foundMember -> {
        final Long currentUserId = user.getId();
        final Long foundUserId = foundMember.getMemberId();
        final boolean idsNotEqual = currentUserId.equals(foundUserId);

        // Throw an exception if the new email address belongs to another member
        checkIsFalse(idsNotEqual, EmailAddressAlreadyExistsException.of(updateEmailAddressDto.getEmailAddress()));
    });

    // Update the member's email address and mark the email as verified
    member.updateAndVerifyEmail(updateEmailAddressDto.getEmailAddress());
    // Save the updated member information
    memberRepository.save(member);
    // Clear the OTP associated with the email update process
    clearUpdateEmailAddressOtp(username);

    // Return the response indicating successful email address update
    return localizedResponse.of(UpdateEmailAddressResponse.of());
  }

  /**
   * Updates the phone number for the specified user after verifying the provided code.
   *
   * @param updatePhoneNumberDto the {@link ConfirmUpdatePhoneNumberDto} containing the new phone number and verification code.
   * @param user the {@link FleenUser} whose phone number is being updated.
   * @return a localized {@link UpdatePhoneNumberResponse} indicating the result of the operation.
   * @throws FailedOperationException if the user cannot be found or if the verification fails.
   * @throws PhoneNumberAlreadyExistsException if the new phone number is already in use by another member.
   */
  @Override
  @Transactional
  public UpdatePhoneNumberResponse updatePhoneNumber(final ConfirmUpdatePhoneNumberDto updatePhoneNumberDto, final FleenUser user) {
    // Get the current user's email address
    final String username = user.getEmailAddress();
    // Generate the cache key for the phone number update verification
    final String verificationKey = getUpdatePhoneNumberCacheKey(username);
    // Retrieve the verification code from the DTO
    final String code = updatePhoneNumberDto.getVerificationCode();

    // Validate the provided verification code
    validateVerificationCode(verificationKey, code);
    // Retrieve the member associated with the user's email address
    final Member member = findMember(user.getEmailAddress());

    // Check if the new phone number is already associated with another member
    memberRepository.findPhoneOfMember(updatePhoneNumberDto.getPhoneNumber())
      .ifPresent(foundMember -> {
        final Long currentUserId = user.getId();
        final Long foundUserId = foundMember.getMemberId();
        final boolean idsNotEqual = currentUserId.equals(foundUserId);

        // Throw an exception if the new phone number belongs to another member
        checkIsFalse(idsNotEqual, PhoneNumberAlreadyExistsException.of(updatePhoneNumberDto.getPhoneNumber()));
    });

    // Update the member's phone number and mark the phone number as verified
    member.updateAndVerifyPhone(updatePhoneNumberDto.getPhoneNumber());
    // Save the updated member information
    memberRepository.save(member);
    // Clear the OTP associated with the phone number update process
    clearUpdatePhoneNumberOtp(username);

    // Return the response indicating successful phone number update
    return localizedResponse.of(UpdatePhoneNumberResponse.of());
  }

  /**
   * Updates the profile status of the specified user to active.
   *
   * @param user the {@link FleenUser} whose profile status is to be updated.
   * @return a response indicating the outcome of the profile status update operation.
   */
  @Override
  public UpdateProfileStatusResponse updateProfileActive(final FleenUser user) {
    // Delegate to updateProfileStatus method to set the profile status to ACTIVE
    return updateProfileStatus(ProfileStatus.ACTIVE, user);
  }

  /**
   * Updates the profile status of the specified user to inactive.
   *
   * @param user the {@link FleenUser} whose profile status is to be updated.
   * @return a response indicating the outcome of the profile status update operation.
   */
  @Override
  public UpdateProfileStatusResponse updateProfileInactive(final FleenUser user) {
    // Delegate to updateProfileStatus method to set the profile status to INACTIVE
    return updateProfileStatus(ProfileStatus.INACTIVE, user);
  }

  /**
   * Updates the profile photo of the specified user.
   *
   * @param dto the {@link UpdateProfilePhotoDto} containing the new profile photo URL.
   * @param user the {@link FleenUser} whose profile photo is to be updated.
   * @return a response indicating the outcome of the profile photo update operation.
   */
  public UpdateProfilePhotoResponse updateProfilePhoto(final UpdateProfilePhotoDto dto, final FleenUser user) {
/*    // Retrieve the member associated with the user's email address
    Member member = findMember(user.getEmailAddress());
    String profilePhotoUrl = member.getProfilePhotoUrl();

    // Check if the member currently has a profile photo
    if (nonNull(profilePhotoUrl)) {
      // Delete the existing profile photo from S3 storage
      s3Service.deleteObject(s3BucketNames.getUserPhoto(), s3Service.getObjectKeyFromUrl(profilePhotoUrl));
    }

    // Update the member's profile photo URL with the new one
    member.setProfilePhotoUrl(dto.getProfilePhoto());
    // Save the updated member information to the repository
    memberRepository.save(member);*/
    // Return a response indicating the successful update of the profile photo
    return localizedResponse.of(UpdateProfilePhotoResponse.of());
  }

  /**
   * Removes the profile photo for the specified user.
   *
   * @param user the {@link FleenUser} whose profile photo is to be removed.
   * @return a localized {@link RemoveProfilePhotoResponse} indicating the result of the operation.
   */
  public RemoveProfilePhotoResponse removeProfilePhoto(final FleenUser user) {
    // Find the member associated with the user's email address
    final Member member = findMember(user.getEmailAddress());
    final String profilePhotoUrl = member.getProfilePhotoUrl();

    // Check if the member has a profile photo set
    if (nonNull(profilePhotoUrl)) {
      // Retrieve the object key from the profile photo URL
      final String key = s3Service.getObjectKeyFromUrl(profilePhotoUrl);
      // Delete the profile photo from the S3 bucket
      s3Service.deleteObject(s3BucketNames.getUserPhoto(), key);
      // Delete the member's profile photo
      member.deleteProfilePhoto();
      // Save the updated member information to the repository
      memberRepository.save(member);
    }

    // Return a response indicating successful removal of the profile photo
    return localizedResponse.of(RemoveProfilePhotoResponse.of());
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

    // Check if the profile status is inactive
    if (ProfileStatus.isInactive(member.getProfileStatus())) {
      // Update the profile status if currently inactive
      userProfileRepository.updateProfileStatus(user.toMember(), profileStatus);
      return localizedResponse.of(UpdateProfileStatusResponse.of(profileStatus));
    } else if (ProfileStatus.isActive(member.getProfileStatus())) {
      // Update the profile status if currently active
      userProfileRepository.updateProfileStatus(user.toMember(), profileStatus);
      return localizedResponse.of(UpdateProfileStatusResponse.of(profileStatus));
    }

    // Return a response indicating the status of the profile update operation
    return localizedResponse.of(UpdateProfileStatusResponse.of(member.getProfileStatus()));
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


}
