package com.fleencorp.feen.service.external.google.oauth2;

import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.user.model.domain.Oauth2Authorization;
import com.fleencorp.feen.user.model.request.security.Oauth2AuthenticationRequest;
import com.fleencorp.feen.model.response.external.google.oauth2.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.model.response.external.google.oauth2.StartOauth2AuthorizationResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface GoogleOauth2Service {

  StartOauth2AuthorizationResponse startOauth2Authentication(Oauth2AuthenticationRequest authenticationRequest);

  CompletedOauth2AuthorizationResponse verifyAuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(String authorizationCode, Oauth2AuthenticationRequest authenticationRequest, RegisteredUser user);

  Oauth2Authorization validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType oauth2ServiceType, RegisteredUser user);
}
