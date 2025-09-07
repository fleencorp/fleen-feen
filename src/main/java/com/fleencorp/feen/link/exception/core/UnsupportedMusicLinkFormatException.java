package com.fleencorp.feen.link.exception.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class UnsupportedMusicLinkFormatException extends LocalizedException {

  @Override
  @JsonIgnore
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
