package com.fleencorp.feen.service.impl.security.mfa;

import com.fleencorp.feen.configuration.security.properties.MfaProperties;
import com.fleencorp.feen.constant.security.mfa.MfaSetupStatus;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.event.model.base.PublishMessageRequest;
import com.fleencorp.feen.event.publisher.ProfileRequestPublisher;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.security.mfa.MfaGenerationFailedException;
import com.fleencorp.feen.exception.security.mfa.MfaVerificationFailed;
import com.fleencorp.feen.exception.verification.ExpiredVerificationCodeException;
import com.fleencorp.feen.exception.verification.InvalidVerificationCodeException;
import com.fleencorp.feen.mapper.CommonMapper;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.security.mfa.ConfirmSetupMfaDto;
import com.fleencorp.feen.model.dto.security.mfa.SetupMfaDto;
import com.fleencorp.feen.model.info.security.IsMfaEnabledInfo;
import com.fleencorp.feen.model.info.security.MfaTypeInfo;
import com.fleencorp.feen.model.other.MfaAuthenticatorSecurityInfo;
import com.fleencorp.feen.model.request.mfa.MfaSetupVerificationRequest;
import com.fleencorp.feen.model.response.security.mfa.ConfirmMfaSetupResponse;
import com.fleencorp.feen.model.response.security.mfa.EnableOrDisableMfaResponse;
import com.fleencorp.feen.model.response.security.mfa.MfaStatusResponse;
import com.fleencorp.feen.model.response.security.mfa.SetupMfaResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.security.MfaRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.feen.service.security.OtpService;
import com.fleencorp.feen.service.security.mfa.MfaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

import static com.fleencorp.feen.service.impl.common.CacheKeyService.getMfaAuthenticationCacheKey;
import static com.fleencorp.feen.service.impl.common.CacheKeyService.getMfaSetupCacheKey;
import static com.fleencorp.feen.service.security.OtpService.getRandomSixDigitOtp;

