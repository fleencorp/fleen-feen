package com.fleencorp.feen.bookmark.service.impl;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
import com.fleencorp.feen.bookmark.constant.BookmarkType;
import com.fleencorp.feen.bookmark.mapper.BookmarkMapper;
import com.fleencorp.feen.bookmark.model.domain.Bookmark;
import com.fleencorp.feen.bookmark.model.request.search.BookmarkSearchRequest;
import com.fleencorp.feen.bookmark.model.response.BookmarkResponse;
import com.fleencorp.feen.bookmark.model.search.BookmarkSearchResult;
import com.fleencorp.feen.bookmark.repository.BookmarkSearchRepository;
import com.fleencorp.feen.bookmark.service.BookmarkSearchService;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.nonNull;

@Service
public class BookmarkSearchServiceImpl implements BookmarkSearchService {

  private final BookmarkSearchRepository bookmarkSearchRepository;
  private final BookmarkMapper bookmarkMapper;
  private final Localizer localizer;

  public BookmarkSearchServiceImpl(
      final BookmarkSearchRepository bookmarkSearchRepository,
      final BookmarkMapper bookmarkMapper,
      final Localizer localizer) {
    this.bookmarkSearchRepository = bookmarkSearchRepository;
    this.bookmarkMapper = bookmarkMapper;
    this.localizer = localizer;
  }

  /**
   * Finds bookmarks for the specified user based on the given search criteria.
   *
   * <p>This method delegates to {@link #searchBookmarks(BookmarkSearchRequest, RegisteredUser)}
   * to perform the database query, converts the resulting {@link Bookmark} entities into
   * {@link BookmarkResponse} objects, wraps them in a {@link SearchResult}, and finally
   * returns a localized {@link BookmarkSearchResult}.</p>
   *
   * @param searchRequest the request containing bookmark search criteria such as dates,
   *                      title, parent types, and pagination information
   * @param user the {@link RegisteredUser} whose bookmarks are being searched
   * @return a localized {@link BookmarkSearchResult} containing the paginated bookmarks
   *         that match the search criteria
   * @throws FailedOperationException if the user is invalid or not properly initialized
   */
  @Override
  @Transactional(readOnly = true)
  public BookmarkSearchResult findBookmarks(final BookmarkSearchRequest searchRequest, final RegisteredUser user) {
    final Page<Bookmark> page = searchBookmarks(searchRequest, user);

    final Collection<BookmarkResponse> bookmarkResponses = bookmarkMapper.toBookmarkResponses(page.getContent());
    final SearchResult searchResult = toSearchResult(bookmarkResponses, page);
    final BookmarkSearchResult bookmarkSearchResult = BookmarkSearchResult.of(searchResult);

    return localizer.of(bookmarkSearchResult);
  }

  /**
   * Searches for bookmarks belonging to the specified user based on the given search criteria.
   *
   * <p>This method validates the user, extracts the search parameters from the
   * {@link BookmarkSearchRequest}, and queries the {@code bookmarkSearchRepository}
   * accordingly. The search supports filtering by date range, title, and parent types,
   * with results returned in a paginated format. If all dates are provided, a
   * date-range search is executed; otherwise, if a title is provided, a title-based
   * search is executed; otherwise, a general search by parent types is performed.</p>
   *
   * @param searchRequest the request containing bookmark search criteria, including dates,
   *                      title, parent types, and pagination information
   * @param user the {@link RegisteredUser} whose bookmarks are being searched;
   *             must not be {@code null}
   * @return a {@link Page} of {@link Bookmark} entities that match the search criteria
   * @throws FailedOperationException if the user is invalid or not properly initialized
   */
  private Page<Bookmark> searchBookmarks(final BookmarkSearchRequest searchRequest, final RegisteredUser user) {
    checkIsNull(user.toMember(), FailedOperationException::new);

    final Page<Bookmark> page;
    final Pageable pageable = searchRequest.getPage();
    final LocalDateTime endDate = searchRequest.getEndDateTime();
    final LocalDateTime startDate = searchRequest.getStartDateTime();
    final String title = searchRequest.getTitle();
    final List<BookmarkParentType> bookmarkParentTypes = searchRequest.getBookmarkParentType();
    final BookmarkType bookmarkType = BookmarkType.BOOKMARK;
    final Long memberId = user.getId();

    if (searchRequest.areAllDatesSet()) {
      page = bookmarkSearchRepository.findByDateBetween(startDate, endDate, bookmarkParentTypes, bookmarkType, memberId, pageable);
    } else if (nonNull(title))  {
      page = bookmarkSearchRepository.findByTitle(title, bookmarkParentTypes, bookmarkType, memberId, pageable);
    } else {
      page = bookmarkSearchRepository.findMany(bookmarkParentTypes, bookmarkType, memberId, pageable);
    }

    return page;
  }
}
