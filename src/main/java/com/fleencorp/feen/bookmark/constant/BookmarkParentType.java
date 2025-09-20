package com.fleencorp.feen.bookmark.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum BookmarkParentType {

  BUSINESS("Business"),
  CHAT_SPACE("Chat Space"),
  JOB_OPPORTUNITY("Job Opportunity"),
  POLL("Poll"),
  REVIEW("Review"),
  SOFT_ASK("Soft Ask"),
  SOFT_ASK_REPLY("Soft Ask Reply"),
  STREAM("Stream");

  private final String label;

  BookmarkParentType(final String label) {
    this.label = label;
  }

  public static BookmarkParentType of(final String value) {
    return parseEnumOrNull(value, BookmarkParentType.class);
  }

  public static List<BookmarkParentType> all() {
    return Arrays.asList(values());
  }

  public static boolean isChatSpace(final BookmarkParentType bookmarkParentType) {
    return bookmarkParentType == CHAT_SPACE;
  }

  public static boolean isPoll(final BookmarkParentType bookmarkParentType) {
    return bookmarkParentType == POLL;
  }

  public static boolean isReview(final BookmarkParentType bookmarkParentType) {
    return bookmarkParentType == REVIEW;
  }

  public static boolean isSoftAsk(final BookmarkParentType bookmarkParentType) {
    return bookmarkParentType == SOFT_ASK;
  }

  public static boolean isSoftAskReply(final BookmarkParentType bookmarkParentType) {
    return bookmarkParentType == SOFT_ASK_REPLY;
  }

  public static boolean isStream(final BookmarkParentType bookmarkParentType) {
    return bookmarkParentType == STREAM;
  }
}
