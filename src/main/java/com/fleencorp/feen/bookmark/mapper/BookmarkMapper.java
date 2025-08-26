package com.fleencorp.feen.bookmark.mapper;

import com.fleencorp.feen.bookmark.model.domain.Bookmark;
import com.fleencorp.feen.bookmark.model.response.BookmarkResponse;

import java.util.Collection;

public interface BookmarkMapper {

  BookmarkResponse toBookmarkResponse(Bookmark entry);

  Collection<BookmarkResponse> toBookmarkResponses(Collection<Bookmark> entries);
}
