package com.fleencorp.feen.controller.security;

import com.fleencorp.feen.model.dto.security.mfa.ConfirmMfaVerificationCodeDto;
import com.fleencorp.feen.model.dto.security.mfa.ConfirmSetupMfaDto;
import com.fleencorp.feen.model.dto.security.mfa.ResendMfaVerificationCodeDto;
import com.fleencorp.feen.model.dto.security.mfa.SetupMfaDto;
import com.fleencorp.feen.model.response.auth.SignInResponse;
import com.fleencorp.feen.model.response.security.mfa.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.auth.AuthenticationService;
import com.fleencorp.feen.service.security.mfa.MfaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/mfa")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class MfaController {

  private final AuthenticationService authenticationService;
  private final MfaService mfaService;

  public MfaController(
      final AuthenticationService authenticationService,
      final MfaService mfaService) {
    this.authenticationService = authenticationService;
    this.mfaService = mfaService;
  }

  @GetMapping(value = "/status")
  public MfaStatusResponse getMfaStatus(@AuthenticationPrincipal final FleenUser user) {
    return mfaService.getMfaStatus(user);
  }

  @PostMapping(value = "/setup")
  public SetupMfaResponse setupMfa(
      @Valid @RequestBody final SetupMfaDto dto,
      @AuthenticationPrincipal final FleenUser user) {
    return mfaService.setupMfa(dto, user);
  }

  @PutMapping(value = "/confirm-setup")
  public ConfirmMfaSetupResponse confirmMfaSetup(
      @Valid @RequestBody final ConfirmSetupMfaDto dto,
      @AuthenticationPrincipal final FleenUser user) {
    return mfaService.confirmMfaSetup(dto, user);
  }

  @PostMapping(value = "/resend-setup-code")
  public SetupMfaResponse resendMfaSetupCode(
      @Valid @RequestBody final SetupMfaDto dto,
      @AuthenticationPrincipal final FleenUser user) {
    return mfaService.resendMfaSetupCode(dto, user);
  }

  @PreAuthorize("hasAnyRole('PRE_AUTHENTICATED_USER')")
  @PostMapping(value = "/verify-code")
  public SignInResponse verifyMfaCode(
      @Valid @RequestBody final ConfirmMfaVerificationCodeDto confirmMfaVerificationCodeDto,
      @AuthenticationPrincipal final FleenUser user) {
    return authenticationService.verifyMfaVerificationCodeAndAuthenticateUser(confirmMfaVerificationCodeDto, user);
  }

  @PreAuthorize("hasAnyRole('PRE_AUTHENTICATED_USER')")
  @PostMapping(value = "/resend-verification-code")
  public ResendMfaVerificationCodeResponse resendMfaVerificationCode(
      @Valid @RequestBody final ResendMfaVerificationCodeDto resendMfaVerificationCodeDto,
      @AuthenticationPrincipal final FleenUser user) {
    return authenticationService.resendMfaVerificationCode(resendMfaVerificationCodeDto, user);
  }

  @PutMapping(value = "/re-enable")
  public EnableOrDisableMfaResponse reEnableMfa(@AuthenticationPrincipal final FleenUser user) {
    return mfaService.reEnableMfa(user);
  }

  @PutMapping(value = "/disable")
  public EnableOrDisableMfaResponse disableMfa(@AuthenticationPrincipal final FleenUser user) {
    return mfaService.disableMfa(user);
  }
}
