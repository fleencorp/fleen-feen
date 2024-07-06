package com.fleencorp.feen.controller;

import com.fleencorp.feen.model.response.external.google.oauth2.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.model.response.external.google.oauth2.StartOauth2AuthorizationResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.external.google.oauth2.GoogleOauth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/youtube/oauth2")
public class YouTubeOauth2Controller {

  private final GoogleOauth2Service googleOauth2Service;

  public YouTubeOauth2Controller(final GoogleOauth2Service googleOauth2Service) {
    this.googleOauth2Service = googleOauth2Service;
  }

  @GetMapping("/get-authorization-uri")
  public StartOauth2AuthorizationResponse getAuthorizationUriAndStartOauth2Authentication() {
    return googleOauth2Service.startOauth2Authentication();
  }

  @GetMapping("/verify-authorization-code")
  public CompletedOauth2AuthorizationResponse verifyOauth2AuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(
      @RequestParam(name = "authorization_code") final String authorizationCode, final FleenUser user) {
    return googleOauth2Service.verifyAuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(authorizationCode, user);
  }
}
