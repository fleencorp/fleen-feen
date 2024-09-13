package com.fleencorp.feen.controller;

import com.fleencorp.feen.model.dto.aws.VerifyEmailIdentityDto;
import com.fleencorp.feen.model.response.external.aws.VerifyEmailIdentityResponse;
import com.fleencorp.feen.service.impl.external.aws.SesService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/ses")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class SesController {

  private final SesService sesService;

  public SesController(final SesService sesService) {
    this.sesService = sesService;
  }

  @PostMapping(value = "/verify-email-identity")
  public VerifyEmailIdentityResponse verifyEmailIdentity(@Valid @RequestBody final VerifyEmailIdentityDto verifyEmailIdentityDto) {
    return sesService.verifyEmailIdentity(verifyEmailIdentityDto);
  }
}
