package com.fleencorp.feen.adapter.google.oauth2;

import com.fleencorp.base.adapter.base.BaseAdapter;
import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.feen.adapter.google.oauth2.model.Oauth2UserResponse;
import com.fleencorp.feen.adapter.google.oauth2.model.constant.GoogleOauth2EndpointBlock;
import com.fleencorp.feen.common.constant.external.ExternalSystemType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static com.fleencorp.feen.common.util.common.LoggingUtil.logIfEnabled;

@Component
public class Oauth2Adapter extends BaseAdapter {

  protected Oauth2Adapter(
      @Value("${google.oauth2.base-url}") final String baseUrl,
      final RestClient restClient,
      final RestTemplate restTemplate) {
    super(baseUrl,restTemplate, restClient);
  }

  /**
   * Retrieves the user information from the Google OAuth2 user info endpoint using the provided access token.
   *
   * <p>This method builds the appropriate URI for the user info endpoint, attaches the authorization
   * header, and performs an HTTP GET request. If the request is successful (2xx status code),
   * the response body containing the {@link Oauth2UserResponse} is returned. Otherwise, an error
   * is logged and an {@link ExternalSystemException} is thrown to indicate a failure in
   * communicating with Google OAuth2.</p>
   *
   * @param accessToken the OAuth2 access token used to authenticate the request
   * @return the {@link Oauth2UserResponse} containing user details retrieved from Google OAuth2
   * @throws ExternalSystemException if the request fails or a non-2xx status code is returned
   */
  public Oauth2UserResponse getUserInfo(final String accessToken) {
    final URI uri = buildUri(GoogleOauth2EndpointBlock.USER_INFO);
    final Map<String, String> headers = getAuthHeader(accessToken);

    final ResponseEntity<Oauth2UserResponse> response = doCall(uri, HttpMethod.GET, headers, null, Oauth2UserResponse.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      logIfEnabled(log::isErrorEnabled,
        () -> log.error("An error occurred while calling getUserInfo method of {}: {}",
          response.getBody(),
          this.getClass().getSimpleName())
      );
      throw new ExternalSystemException(ExternalSystemType.GOOGLE_OAUTH2.getValue());
    }
  }
}
