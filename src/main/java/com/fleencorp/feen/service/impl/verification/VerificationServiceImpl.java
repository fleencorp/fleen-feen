package com.fleencorp.feen.service.impl.verification;

import com.fleencorp.base.service.i18n.LocalizedResponse;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.event.model.base.PublishMessageRequest;
import com.fleencorp.feen.event.publisher.ProfileRequestPublisher;
import com.fleencorp.feen.exception.auth.AlreadySignedUpException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.auth.ResendSignUpVerificationCodeDto;
import com.fleencorp.feen.model.dto.security.mfa.ResendMfaVerificationCodeDto;
import com.fleencorp.feen.model.request.auth.SignUpVerificationRequest;
import com.fleencorp.feen.model.request.mfa.MfaVerificationRequest;
import com.fleencorp.feen.model.response.auth.ResendSignUpVerificationCodeResponse;
import com.fleencorp.feen.model.response.security.mfa.ResendMfaVerificationCodeResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.feen.service.verification.VerificationService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.feen.service.impl.common.CacheKeyService.getMfaAuthenticationCacheKey;
import static com.fleencorp.feen.service.impl.common.CacheKeyService.getSignUpVerificationCacheKey;
import static com.fleencorp.feen.service.security.OtpService.generateOtp;

@Service
@Primary
public class VerificationServiceImpl implements VerificationService {

  private final CacheService cacheService;
  private final ProfileRequestPublisher profileRequestPublisher;
  private final LocalizedResponse localizedResponse;

  public VerificationServiceImpl(
      CacheService cacheService,
      ProfileRequestPublisher profileRequestPublisher,
      LocalizedResponse localizedResponse) {
    this.cacheService = cacheService;
    this.profileRequestPublisher = profileRequestPublisher;
    this.localizedResponse = localizedResponse;
  }

  @Override
  public CacheService getCacheService() {
    return cacheService;
  }

  /**
   * Resends the sign-up verification code to the user.
   *
   * <p>This method generates a new OTP, prepares a request to resend the sign-up verification code
   * with user details, publishes the request to a profile request publisher, and saves the newly generated
   * verification code temporarily for the user.</p>
   *
   * @param resendSignUpVerificationCodeDto the DTO containing details for resending the verification code
   * @param user the authenticated user requesting to resend the verification code
   * @return ResendSignUpVerificationCodeResponse indicating the successful initiation of code resend
   */
  @Override
  @Transactional
  public ResendSignUpVerificationCodeResponse resendSignUpVerificationCode(final ResendSignUpVerificationCodeDto resendSignUpVerificationCodeDto, final FleenUser user) {
    // Generate a new OTP
    final String otpCode = generateOtp();
    // Verify if the two provided email addresses is the same
    validateAndCheckIfEmailsInRequestAndAuthenticatedUserAreSame(resendSignUpVerificationCodeDto.getEmailAddress(), user.getEmailAddress());
    // Check if user is already sign up and profile is active
    checkIfSignUpIsAlreadyCompleted(user.toMember());

    // Prepare the request to resend the sign-up verification code
    final VerificationType verificationType = resendSignUpVerificationCodeDto.getActualVerificationType();
    // Send sign up verification message to user
    sendSignUpVerificationMessage(otpCode, verificationType, user);
    // Save the newly generated verification code temporarily for the user
    saveSignUpVerificationCodeTemporarily(user.getUsername(), otpCode);
    // Return response indicating the successful initiation of code resend
    return localizedResponse.of(ResendSignUpVerificationCodeResponse.of());
  }

  /**
   * Validates that the provided email addresses are not null and checks if they match.
   *
   * <p>This method ensures that neither requestEmailAddress nor authenticatedUserEmailAddress
   * is null. If either is null, an UnableToCompleteOperationException is thrown.
   * It then compares the two email addresses and throws a FailedOperationException if they are not the same.</p>
   *
   * @param requestEmailAddress the email address from the request. It must not be null.
   * @param authenticatedUserEmailAddress the email address of the authenticated user. It must not be null.
   * @throws FailedOperationException if either requestEmailAddress or authenticatedUserEmailAddress is null.
   * @throws FailedOperationException if requestEmailAddress does not match authenticatedUserEmailAddress.
   */
  private void validateAndCheckIfEmailsInRequestAndAuthenticatedUserAreSame(final String requestEmailAddress, final String authenticatedUserEmailAddress) {
    // Check if either email address is null and throw an exception if so
    checkIsNullAny(List.of(requestEmailAddress, authenticatedUserEmailAddress), FailedOperationException::new);

    // Compare the email addresses and throw an exception if they don't match
    if (!authenticatedUserEmailAddress.equals(requestEmailAddress)) {
      throw new FailedOperationException();
    }
  }

  /**
   * Checks if the sign-up process is already completed for a member.
   *
   * <p>This method first checks if the provided member object is null. If it is, it throws an UnableToCompleteOperationException.</p>
   *
   * <p>Then, it checks the profile status of the member. If the profile status is not null and indicates that the member is already active,
   * it throws an AlreadySignedUpException. This ensures that the sign-up process is not repeated for an already active member.</p>
   *
   * @param member the Member object whose sign-up status is to be checked
   * @throws FailedOperationException if the member object is null
   * @throws AlreadySignedUpException if the member is already signed up and active
   */
  public void checkIfSignUpIsAlreadyCompleted(final Member member) {
    // Throw an exception if the provided member is null
    checkIsNull(member, FailedOperationException::new);

    // Check if the member status indicates that the member is already signed up
    if (member.isProfileActiveAndApproved()) {
      throw new AlreadySignedUpException();
    }
  }

