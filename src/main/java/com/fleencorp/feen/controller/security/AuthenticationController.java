package com.fleencorp.feen.controller.security;

import com.fleencorp.feen.model.dto.auth.*;
import com.fleencorp.feen.model.response.auth.DataForSignUpResponse;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.security.ForgotPasswordResponse;
import com.fleencorp.feen.model.response.security.InitiatePasswordChangeResponse;
import com.fleencorp.feen.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  public AuthenticationController(final AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  @GetMapping(value = "/data-for-sign-up")
  @Cacheable(value = "data-required-to-sign-up")
  public DataForSignUpResponse getDataForSignUp() {
    return authenticationService.getDataForSignUp();
  }

  @PostMapping(value = "/sign-up")
  public SignUpResponse signUp(@Valid @RequestBody final SignUpDto signUpDto) {
    return authenticationService.signUp(signUpDto);
  }

  @PostMapping(value = "/sign-in")
  public SignInResponse signIn(
      @Valid @RequestBody final SignInDto signInDto) {
    return authenticationService.signIn(signInDto);
  }

  @PostMapping(value = "/forgot-password")
  public ForgotPasswordResponse forgotPassword(
      @Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
    return authenticationService.forgotPassword(forgotPasswordDto);
  }

  @PostMapping(value = "/verify-reset-password-code")
  public InitiatePasswordChangeResponse validateResetPasswordCode(
      @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
    return authenticationService.verifyResetPasswordCode(resetPasswordDto);
  }
}
