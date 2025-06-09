package com.fleencorp.feen.user.controller.authentication;

import com.fleencorp.feen.user.model.dto.authentication.ChangePasswordDto;
import com.fleencorp.feen.user.model.dto.authentication.CompleteSignUpDto;
import com.fleencorp.feen.user.model.dto.authentication.ResendSignUpVerificationCodeDto;
import com.fleencorp.feen.model.response.auth.ResendSignUpVerificationCodeResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.response.security.ChangePasswordResponse;
import com.fleencorp.feen.model.response.security.SignOutResponse;
import com.fleencorp.feen.user.security.RegisteredUser;
import com.fleencorp.feen.service.auth.AuthenticationService;
import com.fleencorp.feen.service.security.VerificationService;
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
      @AuthenticationPrincipal final RegisteredUser user) {
    return verificationService.completeSignUp(completeSignUpDto, user);
  }

  @PostMapping(value = "/resend-sign-up-verification-code")
  public ResendSignUpVerificationCodeResponse resendSignUpVerificationCode(
      @Valid @RequestBody final ResendSignUpVerificationCodeDto resendSignUpVerificationCodeDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return verificationService.resendSignUpVerificationCode(resendSignUpVerificationCodeDto, user);
  }

  @PreAuthorize("hasRole('RESET_PASSWORD_USER')")
  @PostMapping(value = "/reset-change-password")
  public ChangePasswordResponse changePassword(
      @Valid @RequestBody final ChangePasswordDto changePasswordDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return verificationService.changePassword(changePasswordDto, user);
  }

  @GetMapping(value = "/sign-out")
  @PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'PRE_VERIFIED_USER', 'PRE_AUTHENTICATED_USER')")
  public SignOutResponse signOut(@AuthenticationPrincipal final RegisteredUser user) {
    return authenticationService.signOut(user);
  }
}
