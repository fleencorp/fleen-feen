package com.fleencorp.feen.oauth2.service.external;

import com.fleencorp.feen.oauth2.model.request.Oauth2AuthenticationRequest;
import com.fleencorp.feen.oauth2.model.response.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.model.response.RefreshOauth2TokenResponse;

public interface ExternalOauth2Service {

  String getAuthorizationUri(Oauth2AuthenticationRequest authenticationRequest);

  CompletedOauth2AuthorizationResponse verifyAuthorizationCode(String authorizationCode, Oauth2AuthenticationRequest authenticationRequest);

  RefreshOauth2TokenResponse refreshUserToken(String refreshToken);
}
