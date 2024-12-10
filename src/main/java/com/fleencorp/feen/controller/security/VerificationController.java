package com.fleencorp.feen.controller.security;

import com.fleencorp.feen.model.dto.auth.ChangePasswordDto;
import com.fleencorp.feen.model.dto.auth.CompleteSignUpDto;
import com.fleencorp.feen.model.dto.auth.ResendSignUpVerificationCodeDto;
import com.fleencorp.feen.model.response.auth.ResendSignUpVerificationCodeResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.security.ChangePasswordResponse;
import com.fleencorp.feen.model.response.security.SignOutResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.auth.AuthenticationService;
import com.fleencorp.feen.service.verification.VerificationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/verification")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'PRE_VERIFIED_USER', 'PRE_AUTHENTICATED_USER')")
public class VerificationController {

  private final AuthenticationService authenticationService;
  private final VerificationService verificationService;

  public VerificationController(
      final AuthenticationService authenticationService,
      final VerificationService verificationService) {
    this.authenticationService = authenticationService;
    this.verificationService = verificationService;
  }

  @PreAuthorize("hasAnyRole('PRE_VERIFIED_USER')")
  @PostMapping(value = "/complete-sign-up")
  public SignUpResponse completeSignUp(
      @Valid @RequestBody final CompleteSignUpDto completeSignUpDto,
      @AuthenticationPrincipal final FleenUser user) {
    return authenticationService.completeSignUp(completeSignUpDto, user);
  }

  @PostMapping(value = "/resend-sign-up-verification-code")
  public ResendSignUpVerificationCodeResponse resendSignUpVerificationCode(
      @Valid @RequestBody final ResendSignUpVerificationCodeDto resendSignUpVerificationCodeDto,
      @AuthenticationPrincipal final FleenUser user) {
    return verificationService.resendSignUpVerificationCode(resendSignUpVerificationCodeDto, user);
  }

  @PreAuthorize("hasRole('RESET_PASSWORD_USER')")
  @PostMapping(value = "/reset-change-password")
  public ChangePasswordResponse changePassword(
      @Valid @RequestBody final ChangePasswordDto changePasswordDto,
      @AuthenticationPrincipal final FleenUser user) {
    return authenticationService.changePassword(changePasswordDto, user);
  }

  @GetMapping(value = "/sign-out")
  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'PRE_VERIFIED_USER', 'PRE_AUTHENTICATED_USER')")
  public SignOutResponse signOut(@AuthenticationPrincipal final FleenUser user) {
    return authenticationService.signOut(user);
  }
}