/**
 * Implementation of the MfaService interface, providing methods for managing multifactor authentication (MFA) processes.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Component
public class MfaServiceImpl implements MfaService {

  private final CacheService cacheService;
  private final OtpService otpService;
  private final MfaRepository mfaRepository;
  private final MemberRepository memberRepository;
  private final ProfileRequestPublisher profileRequestPublisher;
  private final LocalizedResponse localizedResponse;
  private final MfaProperties mfaProperties;
  private final CommonMapper commonMapper;

  /**
   * Constructs an instance of MfaServiceImpl with the required services, properties, and mappers for Multi-Factor Authentication (MFA) operations.
   *
   * @param cacheService         The service used for caching data.
   * @param otpService           The service for generating One-Time Passwords (OTPs).
   * @param mfaRepository        The repository responsible for handling MFA data.
   * @param memberRepository     The repository responsible for managing member data.
   * @param profileRequestPublisher Publishes profile requests when necessary.
   * @param localizedResponse    The service used to fetch localized responses based on the user's locale.
   * @param mfaProperties        Configuration properties for MFA settings.
   * @param commonMapper            The mapper service responsible for mapping MFA-related entities and responses.
   */
  public MfaServiceImpl(
      final CacheService cacheService,
      final OtpService otpService,
      final MfaRepository mfaRepository,
      final MemberRepository memberRepository,
      final ProfileRequestPublisher profileRequestPublisher,
      final LocalizedResponse localizedResponse,
      final MfaProperties mfaProperties,
      final CommonMapper commonMapper) {
    this.cacheService = cacheService;
    this.otpService = otpService;
    this.mfaRepository = mfaRepository;
    this.memberRepository = memberRepository;
    this.profileRequestPublisher = profileRequestPublisher;
    this.localizedResponse = localizedResponse;
    this.mfaProperties = mfaProperties;
    this.commonMapper = commonMapper;
  }

  /**
   * Re-enables MFA (Multi-Factor Authentication) for a user if it is not already enabled and the MFA type is set.
   *
   * @param user the user for whom MFA is to be re-enabled
   * @return a response indicating the result of the operation
   * @throws FailedOperationException if the operation cannot be completed
   */
  @Override
  public EnableOrDisableMfaResponse reEnableMfa(final FleenUser user) {
    // Retrieve the member associated with the user's email address
    final Member member = memberRepository.findByEmailAddress(user.getEmailAddress())
      .orElseThrow(FailedOperationException::new);

    // Check if the MFA type is set and MFA is not already enabled
    if (isMfaMethodOrTypeNotEmpty(member.getMfaType()) && member.isMfaDisabled()) {
      // Update member mfa status
      member.setMfaEnabled(true);
      // Enable two-factor authentication (2FA) for the user
      mfaRepository.enableOrDisableTwoFa(user.toMember(), true);
    }

    // Return a response indicating the result of the MFA enable operation
    return localizedResponse.of(EnableOrDisableMfaResponse.of(member.isMfaEnabled()));
  }

  /**
   * Disables MFA (Multi-Factor Authentication) for a user if it is currently enabled and the MFA type is set.
   *
   * @param user the user for whom MFA is to be disabled
   * @return a response indicating the status of the MFA disable operation
   * @throws FailedOperationException if the operation cannot be completed
   */
  @Override
  public EnableOrDisableMfaResponse disableMfa(final FleenUser user) {
    // Retrieve the member associated with the user's email address
    final Member member = memberRepository.findByEmailAddress(user.getEmailAddress())
        .orElseThrow(FailedOperationException::new);

    // Check if the MFA type is set and MFA is currently enabled
    if (isMfaMethodOrTypeNotEmpty(member.getMfaType()) && member.isMfaEnabled()) {
      // Update member mfa status
      member.setMfaEnabled(false);
      // Disable two-factor authentication (2FA) for the user
      mfaRepository.enableOrDisableTwoFa(user.toMember(), false);
    }

    // Return a response indicating the status of the MFA disable operation
    return localizedResponse.of(EnableOrDisableMfaResponse.of(member.isMfaDisabled()));
  }

  /**
   * Retrieves the MFA (Multi-Factor Authentication) status for a user.
   *
   * @param user the user whose MFA status is to be retrieved
   * @return a response containing the MFA status and type
   * @throws FailedOperationException if the operation cannot be completed
   */
  @Override
  public MfaStatusResponse getMfaStatus(final FleenUser user) {
    // Retrieve the member associated with the user's ID
    final Member member = memberRepository.findById(user.getId())
      .orElseThrow(FailedOperationException::new);
    // Get Mfa Enabled Info
    final IsMfaEnabledInfo mfaEnabledInfo = commonMapper.toIsMfaEnabledInfo(member.isMfaEnabled());
    // Get Mfa Type Info
    final MfaTypeInfo mfaTypeInfo = commonMapper.toMfaTypeInfo(member.getMfaType());
    // Return a response with the MFA status and type
    return localizedResponse.of(MfaStatusResponse.of(mfaEnabledInfo, mfaTypeInfo));
  }

  /**
   * Sets up Multi-Factor Authentication (MFA) for a given user based on the provided setup DTO.
   * This method handles initialization, verification, and configuration of MFA methods.
   *
   * <p>It initializes the setup response based on the member's current MFA status and proposed MFA type.
   * If the proposed MFA type matches the current MFA type, the setup completes without further verification.
   * If the proposed MFA type is empty, the method resets the MFA type to default for the member.
   * Updates to the setup response depend on whether the proposed MFA method or type is empty or not.</p>
   *
   * <p>The method then proceeds to send a verification code or generate a secret key and QR code
   * depending on the chosen MFA type. If the MFA type is set to AUTHENTICATOR, it generates and
   * includes the secret key and QR code data URI in the setup response.</p>
   *
   * <p>After updating the member's MFA setup details and saving them, the method returns the setup response
   * containing relevant information such as the MFA type, status, and optionally, authentication setup details.</p>
   *
   * @param dto The DTO containing MFA setup details including the type of MFA to set up.
   * @param user The authenticated user requesting MFA setup.
   * @return A response containing details of the MFA setup operation, including QR code for authenticator setup.
   * @throws FailedOperationException If the operation to retrieve or update member information fails.
   */
  @Override
  @Transactional
  public SetupMfaResponse setupMfa(final SetupMfaDto dto, final FleenUser user) {
    final MfaType newMfaType = dto.getActualMfaType();
    final Long userId = user.getId();
    // Retrieve member with associated user id
    final Member member = memberRepository.findById(userId)
      .orElseThrow(FailedOperationException::new);

    // Initialize setup response
    final SetupMfaResponse setupMfaResponse = initializeAndSetupMfaResponseBeforeCompletionOrVerificationOrReverification(member, newMfaType);

    // Check if proposed MFA type is the same as existing MFA type
    if (isProposedMfaTypeToSetSameAsExistingMfaType(newMfaType, member.getMfaType())) {
      return completeMfaSetupWithoutVerificationIfProposedAndCurrentMfaTypeIsSame(setupMfaResponse);
    }
    // Check if proposed MFA type is the same as existing MFA type
    if (isMfaMethodOrTypeEmpty(newMfaType)) {
      resetMfaTypeToDefault(member);
    }

    // Update setup response if proposed MFA method or type is empty
    updateMfaSetupResponseIfProposedMfaMethodOrTypeIsEmpty(setupMfaResponse, newMfaType);
    // Send MFA verification code or generate secret key and QR code
    sendMfaVerificationCodeRequestOrGenerateSecretKeyAndQrCode(member, setupMfaResponse, newMfaType);
    // Update setup response and possibly set MFA authenticator secret
    updateMfaSetupResponseAndIfPossibleSetMfaAuthenticatorSecret(newMfaType, setupMfaResponse, member);

    // Save member changes and return setup response
    memberRepository.save(member);
    return localizedResponse.of(setupMfaResponse);
  }


  /**
   * Resends the MFA verification code or regenerates the Authenticator secret and QR code
   * based on the proposed MFA method or type.
   *
   * @param dto The SetupMfaDto containing the proposed MFA method or type.
   * @param user The FleenUser for whom the MFA setup is being resent.
   * @return The SetupMfaResponse reflecting the updated MFA setup details.
   * @throws FailedOperationException if the operation cannot be completed
   */
  @Override
  public SetupMfaResponse resendMfaSetupCode(final SetupMfaDto dto, final FleenUser user) {
    final Long userId = user.getId();
    final MfaType newMfaType = dto.getActualMfaType();
    // Retrieve member with associated user id
    final Member member = memberRepository.findById(userId)
      .orElseThrow(FailedOperationException::new);

    // Initialize setup response
    final SetupMfaResponse setupMfaResponse = initializeAndSetupMfaResponseBeforeCompletionOrVerificationOrReverification(member, newMfaType);

    // If the proposed MFA method or type is None or Authenticator, update the setup response
    if (checkIfProposedMfaMethodOrTypeIsEmptyOrAuthenticator(newMfaType)) {
      return updateSetupMfaResponseIfProposedMfaMethodOrTypeIsEmptyOrAuthenticator(setupMfaResponse, newMfaType);
    }

    // Otherwise, send a new MFA verification code or generate secret key and QR code
    sendMfaVerificationCodeRequestOrGenerateSecretKeyAndQrCode(member, setupMfaResponse, newMfaType);
    // Update the setup response with Authenticator secret if applicable
    updateMfaSetupResponseAndIfPossibleSetMfaAuthenticatorSecret(newMfaType, setupMfaResponse, member);

    return localizedResponse.of(setupMfaResponse);
  }

  /**
   * Confirms the setup of MFA for the user based on the provided verification code.
   *
   * @param confirmSetupMfaDto DTO containing the actual MFA type and verification code.
   * @param user The authenticated FleenUser for whom MFA setup is being confirmed.
   */
  @Override
  public ConfirmMfaSetupResponse confirmMfaSetup(final ConfirmSetupMfaDto confirmSetupMfaDto, final FleenUser user) {
    final String emailAddress = user.getEmailAddress();
    final String username = user.getUsername();
    final MfaType mfaType = confirmSetupMfaDto.getActualMfaType();
    final Member member = memberRepository.findByEmailAddress(emailAddress)
      .orElseThrow(FailedOperationException::new);

    // Check and verify the provided MFA type's OTP or verification code
    checkMfaTypeAndVerifyMfaOtpOrVerificationCode(username, mfaType, confirmSetupMfaDto.getVerificationCode(), member.getMfaSecret());
    // Verify the user with the provided MFA type
    member.verifyUserMfa(mfaType);
    // Update setup response and set Authenticator secret if applicable
    updateMfaSetupResponseAndIfPossibleSetMfaAuthenticatorSecret(member, mfaType);
    // Save the updated member details
    memberRepository.save(member);

    return localizedResponse.of(ConfirmMfaSetupResponse.of());
  }

  /**
   * Validates Multi-Factor Authentication (MFA) setup code for the specified user and MFA type.
   * Uses the provided verification code to validate and clear the setup OTP if validation succeeds.
   *
   * @param username The username or email address of the user for whom MFA setup code is being validated.
   * @param code The verification code for MFA setup validation.
   * @param mfaType The type of MFA for which the code is being validated.
   */
  protected void validateEmailOrPhoneMfaSetupCode(final String username, final String code, final MfaType mfaType) {
    final String verificationKey = getMfaSetupCacheKey(username, mfaType);
    // Verify the code through email or phone verification type
    validateEmailOrPhoneVerificationCode(verificationKey, code);
    // Clear the otp if the verification of the code pass
    clearMfaSetupOtp(username, mfaType);
  }

  /**
   * <p>Validate a code like OTP by checking if it exists in the DB or cache for example {@link CacheService} and confirm if it is equal to the code saved in
   * the store.</p>
   *
   * @param verificationKey the key to check for the existence of the verification code and the validity of the code associated with it
   * @param code the code to validate against the code saved and associated with the verification key
   */
  protected void validateEmailOrPhoneVerificationCode(final String verificationKey, final String code) {
    // Check if the verification code exists
    if (!cacheService.exists(verificationKey)) {
      throw ExpiredVerificationCodeException.of(code);
    }
    // Check if the provided verification code is valid and equal to the saved verification code
    if (!cacheService.get(verificationKey).equals(code)) {
      throw new InvalidVerificationCodeException(code);
    }
  }

  /**
   * Clears the MFA setup OTP for the specified user and MFA type.
   *
   * @param username the username of the user for whom the MFA setup OTP is to be cleared.
   * @param mfaType  the type of MFA for which the OTP is to be cleared.
   */
  protected void clearMfaSetupOtp(final String username, final MfaType mfaType) {
    // Delete the OTP from the cache for the given username and MFA type
    cacheService.delete(getMfaSetupCacheKey(username, mfaType));
  }

  /**
   * Verifies an authentication code against the provided secret key for Multi-Factor Authentication (MFA).
   *
   * @param code The authentication code to be verified.
   * @param secret The secret key associated with the user for MFA.
   * @return true if the code is valid, false otherwise.
   */
  protected boolean verifyAuthenticatorOtp(final String code, final String secret) {
    return otpService.validateOtpCode(code, secret);
  }

  /**
   * Checks if the specified Multi-Factor Authentication (MFA) type is associated with SMS or email.
   *
   * @param mfaType The MFA type to be checked.
   * @return true if the MFA type is SMS or email, false otherwise.
   */
  public boolean isPhoneOrEmailMfaType(final MfaType mfaType) {
    return MfaType.isPhoneOrEmail(mfaType);
  }

  /**
   * Validates Multi-Factor Authentication (MFA) code for SMS or email verification.
   *
   * @param username The username or email address associated with the MFA setup.
   * @param code The verification code for MFA validation.
   */
  public void validateEmailOrPhoneMfaVerificationCode(final String username, final String code) {
    final String verificationKey = getMfaAuthenticationCacheKey(username);
    validateEmailOrPhoneVerificationCode(verificationKey, code);
  }

  /**
   * Checks if the provided MFA type is an authenticator.
   *
   * <p>This method compares the given {@link MfaType} with the predefined {@code AUTHENTICATOR} type
   * to determine if they are the same.</p>
   *
   * @param mfaType the multi-factor authentication type to be checked.
   * @return {@code true} if the provided MFA type is {@code AUTHENTICATOR}; {@code false} otherwise.
   */
  @Override
  public boolean isAuthenticatorMfaType(final MfaType mfaType) {
    return MfaType.isAuthenticator(mfaType);
  }

  /**
   * Validates the multi-factor authentication (MFA) verification code for the authenticator.
   *
   * <p>This method retrieves the user's two-factor authentication secret from the repository using the user ID.
   * If the secret is not found, it throws a {@link MfaVerificationFailed} exception. The provided OTP code
   * is then validated against the retrieved secret. If the code is invalid, an {@link InvalidVerificationCodeException}
   * is thrown, including the invalid OTP code.</p>
   *
   * @param otpCode the one-time password (OTP) code to be validated.
   * @param userId the ID of the user whose authenticator code is being verified.
   * @throws MfaVerificationFailed if the two-factor authentication secret is not found for the user.
   * @throws InvalidVerificationCodeException if the provided OTP code is invalid.
   */
  @Override
  public void validateAuthenticatorMfaVerificationCode(final String otpCode, final Long userId) {
    final String secret = mfaRepository.getTwoFaSecret(userId)
      .orElseThrow(MfaVerificationFailed::new);

    // Validate the provided otp code against the multifactor secret key
    final boolean isValid = verifyAuthenticatorOtp(otpCode, secret);
    if (!isValid) {
      throw new InvalidVerificationCodeException(otpCode);
    }
  }

  /**
   * Completes the MFA setup without verification if the proposed and current MFA types are the same.
   *
   * @param setupMfaResponse the MFA setup response to update
   * @return the updated setup MFA response
   */
  private SetupMfaResponse completeMfaSetupWithoutVerificationIfProposedAndCurrentMfaTypeIsSame(final SetupMfaResponse setupMfaResponse) {
    // Enable MFA and set the setup status to COMPLETE
    setupMfaResponse.setMfaSetupStatus(MfaSetupStatus.COMPLETE);
    // Get Mfa Enabled Info
    final IsMfaEnabledInfo mfaEnabledInfo = commonMapper.toIsMfaEnabledInfo(true);
    // Update the setup response and return
    setupMfaResponse.setIsMfaEnabledInfo(mfaEnabledInfo);
    return setupMfaResponse;
  }

  /**
   * Completes Multi-Factor Authentication (MFA) setup for the specified member based on the provided MFA type.
   * Updates the member's MFA details and saves the changes.
   *
   * @param member The member for whom MFA setup is being completed.
   * @param mfaType The type of MFA for which the setup is completed.
   */
  private void updateMfaSetupResponseAndIfPossibleSetMfaAuthenticatorSecret(final Member member, final MfaType mfaType) {
    member.setMfaEnabled(true);
    member.setMfaType(mfaType);
    if (MfaType.isNotAuthenticator(mfaType)) {
      member.setMfaSecret(null);
    }
    memberRepository.save(member);
  }

  /**
   * Checks if the MFA (Multi-Factor Authentication) method or type is not set to NONE.
   *
   * @param mfaType the MFA type to check
   * @return true if the MFA type is not NONE, false otherwise
   */
  protected boolean isMfaMethodOrTypeNotEmpty(final MfaType mfaType) {
    return MfaType.isNotNone(mfaType); // Check if the MFA type is not NONE
  }

  /**
   * Initializes and sets up the MFA response before completion or setup verification.
   *
   * @param member the member for whom MFA is being set up
   * @param proposedMfaType the proposed MFA type
   * @return the setup MFA response
   */
  protected SetupMfaResponse initializeAndSetupMfaResponseBeforeCompletionOrVerificationOrReverification(final Member member, final MfaType proposedMfaType) {
    // Get Mfa Enabled Info
    final IsMfaEnabledInfo mfaEnabledInfo = commonMapper.toIsMfaEnabledInfo(false);
    // Get Mfa Type Info
    final MfaTypeInfo mfaTypeInfo = commonMapper.toMfaTypeInfo(proposedMfaType);
    // Build and return the setup MFA response
    return SetupMfaResponse.of(member.getEmailAddress(), member.getPhoneNumber(), MfaSetupStatus.IN_PROGRESS, mfaEnabledInfo, mfaTypeInfo);
  }

  /**
   * Checks if the proposed MFA type to set is the same as the existing MFA type.
   *
   * @param newMfaType the new MFA type being proposed
   * @param currentMfaType the current MFA type
   * @return true if the proposed MFA type is the same as the existing one, false otherwise
   */
  protected boolean isProposedMfaTypeToSetSameAsExistingMfaType(final MfaType newMfaType, final MfaType currentMfaType) {
    // Check if the proposed MFA type is the same as the existing MFA type and is not AUTHENTICATOR or NONE
    return currentMfaType == newMfaType
        && MfaType.isNotAuthenticator(newMfaType)
        && MfaType.isNotNone(currentMfaType);
  }

  /**
   * Checks if the MFA type is NONE, indicating no MFA method is set.
   *
   * @param mfaType the MFA type to check
   * @return true if the MFA type is NONE, false otherwise
   */
  private boolean isMfaMethodOrTypeEmpty(final MfaType mfaType) {
    return MfaType.isNone(mfaType); // Check if the MFA type is NONE
  }

  /**
   * Resets the MFA type for a member to default (MfaType.NONE).
   *
   * @param member the member whose MFA type will be reset
   */
  protected void resetMfaTypeToDefault(final Member member) {
    member.setMfaType(MfaType.NONE);
  }

  /**
   * Updates the MFA setup response if the proposed MFA method or type is empty (MfaType.NONE).
   * Marks the setup status as complete if the new MFA type is NONE.
   *
   * @param setupMfaResponse the setup MFA response to update
   * @param newMfaType       the proposed new MFA type
   */
  protected void updateMfaSetupResponseIfProposedMfaMethodOrTypeIsEmpty(final SetupMfaResponse setupMfaResponse, final MfaType newMfaType) {
    if (MfaType.isNone(newMfaType)) {
      setupMfaResponse.setMfaSetupStatus(MfaSetupStatus.COMPLETE);
    }
  }

  /**
   * Sends an MFA verification code request or generates a secret key and QR code for MFA setup.
   *
   * @param member the member for whom the verification or setup is being performed
   * @param setupMfaResponse the response object to update with generated details
   * @param mfaType the type of MFA setup being performed (PHONE, EMAIL, or AUTHENTICATOR)
   */
  public void sendMfaVerificationCodeRequestOrGenerateSecretKeyAndQrCode(final Member member, final SetupMfaResponse setupMfaResponse, final MfaType mfaType) {
    switch (mfaType) {
      case EMAIL -> saveAndSendMfaVerificationCodeRequest(member, VerificationType.EMAIL, MfaType.EMAIL);
      case PHONE -> saveAndSendMfaVerificationCodeRequest(member, VerificationType.PHONE, MfaType.PHONE);
      case AUTHENTICATOR -> {
        final MfaAuthenticatorSecurityInfo mfaAuthenticator = generateAuthenticatorSecretDetails();
        setupMfaResponse.setSecret(mfaAuthenticator.getSecret());
        setupMfaResponse.setQrCode(mfaAuthenticator.getQrCode());
      }
    }
  }

  /**
   * Saves the MFA setup OTP or verification code temporarily in the cache and sends a verification code request.
   *
   * @param member the member for whom the verification code is being sent
   * @param verificationType the type of verification (e.g., EMAIL or SMS)
   * @param mfaType the MFA type for which the verification code is being sent
   */
  public void saveAndSendMfaVerificationCodeRequest(final Member member, final VerificationType verificationType, final MfaType mfaType) {
    // Generate a random six-digit OTP
    final String otpCode = getRandomSixDigitOtp();
    final FleenUser user = FleenUser.fromMemberBasic(member);

    // Create MFA verification request to send otp code to user
    final MfaSetupVerificationRequest mfaVerificationRequest = MfaSetupVerificationRequest
        .of(otpCode, user.getFirstName(), user.getLastName(), user.getEmailAddress(), user.getPhoneNumber(), verificationType);

    // Send MFA verification code request
    profileRequestPublisher.publishMessage(PublishMessageRequest.of(mfaVerificationRequest));

    // Save OTP or verification code temporarily
    saveMfaSetupOtpOrVerificationCodeTemporarily(member.getEmailAddress(), otpCode, mfaType);
  }

  /**
   * Saves the MFA setup OTP or verification code temporarily in the cache.
   *
   * @param username the username of the user for whom the code is being saved
   * @param otp the OTP or verification code to be saved
   * @param mfaType the MFA type for which the code is being saved
   */
  protected void saveMfaSetupOtpOrVerificationCodeTemporarily(final String username, final String otp, final MfaType mfaType) {
    final String key = getMfaSetupCacheKey(username, mfaType);
    cacheService.set(key, otp, Duration.ofMinutes(5));
  }

  /**
   * Generates the secret key, authenticator app authentication URI, and QR code data URI for setting up MFA.
   *
   * @return the MfaAuthenticatorSecurityInfo containing QR code data URI and secret key
   * @throws MfaGenerationFailedException if QR code data URI cannot be generated
   */
  protected MfaAuthenticatorSecurityInfo generateAuthenticatorSecretDetails() {
    // Generate a secret key for the authenticator app
    final String secretKey = generateSecretKey();
    // Generate authentication URI for the authenticator app using the secret key
    final String authUri = getAuthenticatorAuthUri(secretKey);
    // Generate QR code data URI for displaying in the authenticator app
    final String qrCodeDataUri = getQrImageDataUri(authUri)
        .orElseThrow(MfaGenerationFailedException::new);

    // Return MFA authenticator security information including QR code and secret key
    return MfaAuthenticatorSecurityInfo.of(qrCodeDataUri, secretKey);
  }

  /**
   * Generates a secret key for use in MFA (Multi-Factor Authentication).
   * Delegates the actual key generation to the OTP (One-Time Password) service.
   *
   * @return Generated secret key.
   */
  public String generateSecretKey() {
    return otpService.generateSecretKey();
  }

  /**
   * Generates the authenticator app authentication URI using the provided secret key.
   *
   * @param secretKey the secret key used for generating OTP
   * @return the authenticator app authentication URI
   */
  protected String getAuthenticatorAuthUri(final String secretKey) {
    return otpService.getOtpAuthURL(secretKey, mfaProperties.getSecretLabel(), mfaProperties.getSecretIssuer());
  }

  /**
   * Retrieves the QR image data URI for the provided authentication URI.
   *
   * @param authUri the authentication URI for generating QR code
   * @return an optional containing the QR image data URI, or empty if generation fails
   */
  protected Optional<String> getQrImageDataUri(final String authUri) {
    return Optional.of(otpService.getQRImageDataURI(authUri));
  }

  /**
   * Updates the MFA setup response and, if the MFA type is AUTHENTICATOR, sets the authenticator secret in the member.
   *
   * @param mfaType           the MFA type to check
   * @param setupMfaResponse  the setup MFA response to update
   * @param member            the member entity to update with MFA secret
   */
  protected void updateMfaSetupResponseAndIfPossibleSetMfaAuthenticatorSecret(final MfaType mfaType, final SetupMfaResponse setupMfaResponse, final Member member) {
    if (MfaType.isAuthenticator(mfaType)) {
      member.setMfaSecret(setupMfaResponse.getSecret());
    }
  }

  /**
   * Updates the MFA setup response if the proposed MFA method or type is Authenticator.
   *
   * <p>If the new MFA type is set to Authenticator, this method sets the MFA setup status
   * in the provided setup response to indicate that the setup process is complete.</p>
   *
   * @param setupMfaResponse The setup response object to update.
   * @param newMfaType The proposed new MFA type.
   */
  protected void updateMfaSetupResponseIfProposedMfaMethodOrTypeIsAuthenticator(final SetupMfaResponse setupMfaResponse, final MfaType newMfaType) {
    if (MfaType.isAuthenticator(newMfaType)) {
      setupMfaResponse.setMfaSetupStatus(MfaSetupStatus.COMPLETE);
    }
  }

  /**
   * Checks if the proposed MFA method or type is either Authenticator or None.
   *
   * <p>This method returns {@code true} if the new MFA type is set to Authenticator
   * or if it is set to None, indicating that no MFA method is to be used.</p>
   *
   * @param newMfaType The proposed new MFA type to check.
   * @return {@code true} if the new MFA type is Authenticator or None, {@code false} otherwise.
   */
  protected boolean checkIfProposedMfaMethodOrTypeIsEmptyOrAuthenticator(final MfaType newMfaType) {
    return MfaType.isAuthenticator(newMfaType) || MfaType.isNone(newMfaType);
  }

  /**
   * Updates the SetupMfaResponse based on the proposed MFA method or type being None or Authenticator.
   *
   * @param setupMfaResponse The SetupMfaResponse object to update.
   * @param newMfaType The proposed new MFA type to check against None or Authenticator.
   * @return The updated SetupMfaResponse reflecting the changes based on the new MFA type.
   */
  protected SetupMfaResponse updateSetupMfaResponseIfProposedMfaMethodOrTypeIsEmptyOrAuthenticator(final SetupMfaResponse setupMfaResponse, final MfaType newMfaType) {
    // If the new MFA type is None, update the response accordingly
    if (MfaType.isNone(newMfaType)) {
      updateMfaSetupResponseIfProposedMfaMethodOrTypeIsEmpty(setupMfaResponse, newMfaType);
    }
    // If the new MFA type is Authenticator, update the response for complete setup
    else if (MfaType.isAuthenticator(newMfaType)) {
      updateMfaSetupResponseIfProposedMfaMethodOrTypeIsAuthenticator(setupMfaResponse, newMfaType);
    }
    return setupMfaResponse;
  }

  /**
   * Checks the MFA type and verifies the OTP or verification code based on the MFA type.
   *
   * @param username The username associated with the MFA setup.
   * @param mfaType The type of MFA (Authenticator, Phone, or Email).
   * @param verificationCode The OTP or verification code to be validated.
   * @param mfaSecret The secret key for Authenticator MFA type, ignored for other types.
   */
  protected void checkMfaTypeAndVerifyMfaOtpOrVerificationCode(final String username, final MfaType mfaType, final String verificationCode, final String mfaSecret) {
    // Validate Authenticator MFA code if the MFA type is Authenticator
    if (isAuthenticatorMfaType(mfaType)) {
      validateAuthenticatorMfaCode(verificationCode, mfaSecret);
    }

    // Validate Email or Phone MFA setup code if the MFA type is Phone or Email
    if (isPhoneOrEmailMfaType(mfaType)) {
      validateEmailOrPhoneMfaSetupCode(username, verificationCode, mfaType);
    }
  }

  /**
   * Validates the provided OTP code against the MFA secret using the Authenticator method.
   *
   * @param otpCode The OTP code entered by the user for verification.
   * @param mfaSecret The secret key associated with the Authenticator MFA method.
   * @throws InvalidVerificationCodeException If the OTP code is invalid or verification fails.
   */
  protected void validateAuthenticatorMfaCode(final String otpCode, final String mfaSecret) {
    final boolean isValid = verifyAuthenticatorOtp(otpCode, mfaSecret);

    // Throw exception if OTP code is invalid
    if (!isValid) {
      throw new InvalidVerificationCodeException(otpCode);
    }
  }

}
