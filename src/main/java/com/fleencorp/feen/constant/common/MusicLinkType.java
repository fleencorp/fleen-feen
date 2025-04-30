package com.fleencorp.feen.constant.common;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
public enum MusicLinkType implements ApiParameter {

  SPOTIFY("Spotify", "https://open.spotify.com/"),
  YOUTUBE_MUSIC("YouTube Music", "https://music.youtube.com/");

  private final String value;
  private final String format;

  MusicLinkType(
      final String value,
      final String linkFormat) {
    this.value = value;
    this.format = linkFormat;
  }

  public static boolean isValid(final String link) {
    if (isNull(link)) {
      return false;
    }

    for (final MusicLinkType musicLinkType : MusicLinkType.values()) {
      if (link.startsWith(musicLinkType.getFormat())) {
        return true;
      }
    }

    return false;
  }

  public static MusicLinkType ofType(final String url) {
    if (nonNull(url)) {
      return MusicLinkType.SPOTIFY.getFormat().startsWith(url) ? SPOTIFY : YOUTUBE_MUSIC;
    }

    return null;
  }
}
