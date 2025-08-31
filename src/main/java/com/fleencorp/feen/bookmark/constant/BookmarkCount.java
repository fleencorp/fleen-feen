package com.fleencorp.feen.bookmark.constant;

import lombok.Getter;

@Getter
public enum BookmarkCount {

  TOTAL_BOOKMARKS("total.bookmark.entries");

  private final String messageCode;

  BookmarkCount(final String messageCode) {
    this.messageCode = messageCode;
  }

  public String getMessageCode() {
    return messageCode;
  }

  public static BookmarkCount totalBookmarks() {
    return TOTAL_BOOKMARKS;
  }
}