  /**
   * Sends a sign-up verification message to the user.
   *
   * <p>This method prepares a {@link SignUpVerificationRequest} with the provided OTP code,
   * verification type, and user details, then publishes the request to send the verification message to the user.</p>
   *
   * @param otpCode the one-time password (OTP) code to be sent for sign-up verification
   * @param verificationType the type of verification to be used (e.g., email, phone)
   * @param user the {@link FleenUser} whose details (e.g., name, email) are included in the verification request
   */
  @Override
  public void sendSignUpVerificationMessage(final String otpCode, final VerificationType verificationType, final FleenUser user) {
    // Prepare and send sign-up verification code request
    final SignUpVerificationRequest signUpVerificationRequest = createSignUpVerificationRequest(otpCode, verificationType, user);
    // Publish to send message to user
    profileRequestPublisher.publishMessage(PublishMessageRequest.of(signUpVerificationRequest));
  }

  /**
   * Saves the sign-up verification code temporarily in the cache.
   * The verification code is associated with the provided username and expires after 5 minutes.
   *
   * @param username         The username for which the verification code is saved.
   * @param verificationCode The verification code to be saved.
   */
  @Override
  public void saveSignUpVerificationCodeTemporarily(final String username, final String verificationCode) {
    cacheService.set(getSignUpVerificationCacheKey(username), verificationCode, Duration.ofMinutes(5));
  }

  /**
   * Resends the MFA (Multi-Factor Authentication) verification code to the user.
   *
   * <p>This method generates a new OTP, prepares a request to resend the MFA verification code
   * with user details, publishes the request to a profile request publisher, and saves the newly generated
   * verification code temporarily for the user.</p>
   *
   * @param resendMfaVerificationCodeDto the DTO containing details for resending the MFA verification code
   * @param user the authenticated user requesting to resend the MFA verification code
   * @return ResendMfaVerificationCodeResponse indicating the successful initiation of code resend
   */
  @Override
  public ResendMfaVerificationCodeResponse resendMfaVerificationCode(final ResendMfaVerificationCodeDto resendMfaVerificationCodeDto, final FleenUser user) {
    // Generate a new OTP
    final String otpCode = generateOtp();

    // Prepare the request to resend the MFA verification code
    sendResendMfaVerificationMessage(resendMfaVerificationCodeDto, user, otpCode);
    // Save the newly generated verification code temporarily for the user
    saveMfaVerificationCodeTemporarily(user.getUsername(), otpCode);
    // Return response indicating the successful initiation of code resend
    return localizedResponse.of(ResendMfaVerificationCodeResponse.of());
  }

  /**
   * Sends a request to resend the MFA verification code.
   *
   * <p>This method creates a {@link MfaVerificationRequest} with the provided OTP code and
   * user details (first name, last name, email address, and phone number). It also includes
   * the actual verification type from the {@code resendMfaVerificationCodeDto}.
   * The request is then published to the profile request publisher to resend the MFA verification code.</p>
   *
   * @param resendMfaVerificationCodeDto the DTO containing details for resending the MFA verification code
   * @param user the {@link FleenUser} whose details (e.g., name, email, phone number) are included in the request
   * @param otpCode the one-time password (OTP) code to be resent for MFA verification
   */
  protected void sendResendMfaVerificationMessage(final ResendMfaVerificationCodeDto resendMfaVerificationCodeDto, final FleenUser user, final String otpCode) {
    final MfaVerificationRequest resendMfaVerificationCodeRequest = MfaVerificationRequest
      .of(otpCode, user.getFirstName(), user.getLastName(), user.getEmailAddress(), user.getPhoneNumber(), resendMfaVerificationCodeDto.getActualVerificationType());
    // Resend mfa verification code request
    profileRequestPublisher.publishMessage(PublishMessageRequest.of(resendMfaVerificationCodeRequest));
  }

  /**
   * Saves the MFA verification code temporarily in the cache.
   *
   * <p>Sets the MFA verification code for the specified username in the cache with a temporary
   * duration of 5 minutes.</p>
   *
   * @param username the username for which the MFA verification code is saved
   * @param verificationCode the MFA verification code to be saved
   */
  @Override
  public void saveMfaVerificationCodeTemporarily(final String username, final String verificationCode) {
    cacheService.set(getMfaAuthenticationCacheKey(username), verificationCode, Duration.ofMinutes(5));
  }

  /**
   * Creates a sign-up verification request.
   *
   * @param otp              the one-time password for verification
   * @param verificationType the type of verification (e.g., EMAIL)
   * @param user             the FleenUser for whom the verification request is being created
   * @return the SignUpVerificationRequest object
   */
  @Override
  public SignUpVerificationRequest createSignUpVerificationRequest(final String otp, final VerificationType verificationType, final FleenUser user) {
    // Create and return the sign-up verification request with user details and verification type
    return SignUpVerificationRequest
      .of(otp, user.getFirstName(), user.getLastName(), user.getEmailAddress(), user.getPhoneNumber(), verificationType);
  }

}
