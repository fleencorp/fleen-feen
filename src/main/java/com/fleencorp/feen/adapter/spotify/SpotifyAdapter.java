package com.fleencorp.feen.adapter.spotify;

import com.fleencorp.base.adapter.base.BaseAdapter;
import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.feen.adapter.spotify.model.response.CurrentlyPlayingResponse;
import com.fleencorp.feen.common.constant.external.ExternalSystemType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static com.fleencorp.feen.adapter.spotify.model.constant.SpotifyEndpointBlock.*;
import static com.fleencorp.feen.common.util.common.LoggingUtil.logIfEnabled;

@Component
public class SpotifyAdapter extends BaseAdapter {

  public SpotifyAdapter(
      @Value("${spotify.base-url}") final String baseUrl,
      final RestTemplate restTemplate,
      final RestClient restClient) {
    super(baseUrl, restTemplate, restClient);
  }

  public CurrentlyPlayingResponse getCurrentlyPlaying(final String accessToken) {
    final URI uri = buildUri(V1, ME, PLAYER, CURRENTLY_PLAYING);
    final Map<String, String> headers = getAuthHeaderWithBearerToken(accessToken);

    final ResponseEntity<CurrentlyPlayingResponse> response =
      doCall(uri, HttpMethod.GET, headers, null, CurrentlyPlayingResponse.class);

    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
      return response.getBody();
    } else {
      logIfEnabled(log::isErrorEnabled,
        () -> log.error("An error occurred while calling getCurrentlyPlaying method of {}: {}",
          this.getClass().getSimpleName(),
          response.getStatusCode())
      );
      throw new ExternalSystemException(ExternalSystemType.SPOTIFY.getValue());
    }
  }

}
