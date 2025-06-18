package com.fleencorp.feen.mfa.controller;

import com.fleencorp.feen.mfa.model.dto.ConfirmMfaVerificationCodeDto;
import com.fleencorp.feen.mfa.model.dto.ConfirmSetupMfaDto;
import com.fleencorp.feen.mfa.model.dto.ResendMfaVerificationCodeDto;
import com.fleencorp.feen.mfa.model.dto.SetupMfaDto;
import com.fleencorp.feen.mfa.model.response.*;
import com.fleencorp.feen.mfa.service.MfaService;
import com.fleencorp.feen.model.response.authentication.SignInResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.verification.service.VerificationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/mfa")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class MfaController {

  private final MfaService mfaService;
  private final VerificationService verificationService;

  public MfaController(
      final MfaService mfaService,
      final VerificationService verificationService) {
    this.mfaService = mfaService;
    this.verificationService = verificationService;
  }

  @GetMapping(value = "/status")
  public MfaStatusResponse getMfaStatus(@AuthenticationPrincipal final RegisteredUser user) {
    return mfaService.getMfaStatus(user);
  }

  @PostMapping(value = "/setup")
  public SetupMfaResponse setupMfa(
      @Valid @RequestBody final SetupMfaDto dto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return mfaService.setupMfa(dto, user);
  }

  @PutMapping(value = "/confirm-setup")
  public ConfirmMfaSetupResponse confirmMfaSetup(
      @Valid @RequestBody final ConfirmSetupMfaDto dto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return mfaService.confirmMfaSetup(dto, user);
  }

  @PostMapping(value = "/resend-setup-code")
  public SetupMfaResponse resendMfaSetupCode(
      @Valid @RequestBody final SetupMfaDto dto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return mfaService.resendMfaSetupCode(dto, user);
  }

  @PreAuthorize("hasAnyRole('PRE_AUTHENTICATED_USER')")
  @PostMapping(value = "/verify-code")
  public SignInResponse verifyMfaCode(
      @Valid @RequestBody final ConfirmMfaVerificationCodeDto confirmMfaVerificationCodeDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return verificationService.verifyMfaVerificationCodeAndAuthenticateUser(confirmMfaVerificationCodeDto, user);
  }

  @PreAuthorize("hasAnyRole('PRE_AUTHENTICATED_USER')")
  @PostMapping(value = "/resend-verification-code")
  public ResendMfaVerificationCodeResponse resendMfaVerificationCode(
      @Valid @RequestBody final ResendMfaVerificationCodeDto resendMfaVerificationCodeDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return verificationService.resendMfaVerificationCode(resendMfaVerificationCodeDto, user);
  }

  @PutMapping(value = "/re-enable")
  public EnableOrDisableMfaResponse reEnableMfa(@AuthenticationPrincipal final RegisteredUser user) {
    return mfaService.reEnableMfa(user);
  }

  @PutMapping(value = "/disable")
  public EnableOrDisableMfaResponse disableMfa(@AuthenticationPrincipal final RegisteredUser user) {
    return mfaService.disableMfa(user);
  }
}
