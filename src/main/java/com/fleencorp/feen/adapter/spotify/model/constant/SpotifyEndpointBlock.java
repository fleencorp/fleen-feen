package com.fleencorp.feen.adapter.spotify.model.constant;

import com.fleencorp.base.constant.base.EndpointBlock;
import lombok.Getter;

@Getter
public enum SpotifyEndpointBlock implements EndpointBlock {

  API("/api"),
  CURRENTLY_PLAYING("/currently-playing"),
  ME("/me"),
  PLAYER("/player"),
  TOKEN("/token"),
  V1("/v1");

  private final String value;

  SpotifyEndpointBlock(final String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
