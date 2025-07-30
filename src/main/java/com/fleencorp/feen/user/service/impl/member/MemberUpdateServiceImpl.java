package com.fleencorp.feen.user.service.impl.member;

import com.fleencorp.base.service.EmailService;
import com.fleencorp.base.service.PhoneService;
import com.fleencorp.feen.configuration.external.aws.s3.S3BucketNames;
import com.fleencorp.feen.country.model.domain.Country;
import com.fleencorp.feen.country.service.CountryService;
import com.fleencorp.feen.common.event.model.base.PublishMessageRequest;
import com.fleencorp.feen.common.event.publisher.ProfileRequestPublisher;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.common.service.impl.cache.CacheService;
import com.fleencorp.feen.service.impl.external.aws.s3.StorageService;
import com.fleencorp.feen.user.constant.profile.ProfileStatus;
import com.fleencorp.feen.user.exception.user.*;
import com.fleencorp.feen.user.mapper.UserMapper;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.dto.profile.*;
import com.fleencorp.feen.user.model.info.ProfileStatusInfo;
import com.fleencorp.feen.user.model.request.profile.ProfileUpdateVerificationRequest;
import com.fleencorp.feen.user.model.response.RemoveProfilePhotoResponse;
import com.fleencorp.feen.user.model.response.SendUpdateEmailOrPhoneVerificationCodeResponse;
import com.fleencorp.feen.user.model.response.update.*;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.repository.MemberRepository;
import com.fleencorp.feen.user.repository.UserProfileRepository;
import com.fleencorp.feen.user.service.authentication.PasswordService;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.feen.user.service.member.MemberUpdateService;
import com.fleencorp.feen.verification.constant.VerificationType;
import com.fleencorp.feen.verification.exception.core.ExpiredVerificationCodeException;
import com.fleencorp.feen.verification.exception.core.InvalidVerificationCodeException;
import com.fleencorp.feen.verification.exception.core.VerificationFailedException;
import com.fleencorp.feen.verification.service.VerificationService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static com.fleencorp.base.util.ExceptionUtil.checkIsFalse;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.feen.common.service.impl.cache.CacheKeyService.getUpdateEmailCacheKey;
import static com.fleencorp.feen.common.service.impl.cache.CacheKeyService.getUpdatePhoneNumberCacheKey;
import static com.fleencorp.feen.verification.service.OtpService.getRandomSixDigitOtp;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link MemberService}, {@link EmailService}, and {@link PhoneService} interfaces.
 *This class provides functionalities for managing members, including operations related to email and phone services.
 *
 * @author Yusuf Àlàmú Musa
 * @version 1.0
 */
@Slf4j
@Service
public class MemberUpdateServiceImpl implements MemberUpdateService, PasswordService {

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
  public MemberUpdateServiceImpl(
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

  @Override
  public PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }

  /**
   * Updates the password for the specified user.
   *
   * @param updatePasswordDto the {@link UpdatePasswordDto} containing the old and new password information.
   * @param user the {@link RegisteredUser} whose password is being updated.
   * @return a localized {@link UpdatePasswordResponse} indicating the result of the password update operation.
   * @throws UpdatePasswordFailedException if the old password does not match or if the user is not found.
   */
  @Override
  @Transactional
  public UpdatePasswordResponse updatePassword(final UpdatePasswordDto updatePasswordDto, final RegisteredUser user) throws UpdatePasswordFailedException {
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

    // Create the response
    final UpdatePasswordResponse updatePasswordResponse = UpdatePasswordResponse.of();
    // Return the response indicating successful password update
    return localizer.of(updatePasswordResponse);
  }

