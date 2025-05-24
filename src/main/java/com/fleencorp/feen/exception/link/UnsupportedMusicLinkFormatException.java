package com.fleencorp.feen.exception.link;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class UnsupportedMusicLinkFormatException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "unsupported.music.link.format";
  }

  public UnsupportedMusicLinkFormatException(final Object...params) {
    super(params);
  }

  public static UnsupportedMusicLinkFormatException of() {
    return new UnsupportedMusicLinkFormatException();
  }
}
