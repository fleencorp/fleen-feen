package com.fleencorp.feen.bookmark.service.impl;

import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
import com.fleencorp.feen.bookmark.constant.BookmarkType;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.bookmark.model.projection.UserBookmarkInfoSelect;
import com.fleencorp.feen.bookmark.repository.BookmarkRepository;
import com.fleencorp.feen.bookmark.service.BookmarkOperationService;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.model.contract.Bookmarkable;
import com.fleencorp.feen.model.contract.HasId;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fleencorp.feen.bookmark.constant.BookmarkParentType.*;

@Service
public class BookmarkOperationServiceImpl implements BookmarkOperationService {

  private final BookmarkRepository bookmarkRepository;
  private final UnifiedMapper unifiedMapper;

  public BookmarkOperationServiceImpl(
      final BookmarkRepository bookmarkRepository,
      final UnifiedMapper unifiedMapper) {
    this.bookmarkRepository = bookmarkRepository;
    this.unifiedMapper = unifiedMapper;
  }

  public <T extends Bookmarkable> void populateChatSpaceBookmarksFor(final Collection<T> responses, final IsAMember member) {
    populateBookmarkFor(responses, member, BookmarkParentType.CHAT_SPACE);
  }

  public <T extends Bookmarkable> void populateStreamBookmarksFor(final Collection<T> responses, final IsAMember member) {
    populateBookmarkFor(responses, member, STREAM);
  }

  public <T extends Bookmarkable> void populateBookmarkForReviews(final Collection<T> responses, final IsAMember member) {
    populateBookmarkFor(responses, member, REVIEW);
  }

  public <T extends Bookmarkable> void populateSoftAskBookmarksFor(final Collection<T> responses, final IsAMember member) {
    populateBookmarkFor(responses, member, SOFT_ASK);
  }

  public <T extends Bookmarkable> void populateSoftAskReplyBookmarksFor(final Collection<T> responses, final IsAMember member) {
    populateBookmarkFor(responses, member, SOFT_ASK_REPLY);
  }

  public <T extends Bookmarkable> void populateBookmarkFor(final Collection<T> responses, final IsAMember member, final BookmarkParentType bookmarkParentType) {
    final List<Long> entitiesIds = HasId.getIds(responses);
    final Long otherId = Bookmarkable.getOtherId(responses);

    if (!entitiesIds.isEmpty()) {
      final Map<Long, UserBookmarkInfoSelect> bookmarkInfoMap = findBookmarkByParentIdsAndMember(entitiesIds, otherId, member, bookmarkParentType);
      setUserInfo(responses, bookmarkInfoMap);
    }
  }
  
  protected void setUserInfo(final Collection<? extends Bookmarkable> responses, final Map<Long, UserBookmarkInfoSelect> bookmarkInfoMap) {
    responses.stream()
      .filter(Objects::nonNull)
      .forEach(response -> {
        if (bookmarkInfoMap.containsKey(response.getNumberId())) {
          final UserBookmarkInfoSelect info = bookmarkInfoMap.get(response.getNumberId());
          final UserBookmarkInfo userBookmarkInfo = unifiedMapper.toBookmarkInfo(info != null && info.isBookmarked());
          response.setUserBookmarkInfo(userBookmarkInfo);
        } else {
          final UserBookmarkInfo userBookmarkInfo = unifiedMapper.toBookmarkInfo(false);
          response.setUserBookmarkInfo(userBookmarkInfo);
        }
    });
  }
  
  protected Map<Long, UserBookmarkInfoSelect> findBookmarkByParentIdsAndMember(final List<Long> parentIds, final Long otherId, final IsAMember member, final BookmarkParentType bookmarkParentType) {
    // Return empty map if no parent IDs are provided
    if (parentIds == null || parentIds.isEmpty()) {
      return Collections.emptyMap();
    }

    // Query the database for LIKE and UNLIKE entries for the given member and parent type
    final List<UserBookmarkInfoSelect> bookmarks = bookmarkRepository.findBookmarksByParentIdsAndMember(
      parentIds,
      otherId,
      member.getMemberId(),
      bookmarkParentType,
      BookmarkType.all()
    );

    // Map the results by the appropriate parent ID depending on the BookmarkParentType
    return bookmarks.stream()
      .collect(Collectors.toMap(
      info -> switch (bookmarkParentType) {
        case STREAM -> info.getStreamId();
        case CHAT_SPACE -> info.getChatSpaceId();
        case REVIEW -> info.getReviewId();
        case SOFT_ASK -> info.getSoftAskId();
        case SOFT_ASK_REPLY -> info.getSoftAskReplyId();
        case BUSINESS, JOB_OPPORTUNITY -> null;
      },
      Function.identity()
    ));
  }

}
