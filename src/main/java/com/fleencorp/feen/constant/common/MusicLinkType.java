package com.fleencorp.feen.constant.common;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static java.util.Objects.isNull;

@Getter
public enum MusicLinkType implements ApiParameter {

  SPOTIFY("Spotify", "https://open.spotify.com/"),
  YOUTUBE_MUSIC("YouTube Music", "https://music.youtube.com/");

  private final String value;
  private final String linkFormat;

  MusicLinkType(
    final String value,
    final String linkFormat) {
    this.value = value;
    this.linkFormat = linkFormat;
  }

  public static boolean isValid(final String link) {
    if (isNull(link)) {
      return false;
    }

    for (final MusicLinkType musicLinkType : MusicLinkType.values()) {
      if (link.startsWith(musicLinkType.getLinkFormat())) {
        return true;
      }
    }

    return false;
  }
}
