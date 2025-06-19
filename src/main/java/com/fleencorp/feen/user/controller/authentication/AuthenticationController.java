package com.fleencorp.feen.user.controller.authentication;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.response.authentication.DataForSignUpResponse;
import com.fleencorp.feen.model.response.authentication.SignInResponse;
import com.fleencorp.feen.model.response.authentication.SignUpResponse;
import com.fleencorp.feen.model.response.security.ForgotPasswordResponse;
import com.fleencorp.feen.model.response.security.InitiatePasswordChangeResponse;
import com.fleencorp.feen.user.exception.authentication.InvalidAuthenticationException;
import com.fleencorp.feen.user.exception.user.BannedAccountException;
import com.fleencorp.feen.user.exception.user.DisabledAccountException;
import com.fleencorp.feen.user.exception.user.UserNotFoundException;
import com.fleencorp.feen.user.model.dto.authentication.ForgotPasswordDto;
import com.fleencorp.feen.user.model.dto.authentication.ResetPasswordDto;
import com.fleencorp.feen.user.model.dto.authentication.SignInDto;
import com.fleencorp.feen.user.model.dto.authentication.SignUpDto;
import com.fleencorp.feen.user.service.authentication.AuthenticationService;
import com.fleencorp.feen.verification.exception.core.ResetPasswordCodeInvalidException;
import com.fleencorp.feen.verification.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationController {

  private final AuthenticationService authenticationService;
  private final VerificationService verificationService;

  public AuthenticationController(
      final AuthenticationService authenticationService,
      final VerificationService verificationService) {
    this.authenticationService = authenticationService;
    this.verificationService = verificationService;
  }

  @Operation(summary = "Retrieve data required for user sign-up",
    description = "Returns the necessary data for a user to register, such as available roles or " +
      "other configuration options. This data is cached for efficiency."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Data for sign-up retrieved successfully",
      content = @Content(schema = @Schema(implementation = DataForSignUpResponse.class)))
  })
  @GetMapping(value = "/data-for-sign-up")
  @Cacheable(value = "data-required-to-sign-up")
  public DataForSignUpResponse getDataForSignUp() {
    return authenticationService.getDataForSignUp();
  }

  @Operation(summary = "Register a new user",
    description = "Registers a new user account based on the provided sign-up details."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "User registered successfully",
      content = @Content(schema = @Schema(implementation = SignUpResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid sign-up details provided",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class)))
  })
  @PostMapping(value = "/sign-up")
  public SignUpResponse signUp(
      @Parameter(description = "Sign-up details for the new user", required = true)
        @Valid @RequestBody final SignUpDto signUpDto) {
    return authenticationService.signUp(signUpDto);
  }

  @Operation(summary = "Authenticate an existing user",
    description = "Authenticates a user based on their provided sign-in credentials."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "User authenticated successfully",
      content = @Content(schema = @Schema(implementation = SignInResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid sign-in credentials provided",
      content = @Content(schema = @Schema(implementation = FailedOperationException.class))),
    @ApiResponse(responseCode = "401", description = "Invalid authentication credentials",
      content = @Content(schema = @Schema(implementation = InvalidAuthenticationException.class))),
    @ApiResponse(responseCode = "403", description = "Account is either disabled or banned",
      content = @Content(schema = @Schema(oneOf = {DisabledAccountException.class, BannedAccountException.class})))
  })
  @PostMapping(value = "/sign-in")
  public SignInResponse signIn(
      @Parameter(description = "Sign-in credentials of the user", required = true)
        @Valid @RequestBody final SignInDto signInDto) {
    return authenticationService.signIn(signInDto);
  }

  @Operation(summary = "Initiate the forgot password process",
    description = "Initiates the process for a user to reset their forgotten password by sending " +
      "a verification code to their registered email address or username."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Forgot password process initiated successfully",
      content = @Content(schema = @Schema(implementation = ForgotPasswordResponse.class))),
    @ApiResponse(responseCode = "404", description = "User not found",
      content = @Content(schema = @Schema(implementation = UserNotFoundException.class)))
  })
  @PostMapping(value = "/forgot-password")
  public ForgotPasswordResponse forgotPassword(
      @Parameter(description = "Details (email or username) to initiate password reset", required = true)
        @Valid @RequestBody final ForgotPasswordDto forgotPasswordDto) {
    return verificationService.forgotPassword(forgotPasswordDto);
  }

  @Operation(summary = "Verify the reset password code",
    description = "Verifies the code provided by the user during the forgot password process. " +
      "Upon successful verification, it initiates the password change process."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Reset password code verified successfully",
      content = @Content(schema = @Schema(implementation = InitiatePasswordChangeResponse.class))),
    @ApiResponse(responseCode = "400", description = "Invalid reset password code provided",
      content = @Content(schema = @Schema(implementation = ResetPasswordCodeInvalidException.class))),
    @ApiResponse(responseCode = "404", description = "User not found",
      content = @Content(schema = @Schema(implementation = UserNotFoundException.class)))
  })
  @PostMapping(value = "/verify-reset-password-code")
  public InitiatePasswordChangeResponse validateResetPasswordCode(
      @Parameter(description = "Details including the reset password code", required = true)
        @Valid @RequestBody final ResetPasswordDto resetPasswordDto) {
    return verificationService.verifyResetPasswordCode(resetPasswordDto);
  }
}
