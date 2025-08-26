package com.fleencorp.feen.bookmark.service;

import com.fleencorp.feen.bookmark.model.request.search.BookmarkSearchRequest;
import com.fleencorp.feen.bookmark.model.search.BookmarkSearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface BookmarkSearchService {

  BookmarkSearchResult findBookmarks(BookmarkSearchRequest searchRequest, RegisteredUser user);
}
