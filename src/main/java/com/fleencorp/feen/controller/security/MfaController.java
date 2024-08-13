package com.fleencorp.feen.controller.security;

import com.fleencorp.feen.model.dto.security.mfa.ConfirmSetupMfaDto;
import com.fleencorp.feen.model.dto.security.mfa.SetupMfaDto;
import com.fleencorp.feen.model.response.security.mfa.ConfirmMfaSetupResponse;
import com.fleencorp.feen.model.response.security.mfa.EnableOrDisableMfaResponse;
import com.fleencorp.feen.model.response.security.mfa.MfaStatusResponse;
import com.fleencorp.feen.model.response.security.mfa.SetupMfaResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.security.mfa.MfaService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/mfa")
public class MfaController {

  private final MfaService mfaService;

  public MfaController(MfaService mfaService) {
    this.mfaService = mfaService;
  }

  @GetMapping(value = "/status")
  public MfaStatusResponse getMfaStatus(@AuthenticationPrincipal FleenUser user) {
    return mfaService.getMfaStatus(user);
  }

  @PutMapping(value = "/setup")
  public SetupMfaResponse setupMfa(
      @Valid @RequestBody SetupMfaDto dto,
      @AuthenticationPrincipal FleenUser user) {
    return mfaService.setupMfa(dto, user);
  }

  @PutMapping(value = "/confirm-setup")
  public ConfirmMfaSetupResponse confirmMfaSetup(
      @Valid @RequestBody ConfirmSetupMfaDto dto,
      @AuthenticationPrincipal FleenUser user) {
    return mfaService.confirmMfaSetup(dto, user);
  }

  @PutMapping(value = "/resend-setup-code")
  public SetupMfaResponse resendMfaSetupCode(
      @Valid @RequestBody SetupMfaDto dto,
      @AuthenticationPrincipal FleenUser user) {
    return mfaService.resendMfaSetupCode(dto, user);
  }

  @PutMapping(value = "/re-enable")
  public EnableOrDisableMfaResponse reEnableMfa(@AuthenticationPrincipal FleenUser user) {
    return mfaService.reEnableMfa(user);
  }

  @PutMapping(value = "/disable")
  public EnableOrDisableMfaResponse disableMfa(@AuthenticationPrincipal FleenUser user) {
    return mfaService.disableMfa(user);
  }
}
