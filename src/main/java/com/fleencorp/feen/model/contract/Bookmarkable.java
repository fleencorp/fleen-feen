package com.fleencorp.feen.model.contract;

import com.fleencorp.feen.bookmark.model.info.BookmarkCountInfo;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.common.model.info.ParentInfo;

import java.util.Collection;
import java.util.Optional;

public interface Bookmarkable extends HasId {

  void setUserBookmarkInfo(UserBookmarkInfo userBookmarkInfo);

  void setBookmarkCountInfo(BookmarkCountInfo bookmarkCountInfo);

  ParentInfo getParentInfo();

  static <T extends Bookmarkable> Long getOtherId(final Collection<T> responses) {
    final Optional<T> bookmarkable = responses.stream().findFirst();
    return bookmarkable
      .filter(bookmarked -> bookmarked.getParentInfo() != null)
      .map(bookmarkableDetail -> bookmarkableDetail.getParentInfo().getParentId())
      .orElse(null);
  }
}
