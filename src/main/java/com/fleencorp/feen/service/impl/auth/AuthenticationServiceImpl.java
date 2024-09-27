package com.fleencorp.feen.service.impl.auth;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.security.auth.AuthenticationStage;
import com.fleencorp.feen.constant.security.auth.AuthenticationStatus;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.constant.security.profile.ProfileVerificationStatus;
import com.fleencorp.feen.constant.security.role.RoleType;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.event.model.base.PublishMessageRequest;
import com.fleencorp.feen.event.publisher.ProfileRequestPublisher;
import com.fleencorp.feen.exception.auth.AlreadySignedUpException;
import com.fleencorp.feen.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.UnableToCompleteOperationException;
import com.fleencorp.feen.exception.user.UserNotFoundException;
import com.fleencorp.feen.exception.user.profile.BannedAccountException;
import com.fleencorp.feen.exception.user.profile.DisabledAccountException;
import com.fleencorp.feen.exception.user.role.NoRoleAvailableToAssignException;
import com.fleencorp.feen.exception.verification.ResetPasswordCodeExpiredException;
import com.fleencorp.feen.exception.verification.ResetPasswordCodeInvalidException;
import com.fleencorp.feen.exception.verification.VerificationFailedException;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.domain.user.ProfileToken;
import com.fleencorp.feen.model.domain.user.Role;
import com.fleencorp.feen.model.dto.auth.*;
import com.fleencorp.feen.model.dto.security.mfa.ConfirmMfaVerificationCodeDto;
import com.fleencorp.feen.model.dto.security.mfa.ResendMfaVerificationCodeDto;
import com.fleencorp.feen.model.request.auth.CompletedUserSignUpRequest;
import com.fleencorp.feen.model.request.auth.ForgotPasswordRequest;
import com.fleencorp.feen.model.request.auth.SignUpVerificationRequest;
import com.fleencorp.feen.model.request.mfa.MfaVerificationRequest;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.auth.DataForSignUpResponse;
import com.fleencorp.feen.model.response.auth.ResendSignUpVerificationCodeResponse;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.country.CountryResponse;
import com.fleencorp.feen.model.response.security.ChangePasswordResponse;
import com.fleencorp.feen.model.response.security.ForgotPasswordResponse;
import com.fleencorp.feen.model.response.security.InitiatePasswordChangeResponse;
import com.fleencorp.feen.model.response.security.SignOutResponse;
import com.fleencorp.feen.model.response.security.mfa.ResendMfaVerificationCodeResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.security.ProfileTokenRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.auth.AuthenticationService;
import com.fleencorp.feen.service.auth.PasswordService;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.feen.service.security.TokenService;
import com.fleencorp.feen.service.security.VerificationService;
import com.fleencorp.feen.service.security.mfa.MfaService;
import com.fleencorp.feen.service.user.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.datetime.DateTimeUtil.addMinutesFromNow;
import static com.fleencorp.feen.service.impl.common.CacheKeyService.*;
import static com.fleencorp.feen.service.security.OtpService.generateOtp;
import static com.fleencorp.feen.service.security.OtpService.getRandomSixDigitOtp;
import static com.fleencorp.feen.util.security.UserAuthoritiesUtil.getPreAuthenticatedAuthorities;
import static com.fleencorp.feen.util.security.UserAuthoritiesUtil.getUserPreVerifiedAuthorities;
import static java.util.Objects.*;

/**
 * Implementation of the {@link AuthenticationService} and {@link PasswordService} interfaces.
 *
 * <p>This service provides authentication-related functionality, including user authentication,
 * password management, multi-factor authentication (MFA), and token management. It utilizes
 * various injected services such as {@link AuthenticationManager}, {@link CacheService},
 * {@link MfaService}, {@link TokenService}, and {@link MemberRepository} to perform these
 * operations. Additionally, it manages roles, user profiles, and localized responses.</p>
 *
 * <p>Clients of this service can authenticate users, handle password changes, validate MFA codes,
 * and manage session tokens, among other authentication-related tasks.</p>
 */
