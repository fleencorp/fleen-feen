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

  /**
   * <p>Attempts to determine the {@code MusicLinkType} based on the given URL.</p>
   *
   * <p>This method iterates through all known music link types and checks whether
   * the provided URL starts with the expected format for any of them.</p>
   *
   * <p>If a matching format is found, the corresponding {@code MusicLinkType} is returned.
   * If no match is found or the URL is {@code null}, {@code null} is returned.</p>
   *
   * @param url the music link URL to inspect
   * @return the corresponding {@code MusicLinkType} if detected, otherwise {@code null}
   */
  public static MusicLinkType fromUrl(final String url) {
    if (isNull(url)) {
      return null;
    }

    for (final MusicLinkType type : MusicLinkType.values()) {
      if (url.startsWith(type.getFormat())) {
        return type;
      }
    }

    return null;
  }

  /**
   * <p>Returns the {@code MusicLinkType} corresponding to the provided URL.</p>
   *
   * <p>This method checks if the URL starts with any of the known formats associated
   * with the available {@code MusicLinkType} values.</p>
   *
   * <p>If a matching type is found, it is returned. If no matching type is found
   * or the URL is {@code null}, {@code null} is returned.</p>
   *
   * @param url the URL to check against known music link formats
   * @return the {@code MusicLinkType} corresponding to the URL format, or {@code null} if no match is found
   */
  public static MusicLinkType ofType(final String url) {
    if (nonNull(url)) {
      for (final MusicLinkType type : MusicLinkType.values()) {
        if (url.startsWith(type.getFormat())) {
          return type;
        }
      }
    }

    return null;
  }
}
