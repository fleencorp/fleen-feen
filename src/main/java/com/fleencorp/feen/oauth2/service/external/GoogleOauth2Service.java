package com.fleencorp.feen.oauth2.service.external;

import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import com.fleencorp.feen.oauth2.model.request.Oauth2AuthenticationRequest;
import com.fleencorp.feen.oauth2.model.response.CompletedOauth2AuthorizationResponse;
import com.fleencorp.feen.oauth2.model.response.StartOauth2AuthorizationResponse;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface GoogleOauth2Service {

  StartOauth2AuthorizationResponse startOauth2Authentication(Oauth2AuthenticationRequest authenticationRequest);

  CompletedOauth2AuthorizationResponse verifyAuthorizationCodeAndSaveOauth2AuthorizationTokenDetails(String authorizationCode, Oauth2AuthenticationRequest authenticationRequest, RegisteredUser user);

  Oauth2Authorization validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType oauth2ServiceType, RegisteredUser user);
}
