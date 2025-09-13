package com.fleencorp.feen.oauth2.controller;

import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.model.request.Oauth2AuthenticationRequest;
import com.fleencorp.feen.oauth2.model.response.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.model.response.StartOauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.service.external.BaseOauth2Service;
import com.fleencorp.feen.shared.security.RegisteredUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.fleencorp.feen.oauth2.util.Oauth2Util.validateOauth2ScopeAndReturn;

@Slf4j
@RestController
@RequestMapping("/api/oauth2")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class Oauth2Controller {

  private final BaseOauth2Service baseOauth2Service;

  public Oauth2Controller(final BaseOauth2Service baseOauth2Service) {
    this.baseOauth2Service = baseOauth2Service;
  }

  @GetMapping("/calendar/get-authorization-uri")
  public StartOauth2AuthorizationResponse getAuthorizationUriAndStartOauth2AuthenticationForCalendar() {
    return baseOauth2Service.startOauth2Authentication(Oauth2AuthenticationRequest.getGoogleCalendarOauth2AuthenticationRequest());
  }

  @GetMapping("/spotify/get-authorization-uri")
  public StartOauth2AuthorizationResponse getAuthorizationUriAndStartOauth2AuthenticationForSpotify() {
    return baseOauth2Service.startOauth2Authentication(Oauth2AuthenticationRequest.getSpotifyOauth2AuthenticationRequest());
  }

  @GetMapping("/youtube/get-authorization-uri")
  public StartOauth2AuthorizationResponse getAuthorizationUriAndStartOauth2AuthenticationForYouTube() {
    return baseOauth2Service.startOauth2Authentication(Oauth2AuthenticationRequest.getYouTubeOauth2AuthenticationRequest());
  }

  @GetMapping("/verify-authorization-code")
  public CompletedOauth2AuthorizationResponse verifyOauth2AuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(
      @RequestParam(name = "code") final String authorizationCode,
      @RequestParam(name = "state") final String statesStr,
      @AuthenticationPrincipal final RegisteredUser user) {
    final Oauth2ServiceType oauth2ServiceType = validateOauth2ScopeAndReturn(statesStr);
    final Oauth2AuthenticationRequest oauth2AuthenticationRequest = Oauth2AuthenticationRequest.of(oauth2ServiceType);
    return baseOauth2Service.verifyAuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(authorizationCode, oauth2AuthenticationRequest, user);
  }
}
