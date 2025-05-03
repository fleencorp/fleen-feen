package com.fleencorp.feen.service.impl.auth;

import com.fleencorp.feen.constant.security.auth.AuthenticationStage;
import com.fleencorp.feen.constant.security.auth.AuthenticationStatus;
import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.constant.security.profile.ProfileVerificationStatus;
import com.fleencorp.feen.constant.security.role.RoleType;
import com.fleencorp.feen.constant.security.verification.VerificationType;
import com.fleencorp.feen.event.model.base.PublishMessageRequest;
import com.fleencorp.feen.event.publisher.ProfileRequestPublisher;
import com.fleencorp.feen.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.user.UserNotFoundException;
import com.fleencorp.feen.exception.user.profile.BannedAccountException;
import com.fleencorp.feen.exception.user.profile.DisabledAccountException;
import com.fleencorp.feen.exception.user.role.NoRoleAvailableToAssignException;
import com.fleencorp.feen.mapper.CommonMapper;
import com.fleencorp.feen.model.domain.other.Country;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.domain.user.Role;
import com.fleencorp.feen.model.dto.auth.SignInDto;
import com.fleencorp.feen.model.dto.auth.SignUpDto;
import com.fleencorp.feen.model.request.auth.SignUpVerificationRequest;
import com.fleencorp.feen.model.request.mfa.MfaVerificationRequest;
import com.fleencorp.feen.model.request.search.CountrySearchRequest;
import com.fleencorp.feen.model.response.auth.DataForSignUpResponse;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.country.CountryResponse;
import com.fleencorp.feen.model.response.security.SignOutResponse;
import com.fleencorp.feen.model.search.country.CountrySearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.security.ProfileTokenRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.auth.AuthenticationService;
import com.fleencorp.feen.service.auth.PasswordService;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.feen.service.security.TokenService;
import com.fleencorp.feen.service.security.mfa.MfaService;
import com.fleencorp.feen.service.user.MemberService;
import com.fleencorp.feen.service.user.RoleService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.feen.service.impl.common.CacheKeyService.getMfaAuthenticationCacheKey;
import static com.fleencorp.feen.service.impl.common.CacheKeyService.getSignUpVerificationCacheKey;
import static com.fleencorp.feen.service.security.OtpService.generateOtp;
import static com.fleencorp.feen.util.security.UserAuthoritiesUtil.getPreAuthenticatedAuthorities;
import static com.fleencorp.feen.util.security.UserAuthoritiesUtil.getUserPreVerifiedAuthorities;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

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
 *
 * @author Yusuf Àlàmù Mùsà
 * @version 1.0
 */
