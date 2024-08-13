package com.fleencorp.feen.controller;

import com.fleencorp.feen.model.dto.auth.CompleteSignUpDto;
import com.fleencorp.feen.model.dto.auth.SignUpDto;
import com.fleencorp.feen.model.response.auth.DataForSignUpResponse;
import com.fleencorp.feen.model.response.auth.SignUpResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

  @PostMapping(value = "/complete-sign-up")
  public SignUpResponse completeSignUp(
      @Valid @RequestBody final CompleteSignUpDto completeSignUpDto,
      @AuthenticationPrincipal final FleenUser user) {
    return authenticationService.completeSignUp(completeSignUpDto, user);
  }
}
