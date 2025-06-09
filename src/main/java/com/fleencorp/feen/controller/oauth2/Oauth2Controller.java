package com.fleencorp.feen.controller.oauth2;

import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.user.model.request.security.Oauth2AuthenticationRequest;
import com.fleencorp.feen.model.response.external.google.oauth2.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.model.response.external.google.oauth2.StartOauth2AuthorizationResponse;
import com.fleencorp.feen.user.security.RegisteredUser;
import com.fleencorp.feen.service.external.google.oauth2.GoogleOauth2Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.fleencorp.feen.util.security.Oauth2Util.toOauth2AuthenticationRequest;
import static com.fleencorp.feen.util.security.Oauth2Util.validateOauth2ScopeAndReturn;

@RestController
@RequestMapping("/api/oauth2")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SUPER_ADMINISTRATOR', 'USER')")
public class Oauth2Controller {

  private final GoogleOauth2Service googleOauth2Service;

  public Oauth2Controller(final GoogleOauth2Service googleOauth2Service) {
    this.googleOauth2Service = googleOauth2Service;
  }

  @GetMapping("/calendar/get-authorization-uri")
  public StartOauth2AuthorizationResponse getAuthorizationUriAndStartOauth2AuthenticationForCalendar() {
    return googleOauth2Service.startOauth2Authentication(Oauth2AuthenticationRequest.getGoogleCalendarOauth2AuthenticationRequest());
  }

  @GetMapping("/youtube/get-authorization-uri")
  public StartOauth2AuthorizationResponse getAuthorizationUriAndStartOauth2AuthenticationForYouTube() {
    return googleOauth2Service.startOauth2Authentication(Oauth2AuthenticationRequest.getYouTubeOauth2AuthenticationRequest());
  }

  @GetMapping("/verify-authorization-code")
  public CompletedOauth2AuthorizationResponse verifyOauth2AuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(
      @RequestParam(name = "code") final String authorizationCode,
      @RequestParam(name = "state") final String statesStr,
      @AuthenticationPrincipal final RegisteredUser user) {
    final Oauth2ServiceType oauth2ServiceType = validateOauth2ScopeAndReturn(statesStr);
    return googleOauth2Service.verifyAuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(authorizationCode, toOauth2AuthenticationRequest(oauth2ServiceType), user);
  }
}