@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService,
    PasswordService, VerificationService {

  private final AuthenticationManager authenticationManager;
  private final CacheService cacheService;
  private final CountryService countryService;
  private final MfaService mfaService;
  private final RoleService roleService;
  private final TokenService tokenService;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final ProfileRequestPublisher profileRequestPublisher;
  private final ProfileTokenRepository profileTokenRepository;
  private final LocalizedResponse localizedResponse;
  private final String originDomain;

  /**
   * Constructs an instance of {@link AuthenticationServiceImpl} with the provided dependencies.
   *
   * <p>This constructor initializes the service with various components necessary for managing
   * authentication, such as the {@link AuthenticationManager}, {@link CacheService}, {@link CountryService},
   * {@link MfaService}, {@link RoleService}, {@link TokenService}, {@link MemberRepository},
   * {@link PasswordEncoder}, {@link ProfileRequestPublisher}, {@link ProfileTokenRepository},
   * and {@link LocalizedResponse}. These dependencies are injected to facilitate authentication
   * operations, including managing user roles, tokens, MFA, and profile-related actions.</p>
   *
   * @param authenticationManager the manager responsible for processing authentication requests.
   * @param cacheService the service handling cache operations.
   * @param countryService the service providing country-related data and operations.
   * @param mfaService the service managing multi-factor authentication (MFA).
   * @param roleService the service managing user roles.
   * @param tokenService the service handling token generation and validation.
   * @param memberRepository the repository for accessing and managing {@link Member} entities.
   * @param passwordEncoder the encoder for processing passwords.
   * @param profileRequestPublisher the publisher for sending profile-related requests.
   * @param profileTokenRepository the repository for managing profile-related tokens.
   * @param localizedResponse the service for handling localized responses.
   * @param originDomain the origin domain used in the app to perform actions
   */
  public AuthenticationServiceImpl(
      final AuthenticationManager authenticationManager,
      final CacheService cacheService,
      final CountryService countryService,
      final MfaService mfaService,
      final RoleService roleService,
      final TokenService tokenService,
      final MemberRepository memberRepository,
      final PasswordEncoder passwordEncoder,
      final ProfileRequestPublisher profileRequestPublisher,
      final ProfileTokenRepository profileTokenRepository,
      final LocalizedResponse localizedResponse,
      @Value("${origin-domain}") final String originDomain) {
    this.authenticationManager = authenticationManager;
    this.cacheService = cacheService;
    this.countryService = countryService;
    this.mfaService = mfaService;
    this.roleService = roleService;
    this.tokenService = tokenService;
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
    this.profileRequestPublisher = profileRequestPublisher;
    this.profileTokenRepository = profileTokenRepository;
    this.localizedResponse = localizedResponse;
    this.originDomain = originDomain;
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
   * Retrieves data required for creating a calendar, including a list of countries and available timezones.
   *
   * @return a DataForCreateCalendarResponse object containing a list of countries and a set of timezones
   */
  @Override
  public DataForSignUpResponse getDataForSignUp() {
    // Fetch a list of countries with a large number of entries (1000 in this case).
    final SearchResultView searchResult = countryService.findCountries(CountrySearchRequest.of(1000));
    // Get the countries in the search result
    final List<?> countries = searchResult.getValues();
    // Return the response object containing both the countries and timezones.
    return localizedResponse.of(DataForSignUpResponse.of(countries));
  }

  /**
   * Signs up a new member based on the provided sign-up data.
   * Configures roles and statuses for the new member's profile, encodes the password,
   * saves the member to the repository, initializes authentication and context,
   * generates access and refresh tokens, sends a sign-up verification code,
   * and saves authentication tokens.
   *
   * @param signUpDto The DTO containing sign-up data.
   * @return SignUpResponse containing access token, refresh token, email, phone number,
   *         authentication status, and profile verification type.
   */
  @Override
  @Transactional
  public SignUpResponse signUp(final SignUpDto signUpDto) {
    final Member member = signUpDto.toMember();

    // Configure roles for the new member's profile
    configureRolesForNewProfile(member);
    // Configure statuses for the new member's profile
    configureStatusesForNewProfile(member);

    // Encode or hash the user's password before saving
    final String password = signUpDto.getPassword();
    encodeOrHashUserPassword(member, password);
    // Validate user email address domain and mark as an internal user
    member.confirmAndSetInternalUser(originDomain);

    // Set user location details
    configureUserLocationDetails(member, signUpDto.getCountryCode());

    // Save the member to the repository
    memberRepository.save(member);

    // Initialize authentication and set context for the new member
    final FleenUser user = initializeAuthenticationAndContext(member);
    // Set user timezone after authentication
    setUserTimezoneAfterAuthentication(user);

    // Generate access and refresh tokens for the authenticated user
    final String accessToken = tokenService.createAccessToken(user);
    final String refreshToken = tokenService.createRefreshToken(user);

    // Generate OTP for sign-up verification
    final String otpCode = generateOtp();
    final VerificationType verificationType = signUpDto.getActualVerificationType();

    // Prepare and send sign-up verification code request
    final SignUpVerificationRequest signUpVerificationRequest = createSignUpVerificationRequest(otpCode, verificationType, user);
    profileRequestPublisher.publishMessage(PublishMessageRequest.of(signUpVerificationRequest));

    // Save sign-up verification code temporarily
    saveSignUpVerificationCodeTemporarily(user.getUsername(), otpCode);
    // Save authentication tokens for the user
    saveAuthenticationTokensToRepositoryOrCache(user.getUsername(), accessToken, refreshToken);

    // Return the sign-up response with necessary details
    return SignUpResponse
      .of(accessToken, refreshToken, user.getEmailAddress(), user.getPhoneNumber(), AuthenticationStatus.IN_PROGRESS, verificationType);
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
    final VerificationType verificationType = completeSignUpDto.getActualVerificationType();

    // Retrieve member details
    final Member member = memberRepository.findByEmailAddress(username)
        .orElseThrow(VerificationFailedException::new);

    // Check if sign-up is already completed
    checkIfSignUpIsAlreadyCompleted(member);
    // Clear default roles assigned during sign-up
    member.clearDefaultRolesAssignedDuringSignUpRole();

    // Get roles for new user
    final List<Role> userRoles = getRolesForNewUser();
    // Verify user and update signed-up user details
    verifyUserAndUpdateSignedUpUserDetailsForNewUser(member, userRoles, verificationType);

    // Initialize authentication and context for the new user
    final FleenUser newUser = initializeAuthenticationAndContext(member);
    // Clear temporary sign-up verification code
    clearSignUpVerificationCodeSavedTemporarily(username);
    // Set user timezone after authentication
    setUserTimezoneAfterAuthentication(user);

    // Generate access and refresh tokens
    final String accessToken = tokenService.createAccessToken(newUser, AuthenticationStatus.COMPLETED);
    final String refreshToken = tokenService.createRefreshToken(newUser);

    // Save authentication tokens
    saveAuthenticationTokensToRepositoryOrCache(username, accessToken, refreshToken);

    // Send completed sign-up verification request
    final CompletedUserSignUpRequest completedUserSignUpRequest = CompletedUserSignUpRequest
        .of(user.getFirstName(), user.getLastName(), user.getEmailAddress(), user.getPhoneNumber(), member.getVerificationStatus());
    profileRequestPublisher.publishMessage(PublishMessageRequest.of(completedUserSignUpRequest));

    return SignUpResponse.of(accessToken, refreshToken);
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
    final SignUpVerificationRequest resendSignUpVerificationCodeRequest = createSignUpVerificationRequest(otpCode, verificationType, user);

    // Resend sign-up verification code request
    profileRequestPublisher.publishMessage(PublishMessageRequest.of(resendSignUpVerificationCodeRequest));
    // Save the newly generated verification code temporarily for the user
    saveSignUpVerificationCodeTemporarily(user.getUsername(), otpCode);

    // Return response indicating the successful initiation of code resend
    return localizedResponse.of(ResendSignUpVerificationCodeResponse.of());
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
    final MfaVerificationRequest resendMfaVerificationCodeRequest = MfaVerificationRequest
      .of(otpCode, user.getFirstName(), user.getLastName(), user.getEmailAddress(), user.getPhoneNumber(), resendMfaVerificationCodeDto.getActualVerificationType());

    // Resend mfa verification code request
    profileRequestPublisher.publishMessage(PublishMessageRequest.of(resendMfaVerificationCodeRequest));
    // Save the newly generated verification code temporarily for the user
    saveMfaVerificationCodeTemporarily(user.getUsername(), otpCode);

    // Return response indicating the successful initiation of code resend
    return localizedResponse.of(ResendMfaVerificationCodeResponse.of());
  }

  /**
   * Signs out the user by deleting their access and refresh tokens from cache
   * and clearing the security context.
   *
   * @param user the authenticated user to sign out
   */
  @Override
  public SignOutResponse signOut(final FleenUser user) {
    final String username = user.getUsername();
    // Clear saved authentication tokens including access and refresh token
    clearAuthenticationTokens(username);
    // Clear the security context
    clearUserAuthenticationDetails();
    return localizedResponse.of(SignOutResponse.of());
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
      .orElseThrow(() -> new InvalidAuthenticationException(username));

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
   * Signs in a user with the provided credentials.
   *
   * <p>This method authenticates the user using the provided email address and password.
   * It validates the user's profile status to ensure it is not disabled or banned.
   * Depending on the user's profile status and MFA settings, it handles the sign-in process
   * accordingly by initializing authentication, creating tokens, and updating the sign-in response.</p>
   *
   * @param signInDto the DTO containing user's sign-in credentials
   * @return SignInResponse containing authentication details and user information
   * @throws InvalidAuthenticationException if authentication fails for the provided credentials
   * @throws DisabledAccountException       if the user's profile status is disabled
   * @throws BannedAccountException         if the user's profile status is banned
   */
  @Override
  public SignInResponse signIn(final SignInDto signInDto) {
    final String emailAddress = signInDto.getEmailAddress();
    final String password = signInDto.getPassword();

    // Authenticate user with email and password
    final Authentication authentication = authenticate(emailAddress, password)
      .orElseThrow(() -> new InvalidAuthenticationException(emailAddress));

    // Retrieve the user from the Authentication Object
    final FleenUser user = (FleenUser) authentication.getPrincipal();
    // Set user timezone after authentication
    setUserTimezoneAfterAuthentication(user);

    // Validate profile status before proceeding
    validateProfileIsNotDisabledOrBanned(user.getProfileStatus());

    final SignInResponse signInResponse = createDefaultSignInResponse(user);

    // Handle sign-in based on user's profile and MFA settings
    if (isProfileInactiveAndUserYetToBeVerified(user)) {
      handleProfileYetToBeVerified(signInResponse, user);
      return localizedResponse.of(signInResponse);
    }

    // Handle sign-in based on user's profile with enabled MFA
    if (isMfaEnabledAndMfaTypeSet(user)) {
      handleProfileWithMfaEnabled(signInResponse, user);
      return localizedResponse.of(signInResponse);
    }

    // Handle verified profile sign-in
    handleProfileThatIsVerified(signInResponse, user, authentication);

    return localizedResponse.of(signInResponse);
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
    final Member member = memberRepository.findByEmailAddress(emailAddress)
      .orElseThrow(() -> new UserNotFoundException(emailAddress));

    // Determine the verification type from DTO
    final VerificationType verificationType = forgotPasswordDto.getActualVerificationType();
    // Generate a random six-digit OTP as the reset password token.
    final String otpCode = getRandomSixDigitOtp();
    // Generate and save reset password OTP for the user
    generateAndSaveResetPasswordToken(emailAddress, member, otpCode);
    // Create a FleenUser object from basic member details
    final FleenUser user = FleenUser.fromMemberBasic(member);

    // Create a request to send forgot password code with OTP and user details
    final ForgotPasswordRequest forgotPasswordRequest = ForgotPasswordRequest
      .of(otpCode, user.getFirstName(), user.getLastName(), user.getEmailAddress(), user.getPhoneNumber(), verificationType);
    // Publish forgot password code request to external profile request publisher
    profileRequestPublisher.publishMessage(PublishMessageRequest.of(forgotPasswordRequest));
    // Save reset password OTP in cache or storage
    saveResetPasswordOtpTemporarily(member.getEmailAddress(), otpCode);

    // Return response with email address and phone number for confirmation
    return localizedResponse.of(ForgotPasswordResponse.of(emailAddress, user.getPhoneNumber()));
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
    final String emailAddress = resetPasswordDto.getEmailAddress();
    final Member member = memberRepository.findByEmailAddress(emailAddress)
      .orElseThrow(() -> new UserNotFoundException(emailAddress));

    validateProfileTokenAndResetPasswordCode(emailAddress, resetPasswordDto.getVerificationCode());
    final FleenUser user = initializeAuthenticationAndContext(member);
    final String resetPasswordToken = tokenService.createResetPasswordToken(user);

    clearResetPasswordOtpSavedTemporarily(user.getUsername());
    tokenService.saveResetPasswordToken(user.getUsername(), resetPasswordToken);

    return localizedResponse.of(InitiatePasswordChangeResponse.of(resetPasswordToken));
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
    final Member member = memberRepository.findByEmailAddress(emailAddress)
      .orElseThrow(() -> new UserNotFoundException(emailAddress));

    // Find any existing password reset token and reset or clear details
    findPasswordTokenAndResetOrClearDetails(emailAddress);
    // Encode or hash the new password for the member
    encodeOrHashUserPassword(member, changePasswordDto.getPassword());
    // Save the updated member with the new password
    memberRepository.save(member);
    // Clear access token associated with reset password operation
    clearResetPasswordToken(emailAddress);

    // Return response indicating successful password change
    return localizedResponse.of(ChangePasswordResponse.of());
  }

  /**
   * Configures roles for a new profile.
   *
   * <p>This method assigns default roles to a new member's profile. It first checks if the member
   * is null and throws an {@link UnableToCompleteOperationException} if true. Then, it collects the
   * default user roles by mapping {@link RoleType#USER} to its value. If no default roles are found,
   * it throws a {@link NoRoleAvailableToAssignException}. Finally, it retrieves the roles from the
   * {@link RoleService} and adds them to the member's roles.</p>
   *
   * @param member The new member for whom the roles are to be configured.
   * @throws UnableToCompleteOperationException if the member is null.
   * @throws NoRoleAvailableToAssignException if no default roles are available to assign.
   */
  public void configureRolesForNewProfile(final Member member) {
    // Throw an exception if the provided member is null
    checkIsNull(member, UnableToCompleteOperationException::new);

    // Collect default user roles
    final Set<String> defaultUserRoles = Stream
        .of(RoleType.PRE_VERIFIED_USER)
        .map(RoleType::name)
        .collect(Collectors.toSet());

    // Retrieve roles from the role service and add them to the member
    final List<Role> roles = roleService.findAllByCode(defaultUserRoles);
    // Check if the default roles available to assign to user is not empty
    if (roles.isEmpty()) {
      throw new NoRoleAvailableToAssignException();
    }

    member.getRoles().addAll(roles);
  }

  /**
   * Configures statuses for a new profile.
   *
   * <p>This method assigns initial statuses to a new member's profile. It checks if the member
   * is null and throws an {@link UnableToCompleteOperationException} if true. Then, it sets the
   * profile status to {@link ProfileStatus#INACTIVE} and the verification status to
   * {@link ProfileVerificationStatus#PENDING}.</p>
   *
   * @param member The new member for whom the statuses are to be configured.
   * @throws UnableToCompleteOperationException if the member is null.
   */
  public void configureStatusesForNewProfile(final Member member) {
    // Throw an exception if the provided member is null
    checkIsNull(member, UnableToCompleteOperationException::new);

    // Set initial profile status
    member.setProfileStatus(ProfileStatus.INACTIVE);
    // Set initial profile verification status
    member.setVerificationStatus(ProfileVerificationStatus.PENDING);
  }

  /**
   * Configures the location details of a given member by setting the country name
   * based on the provided country code.
   *
   * <p>This method retrieves the country details using the country code and sets
   * the country name in the member's profile.</p>
   *
   * @param member the {@link Member} whose location details are to be configured.
   * @param countryCode the ISO country code used to look up the country details.
   * @throws UnableToCompleteOperationException if the member is null.
   */
  public void configureUserLocationDetails(final Member member, final String countryCode) {
    // Throw an exception if the provided member is null
    checkIsNull(List.of(member, countryCode), UnableToCompleteOperationException::new);

    // Get country name and details
    final Country country = countryService.getCountryByCode(countryCode);
    // Set user country
    member.setCountry(country.getTitle());
  }

  /**
   * Initializes authentication and sets the authentication context for the provided member.
   * Throws {@link UnableToCompleteOperationException} if the member is null.
   *
   * @param member The member for whom authentication is to be initialized.
   * @return The {@link FleenUser} associated with the member after authentication and context setup.
   * @throws UnableToCompleteOperationException If the member is null.
   */
  protected FleenUser initializeAuthenticationAndContext(final Member member) {
    // Throw an exception if the provided member is null
    checkIsNull(member, UnableToCompleteOperationException::new);

    // Create FleenUser from Member
    final FleenUser user = FleenUser.fromMember(member);
    // Create Authentication object
    final Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    // Set Authentication object in context (example: Spring Security context)
    setContext(authentication);

    return user;
  }

  /**
   * Sets the authentication context in the security context holder if the authentication
   * object is not null and is authenticated.
   *
   * @param authentication The authentication object to set in the security context holder.
   */
  protected void setContext(final Authentication authentication) {
    if (nonNull(authentication) && authentication.isAuthenticated()) {
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
  }

  /**
   * Saves the sign-up verification code temporarily in the cache.
   * The verification code is associated with the provided username and expires after 5 minutes.
   *
   * @param username         The username for which the verification code is saved.
   * @param verificationCode The verification code to be saved.
   */
  private void saveSignUpVerificationCodeTemporarily(final String username, final String verificationCode) {
    cacheService.set(getSignUpVerificationCacheKey(username), verificationCode, Duration.ofMinutes(5));
  }

  /**
   * Saves the access token and refresh token for the given username using the JWT service.
   *
   * @param username      The username for which the tokens are saved.
   * @param accessToken   The access token to be saved.
   * @param refreshToken  The refresh token to be saved.
   */
  private void saveAuthenticationTokensToRepositoryOrCache(final String username, final String accessToken, final String refreshToken) {
    // Save the access token for the username using the JWT service.
    tokenService.saveAccessToken(username, accessToken);
    // Save the refresh token for the username using the JWT service.
    tokenService.saveRefreshToken(username, refreshToken);
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
  private void validateSignUpVerificationCode(final String username, final String code) {
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
   * Checks if the sign-up process is already completed for a member.
   *
   * <p>This method first checks if the provided member object is null. If it is, it throws an UnableToCompleteOperationException.</p>
   *
   * <p>Then, it checks the profile status of the member. If the profile status is not null and indicates that the member is already active,
   * it throws an AlreadySignedUpException. This ensures that the sign-up process is not repeated for an already active member.</p>
   *
   * @param member the Member object whose sign-up status is to be checked
   * @throws UnableToCompleteOperationException if the member object is null
   * @throws AlreadySignedUpException if the member is already signed up and active
   */
  private void checkIfSignUpIsAlreadyCompleted(final Member member) {
    // Throw an exception if the provided member is null
    checkIsNull(member, UnableToCompleteOperationException::new);

    // Check if the member status indicates that the member is already signed up
    if (nonNull(member.getProfileStatus()) && member.isProfileActiveAndApproved()) {
      throw new AlreadySignedUpException();
    }
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
    // Set the profile status of the member to ACTIVE
    member.setProfileStatus(ProfileStatus.ACTIVE);
    // Set the verification status of the member to APPROVED
    member.setVerificationStatus(ProfileVerificationStatus.APPROVED);
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
   * Validates that the provided email addresses are not null and checks if they match.
   *
   * <p>This method ensures that neither requestEmailAddress nor authenticatedUserEmailAddress
   * is null. If either is null, an UnableToCompleteOperationException is thrown.
   * It then compares the two email addresses and throws a FailedOperationException if they are not the same.</p>
   *
   * @param requestEmailAddress the email address from the request. It must not be null.
   * @param authenticatedUserEmailAddress the email address of the authenticated user. It must not be null.
   * @throws UnableToCompleteOperationException if either requestEmailAddress or authenticatedUserEmailAddress is null.
   * @throws FailedOperationException if requestEmailAddress does not match authenticatedUserEmailAddress.
   */
  private void validateAndCheckIfEmailsInRequestAndAuthenticatedUserAreSame(final String requestEmailAddress, final String authenticatedUserEmailAddress) {
    // Check if either email address is null and throw an exception if so
    checkIsNullAny(List.of(requestEmailAddress, authenticatedUserEmailAddress), UnableToCompleteOperationException::new);

    // Compare the email addresses and throw an exception if they don't match
    if (!authenticatedUserEmailAddress.equals(requestEmailAddress)) {
      throw new FailedOperationException();
    }
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
  private void saveMfaVerificationCodeTemporarily(final String username, final String verificationCode) {
    cacheService.set(getMfaAuthenticationCacheKey(username), verificationCode, Duration.ofMinutes(5));
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
   * Authenticates a user with the provided email address and password.
   *
   * @param emailAddress the user's email address
   * @param password     the user's password
   * @return Authentication object representing the authenticated user
   * @throws org.springframework.security.core.AuthenticationException if authentication fails
   */
  public Optional<Authentication> authenticate(final String emailAddress, final String password) {
    Authentication authenticationToken = new UsernamePasswordAuthenticationToken(emailAddress, password);
    authenticationToken = authenticationManager.authenticate(authenticationToken);
    if (nonNull(authenticationToken)) {
      return Optional.of(authenticationToken);
    }
    return Optional.empty();
  }

  /**
   * Validates that the profile is not disabled or banned.
   *
   * @param profileStatus the current status of the profile
   * @throws DisabledAccountException if the profile is disabled
   * @throws BannedAccountException   if the profile is banned
   */
  protected void validateProfileIsNotDisabledOrBanned(final ProfileStatus profileStatus) {
    // Check if the profile is disabled
    if (ProfileStatus.isDisabled(profileStatus)) {
      throw new DisabledAccountException();
    }
    // Check if the profile is banned
    else if (ProfileStatus.isBanned(profileStatus)) {
      throw new BannedAccountException();
    }
  }

  /**
   * Creates a default sign-in response for the given user.
   *
   * @param user the FleenUser for whom the sign-in response is created
   * @return SignInResponse the default sign-in response
   */
  protected SignInResponse createDefaultSignInResponse(final FleenUser user) {
    // Create and return the default sign-in response using the user's email address
    return SignInResponse.createDefault(user.getEmailAddress());
  }

  /**
   * Checks if the user's profile is inactive and if the user is yet to be verified.
   *
   * @param user the FleenUser to check
   * @return boolean true if the user's profile is inactive, and they are yet to be verified, false otherwise
   */
  protected boolean isProfileInactiveAndUserYetToBeVerified(final FleenUser user) {
    return ProfileStatus.isInactive(user.getProfileStatus())
        && RoleType.isPreVerified(retrieveRoleForUserYetToCompleteSignUp(user));
  }

  /**
   * Retrieves the role type for a user who is yet to complete the sign-up process.
   *
   * @param user the FleenUser whose role is to be retrieved
   * @return RoleType the role type of the user before completing sign-up, or null if not available
   */
  protected RoleType retrieveRoleForUserYetToCompleteSignUp(final FleenUser user) {
    // Check if the user and their authorities are not null
    if (nonNull(user) && nonNull(user.getAuthorities())) {
      // Retrieve the first role of the user before completing sign-up
      final Role defaultUserRoleBeforeCompletingSignUp = user.authoritiesToRoles().getFirst();
      // Return the role type based on the retrieved role's code
      return RoleType.valueOf(defaultUserRoleBeforeCompletingSignUp.getCode());
    }

    // Return null if the user or their authorities are null
    return null;
  }

  /**
   * Handles the scenario where a user's profile is yet to be verified during sign-in.
   *
   * @param signInResponse the response object for the sign-in process
   * @param user           the FleenUser whose profile is yet to be verified
   */
  private void handleProfileYetToBeVerified(final SignInResponse signInResponse, final FleenUser user) {
    // Generate a one-time password (OTP)
    final String otpCode = generateOtp();
    // Create a pre-verification request with the OTP
    final SignUpVerificationRequest signUpVerificationRequest = createSignUpVerificationRequest(otpCode, VerificationType.EMAIL, user);
    // Send the sign-up verification code to the user
    profileRequestPublisher.publishMessage(PublishMessageRequest.of(signUpVerificationRequest));
    // Save the OTP code temporarily in the cache
    saveSignUpVerificationCodeTemporarily(user.getUsername(), otpCode);
    // Configure pre-verification authorities based on user role
    configureAuthoritiesOrRolesForUserYetToCompleteSignUp(user, retrieveRoleForUserYetToCompleteSignUp(user));
    // Initialize sign-in details for the user
    initializeAuthenticationAndCreateTokens(user, signInResponse);
    // Set the authentication stage in the sign-in response
    signInResponse.setAuthenticationStage(AuthenticationStage.PRE_VERIFICATION);
  }

  /**
   * Creates a sign-up verification request.
   *
   * @param otp              the one-time password for verification
   * @param verificationType the type of verification (e.g., EMAIL)
   * @param user             the FleenUser for whom the verification request is being created
   * @return the SignUpVerificationRequest object
   */
  public SignUpVerificationRequest createSignUpVerificationRequest(final String otp, final VerificationType verificationType, final FleenUser user) {
    // Create and return the sign-up verification request with user details and verification type
    return SignUpVerificationRequest
        .of(otp, user.getFirstName(), user.getLastName(), user.getEmailAddress(), user.getPhoneNumber(), verificationType);
  }

  /**
   * Configures authorities or roles for a user who has not yet completed the sign-up process.
   *
   * @param user     the FleenUser whose roles need to be configured
   * @param roleType the RoleType indicating the user's role
   */
  protected void configureAuthoritiesOrRolesForUserYetToCompleteSignUp(final FleenUser user, final RoleType roleType) {
    if (RoleType.isPreVerified(requireNonNull(roleType))) {
      user.setAuthorities(getUserPreVerifiedAuthorities());
    }
  }

  /**
   * Initializes authentication and creates tokens for the given user, updating the sign-in response accordingly.
   *
   * @param fleenUser      the FleenUser to authenticate and create tokens for
   * @param signInResponse the SignInResponse to update with authentication details
   */
  protected void initializeAuthenticationAndCreateTokens(final FleenUser fleenUser, final SignInResponse signInResponse) {
    // Initialize authentication for the user
    initializeAuthentication(fleenUser);
    // Create tokens, save them, and update the sign-in response
    createTokeAndSaveTokenAndUpdateResponse(fleenUser, signInResponse);
  }

  /**
   * Creates and saves authentication tokens, then updates the sign-in response with the tokens.
   *
   * @param user           the FleenUser for whom the tokens are generated
   * @param signInResponse the SignInResponse to be updated with the generated tokens and user details
   */
  protected void createTokeAndSaveTokenAndUpdateResponse(final FleenUser user, final SignInResponse signInResponse) {
    // Generate access and refresh tokens for the user
    final String accessToken = tokenService.createAccessToken(user);
    final String refreshToken = tokenService.createRefreshToken(user);

    // Save the generated tokens for the user
    saveAuthenticationTokensToRepositoryOrCache(user.getUsername(), accessToken, refreshToken);

    // Populate the sign-in response object with authentication details
    signInResponse.updateDetails(accessToken, refreshToken, user.getPhoneNumber());
  }

  /**
   * Initializes the authentication context for the given user.
   *
   * @param user the FleenUser whose authentication context is to be initialized
   */
  private void initializeAuthentication(final FleenUser user) {
    // Create an authentication token using the user's information and authorities
    final Authentication authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    // Set the authentication context with the authentication token
    setContext(authenticationToken);
  }

  /**
   * Checks if MFA (Multi-Factor Authentication) is enabled for the user and a valid MFA type is set.
   *
   * @param user the FleenUser to check for MFA settings
   * @return true if MFA is enabled and a valid MFA type is set; false otherwise
   */
  protected boolean isMfaEnabledAndMfaTypeSet(final FleenUser user) {
    return nonNull(user) && user.isMfaEnabled() && MfaType.isNotNone(user.getMfaType());
  }

  /**
   * Handles the sign-in process for a user with MFA (Multi-Factor Authentication) enabled.
   * Generates an OTP (One-Time Password), sends a verification message based on the user's MFA type,
   * sets pre-authentication authorities, creates an authentication token, generates an access token,
   * and updates the sign-in response with authentication details and MFA status.
   *
   * @param signInResponse the response object to update with authentication details
   * @param user           the FleenUser attempting to sign in
   */
  private void handleProfileWithMfaEnabled(final SignInResponse signInResponse, final FleenUser user) {
    // Generate a one-time password (OTP)
    final String otpCode = generateOtp();

    // Send a verification message based on the user's MFA type
    if (isMfaTypeByEmailOrPhone(user.getMfaType())) {
      final MfaVerificationRequest mfaVerificationRequest = getVerificationTypeAndCreateMfaVerificationRequest(otpCode, user);
      // Send the sign-up verification code to the user
      profileRequestPublisher.publishMessage(PublishMessageRequest.of(mfaVerificationRequest));
      // Save the OTP code temporarily in the cache
      saveMfaVerificationCodeTemporarily(user.getUsername(), otpCode);
    }

    // Set up pre-authentication authorities for the user
    user.setAuthorities(getPreAuthenticatedAuthorities());
    // Create an authentication token using the user's information and authorities
    final Authentication authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    // Generate an access token for the user
    final String accessToken = tokenService.createAccessToken(user);
    // Set the authentication context with the authentication token
    setContext(authenticationToken);
    // Save the access token for the user
    tokenService.saveAccessToken(user.getUsername(), accessToken);
    // Update authentication stage and MFA status in the sign-in response
    updateSignInResponseForMfaVerification(accessToken, user, signInResponse);
  }

  /**
   * Checks if the provided MFA (Multi-Factor Authentication) type is either by phone or email.
   *
   * @param mfaType the MFA type to check
   * @return true if the MFA type is PHONE or EMAIL, false otherwise
   */
  protected boolean isMfaTypeByEmailOrPhone(final MfaType mfaType) {
    return MfaType.isPhoneOrEmail(mfaType);
  }

  /**
   * Retrieves the corresponding verification type based on the provided MFA (Multi-Factor Authentication) type.
   *
   * @param mfaType the MFA type to retrieve verification type for
   * @return the VerificationType associated with the MFA type
   * @throws UnableToCompleteOperationException if the MFA type is null or cannot be mapped to a VerificationType
   */
  protected VerificationType getVerificationTypeByMfaType(final MfaType mfaType) {
    // Throw an exception if the provided MFA type is null
    checkIsNull(mfaType, UnableToCompleteOperationException::new);
    // Parse the MFA type into a VerificationType enum value
    final VerificationType verificationType = VerificationType.of(mfaType.name());
    // Throw an exception if the parsed VerificationType is null
    checkIsNull(verificationType, UnableToCompleteOperationException::new);

    return verificationType;
  }

  /**
   * Retrieves the verification type based on the user's MFA type and creates an MFA verification request.
   *
   * <p>This method determines the verification type based on the user's configured MFA type.
   * It then creates an MFA verification request object using the generated OTP code, user details,
   * and the determined verification type.</p>
   *
   * @param otpCode     the one-time password (OTP) generated for verification
   * @param user        the FleenUser for whom the verification request is being created
   * @return MfaVerificationRequest the request object containing OTP, user details, and verification type
   * @throws UnableToCompleteOperationException if there is an issue with determining or creating the verification type
   */
  protected MfaVerificationRequest getVerificationTypeAndCreateMfaVerificationRequest(final String otpCode, final FleenUser user) {
    final VerificationType verificationType = getVerificationTypeByMfaType(user.getMfaType());
    return MfaVerificationRequest
      .of(otpCode, user.getFirstName(), user.getLastName(), user.getEmailAddress(), user.getPhoneNumber(), verificationType);
  }

  /**
   * Updates the sign-in response object with details related to MFA verification.
   *
   * <p>This method sets the authentication stage to MFA verification,
   * enables MFA in the response, sets the MFA type, updates the access token,
   * clears the refresh token, and updates the user's email address and phone number
   * in the sign-in response.</p>
   *
   * @param accessToken     the access token generated for the authenticated user
   * @param user            the authenticated FleenUser for whom the sign-in response is being updated
   * @param signInResponse  the SignInResponse object to be updated with authentication details
   */
  protected void updateSignInResponseForMfaVerification(final String accessToken, final FleenUser user, final SignInResponse signInResponse) {
    // Set the authentication stage to MFA verification
    signInResponse.setAuthenticationStage(AuthenticationStage.MFA_VERIFICATION);
    // Enable MFA in the sign-in response
    signInResponse.setMfaEnabled(true);
    // Set the MFA type used by the user
    signInResponse.setMfaType(user.getMfaType());
    // Update the access token in the sign-in response
    signInResponse.setAccessToken(accessToken);
    // Clear the refresh token as it is not used in MFA authentication
    signInResponse.setRefreshToken(null);
    // Update the user's email address and phone number in the sign-in response
    signInResponse.updateEmailAndPhone(user.getEmailAddress(), user.getPhoneNumber());
  }

  /**
   * Handles the profile of a verified user during sign-in.
   *
   * <p>This method generates an access token with authentication status as COMPLETED,
   * creates a refresh token for the user, sets the provided authentication context,
   * saves the generated tokens, and updates the sign-in response with the tokens and authentication status.</p>
   *
   * @param signInResponse   the SignInResponse object to be updated with authentication details
   * @param user             the verified FleenUser for whom the sign-in response is being updated
   * @param authentication   the Authentication object representing the authenticated user's credentials
   */
  private void handleProfileThatIsVerified(final SignInResponse signInResponse, final FleenUser user, final Authentication authentication) {
    final AuthenticationStatus authenticationStatus = AuthenticationStatus.COMPLETED;
    // Generate an access token with authentication status as COMPLETED
    final String accessToken = tokenService.createAccessToken(user, authenticationStatus);
    // Generate a refresh token for the user
    final String refreshToken = tokenService.createRefreshToken(user);
    // Set the authentication context with the provided authentication object
    setContext(authentication);
    // Save the generated tokens for the user
    saveAuthenticationTokensToRepositoryOrCache(user.getUsername(), accessToken, refreshToken);
    // Update the sign-in response with the generated tokens and authentication status
    signInResponse.setAccessToken(accessToken);
    signInResponse.setRefreshToken(refreshToken);
    signInResponse.setAuthenticationStatus(authenticationStatus);
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
    // Set the member associated with the profile token.
    profileToken.setMember(member);
    // Set the reset password token in the profile token.
    profileToken.setResetPasswordToken(verificationTokenOrCode);
    // Set the expiry date for the reset password token (10 minutes from now).
    profileToken.setResetPasswordTokenExpiryDate(addMinutesFromNow(10));
    // Save the profile token.
    profileTokenRepository.save(profileToken);
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
    verifyResetPasswordTokenHasNotExpired(profileToken.getResetPasswordTokenExpiryDate());
  }

  /**
   * Finds the profile token associated with the given email address.
   *
   * @param emailAddress the email address associated with the profile token
   * @return the profile token found
   * @throws ResetPasswordCodeInvalidException if no profile token exists for the email address
   */
  protected ProfileToken findProfileToken(final String emailAddress) {
    final Optional<ProfileToken> existingProfileToken = profileTokenRepository.findByEmailAddress(emailAddress);

    // If no profile token exists, throw an exception indicating invalid reset password code.
    if (existingProfileToken.isEmpty()) {
      throw new ResetPasswordCodeInvalidException();
    }

    return existingProfileToken.get();
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
    // If no reset password token exists in the profile token, throw an exception indicating invalid reset password code.
    if (isNull(profileToken.getResetPasswordToken())) {
      throw new ResetPasswordCodeInvalidException();
    }

    // Check if the provided token or code matches the reset password token in the profile token.
    if (!(profileToken.getResetPasswordToken().equalsIgnoreCase(verificationTokenOrCode))) {
      throw new ResetPasswordCodeInvalidException();
    }
  }

  /**
   * Verifies that the reset password token has not expired.
   *
   * @param resetPasswordTokenExpiryDate the expiry date of the reset password token to verify
   * @throws ResetPasswordCodeExpiredException if the reset password token expiry date is null
   *                                          or before the current date and time
   */
  protected void verifyResetPasswordTokenHasNotExpired(final LocalDateTime resetPasswordTokenExpiryDate) {
    // Check if the reset password token has expired.
    if (isNull(resetPasswordTokenExpiryDate) || resetPasswordTokenExpiryDate.isBefore(LocalDateTime.now())) {
      throw new ResetPasswordCodeExpiredException();
    }
  }

  /**
   * Clears authentication tokens for the specified user.
   *
   * @param username the username of the user
   */
  @Async
  public void clearAuthenticationTokens(final String username) {
    final String accessTokenCacheKeyKey = getAccessTokenCacheKey(username);
    final String refreshTokenCacheKeyKey = getRefreshTokenCacheKey(username);
    final String resetPasswordTokenCacheKey = getResetPasswordTokenCacheKey(username);

    // Delete access token from cache if it exists
    if (cacheService.exists(accessTokenCacheKeyKey)) {
      cacheService.delete(accessTokenCacheKeyKey);
    }

    // Delete reset password token from cache if it exists
    if (cacheService.exists(resetPasswordTokenCacheKey)) {
      cacheService.delete(accessTokenCacheKeyKey);
    }

    // Delete refresh token from cache if it exists
    if (cacheService.exists(refreshTokenCacheKeyKey)) {
      cacheService.delete(refreshTokenCacheKeyKey);
    }
  }

  /**
   * Finds the profile token by email address and resets the reset password token and its expiry date if found.
   *
   * @param emailAddress the email address associated with the profile token
   */
  public void findPasswordTokenAndResetOrClearDetails(final String emailAddress) {
    // Find the profile token by email address
    final ProfileToken profileToken = profileTokenRepository.findByEmailAddress(emailAddress)
        .orElse(null);

    // If profile token exists, reset the reset password token and its expiry date
    if (nonNull(profileToken)) {
      resetProfileToken(profileToken);
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
  public void verifyUserHasResetPasswordToken(final String emailAddress) {
    if (!tokenService.isResetPasswordTokenExist(emailAddress)) {
      throw new InvalidAuthenticationException(emailAddress);
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
      profileToken.setResetPasswordToken(null);
      profileToken.setResetPasswordTokenExpiryDate(null);
      // Save the updated profile token.
      profileTokenRepository.save(profileToken);
    }
  }

  /**
   * Clears the user's authentication details from the security context.
   *
   * <p>This method removes the current user's authentication details by setting the authentication
   * in the {@link SecurityContextHolder} to null and then clearing the security context entirely.</p>
   */
  protected void clearUserAuthenticationDetails() {
    SecurityContextHolder.getContext().setAuthentication(null);
    SecurityContextHolder.clearContext();
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
      // Retrieve the country information from the cache based on the user's country code
      final Optional<CountryResponse> existingCountry = countryService.getCountryFromCache(user.getCountry());
      // If the country information is present, set the user's timezone
      if (existingCountry.isPresent()) {
        final CountryResponse country = existingCountry.get();
        user.setTimezone(country.getTimezone());
      }
    }
  }
}
