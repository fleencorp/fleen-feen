package com.fleencorp.feen.service.impl.security.verification;

import com.fleencorp.feen.constant.security.auth.AuthenticationStatus;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.event.model.base.PublishMessageRequest;
import com.fleencorp.feen.event.service.PublisherService;
import com.fleencorp.feen.exception.auth.AlreadySignedUpException;
import com.fleencorp.feen.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.user.UserNotFoundException;
import com.fleencorp.feen.exception.user.role.NoRoleAvailableToAssignException;
import com.fleencorp.feen.exception.verification.ResetPasswordCodeExpiredException;
import com.fleencorp.feen.exception.verification.ResetPasswordCodeInvalidException;
import com.fleencorp.feen.exception.verification.VerificationFailedException;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.domain.user.ProfileToken;
import com.fleencorp.feen.model.domain.user.Role;
import com.fleencorp.feen.model.dto.auth.*;
import com.fleencorp.feen.model.dto.security.mfa.ConfirmMfaVerificationCodeDto;
import com.fleencorp.feen.model.dto.security.mfa.ResendMfaVerificationCodeDto;
import com.fleencorp.feen.model.request.auth.CompletedUserSignUpRequest;
import com.fleencorp.feen.model.request.auth.ForgotPasswordRequest;
import com.fleencorp.feen.model.request.mfa.MfaVerificationRequest;
import com.fleencorp.feen.model.response.auth.ResendSignUpVerificationCodeResponse;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.security.ChangePasswordResponse;
import com.fleencorp.feen.model.response.security.ForgotPasswordResponse;
import com.fleencorp.feen.model.response.security.InitiatePasswordChangeResponse;
import com.fleencorp.feen.model.response.security.mfa.ResendMfaVerificationCodeResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.security.ProfileTokenRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.auth.PasswordService;
import com.fleencorp.feen.service.impl.auth.AuthenticationServiceImpl;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.feen.service.security.TokenService;
import com.fleencorp.feen.service.security.VerificationService;
import com.fleencorp.feen.service.security.mfa.MfaService;
import com.fleencorp.feen.service.user.RoleService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.datetime.DateTimeUtil.addMinutesFromNow;
import static com.fleencorp.feen.service.impl.common.CacheKeyService.*;
import static com.fleencorp.feen.service.security.OtpService.generateOtp;
import static com.fleencorp.feen.service.security.OtpService.getRandomSixDigitOtp;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class VerificationServiceImpl implements PasswordService,
  VerificationService {

  private final AuthenticationServiceImpl authenticationService;
  private final CacheService cacheService;
  private final MfaService mfaService;
  private final RoleService roleService;
  private final TokenService tokenService;
  private final MemberRepository memberRepository;
  private final ProfileTokenRepository profileTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final PublisherService publisherService;
  private final Localizer localizer;

  public VerificationServiceImpl(
      @Lazy final AuthenticationServiceImpl authenticationService,
      final CacheService cacheService,
      final MfaService mfaService,
      final RoleService roleService,
      final TokenService tokenService,
      final MemberRepository memberRepository,
      final ProfileTokenRepository profileTokenRepository,
      final PasswordEncoder passwordEncoder,
      @Qualifier("profile-request-pub") final PublisherService publisherService,
      final Localizer localizer) {
    this.authenticationService = authenticationService;
    this.cacheService = cacheService;
    this.mfaService = mfaService;
    this.roleService = roleService;
    this.tokenService = tokenService;
    this.memberRepository = memberRepository;
    this.profileTokenRepository = profileTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.publisherService = publisherService;
    this.localizer = localizer;
  }

  @Override
  public CacheService getCacheService() {
    return cacheService;
  }

  @Override
  public PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }

  /**
   * Completes the sign-up process for a user with verification code validation, role assignment,
   * and token generation.
   *
   * <p>This method validates the sign-up verification code provided by the user, retrieves the member
   * details, checks if the sign-up process is already completed, assigns default roles, verifies and
   * updates the member details, initializes authentication and context for the new user, clears the
   * temporary sign-up verification code, generates access and refresh tokens, and sends a completed
   * sign-up verification code.</p>
   *
   * @param completeSignUpDto the DTO containing the sign-up completion details
   * @param user the authenticated user performing the sign-up completion
   * @return SignUpResponse containing the generated access and refresh tokens
   * @throws VerificationFailedException if the sign-up verification code validation fails
   * @throws AlreadySignedUpException if the user is already signed up
   * @throws NoRoleAvailableToAssignException if no roles are available to assign to the new user
   */
  @Override
  @Transactional
  public SignUpResponse completeSignUp(final CompleteSignUpDto completeSignUpDto, final FleenUser user) {
    final String username = user.getUsername();
    // Validate sign-up verification code
    validateSignUpVerificationCode(username, completeSignUpDto.getVerificationCode());
    // Get verification type associated with sign up operation
    final VerificationType verificationType = completeSignUpDto.getVerificationType();
    // Retrieve member details
    final Member member = getMemberDetailsToCompleteSignUp(username);

    // Check if sign-up is already completed
    checkIfSignUpIsAlreadyCompleted(member);
    // Update member profile details
    updateMemberProfile(member, verificationType);

    // Initialize authentication and context for the new user
    final FleenUser newUser = authenticationService.initializeAuthenticationAndContext(member);
    // Clear temporary sign-up verification code
    clearSignUpVerificationCodeSavedTemporarily(username);
    // Set user timezone after authentication
    authenticationService.setUserTimezoneAfterAuthentication(user);

    // Generate a access token
    final String accessToken = generateAccessTokenWithCompletedAuthentication(newUser);
    // Generate a refresh token
    final String refreshToken = generateRefreshToken(newUser);
    // Save authentication tokens
    authenticationService.saveAuthenticationTokensToRepositoryOrCache(username, accessToken, refreshToken);
    // Send completed sign-up verification message
    sendCompletedSignUpMessage(user, member);
    // Return a localized response with the details
    return createCompletedSignUpResponse(accessToken, refreshToken);
  }

  /**
   * Validates the sign-up verification code for a given username.
   *
   * <p>This method checks if the provided username or code is null, retrieves the verification key,
   * and then validates the verification code against the stored code using the validateVerificationCode method.</p>
   *
   * @param username the username for which the verification code is being validated
   * @param code     the verification code to validate
   * @throws VerificationFailedException if the username or code is null
   */
  protected void validateSignUpVerificationCode(final String username, final String code) {
    // Check if the username or code is null
    if (isNull(username) || isNull(code)) {
      throw new VerificationFailedException();
    }

    // Retrieve the verification key from the cache
    final String verificationKey = getSignUpVerificationCacheKey(username);
    // Validate the verification code using the retrieved key
    validateVerificationCode(verificationKey, code);
  }

  /**
   * Retrieves the details of a {@link Member} using the provided username (email address).
   * <p>
   * This method queries the member repository to find a member by their email address.
   * If a member with the given email address is found, their details are returned.
   * If no matching member is found, a {@link VerificationFailedException} is thrown.
   * </p>
   *
   * @param username the email address of the member whose details are to be retrieved
   * @return the {@link Member} object containing the member's details
   * @throws VerificationFailedException if no member is found with the given email address
   */
  protected Member getMemberDetailsToCompleteSignUp(final String username) {
    return memberRepository.findByEmailAddress(username)
      .orElseThrow(VerificationFailedException::new);
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
  protected void checkIfSignUpIsAlreadyCompleted(final Member member) {
    // Throw an exception if the provided member is null
    checkIsNull(member, FailedOperationException::new);

    // Check if the member status indicates that the member is already signed up
    if (member.isProfileActiveAndApproved()) {
      throw new AlreadySignedUpException();
    }
  }

  /**
   * Updates the profile of the specified member by clearing default roles and updating profile details.
   *
   * <p>This method first clears any default roles that were assigned during the sign-up process for the given {@link Member}.
   * It then updates the profile details including roles and profile status by calling
   * {@link #updateProfileDetailsForNewUser(Member, VerificationType)}.</p>
   *
   * @param member the {@link Member} whose profile is being updated
   * @param verificationType the {@link VerificationType} indicating the verification status to be applied to the member's profile
   */
  protected void updateMemberProfile(final Member member, final VerificationType verificationType) {
    // Clear default roles assigned during sign-up
    member.clearDefaultRolesAssignedDuringSignUpRole();
    // Update profile details including roles and profile status
    updateProfileDetailsForNewUser(member, verificationType);
  }

  /**
   * Updates the profile details for a new user based on their roles and verification type.
   *
   * <p>This method retrieves the appropriate roles for the new user and then verifies the user
   * based on the provided {@code verificationType}. It also updates the user's profile details accordingly.
   * The actual verification and update operations are delegated to
   * {@link #verifyUserAndUpdateSignedUpUserDetailsForNewUser(Member, List, VerificationType)}.</p>
   *
   * @param member the new user whose profile details are to be updated
   * @param verificationType the type of verification used for the user (e.g., email or SMS)
   */
  protected void updateProfileDetailsForNewUser(final Member member, final VerificationType verificationType) {
    // Get roles for new user
    final List<Role> userRoles = getRolesForNewUser();
    // Verify user and update signed-up user details
    verifyUserAndUpdateSignedUpUserDetailsForNewUser(member, userRoles, verificationType);
  }

  /**
   * Verifies the user and updates signed-up user details for a new member.
   *
   * @param member            the member to update
   * @param roles             the roles to add to the member
   * @param verificationType  the type of verification used
   */
  protected void verifyUserAndUpdateSignedUpUserDetailsForNewUser(final Member member, final List<Role> roles, final VerificationType verificationType) {
    // Add the provided roles to the member
    member.addRole(roles);
    // Verify the user using the specified verification type
    member.verifyUser(verificationType);
    // Mark the member's profile as active and approved
    member.markProfileActiveAndApproved();
  }

  /**
   * Retrieves the roles assigned to a new user.
   *
   * <p>Fetches the roles for a new user from the role service. If no roles are available,
   * throws a {@link NoRoleAvailableToAssignException}.</p>
   *
   * @return list of {@link Role} the roles assigned to a new user
   * @throws NoRoleAvailableToAssignException if no roles are available to assign
   */
  private List<Role> getRolesForNewUser() {
    // Fetch roles for a new user from the role service
    final List<Role> userRoles = roleService.getRolesForNewUser();

    // Throw exception if no roles are available
    if (userRoles.isEmpty()) {
      throw new NoRoleAvailableToAssignException();
    }

    return userRoles;
  }

  /**
   * Clears the sign-up verification code saved temporarily for the specified username.
   *
   * <p>Deletes the verification code stored in the cache for the given username.</p>
   *
   * @param username the username for which the sign-up verification code should be cleared
   */
  private void clearSignUpVerificationCodeSavedTemporarily(final String username) {
    // Retrieve the cache key for the sign-up verification code
    final String key = getSignUpVerificationCacheKey(username);
    // Delete the verification code from the cache
    cacheService.delete(key);
  }

  /**
   * Generates an access token for the specified user with a status of {@link AuthenticationStatus#COMPLETED}.
   *
   * <p>This method creates an access token for the given {@link FleenUser} using the
   * {@link TokenService} and sets the authentication status to {@link AuthenticationStatus#COMPLETED}.
   * The generated access token is then returned as a {@link String}.</p>
   *
   * @param user the {@link FleenUser} for whom the access token is generated
   * @return a {@link String} representing the generated access token
   */
  protected String generateAccessTokenWithCompletedAuthentication(final FleenUser user) {
    return tokenService.createAccessToken(user, AuthenticationStatus.COMPLETED);
  }

  /**
   * Generates a refresh token for the specified user.
   *
   * <p>This method creates a refresh token for the given {@link FleenUser} using the
   * {@link TokenService} and returns it as a {@link String}.</p>
   *
   * @param user the {@link FleenUser} for whom the refresh token is generated
   * @return a {@link String} representing the generated refresh token
   */
  protected String generateRefreshToken(final FleenUser user) {
    return tokenService.createRefreshToken(user);
  }

  /**
   * Sends a message indicating the completion of the user sign-up process.
   *
   * <p>This method creates and sends a message containing the user and member information
   * to notify relevant parties about the completion of the sign-up process for the user.</p>
   *
   * @param user the {@link FleenUser} representing the signed-up user
   * @param member the {@link Member} associated with the user for sign-up details
   */
  protected void sendCompletedSignUpMessage(final FleenUser user, final Member member) {
    createAndSendCompletedUserSignUpMessage(user, member);
  }

  /**
   * Creates a response after the user has completed the sign-up process.
   *
   * <p>This method generates a {@link SignUpResponse} containing the provided access and refresh tokens,
   * and returns a localized response with a corresponding message code for the completed sign-up process.</p>
   *
   * @param accessToken the access token generated for the user
   * @param refreshToken the refresh token generated for the user
   * @return a localized {@link SignUpResponse} containing the access and refresh tokens along with a message code
   */
  protected SignUpResponse createCompletedSignUpResponse(final String accessToken, final String refreshToken) {
    // Create a sign up response after the use completes the process
    final SignUpResponse signUpResponse = SignUpResponse.of(accessToken, refreshToken);
    // Return a localized response with the details
    return localizer.of(signUpResponse, signUpResponse.getCompletedSignUpMessageCode());
  }

  /**
   * Creates and sends a completed user sign-up message.
   *
   * <p>This method constructs a {@link CompletedUserSignUpRequest} using the user’s details
   * (first name, last name, email address, phone number) and the member’s verification
   * status. It then publishes the message to the profile request publisher.</p>
   *
   * @param user the {@link FleenUser} whose details are used in the sign-up request
   * @param member the {@link Member} whose verification status is included in the request
   */
  protected void createAndSendCompletedUserSignUpMessage(final FleenUser user, final Member member) {
    final CompletedUserSignUpRequest completedUserSignUpRequest = CompletedUserSignUpRequest
      .of(user.getFirstName(), user.getLastName(), user.getEmailAddress(), user.getPhoneNumber(), member.getVerificationStatus());
    publisherService.publishMessage(PublishMessageRequest.of(completedUserSignUpRequest));
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
    authenticationService.sendSignUpVerificationMessage(otpCode, verificationType, user);
    // Save the newly generated verification code temporarily for the user
    authenticationService.saveSignUpVerificationCodeTemporarily(user.getUsername(), otpCode);
    // Return response indicating the successful initiation of code resend
    return localizer.of(ResendSignUpVerificationCodeResponse.of());
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
  protected void validateAndCheckIfEmailsInRequestAndAuthenticatedUserAreSame(final String requestEmailAddress, final String authenticatedUserEmailAddress) {
    // Check if either email address is null and throw an exception if so
    checkIsNullAny(List.of(requestEmailAddress, authenticatedUserEmailAddress), FailedOperationException::new);

    // Compare the email addresses and throw an exception if they don't match
    if (!authenticatedUserEmailAddress.equals(requestEmailAddress)) {
      throw new FailedOperationException();
    }
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
    authenticationService.saveMfaVerificationCodeTemporarily(user.getUsername(), otpCode);
    // Return response indicating the successful initiation of code resend
    return localizer.of(ResendMfaVerificationCodeResponse.of());
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
    publisherService.publishMessage(PublishMessageRequest.of(resendMfaVerificationCodeRequest));
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
    final FleenUser authenticatedUser = authenticationService.initializeAuthenticationAndContext(member);
    // Set user timezone after authentication
    authenticationService.setUserTimezoneAfterAuthentication(authenticatedUser);
    // Create access and refresh tokens for the authenticated user
    final String accessToken = tokenService.createAccessToken(authenticatedUser, AuthenticationStatus.COMPLETED);
    final String refreshToken = tokenService.createRefreshToken(authenticatedUser);

    // Clear temporarily saved MFA verification code
    clearMfaVerificationCodeSavedTemporarily(username);
    // Save authentication tokens to repository or cache
    authenticationService.saveAuthenticationTokensToRepositoryOrCache(username, accessToken, refreshToken);
    // Return SignInResponse with access and refresh tokens
    return localizer.of(SignInResponse.of(accessToken, refreshToken));
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
    final Member member = authenticationService.getMemberDetails(emailAddress);

    // Validate the provided verification code against the stored profile token and reset code
    validateProfileTokenAndResetPasswordCode(emailAddress, resetPasswordDto.getVerificationCode());
    // Initialize the user's authentication and security context
    final FleenUser user = authenticationService.initializeAuthenticationAndContext(member);
    // Generate a new reset password token for the user
    final String resetPasswordToken = tokenService.createResetPasswordToken(user);

    // Clear any temporarily saved OTP for the user
    clearResetPasswordOtpSavedTemporarily(user.getUsername());
    // Save the new reset password token for the user
    tokenService.saveResetPasswordToken(user.getUsername(), resetPasswordToken);
    // Return a localized response with the reset password token
    return localizer.of(InitiatePasswordChangeResponse.of(resetPasswordToken));
  }

  /**
   * Validates the profile token and reset password code for a given email address.
   *
   * @param emailAddress            the email address associated with the profile token
   * @param verificationTokenOrCode the verification token or code to validate
   * @throws ResetPasswordCodeInvalidException if the profile token or reset password code is invalid
   * @throws ResetPasswordCodeExpiredException if the reset password token has expired
   */
  protected void validateProfileTokenAndResetPasswordCode(final String emailAddress, final String verificationTokenOrCode) {
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
  protected ProfileToken findProfileToken(final String emailAddress) {
    // Get the profile token and If no profile token exists, throw an exception indicating invalid reset password code.
    return profileTokenRepository.findByEmailAddress(emailAddress)
      .orElseThrow(ResetPasswordCodeInvalidException.of());
  }

  /**
   * Validates the profile token and verification code.
   *
   * @param verificationTokenOrCode the verification token or code provided by the user
   * @param profileToken the profile token containing the reset password token to validate against
   * @throws ResetPasswordCodeInvalidException if the reset password token in the profile token is null
   *                                          or does not match the provided verification token or code
   */
  protected void validateProfileTokenAndVerificationCode(final String verificationTokenOrCode, final ProfileToken profileToken) {
    // Check if the provided token or code matches the reset password token in the profile token.
    if (profileToken.isResetPasswordTokenInValid(verificationTokenOrCode)) {
      throw new ResetPasswordCodeInvalidException();
    }
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
  protected void verifyResetPasswordTokenHasNotExpired(final ProfileToken profileToken) {
    // Check if the reset password token has expired.
    if (nonNull(profileToken) && profileToken.isResetPasswordTokenExpired()) {
      throw new ResetPasswordCodeExpiredException();
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

  /**
   * Initiates the password reset process for a user based on their email address.
   *
   * <p>This method retrieves the member details using the provided email address,
   * generates and saves a reset password OTP, and sends a notification containing
   * the OTP to the user's email address. It returns a response indicating the
   * success of initiating the password reset process.</p>
   *
   * @param forgotPasswordDto the DTO containing the user's email address and verification type
   * @return ForgotPasswordResponse the response containing the email address and phone number for confirmation
   * @throws UserNotFoundException if the user with the provided email address is not found in the repository
   */
  @Override
  @Transactional
  public ForgotPasswordResponse forgotPassword(final ForgotPasswordDto forgotPasswordDto) {
    // Retrieve user's email address from DTO
    final String emailAddress = forgotPasswordDto.getEmailAddress();
    // Retrieve member details from repository or throw exception if not found
    final Member member = authenticationService.getMemberDetails(emailAddress);
    // Determine the verification type from DTO
    final VerificationType verificationType = forgotPasswordDto.getVerificationType();
    // Generate a random six-digit OTP as the reset password token.
    final String otpCode = getRandomSixDigitOtp();

    // Generate and save reset password OTP for the user
    generateAndSaveResetPasswordToken(emailAddress, member, otpCode);
    // Create a FleenUser object from basic member details
    final FleenUser user = FleenUser.fromMemberBasic(member);
    // Send forgot password verification message
    sendForgotPasswordMessage(otpCode, user, verificationType);
    // Save reset password OTP in cache or storage
    saveResetPasswordOtpTemporarily(member.getEmailAddress(), otpCode);
    // Return response with email address and phone number for confirmation
    return localizer.of(ForgotPasswordResponse.of(emailAddress, user.getPhoneNumber()));
  }

  /**
   * Generates and saves a reset password token for the given email address and member.
   *
   * <p>Checks if a profile token already exists for the email address. If found, retrieves it;
   * otherwise, creates a new one. Associates the member with the profile token, generates a
   * random six-digit OTP as the reset password token, sets its expiry date to 10 minutes from
   * now, and saves the profile token in the repository.</p>
   *
   * @param emailAddress the email address associated with the member
   * @param member the member for whom the reset password token is generated
   */
  protected void generateAndSaveResetPasswordToken(final String emailAddress, final Member member, final String verificationTokenOrCode) {
    // Check if a profile token already exists for the given email address.
    final Optional<ProfileToken> profileTokenExists = profileTokenRepository.findByEmailAddress(emailAddress);
    // If a profile token exists, retrieve it; otherwise, create a new one.
    final ProfileToken profileToken = profileTokenExists.orElseGet(ProfileToken::new);
    // Set the member associated with the profile token, the reset password token & the expiry date for the reset password token (10 minutes from now).
    profileToken.updateMemberAndResetPasswordTokenAndExpiryDate(member, verificationTokenOrCode, addMinutesFromNow(10));
    // Save the profile token.
    profileTokenRepository.save(profileToken);
  }

  /**
   * Sends a forgot password message to the user.
   *
   * <p>This method creates a {@link ForgotPasswordRequest} using the provided OTP code, user details
   * (first name, last name, email address, phone number), and verification type. It then publishes
   * the request to send the forgot password message to the user.</p>
   *
   * @param otpCode the one-time password (OTP) code to be sent for forgot password verification
   * @param user the {@link FleenUser} whose details (e.g., first name, last name, email, phone number)
   *             are included in the forgot password request
   * @param verificationType the type of verification to be used (e.g., email, phone)
   */
  protected void sendForgotPasswordMessage(final String otpCode, final FleenUser user, final VerificationType verificationType) {
    // Create a request to send forgot password code with OTP and user details
    final ForgotPasswordRequest forgotPasswordRequest = ForgotPasswordRequest
      .of(otpCode, user.getFirstName(), user.getLastName(), user.getEmailAddress(), user.getPhoneNumber(), verificationType);
    // Publish forgot password code request to external profile request publisher
    publisherService.publishMessage(PublishMessageRequest.of(forgotPasswordRequest));
  }

  /**
   * Saves a reset password OTP (One-Time Password) in the cache for a specified duration.
   *
   * <p>This method sets the OTP associated with the username in the cache service
   * using the cache key derived from the username. The OTP remains valid for 3 minutes.</p>
   *
   * @param username the username for which the OTP is generated
   * @param otp      the OTP generated for password reset
   */
  protected void saveResetPasswordOtpTemporarily(final String username, final String otp) {
    cacheService.set(getResetPasswordCacheKey(username), otp, Duration.ofMinutes(3));
  }

  /**
   * Changes the password for the authenticated user.
   *
   * @param changePasswordDto the DTO containing the new password information
   * @param user the authenticated user changing the password
   * @return a ChangePasswordResponse indicating successful password change
   * @throws UserNotFoundException if the user with the provided email address is not found
   */
  @Override
  public ChangePasswordResponse changePassword(final ChangePasswordDto changePasswordDto, final FleenUser user) {
    final String emailAddress = user.getEmailAddress();
    // Check if user has associated reset password access token
    verifyUserHasResetPasswordToken(emailAddress);
    // Retrieve member from repository or throw exception if not found
    final Member member = authenticationService.getMemberDetails(emailAddress);

    // Find any existing password reset token and reset or clear details
    findPasswordTokenAndResetOrClearDetails(emailAddress);
    // Encode or hash the new password for the member
    encodeOrHashUserPassword(member, changePasswordDto.getPassword());
    // Save the updated member with the new password
    memberRepository.save(member);
    // Clear access token associated with reset password operation
    clearResetPasswordToken(emailAddress);
    // Return response indicating successful password change
    return localizer.of(ChangePasswordResponse.of());
  }

  /**
   * Verifies if the user has an existing reset password token associated with the specified email address.
   *
   * <p>If a reset password token is found for the given email address, this method throws an
   * {@link InvalidAuthenticationException} to indicate that the user is not authorized to proceed
   * without clearing the token.</p>
   *
   * @param emailAddress the email address to check for an existing reset password token.
   * @throws InvalidAuthenticationException if a reset password token exists for the given email address.
   */
  protected void verifyUserHasResetPasswordToken(final String emailAddress) {
    if (!tokenService.isResetPasswordTokenExist(emailAddress)) {
      throw new InvalidAuthenticationException(emailAddress);
    }
  }

  /**
   * Finds the profile token by email address and resets the reset password token and its expiry date if found.
   *
   * @param emailAddress the email address associated with the profile token
   */
  protected void findPasswordTokenAndResetOrClearDetails(final String emailAddress) {
    // Find the profile token by email address
    final ProfileToken profileToken = profileTokenRepository.findByEmailAddress(emailAddress)
      .orElse(null);

    // If profile token exists, reset the reset password token and its expiry date
    if (nonNull(profileToken)) {
      resetProfileToken(profileToken);
    }
  }

  /**
   * Resets the reset password token and its expiry date in the profile token.
   *
   * @param profileToken the profile token to reset
   */
  public void resetProfileToken(final ProfileToken profileToken) {
    // Check if a profile token exists and if it contains a reset password token.
    if (nonNull(profileToken.getResetPasswordToken())) {
      // Remove the reset password token and its expiry date.
      profileToken.resetTokenAndExpiryDate();
      // Save the updated profile token.
      profileTokenRepository.save(profileToken);
    }
  }

  /**
   * Clears the reset password token associated with the specified email address.
   *
   * <p>This method delegates the task of removing the reset password token for the given email address
   * to the TokenService's clearResetPasswordToken() method.</p>
   *
   * @param emailAddress the email address for which the reset password token is to be cleared.
   */
  public void clearResetPasswordToken(final String emailAddress) {
    tokenService.clearResetPasswordToken(emailAddress);
  }
}