  /**
   * Updates the profile information for the specified user.
   *
   * @param updateProfileInfoDto the {@link UpdateProfileInfoDto} containing the user's new profile details such as country, first name, and last name.
   * @param user the {@link RegisteredUser} whose profile information is being updated.
   * @return a localized {@link UpdateProfileInfoResponse} indicating the result of the profile update operation.
   * @throws UpdateProfileInfoFailedException if the user is not found in the repository.
   */
  @Override
  @Transactional
  public UpdateProfileInfoResponse updateInfo(final UpdateProfileInfoDto updateProfileInfoDto, final RegisteredUser user) throws UpdateProfileInfoFailedException {
    // Retrieve the member associated with the user's email address
    final Member member = memberRepository.findByEmailAddress(user.getEmailAddress())
      .orElseThrow(UpdateProfileInfoFailedException::new);

    // Update the user's country information
    updateUserCountry(member, updateProfileInfoDto.getCountryCode());
    // Update the user's first and last name
    member.updateDetails(updateProfileInfoDto.getFirstName(), updateProfileInfoDto.getLastName());
    // Return the response indicating successful profile update
    return localizer.of(UpdateProfileInfoResponse.of());
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
   * Sends a verification code for updating the user's email address or phone number.
   *
   * @param updateEmailAddressOrPhoneNumberDto the {@link UpdateEmailAddressOrPhoneNumberDto} containing the details for the update request.
   * @param user the {@link RegisteredUser} initiating the verification process.
   * @return a localized {@link SendUpdateEmailOrPhoneVerificationCodeResponse} indicating the result of the operation.
   * @throws IllegalArgumentException if the verification type is invalid.
   */
  @Override
  @Transactional
  public SendUpdateEmailOrPhoneVerificationCodeResponse sendUpdateEmailAddressOrPhoneNumberVerificationCode(final UpdateEmailAddressOrPhoneNumberDto updateEmailAddressOrPhoneNumberDto, final RegisteredUser user) {
    // Retrieve the verification type from the DTO
    final VerificationType verificationType = updateEmailAddressOrPhoneNumberDto.getVerificationType();
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
    // Create the response
    final SendUpdateEmailOrPhoneVerificationCodeResponse emailOrPhoneVerificationCodeResponse = SendUpdateEmailOrPhoneVerificationCodeResponse.of();
    // Return the response indicating successful sending of the verification code
    return localizer.of(emailOrPhoneVerificationCodeResponse);
  }

  /**
   * Updates the email address for the specified user after verifying the provided code.
   *
   * @param updateEmailAddressDto the {@link ConfirmUpdateEmailAddressDto} containing the new email address and verification code.
   * @param user the {@link RegisteredUser} whose email address is being updated.
   * @return a localized {@link UpdateEmailAddressResponse} indicating the result of the operation.
   * @throws FailedOperationException if the user cannot be found or if the verification fails.
   * @throws EmailAddressAlreadyExistsException if the new email address is already in use by another member.
   */
  @Override
  @Transactional
  public UpdateEmailAddressResponse updateEmailAddress(final ConfirmUpdateEmailAddressDto updateEmailAddressDto, final RegisteredUser user) {
    // Get the current user's email address
    final String username = user.getEmailAddress();
    // Generate the cache key for the email update verification
    final String verificationKey = getUpdateEmailCacheKey(username);
    // Retrieve the verification code from the DTO
    final String code = updateEmailAddressDto.getVerificationCode();

    // Validate the provided verification code
    verificationService.validateVerificationCode(verificationKey, code);
    // Retrieve the member associated with the user's email address
    final Member member = findMember(user.getEmailAddress());
    // Verify the email address to update is not used by another user
    verifyEmailIsNotUsedByAnotherUser(updateEmailAddressDto, user);
    // Update the member's email address and mark the email as verified
    member.updateAndVerifyEmail(updateEmailAddressDto.getEmailAddress());
    // Save the updated member information
    memberRepository.save(member);
    // Clear the OTP associated with the email update process
    clearUpdateEmailAddressOtp(username);

    // Return the response indicating successful email address update
    return localizer.of(UpdateEmailAddressResponse.of());
  }

  private void verifyEmailIsNotUsedByAnotherUser(final ConfirmUpdateEmailAddressDto updateEmailAddressDto, final RegisteredUser user) {
    // Check if the new email address is already associated with another member
    memberRepository.findEmailOfMember(updateEmailAddressDto.getEmailAddress())
      .ifPresent(foundMember -> {
        final Long currentUserId = user.getId();
        final Long foundUserId = foundMember.getMemberId();
        final boolean idsNotEqual = currentUserId.equals(foundUserId);

        // Throw an exception if the new email address belongs to another member
        checkIsFalse(idsNotEqual, EmailAddressAlreadyExistsException.of(updateEmailAddressDto.getEmailAddress()));
    });
  }

  /**
   * Updates the phone number for the specified user after verifying the provided code.
   *
   * @param updatePhoneNumberDto the {@link ConfirmUpdatePhoneNumberDto} containing the new phone number and verification code.
   * @param user the {@link RegisteredUser} whose phone number is being updated.
   * @return a localized {@link UpdatePhoneNumberResponse} indicating the result of the operation.
   * @throws FailedOperationException if the user cannot be found or if the verification fails.
   * @throws PhoneNumberAlreadyExistsException if the new phone number is already in use by another member.
   */
  @Override
  @Transactional
  public UpdatePhoneNumberResponse updatePhoneNumber(final ConfirmUpdatePhoneNumberDto updatePhoneNumberDto, final RegisteredUser user)
    throws VerificationFailedException, ExpiredVerificationCodeException, InvalidVerificationCodeException,
      PhoneNumberAlreadyExistsException {
    // Get the current user's email address
    final String username = user.getEmailAddress();
    // Generate the cache key for the phone number update verification
    final String verificationKey = getUpdatePhoneNumberCacheKey(username);
    // Retrieve the verification code from the DTO
    final String code = updatePhoneNumberDto.getVerificationCode();

    // Validate the provided verification code
    verificationService.validateVerificationCode(verificationKey, code);
    // Retrieve the member associated with the user's email address
    final Member member = findMember(user.getEmailAddress());
    // Verify the phone number to update is not used by another user
    verifyPhoneNumberIsNotUsedByAnotherUser(updatePhoneNumberDto, user);
    // Update the member's phone number and mark the phone number as verified
    member.updateAndVerifyPhone(updatePhoneNumberDto.getPhoneNumber());
    // Save the updated member information
    memberRepository.save(member);
    // Clear the OTP associated with the phone number update process
    clearUpdatePhoneNumberOtp(username);

    // Return the response indicating successful phone number update
    return localizer.of(UpdatePhoneNumberResponse.of());
  }

  private void verifyPhoneNumberIsNotUsedByAnotherUser(final ConfirmUpdatePhoneNumberDto updatePhoneNumberDto, final RegisteredUser user) {
    // Check if the new phone number is already associated with another member
    memberRepository.findPhoneOfMember(updatePhoneNumberDto.getPhoneNumber())
      .ifPresent(foundMember -> {
        final Long currentUserId = user.getId();
        final Long foundUserId = foundMember.getMemberId();
        final boolean idsNotEqual = currentUserId.equals(foundUserId);

        // Throw an exception if the new phone number belongs to another member
        checkIsFalse(idsNotEqual, PhoneNumberAlreadyExistsException.of(updatePhoneNumberDto.getPhoneNumber()));
    });
  }

  /**
   * Updates the profile status of the specified user to active.
   *
   * @param user the {@link RegisteredUser} whose profile status is to be updated.
   * @return a response indicating the outcome of the profile status update operation.
   */
  @Override
  @Transactional
  public UpdateProfileStatusResponse updateProfileActive(final RegisteredUser user) {
    // Delegate to updateProfileStatus method to set the profile status to ACTIVE
    return updateProfileStatus(ProfileStatus.ACTIVE, user);
  }

  /**
   * Updates the profile status of the specified user to inactive.
   *
   * @param user the {@link RegisteredUser} whose profile status is to be updated.
   * @return a response indicating the outcome of the profile status update operation.
   */
  @Override
  @Transactional
  public UpdateProfileStatusResponse updateProfileInactive(final RegisteredUser user) {
    // Delegate to updateProfileStatus method to set the profile status to INACTIVE
    return updateProfileStatus(ProfileStatus.INACTIVE, user);
  }

  /**
   * Updates the profile photo of the specified user.
   *
   * @param dto the {@link UpdateProfilePhotoDto} containing the new profile photo URL.
   * @param user the {@link RegisteredUser} whose profile photo is to be updated.
   * @return a response indicating the outcome of the profile photo update operation.
   */
  @Override
  @Transactional
  public UpdateProfilePhotoResponse updateProfilePhoto(final UpdateProfilePhotoDto dto, final RegisteredUser user) {
    // Retrieve the member associated with the user's email address
    final Member member = findMember(user.getEmailAddress());
    // Retrieve the profile photo associated with the user
    final String profilePhotoUrl = member.getProfilePhotoUrl();
    // Check if the member currently has a profile photo
    if (nonNull(profilePhotoUrl)) {
      // Delete the existing profile photo from S3 storage
      storageService.deleteObject(s3BucketNames.getUserPhoto(), storageService.getObjectKeyFromUrl(profilePhotoUrl));
    }

    // Update the member's profile photo URL with the new one
    member.setProfilePhotoUrl(dto.getProfilePhoto());
    // Save the updated member information to the repository
    memberRepository.save(member);
    // Return a response indicating the successful update of the profile photo
    return localizer.of(UpdateProfilePhotoResponse.of());
  }

  /**
   * Removes the profile photo for the specified user.
   *
   * @param user the {@link RegisteredUser} whose profile photo is to be removed.
   * @return a localized {@link RemoveProfilePhotoResponse} indicating the result of the operation.
   */
  @Override
  @Transactional
  public RemoveProfilePhotoResponse removeProfilePhoto(final RegisteredUser user) throws FailedOperationException {
    // Find the member associated with the user's email address
    final Member member = findMember(user.getEmailAddress());
    final String profilePhotoUrl = member.getProfilePhotoUrl();

    // Check if the member has a profile photo set
    if (nonNull(profilePhotoUrl)) {
      // Retrieve the object key from the profile photo URL
      final String key = storageService.getObjectKeyFromUrl(profilePhotoUrl);
      // Delete the profile photo from the S3 bucket
      storageService.deleteObject(s3BucketNames.getUserPhoto(), key);
      // Delete the member's profile photo
      member.deleteProfilePhoto();
      // Save the updated member information to the repository
      memberRepository.save(member);
    }

    // Return a response indicating successful removal of the profile photo
    return localizer.of(RemoveProfilePhotoResponse.of());
  }

  /**
   * Updates the profile status of the specified user based on the provided profile status.
   *
   * @param profileStatus the {@link ProfileStatus} to be set for the user.
   * @param user the {@link RegisteredUser} whose profile status is being updated.
   * @return a localized {@link UpdateProfileStatusResponse} indicating the result of the operation.
   * @throws FailedOperationException if the user cannot be found in the member repository.
   */
  protected UpdateProfileStatusResponse updateProfileStatus(final ProfileStatus profileStatus, final RegisteredUser user) {
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
    // Create the response
    final UpdateProfileStatusResponse updateProfileStatusResponse = UpdateProfileStatusResponse.of(profileStatusInfo);
    // Return a localized response
    return localizer.of(updateProfileStatusResponse);
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
   * @param user the {@link RegisteredUser} containing the user's details.
   * @return a new instance of {@link ProfileUpdateVerificationRequest} populated with the provided details.
   */
  public ProfileUpdateVerificationRequest createProfileUpdateVerificationRequest(final String otp, final VerificationType verificationType, final RegisteredUser user) {
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
