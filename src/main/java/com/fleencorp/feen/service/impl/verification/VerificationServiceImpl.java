package com.fleencorp.feen.service.impl.verification;

import com.fleencorp.base.service.i18n.LocalizedResponse;
import com.fleencorp.feen.constant.security.auth.AuthenticationStatus;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.event.model.base.PublishMessageRequest;
import com.fleencorp.feen.event.publisher.ProfileRequestPublisher;
import com.fleencorp.feen.exception.auth.AlreadySignedUpException;
import com.fleencorp.feen.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.user.UserNotFoundException;
import com.fleencorp.feen.exception.verification.ResetPasswordCodeExpiredException;
import com.fleencorp.feen.exception.verification.ResetPasswordCodeInvalidException;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.domain.user.ProfileToken;
import com.fleencorp.feen.model.dto.auth.ResendSignUpVerificationCodeDto;
import com.fleencorp.feen.model.dto.auth.ResetPasswordDto;
import com.fleencorp.feen.model.dto.security.mfa.ConfirmMfaVerificationCodeDto;
import com.fleencorp.feen.model.dto.security.mfa.ResendMfaVerificationCodeDto;
import com.fleencorp.feen.model.request.auth.SignUpVerificationRequest;
import com.fleencorp.feen.model.request.mfa.MfaVerificationRequest;
import com.fleencorp.feen.model.response.auth.ResendSignUpVerificationCodeResponse;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.country.CountryResponse;
import com.fleencorp.feen.model.response.security.InitiatePasswordChangeResponse;
import com.fleencorp.feen.model.response.security.mfa.ResendMfaVerificationCodeResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.security.ProfileTokenRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.feen.service.security.TokenService;
import com.fleencorp.feen.service.security.mfa.MfaService;
import com.fleencorp.feen.service.verification.VerificationService;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.feen.service.impl.common.CacheKeyService.*;
import static com.fleencorp.feen.service.security.OtpService.generateOtp;
import static java.util.Objects.nonNull;

@Service
@Primary
public class VerificationServiceImpl implements VerificationService {

  private final CacheService cacheService;
  private final CountryService countryService;
  private final ProfileRequestPublisher profileRequestPublisher;
  private final LocalizedResponse localizedResponse;
  private final MemberRepository memberRepository;
  private final MfaService mfaService;
  private final TokenService tokenService;
  private final ProfileTokenRepository profileTokenRepository;
  private final VerificationService verificationService;