@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService,
    PasswordService {

  private final AuthenticationManager authenticationManager;
  private final MemberService memberService;
  private final CacheService cacheService;
  private final CountryService countryService;
  private final RoleService roleService;
  private final TokenService tokenService;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final ProfileRequestPublisher profileRequestPublisher;
  private final Localizer localizer;
  private final CommonMapper commonMapper;
  private final String originDomain;

  /**
   * Constructs an instance of {@link AuthenticationServiceImpl} with the provided dependencies.
   *
   * <p>This constructor initializes the service with various components necessary for managing
   * authentication, such as the {@link AuthenticationManager}, {@link CacheService}, {@link CountryService},
   * {@link MfaService}, {@link RoleService}, {@link TokenService}, {@link MemberRepository},
   * {@link PasswordEncoder}, {@link ProfileRequestPublisher}, {@link ProfileTokenRepository},
   * and {@link Localizer}. These dependencies are injected to facilitate authentication
   * operations, including managing user roles, tokens, MFA, and profile-related actions.</p>
   *
   * @param authenticationManager the manager responsible for processing authentication requests.
   * @param memberService the service for managing members and profile information
   * @param cacheService the service handling cache operations.
   * @param countryService the service providing country-related data and operations.
   * @param roleService the service managing user roles.
   * @param tokenService the service handling token generation and validation.
   * @param memberRepository the repository for accessing and managing {@link Member} entities.
   * @param passwordEncoder the encoder for processing passwords.
   * @param profileRequestPublisher the publisher for sending profile-related requests.
   * @param localizer the service for handling localized responses.
   * @param commonMapper a service for creating info data and their localized text
   * @param originDomain the origin domain used in the app to perform actions
   */
  public AuthenticationServiceImpl(
      final AuthenticationManager authenticationManager,
      final CacheService cacheService,
      final CountryService countryService,
      @Lazy final MemberService memberService,
      final RoleService roleService,
      final TokenService tokenService,
      final MemberRepository memberRepository,
      final PasswordEncoder passwordEncoder,
      final ProfileRequestPublisher profileRequestPublisher,
      final Localizer localizer,
      final CommonMapper commonMapper,
      @Value("${origin-domain}") final String originDomain) {
    this.authenticationManager = authenticationManager;
    this.cacheService = cacheService;
    this.countryService = countryService;
    this.memberService = memberService;
    this.roleService = roleService;
    this.tokenService = tokenService;
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
    this.profileRequestPublisher = profileRequestPublisher;
    this.localizer = localizer;
    this.commonMapper = commonMapper;
    this.originDomain = originDomain;
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
    // Create the search request
    final CountrySearchRequest countrySearchRequest = CountrySearchRequest.ofPageSize(1000);
    // Fetch a list of countries with a large number of entries (1000 in this case).
    final CountrySearchResult searchResult = countryService.findCountries(countrySearchRequest);
    // Get the countries in the search result
    final Collection<?> countries = searchResult.getResult().getValues();
    // Create the response
    final DataForSignUpResponse dataForSignUpResponse = DataForSignUpResponse.of(countries);
    // Return the response object containing both the countries and timezones.
    return localizer.of(dataForSignUpResponse);
  }

  /**
   * Signs up a new member based on the provided sign-up data.
   *
   * <p>Configures roles and statuses for the new member's profile, encodes the password,
   * saves the member to the repository, initializes authentication and context,
   * generates access and refresh tokens, sends a sign-up verification code,
   * and saves authentication tokens.</p>
   *
   * @param signUpDto The DTO containing sign-up data.
   * @return SignUpResponse containing access token, refresh token, email, phone number,
   *         authentication status, and profile verification type.
   */
  @Override
  @Transactional
  public SignUpResponse signUp(final SignUpDto signUpDto) throws FailedOperationException {
    // Convert the user details to a member
    final Member member = signUpDto.toMember();
    // Retrieve the verification type
    final VerificationType verificationType = signUpDto.getVerificationType();
    // Setup the user roles, status and verification status, user location and encode the password
    setupMemberProfile(member, signUpDto);
    // Save the member to the repository
    memberRepository.save(member);
    // Initialize authentication and set context for the new member
    final FleenUser user = authenticateAndInitializeContext(member);

    // Generate a access token for the authenticated user
    final String accessToken = tokenService.createAccessToken(user);
    // Generate a refresh token for the authenticated user
    final String refreshToken = tokenService.createRefreshToken(user);
    // Handle verifications like sending verification message and saving the otp code
    handleVerificationAndTokens(user, verificationType, accessToken, refreshToken);
    // Return a localized response of the sign up
    return createSignUpResponse(user, accessToken, refreshToken, verificationType);
  }

  /**
   * Sets up the profile for a new member during the sign-up process.
   *
   * <p>This method configures the roles and statuses for the new {@link Member}'s profile,
   * encodes or hashes the user's password, verifies if the user should be marked as an internal user based
   * on their email domain, and sets up the user's location details.</p>
   *
   * @param member the {@link Member} whose profile is being set up
   * @param signUpDto the {@link SignUpDto} containing user sign-up details such as the password and country code
   */
  protected void setupMemberProfile(final Member member, final SignUpDto signUpDto) {
    // Configure roles for the new member's profile
    configureRolesForNewProfile(member);
    // Configure statuses for the new member's profile
    configureStatusesForNewProfile(member);
    // Encode or hash the user's password before saving
    encodeAndHashUserPassword(signUpDto, member);
    // Validate user email address domain and mark as an internal user
    verifyIfUserShouldBeInternal(member);
    // Set user location details
    configureUserLocationDetails(member, signUpDto.getCountryCode());
  }

  /**
   * Authenticates the provided member and initializes the authentication context.
   *
   * <p>This method handles the authentication process for the given {@link Member}, initializes the
   * authentication context for the associated {@link FleenUser}, and sets the user's timezone after
   * successful authentication.</p>
   *
   * @param member the {@link Member} to be authenticated and initialized
   * @return the authenticated {@link FleenUser} with the authentication context set
   */
  protected FleenUser authenticateAndInitializeContext(final Member member) {
    // Initialize authentication and set context for the new member
    final FleenUser user = initializeAuthenticationAndContext(member);
    // Set user timezone after authentication
    setUserTimezoneAfterAuthentication(user);
    // Return the user
    return user;
  }

  /**
   * Handles the verification process and saves authentication tokens for the user.
   *
   * <p>This method generates a one-time password (OTP) for user verification and sends a sign-up
   * verification message to the user. It also temporarily saves the OTP and stores the access
   * and refresh tokens in the repository or cache.</p>
   *
   * @param user the {@link FleenUser} for whom the verification and token handling will be done
   * @param verificationType the type of verification to be performed (e.g., email, phone)
   * @param accessToken the access token to be saved for the user
   * @param refreshToken the refresh token to be saved for the user
   */
  protected void handleVerificationAndTokens(final FleenUser user, final VerificationType verificationType, final String accessToken, final String refreshToken) {
    final String otpCode = generateOtp();
    // Send sign up verification message to user
    sendSignUpVerificationMessage(otpCode, verificationType, user);
    // Save sign-up verification code temporarily
    saveSignUpVerificationCodeTemporarily(user.getUsername(), otpCode);
    // Save authentication tokens for the user
    saveAuthenticationTokensToRepositoryOrCache(user.getUsername(), accessToken, refreshToken);
  }

  /**
   * Creates and returns a sign-up response for the user.
   *
   * <p>This method generates a {@link SignUpResponse} using the provided access token, refresh token,
   * and user details such as email address and phone number. It also updates the response with the
   * verification type information before returning the localized response.</p>
   *
   * @param user the {@link FleenUser} whose details (email and phone number) are included in the sign-up response
   * @param accessToken the access token to be included in the response
   * @param refreshToken the refresh token to be included in the response
   * @param verificationType the type of verification (e.g., email, phone) to be set in the response
   * @return the {@link SignUpResponse} with localized details and necessary information for the sign-up process
   */
  protected SignUpResponse createSignUpResponse(final FleenUser user, final String accessToken, final String refreshToken, final VerificationType verificationType) {
    // Create default sign up response
    final SignUpResponse signUpResponse = SignUpResponse.ofDefault(accessToken, refreshToken, user.getEmailAddress(), user.getPhoneNumber());
    // Update verification type info data and text
    commonMapper.setVerificationType(signUpResponse, verificationType);
    // Return the sign-up response with necessary details
    return localizer.of(signUpResponse);
  }

  /**
   * Encodes or hashes the user's password before saving it to the member entity.
   *
   * <p>This method retrieves the password from the provided {@code signUpDto} and
   * then encodes or hashes the password before setting it in the {@code member} object.
   * The encoding or hashing operation is handled by {@link #encodeOrHashUserPassword(Member, String)}.</p>
   *
   * @param signUpDto the sign-up details containing the user's raw password
   * @param member the member entity where the hashed password will be stored
   */
  protected void encodeAndHashUserPassword(final SignUpDto signUpDto, final Member member) {
    // Encode or hash the user's password before saving
    final String password = signUpDto.getPassword();
    encodeOrHashUserPassword(member, password);
  }

  /**
   * Verifies whether the user should be marked as an internal user based on their email domain.
   *
   * <p>This method checks the domain of the user's email and marks the {@code member}
   * as an internal user if the domain matches certain criteria. The actual logic
   * of confirming and setting the user as internal is handled by {@link Member#confirmAndSetInternalUser(String)}.</p>
   *
   * @param member the member entity to be evaluated and potentially marked as an internal user
   */
  protected void verifyIfUserShouldBeInternal(final Member member) {
    member.confirmAndSetInternalUser(originDomain);
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
    memberService.clearAuthenticationTokens(username);
    // Clear the security context
    clearUserAuthenticationDetails();
    // Return a localized response
    return localizer.of(SignOutResponse.of());
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
    final Authentication authentication = authenticateCredentials(emailAddress, password);
    // Retrieve the user from the Authentication Object
    final FleenUser user = (FleenUser) authentication.getPrincipal();
    // Set user timezone after authentication
    setUserTimezoneAfterAuthentication(user);
    // Validate profile status before proceeding
    validateProfileIsNotDisabledOrBanned(user.getProfileStatus());
    // Build and create a default sign in response before other checks & validations
    final SignInResponse signInResponse = createDefaultSignInResponse(user);

    // Handle sign-in based on user's profile and MFA settings
    if (isProfileInactiveAndUserYetToBeVerified(user)) {
      return processSignInForProfileYetToBeVerified(signInResponse, user);
    }

    // Handle sign-in based on user's profile with enabled MFA
    if (isMfaEnabledAndMfaTypeSet(user)) {
      return processSignInForProfileWithMfaEnabled(signInResponse, user);
    }

    // Handle verified profile sign-in process
    return processSignInForProfileThatIsVerified(signInResponse, user, authentication);
  }

  /**
   * Authenticates a user's credentials using their email address and password.
   *
   * <p>This method attempts to authenticate the user with the provided email address and password.
   * If authentication fails, an {@link InvalidAuthenticationException} is thrown with the email address.</p>
   *
   * @param emailAddress the email address of the user attempting to authenticate
   * @param password the password provided by the user for authentication
   * @return an {@link Authentication} object representing the authenticated user
   * @throws InvalidAuthenticationException if the authentication fails due to invalid credentials
   */
  protected Authentication authenticateCredentials(final String emailAddress, final String password) {
    return authenticate(emailAddress, password)
      .orElseThrow(InvalidAuthenticationException.of(emailAddress));
  }

  /**
   * Processes the sign-in for a user whose profile is yet to be verified.
   *
   * <p>This method handles the sign-in process for a user whose profile has incomplete verification details
   * such as email or phone. The user may not yet have completed all necessary verification steps.
   * The result is returned as a localized response with a message code indicating the pre-verification status.</p>
   *
   * @param signInResponse the sign-in response object to be updated based on the user's verification status
   * @param user the authenticated {@link FleenUser} whose profile is yet to be verified
   * @return a {@link SignInResponse} containing the result of the sign-in process, with a
   *         localization message for pre-verification
   */
  protected SignInResponse processSignInForProfileYetToBeVerified(final SignInResponse signInResponse, final FleenUser user) {
    // Handle verified profile sign-in for verified user incomplete verification details like email or phone
    handleProfileYetToBeVerified(signInResponse, user);
    // Return a localized response for the sign in
    return localizer.of(signInResponse, signInResponse.getPreVerificationMessageCode());
  }

  /**
   * Processes the sign-in for a user with Multi-Factor Authentication (MFA) enabled.
   *
   * <p>This method handles the sign-in process for a user whose profile has MFA or 2FA enabled. It ensures
   * that the necessary MFA steps are carried out before allowing the user to complete the sign-in.
   * The result is returned as a localized response, with an appropriate message code for MFA.</p>
   *
   * @param signInResponse the sign-in response object to be updated based on the user's MFA settings
   * @param user the authenticated {@link FleenUser} whose profile has MFA enabled
   * @return a {@link SignInResponse} containing the final result of the sign-in process, with the
   *         appropriate localization message for MFA
   */
  protected SignInResponse processSignInForProfileWithMfaEnabled(final SignInResponse signInResponse, final FleenUser user) {
    // Handle verified profile sign-in for verified user with mfa or 2fa enabled
    handleProfileWithMfaEnabled(signInResponse, user);
    // Return a localized response for the sign in
    return localizer.of(signInResponse, signInResponse.getMfaMessageCode());
  }

  /**
   * Processes the sign-in for a user with a verified profile.
   *
   * <p>This method handles the final steps of the sign-in process for a user whose profile has been verified. It
   * ensures that the user's profile is fully authenticated and their access is granted, with any necessary
   * post-authentication actions being applied. The result of the sign-in process is then returned as a
   * localized response.</p>
   *
   * @param signInResponse the sign-in response object to be updated based on the verified profile
   * @param user the authenticated {@link FleenUser} whose profile is verified
   * @param authentication the {@link Authentication} object containing the authenticated user's details
   * @return a {@link SignInResponse} containing the final result of the sign-in process, with any
   *         appropriate localization messages
   */
  protected SignInResponse processSignInForProfileThatIsVerified(final SignInResponse signInResponse, final FleenUser user, final Authentication authentication) {
    // Handle verified profile sign-in for verified user
    handleProfileThatIsVerified(signInResponse, user, authentication);
    // Return a localized response after the sign in process completes
    return localizer.of(signInResponse);
  }

  /**
   * Configures roles for a new profile.
   *
   * <p>This method assigns default roles to a new member's profile. It first checks if the member
   * is null and throws an {@link FailedOperationException} if true. Then, it collects the
   * default user roles by mapping {@link RoleType#USER} to its value. If no default roles are found,
   * it throws a {@link NoRoleAvailableToAssignException}. Finally, it retrieves the roles from the
   * {@link RoleService} and adds them to the member's roles.</p>
   *
   * @param member The new member for whom the roles are to be configured.
   * @throws FailedOperationException if the member is null.
   * @throws NoRoleAvailableToAssignException if no default roles are available to assign.
   */
  public void configureRolesForNewProfile(final Member member) {
    // Throw an exception if the provided member is null
    checkIsNull(member, FailedOperationException::new);

    // Retrieve the default user roles
    final Set<String> defaultUserRoles = RoleType.getDefaultUserRoles();
    // Retrieve roles from the role service and add them to the member
    final List<Role> roles = roleService.findAllByCode(defaultUserRoles);
    // Add the roles to the existing user roles
    member.addRoles(roles);

    // Check if the default roles available to assign to user is not empty
    if (roles.isEmpty()) {
      throw new NoRoleAvailableToAssignException();
    }
  }

  /**
   * Configures statuses for a new profile.
   *
   * <p>This method assigns initial statuses to a new member's profile. It checks if the member
   * is null and throws an {@link FailedOperationException} if true. Then, it sets the
   * profile status to {@link ProfileStatus#INACTIVE} and the verification status to
   * {@link ProfileVerificationStatus#PENDING}.</p>
   *
   * @param member The new member for whom the statuses are to be configured.
   * @throws FailedOperationException if the member is null.
   */
  public void configureStatusesForNewProfile(final Member member) {
    // Throw an exception if the provided member is null
    checkIsNull(member, FailedOperationException::new);
    // Mark member's profile status as inactive and the verification status as pending
    member.markProfileInactiveAndPending();
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
   * @throws FailedOperationException if the member is null.
   */
  public void configureUserLocationDetails(final Member member, final String countryCode) {
    // Throw an exception if the provided member is null
    checkIsNull(List.of(member, countryCode), FailedOperationException::new);

    // Get country name and details
    final Country country = countryService.getCountryByCode(countryCode);
    // Set user country
    member.setCountry(country.getTitle());
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
  @Override
  public void saveSignUpVerificationCodeTemporarily(final String username, final String verificationCode) {
    cacheService.set(getSignUpVerificationCacheKey(username), verificationCode, Duration.ofMinutes(5));
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
   * Authenticates a user with the provided email address and password.
   *
   * @param emailAddress the user's email address
   * @param password     the user's password
   * @return Authentication object representing the authenticated user
   * @throws org.springframework.security.core.AuthenticationException if authentication fails
   */
  public Optional<Authentication> authenticate(final String emailAddress, final String password) {
    // Create authentication token using the provided email address and password
    final Authentication authenticationToken = new UsernamePasswordAuthenticationToken(emailAddress, password);
    // Authenticate the token using the authentication manager
    return Optional.of(authenticationToken)
      .map(authenticationManager::authenticate);
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
    final SignInResponse signInResponse = SignInResponse.ofDefault(user.getEmailAddress());
    // Set the mfa status information and localized text
    commonMapper.setMfaEnabled(signInResponse, false);
    return signInResponse;
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
    sendSignUpVerificationMessage(otpCode, VerificationType.EMAIL, user);
    // Save the OTP code temporarily in the cache
    saveSignUpVerificationCodeTemporarily(user.getUsername(), otpCode);
    // Configure pre-verification authorities based on user role
    configureAuthoritiesOrRolesForUserYetToCompleteSignUp(user, retrieveRoleForUserYetToCompleteSignUp(user));
    // Initialize sign-in details for the user
    initializeAuthenticationAndCreateTokens(user, signInResponse);
    // Set the authentication stage in the sign-in response
    signInResponse.setAuthenticationStage(AuthenticationStage.preVerification());
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
   * Handles the sign-in process for a user with multi-factor authentication (MFA) enabled.
   *
   * <p>This method first initiates MFA verification for the user, sets up pre-authenticated
   * authorities, generates and saves an access token, and finally updates the sign-in response
   * with the authentication stage and the access token.</p>
   *
   * @param signInResponse the response object that will be updated with the authentication details
   * @param user the user for whom the sign-in process with MFA will be handled
   */
  protected void handleProfileWithMfaEnabled(final SignInResponse signInResponse, final FleenUser user) {
    // Generate and handle OTP-based MFA verification if applicable
    handleMfaVerification(user);
    // Set up pre-authenticated authorities
    setPreAuthenticatedAuthorities(user);
    // Create and save access token
    final String accessToken = generateAndSaveAccessToken(user);
    // Update sign-in response with the authentication stage and access token
    updateSignInResponseForMfaVerification(accessToken, user, signInResponse);
  }

  /**
   * Handles the multi-factor authentication (MFA) verification process for the user.
   *
   * <p>This method checks if the user's MFA type is either email or phone-based. If so,
   * it generates an OTP code, sends the MFA verification message to the user,
   * and temporarily saves the verification code for future validation.</p>
   *
   * @param user the user for whom the MFA verification process is being handled
   */
  protected void handleMfaVerification(final FleenUser user) {
    // Check if the MFA type is email or phone-based
    if (isMfaTypeByEmailOrPhone(user.getMfaType())) {
      // Generate a one-time password (OTP) for verification
      final String otpCode = generateOtp();
      // Send the MFA verification message to the user
      sendMfaVerificationMessage(user, otpCode);
      // Temporarily save the generated OTP for future validation
      saveMfaVerificationCodeTemporarily(user.getUsername(), otpCode);
    }
  }

  /**
   * Sends a multi-factor authentication (MFA) verification message to the user.
   *
   * <p>This method creates an MFA verification request using the provided one-time
   * password (OTP) code and user details. The request is then published to the
   * profile request publisher to send the verification message.</p>
   *
   * @param user the user for whom the MFA verification message is being sent
   * @param otpCode the one-time password (OTP) code for the MFA verification
   */
  protected void sendMfaVerificationMessage(final FleenUser user, final String otpCode) {
    // Create the MFA verification request based on the OTP code and user details
    final MfaVerificationRequest mfaVerificationRequest = getVerificationTypeAndCreateMfaVerificationRequest(otpCode, user);
    // Publish the MFA verification request message
    profileRequestPublisher.publishMessage(PublishMessageRequest.of(mfaVerificationRequest));
  }

  /**
   * Sets pre-authenticated authorities for the specified user and updates the security context.
   *
   * <p>This method assigns a set of pre-authenticated authorities to the given {@code user},
   * then creates an authentication token with those authorities and updates the security context
   * with the new authentication.</p>
   *
   * @param user the user for whom pre-authenticated authorities will be set
   */
  protected void setPreAuthenticatedAuthorities(final FleenUser user) {
    // Assign pre-authenticated authorities to the user
    user.setAuthorities(getPreAuthenticatedAuthorities());
    // Create an authentication token with the user's authorities
    final Authentication authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    // Set the authentication token in the security context
    setContext(authenticationToken);
  }

  /**
   * Generates and saves an access token for the specified user.
   *
   * <p>This method creates an access token for the given {@code user} and stores it
   * in the token service using the user's username. The generated access token is
   * then returned.</p>
   *
   * @param user the user for whom the access token will be generated
   * @return the generated access token for the user
   */
  protected String generateAndSaveAccessToken(final FleenUser user) {
    final String accessToken = tokenService.createAccessToken(user);
    tokenService.saveAccessToken(user.getUsername(), accessToken);
    return accessToken;
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
   * @throws FailedOperationException if the MFA type is null or cannot be mapped to a VerificationType
   */
  protected VerificationType getVerificationTypeByMfaType(final MfaType mfaType) {
    // Throw an exception if the provided MFA type is null
    checkIsNull(mfaType, FailedOperationException::new);
    // Parse the MFA type into a VerificationType enum value
    final VerificationType verificationType = VerificationType.of(mfaType.name());
    // Throw an exception if the parsed VerificationType is null
    checkIsNull(verificationType, FailedOperationException::new);
    // Return the verification type
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
   * @throws FailedOperationException if there is an issue with determining or creating the verification type
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
    // Set the enabled status and MFA type in the sign-in response
    commonMapper.setMfaEnabledAndMfaType(signInResponse, true, user.getMfaType());
    // Update the access and refresh token in the sign-in response. Refresh token is not available as not needed in MFA authentication stage
    signInResponse.updateAccessAndRefreshToken(accessToken, null);
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
    signInResponse.updateTokenAndAuthenticationStatus(accessToken, refreshToken, authenticationStatus);
  }

  /**
   * Saves the access token and refresh token for the given username using the JWT service.
   *
   * @param username      The username for which the tokens are saved.
   * @param accessToken   The access token to be saved.
   * @param refreshToken  The refresh token to be saved.
   */
  @Override
  public void saveAuthenticationTokensToRepositoryOrCache(final String username, final String accessToken, final String refreshToken) {
    // Save the access token for the username using the JWT service.
    tokenService.saveAccessToken(username, accessToken);
    // Save the refresh token for the username using the JWT service.
    tokenService.saveRefreshToken(username, refreshToken);
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
  @Override
  public void setUserTimezoneAfterAuthentication(final FleenUser user) {
    // Check if the user is not null before proceeding
    if (nonNull(user)) {
      // Retrieve the country information from the cache based on the user's country code and set the user timezone
      countryService.getCountryFromCache(user.getCountry())
        .map(CountryResponse::getTimezone)
        .ifPresent(user::setTimezone);
    }
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
  public Member getMemberDetails(final String emailAddressOrUsername) throws UserNotFoundException {
    return memberRepository.findByEmailAddress(emailAddressOrUsername)
      .orElseThrow(UserNotFoundException.of(emailAddressOrUsername));
  }
}
