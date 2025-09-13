package com.fleencorp.feen.oauth2.service.external;

import com.fleencorp.feen.common.constant.base.ReportMessageType;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;

import java.io.IOException;

public interface Oauth2CommonService {

  void handleTokenResponseException(Exception ex, String authorizationCode, Oauth2ServiceType oauth2ServiceType, ReportMessageType reportMessageType);

  void handleException(Exception ex, String message, ReportMessageType reportMessageType);

  void handleExceptionForTokenRefresh(IOException ex, ReportMessageType reportMessageType);

  void saveTokenIfAccessTokenUpdated(Oauth2Authorization oauth2Authorization, String currentAccessToken);

  Oauth2Authorization findOauth2AuthorizationOrCreateOne(Oauth2ServiceType oauth2ServiceType, Long userId);
}