  public VerificationServiceImpl(
    final CacheService cacheService,
    final CountryService countryService,
    final ProfileRequestPublisher profileRequestPublisher,
    final LocalizedResponse localizedResponse,
    final MemberRepository memberRepository,
    final MfaService mfaService,
    final TokenService tokenService,
    final ProfileTokenRepository profileTokenRepository,
    final VerificationService verificationService) {
    this.cacheService = cacheService;
    this.countryService = countryService;
    this.profileRequestPublisher = profileRequestPublisher;
    this.localizedResponse = localizedResponse;
    this.memberRepository = memberRepository;
    this.mfaService = mfaService;
    this.tokenService = tokenService;
    this.profileTokenRepository = profileTokenRepository;
    this.verificationService = verificationService;
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

  /**
   * Verifies the Multi-Factor Authentication (MFA) verification code provided by the user,
   * authenticates the user, and generates access and refresh tokens.
   *
   * @param confirmMfaCodeDto the DTO containing the verification code and MFA type to confirm
   * @param user              the authenticated user performing the action
   * @return SignInResponse containing the generated access and refresh tokens
   * @throws InvalidAuthenticationException if the user cannot be found or authenticated
   */
  @Override
  public SignInResponse verifyMfaVerificationCodeAndAuthenticateUser(final ConfirmMfaVerificationCodeDto confirmMfaCodeDto, final FleenUser user) {
    final String username = user.getUsername();
    final Member member = memberRepository.findByEmailAddress(username)
      .orElseThrow(InvalidAuthenticationException.of(username));

    // Validate the provided MFA verification code based on its type
    validateMfaVerificationOrOtpCode(confirmMfaCodeDto.getVerificationCode(), confirmMfaCodeDto.getActualMfaType(), username, member.getMemberId());
    // Initialize authentication and context for the authenticated user
    final FleenUser authenticatedUser = initializeAuthenticationAndContext(member);
    // Set user timezone after authentication
    setUserTimezoneAfterAuthentication(authenticatedUser);
    // Create access and refresh tokens for the authenticated user
    final String accessToken = tokenService.createAccessToken(authenticatedUser, AuthenticationStatus.COMPLETED);
    final String refreshToken = tokenService.createRefreshToken(authenticatedUser);

    // Clear temporarily saved MFA verification code
    clearMfaVerificationCodeSavedTemporarily(username);
    // Save authentication tokens to repository or cache
    saveAuthenticationTokensToRepositoryOrCache(username, accessToken, refreshToken);
    // Return SignInResponse with access and refresh tokens
    return localizedResponse.of(SignInResponse.of(accessToken, refreshToken));
  }

  /**
   * Validates the Multi-Factor Authentication (MFA) verification code or OTP (One-Time Password) code
   * based on the provided MFA type and username.
   *
   * @param otpCode  the MFA or OTP code to validate
   * @param mfaType  the type of MFA (phone/email or authenticator app)
   * @param username the username associated with the MFA or OTP code
   */
  private void validateMfaVerificationOrOtpCode(final String otpCode, final MfaType mfaType, final String username, final Long userId) {
    if (mfaService.isPhoneOrEmailMfaType(mfaType)) {
      // Validate email/phone MFA verification code
      mfaService.validateEmailOrPhoneMfaVerificationCode(username, otpCode);
    } else if (mfaService.isAuthenticatorMfaType(mfaType)) {
      // Validate authenticator app MFA verification code
      mfaService.validateAuthenticatorMfaVerificationCode(otpCode, userId);
    }
  }

  /**
   * Initializes authentication and sets the authentication context for the provided member.
   * Throws {@link FailedOperationException} if the member is null.
   *
   * @param member The member for whom authentication is to be initialized.
   * @return The {@link FleenUser} associated with the member after authentication and context setup.
   * @throws FailedOperationException If the member is null.
   */
  @Override
  public FleenUser initializeAuthenticationAndContext(final Member member) {
    // Throw an exception if the provided member is null
    checkIsNull(member, FailedOperationException::new);

    // Create FleenUser from Member
    final FleenUser user = FleenUser.fromMember(member);
    // Create Authentication object
    final Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    // Set Authentication object in context (example: Spring Security context)
    setContext(authentication);
    // Return the authenticated user
    return user;
  }

  /**
   * Sets the authentication context in the security context holder if the authentication
   * object is not null and is authenticated.
   *
   * @param authentication The authentication object to set in the security context holder.
   */
  @Override
  public void setContext(final Authentication authentication) {
    if (nonNull(authentication) && authentication.isAuthenticated()) {
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
  }

  /**
   * Sets the user's timezone after successful authentication based on the user's country.
   *
   * <p>If the provided user is not null, the method retrieves the country information
   * from the cache using the user's country code. If the country information is found,
   * it updates the user's timezone based on the corresponding country data.</p>
   *
   * @param user the {@link FleenUser} object representing the authenticated user.
   *             If the user is {@code null}, the method will not attempt to set the timezone.
   */
  protected void setUserTimezoneAfterAuthentication(final FleenUser user) {
    // Check if the user is not null before proceeding
    if (nonNull(user)) {
      // Retrieve the country information from the cache based on the user's country code and set the user timezone
      countryService.getCountryFromCache(user.getCountry())
        .map(CountryResponse::getTimezone)
        .ifPresent(user::setTimezone);
    }
  }
  /**
   * Clears the temporarily saved MFA (Multi-Factor Authentication) verification code
   * from the cache associated with the given username.
   *
   * @param username the username associated with the MFA verification code to clear
   */
  private void clearMfaVerificationCodeSavedTemporarily(final String username) {
    // Retrieve the cache key for the sign-up verification code
    final String key = getMfaAuthenticationCacheKey(username);
    // Delete the verification code from the cache
    cacheService.delete(key);
  }

  /**
   * Saves the access token and refresh token for the given username using the JWT service.
   *
   * @param username      The username for which the tokens are saved.
   * @param accessToken   The access token to be saved.
   * @param refreshToken  The refresh token to be saved.
   */
   void saveAuthenticationTokensToRepositoryOrCache(final String username, final String accessToken, final String refreshToken) {
    // Save the access token for the username using the JWT service.
    tokenService.saveAccessToken(username, accessToken);
    // Save the refresh token for the username using the JWT service.
    tokenService.saveRefreshToken(username, refreshToken);
  }

  /**
   * Validates the reset password code provided by the user and initiates the password change process.
   *
   * @param resetPasswordDto the DTO containing the reset password information
   * @return an InitiatePasswordChangeResponse containing the reset password token
   * @throws UserNotFoundException if the user with the provided email address is not found
   * @throws ResetPasswordCodeInvalidException if the reset password code is invalid
   * @throws ResetPasswordCodeExpiredException if the reset password code has expired
   */
  @Override
  public InitiatePasswordChangeResponse verifyResetPasswordCode(final ResetPasswordDto resetPasswordDto) {
    // Retrieve the email address from the reset password DTO
    final String emailAddress = resetPasswordDto.getEmailAddress();
    // Fetch the member by email address or throw an exception if not found
    final Member member = getMemberDetails(emailAddress);

    // Validate the provided verification code against the stored profile token and reset code
    validateProfileTokenAndResetPasswordCode(emailAddress, resetPasswordDto.getVerificationCode());
    // Initialize the user's authentication and security context
    final FleenUser user = verificationService.initializeAuthenticationAndContext(member);
    // Generate a new reset password token for the user
    final String resetPasswordToken = tokenService.createResetPasswordToken(user);

    // Clear any temporarily saved OTP for the user
    clearResetPasswordOtpSavedTemporarily(user.getUsername());
    // Save the new reset password token for the user
    tokenService.saveResetPasswordToken(user.getUsername(), resetPasswordToken);
    // Return a localized response with the reset password token
    return localizedResponse.of(InitiatePasswordChangeResponse.of(resetPasswordToken));
  }

  /**
   * Retrieves member details based on the provided email address or username.
   *
   * <p>This method looks up the member by their email address or username. If no matching member is found,
   * a {@link UserNotFoundException} is thrown with the provided email address or username.</p>
   *
   * @param emailAddressOrUsername the email address or username of the member
   * @return the {@link Member} object representing the member's details
   * @throws UserNotFoundException if no member is found with the provided email address or username
   */
  @Override
  public Member getMemberDetails(final String emailAddressOrUsername) {
    return memberRepository.findByEmailAddress(emailAddressOrUsername)
      .orElseThrow(UserNotFoundException.of(emailAddressOrUsername));
  }

  /**
   * Validates the profile token and reset password code for a given email address.
   *
   * @param emailAddress            the email address associated with the profile token
   * @param verificationTokenOrCode the verification token or code to validate
   * @throws ResetPasswordCodeInvalidException if the profile token or reset password code is invalid
   * @throws ResetPasswordCodeExpiredException if the reset password token has expired
   */
  private void validateProfileTokenAndResetPasswordCode(final String emailAddress, final String verificationTokenOrCode) {
    // Find the profile token associated with the email address
    final ProfileToken profileToken = findProfileToken(emailAddress);
    // Validate the provided verification token or code against the profile token
    validateProfileTokenAndVerificationCode(verificationTokenOrCode, profileToken);
    // Verify that the reset password token associated with the profile token has not expired
    verifyResetPasswordTokenHasNotExpired(profileToken);
  }


  /**
   * Finds the profile token associated with the given email address.
   *
   * @param emailAddress the email address associated with the profile token
   * @return the profile token found
   * @throws ResetPasswordCodeInvalidException if no profile token exists for the email address
   */
  @Override
  public ProfileToken findProfileToken(final String emailAddress) {
    // Get the profile token and If no profile token exists, throw an exception indicating invalid reset password code.
    return profileTokenRepository.findByEmailAddress(emailAddress)
      .orElseThrow(ResetPasswordCodeInvalidException.of());
  }

  /**
   * Verifies that the reset password token has not expired.
   *
   * <p>This method checks if the provided {@code profileToken} exists and if its
   * reset password token has expired. If the token has expired, it throws a
   * {@link ResetPasswordCodeExpiredException}.</p>
   *
   * @param profileToken the profile token to be verified for expiry
   * @throws ResetPasswordCodeExpiredException if the reset password token has expired
   */
  @Override
  public void verifyResetPasswordTokenHasNotExpired(final ProfileToken profileToken) {
    // Check if the reset password token has expired.
    if (nonNull(profileToken) && profileToken.isResetPasswordTokenExpired()) {
      throw new ResetPasswordCodeExpiredException();
    }
  }


  /**
   * Validates the profile token and verification code.
   *
   * @param verificationTokenOrCode the verification token or code provided by the user
   * @param profileToken the profile token containing the reset password token to validate against
   * @throws ResetPasswordCodeInvalidException if the reset password token in the profile token is null
   *                                          or does not match the provided verification token or code
   */
  @Override
  public void validateProfileTokenAndVerificationCode(final String verificationTokenOrCode, final ProfileToken profileToken) {
    // Check if the provided token or code matches the reset password token in the profile token.
    if (profileToken.isResetPasswordTokenInValid(verificationTokenOrCode)) {
      throw new ResetPasswordCodeInvalidException();
    }
  }

  /**
   * Clears the saved reset password OTP (One-Time Password) from the cache.
   *
   * @param username the username for which the OTP was saved
   */
  protected void clearResetPasswordOtpSavedTemporarily(final String username) {
    cacheService.delete(getResetPasswordCacheKey(username));
  }
}
