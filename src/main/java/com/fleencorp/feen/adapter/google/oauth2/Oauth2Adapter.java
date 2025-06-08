package com.fleencorp.feen.adapter.google.oauth2;

import com.fleencorp.base.adapter.base.BaseAdapter;
import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.feen.adapter.google.oauth2.model.Oauth2UserResponse;
import com.fleencorp.feen.adapter.google.oauth2.model.constant.GoogleOauth2EndpointBlock;
import com.fleencorp.feen.constant.external.ExternalSystemType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static com.fleencorp.feen.util.LoggingUtil.logIfEnabled;

@Slf4j
public class Oauth2Adapter extends BaseAdapter {

  protected Oauth2Adapter(
      @Value("${google.oauth2.base-url}") final String baseUrl,
      final RestClient restClient,
      final RestTemplate restTemplate) {
    super(baseUrl,restTemplate, restClient);
  }

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
