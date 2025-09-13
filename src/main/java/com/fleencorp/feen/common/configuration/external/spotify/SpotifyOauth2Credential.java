package com.fleencorp.feen.common.configuration.external.spotify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.util.List;

import static com.fleencorp.feen.common.constant.base.SimpleConstant.COMMA;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "spotify.web")
@PropertySources({
  @PropertySource("classpath:properties/spotify-web-client.properties")
})
public class SpotifyOauth2Credential {

  private String clientId;
  private String clientSecret;
  private String redirectUris;

  public List<String> getRedirectUriList() {
    return toList(redirectUris);
  }

  public List<String> toList(final String value) {
    return List.of(value.split(COMMA));
  }

}
