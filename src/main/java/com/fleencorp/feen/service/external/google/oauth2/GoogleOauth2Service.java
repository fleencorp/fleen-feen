package com.fleencorp.feen.service.external.google.oauth2;

import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.request.Oauth2AuthenticationRequest;
import com.fleencorp.feen.model.response.external.google.oauth2.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.model.response.external.google.oauth2.StartOauth2AuthorizationResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface GoogleOauth2Service {

  StartOauth2AuthorizationResponse startOauth2Authentication(Oauth2AuthenticationRequest authenticationRequest);

  CompletedOauth2AuthorizationResponse verifyAuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(String authorizationCode, Oauth2AuthenticationRequest authenticationRequest, FleenUser user);

  Oauth2Authorization validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType oauth2ServiceType, FleenUser user);
}
