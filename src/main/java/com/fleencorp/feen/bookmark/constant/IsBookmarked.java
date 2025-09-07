package com.fleencorp.feen.bookmark.constant;

import lombok.Getter;

@Getter
public enum IsBookmarked {

  NO("No", "is.bookmarked.no"),
  YES("Yes", "is.bookmarked.yes");

  private final String label;
  private final String messageCode;

  IsBookmarked(
      final String label,
      final String messageCode) {
    this.label = label;
    this.messageCode = messageCode;
  }

  public static IsBookmarked by(final boolean bookmarked) {
    return bookmarked ? YES : NO;
  }
}
