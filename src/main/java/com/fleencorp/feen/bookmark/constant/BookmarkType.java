package com.fleencorp.feen.bookmark.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum BookmarkType {

  BOOKMARK("Bookmark"),
  UNBOOKMARK("Unbookmark");

  private final String label;

  BookmarkType(final String label) {
    this.label = label;
  }

  public static BookmarkType of(final String value) {
    return parseEnumOrNull(value, BookmarkType.class);
  }

  public static List<BookmarkType> all() {
    return Arrays.asList(values());
  }

  public static boolean isBookmarked(final BookmarkType bookmarkType) {
    return bookmarkType == BOOKMARK;
  }
}
