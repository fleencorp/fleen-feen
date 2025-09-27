package com.fleencorp.feen.common.configuration.external.spotify;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

public final class SpotifyScopes {

  private SpotifyScopes() {}

  private static final String USER_READ_CURRENTLY_PLAYING = "user-read-currently-playing";
  private static final String USER_READ_PLAYBACK_STATE = "user-read-playback-state";

  public static Collection<String> allScopes() {
    return Arrays.asList(USER_READ_CURRENTLY_PLAYING, USER_READ_PLAYBACK_STATE);
  }

  public static String allScopesAsString() {
    return String.join(" ", allScopes());
  }

  public static String allScopesAsEncodedString() {
    return URLEncoder.encode(SpotifyScopes.allScopesAsString(), StandardCharsets.UTF_8);
  }
}
