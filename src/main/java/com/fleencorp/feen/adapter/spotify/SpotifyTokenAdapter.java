package com.fleencorp.feen.adapter.spotify;

import com.fleencorp.base.adapter.base.BaseAdapter;
import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.feen.adapter.spotify.model.response.SpotifyTokenResponse;
import com.fleencorp.feen.common.configuration.external.spotify.SpotifyOauth2Credential;
import com.fleencorp.feen.common.constant.external.ExternalSystemType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Base64;
import java.util.Map;

import static com.fleencorp.feen.adapter.spotify.model.constant.SpotifyEndpointBlock.API;
import static com.fleencorp.feen.adapter.spotify.model.constant.SpotifyEndpointBlock.TOKEN;
import static com.fleencorp.feen.common.util.common.LoggingUtil.logIfEnabled;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Component
public class SpotifyTokenAdapter extends BaseAdapter {

  private final SpotifyOauth2Credential credential;

  public SpotifyTokenAdapter(
    @Value("${spotify.token.base-url}") final String baseUrl,
    final SpotifyOauth2Credential credential,
    final RestTemplate restTemplate,
    final RestClient restClient) {
    super(baseUrl, restTemplate, restClient);
    this.credential = credential;
  }

  public SpotifyTokenResponse getAccessToken(final String code) {
    final URI uri = buildUri(API, TOKEN);
    final Map<String, String> headers = getDefaultHeaders();

    final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "authorization_code");
    body.add("code", code);
    body.add("redirect_uri", credential.getRedirectUris());

    final ResponseEntity<SpotifyTokenResponse> response =
      doCall(uri, HttpMethod.POST, headers, body, SpotifyTokenResponse.class);

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      return response.getBody();
    } else {
      logIfEnabled(log::isErrorEnabled,
        () -> log.error("Error while calling getAccessToken: {}",
          response.getStatusCode())
      );
      throw new ExternalSystemException(ExternalSystemType.SPOTIFY.getValue());
    }
  }

  public SpotifyTokenResponse refreshAccessToken(final String refreshToken) {
    final URI uri = buildUri(API, TOKEN);
    final Map<String, String> headers = getDefaultHeaders();

    final MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "refresh_token");
    body.add("refresh_token", refreshToken);

    final ResponseEntity<SpotifyTokenResponse> response =
      doCall(uri, HttpMethod.POST, headers, body, SpotifyTokenResponse.class);

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      return response.getBody();
    } else {
      logIfEnabled(log::isErrorEnabled,
        () -> log.error("Error while calling refreshAccessToken: {}",
          response.getStatusCode())
      );
      throw new ExternalSystemException(ExternalSystemType.SPOTIFY.getValue());
    }
  }

  private Map<String, String> getDefaultHeaders() {
    final String authToken = credential.getClientId() + ":" + credential.getClientSecret();
    final String encodedToken = "Basic " + Base64.getEncoder().encodeToString(authToken.getBytes());

    return Map.of(HttpHeaders.AUTHORIZATION, encodedToken);
  }

  @Override
  protected HttpHeaders getHeaders() {
    final HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.setContentType(APPLICATION_FORM_URLENCODED);
    return requestHeaders;
  }

}
