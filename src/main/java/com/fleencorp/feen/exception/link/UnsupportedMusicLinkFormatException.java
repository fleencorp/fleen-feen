package com.fleencorp.feen.exception.link;

import com.fleencorp.localizer.model.exception.ApiException;

public class UnsupportedMusicLinkFormatException extends ApiException {

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
