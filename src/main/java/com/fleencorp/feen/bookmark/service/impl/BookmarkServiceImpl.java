package com.fleencorp.feen.bookmark.service.impl;

import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
import com.fleencorp.feen.bookmark.constant.BookmarkType;
import com.fleencorp.feen.bookmark.mapper.BookmarkMapper;
import com.fleencorp.feen.bookmark.model.domain.Bookmark;
import com.fleencorp.feen.bookmark.model.dto.BookmarkDto;
import com.fleencorp.feen.bookmark.model.factory.BookmarkFactory;
import com.fleencorp.feen.bookmark.model.holder.BookmarkParentDetailHolder;
import com.fleencorp.feen.bookmark.model.response.BookmarkCreateResponse;
import com.fleencorp.feen.bookmark.model.response.BookmarkResponse;
import com.fleencorp.feen.bookmark.repository.BookmarkRepository;
import com.fleencorp.feen.bookmark.service.BookmarkService;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceService;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.service.ReviewOperationService;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.feen.bookmark.constant.BookmarkParentType.*;

@Service
public class BookmarkServiceImpl implements BookmarkService {

  private final ChatSpaceService chatSpaceService;
  private final ReviewOperationService reviewOperationService;
  private final SoftAskOperationService softAskOperationService;
  private final StreamOperationsService streamOperationsService;
  private final BookmarkRepository bookmarkRepository;
  private final BookmarkMapper bookmarkMapper;
  private final Localizer localizer;

  public BookmarkServiceImpl(
      final ChatSpaceService chatSpaceService,
      final ReviewOperationService reviewOperationService,
      final SoftAskOperationService softAskOperationService,
      @Lazy final StreamOperationsService streamOperationsService,
      final BookmarkRepository bookmarkRepository,
      final BookmarkMapper bookmarkMapper,
      final Localizer localizer) {
    this.chatSpaceService = chatSpaceService;
    this.reviewOperationService = reviewOperationService;
    this.softAskOperationService = softAskOperationService;
    this.streamOperationsService = streamOperationsService;
    this.bookmarkRepository = bookmarkRepository;
    this.bookmarkMapper = bookmarkMapper;
    this.localizer = localizer;
  }

  @Override
  @Transactional
  public BookmarkCreateResponse bookmark(final BookmarkDto bookmarkDto, final RegisteredUser user)
      throws StreamNotFoundException, ChatSpaceNotFoundException, SoftAskNotFoundException,
      SoftAskReplyNotFoundException, FailedOperationException {
    final Long parentId = bookmarkDto.getParentId();
    final Long otherId = bookmarkDto.getOtherId();
    final BookmarkParentType parentType = bookmarkDto.getBookmarkParentType();
    final Member member = user.toMember();

    final BookmarkParentDetailHolder detailsHolder = retrieveBookmarkDetailsHolder(parentType, parentId, otherId);
    final Bookmark bookmark = createOrUpdateBookmark(bookmarkDto, otherId, member, detailsHolder);

    bookmarkRepository.save(bookmark);
    final Integer parentTotalBookmarks = updateBookmarkCount(parentId, otherId, parentType, bookmarkDto.getBookmarkType());

    final BookmarkResponse bookmarkResponse = bookmarkMapper.toBookmarkResponse(bookmark);
    final BookmarkCreateResponse bookmarkCreateResponse = BookmarkCreateResponse.of(bookmarkResponse, parentTotalBookmarks);
    return localizer.of(bookmarkCreateResponse);
  }

  protected Bookmark createOrUpdateBookmark(final BookmarkDto bookmarkDto, final Long otherId, final Member member, final BookmarkParentDetailHolder detailsHolder) {
    final BookmarkParentType parentType = bookmarkDto.getBookmarkParentType();
    final Long parentId = bookmarkDto.getParentId();

    return findBookmarkByParent(parentId, otherId, parentType, member)
      .map(existingBookmark -> existingBookmark.updateType(bookmarkDto.getBookmarkType()))
      .orElseGet(() -> BookmarkFactory.by(bookmarkDto, detailsHolder, member));
  }

  protected BookmarkParentDetailHolder retrieveBookmarkDetailsHolder(final BookmarkParentType bookmarkParentType, final Long parentId, final Long otherId) throws FailedOperationException {
    checkIsNull(bookmarkParentType, FailedOperationException::new);

    final ChatSpace chatSpace = BookmarkParentType.isChatSpace(bookmarkParentType) ? chatSpaceService.findChatSpace(parentId) : null;
    final Review review = BookmarkParentType.isReview(bookmarkParentType) ? reviewOperationService.findReview(parentId) : null;
    final FleenStream stream = BookmarkParentType.isStream(bookmarkParentType) ? streamOperationsService.findStream(parentId) : null;
    final SoftAsk softAsk = BookmarkParentType.isSoftAsk(bookmarkParentType) ? softAskOperationService.findSoftAsk(parentId) : null;
    final SoftAskReply softAskReply = BookmarkParentType.isSoftAskReply(bookmarkParentType) ? softAskOperationService.findSoftAskReply(otherId, parentId) : null;

    return BookmarkParentDetailHolder.of(chatSpace, stream, review, softAsk, softAskReply, bookmarkParentType);
  }

  protected Integer updateBookmarkCount(final Long parentId, final Long otherId, final BookmarkParentType parentType, final BookmarkType bookmarkType) {
    checkIsNullAny(List.of(parentId, parentType), FailedOperationException::new);
    final boolean isBookmarked = BookmarkType.isBookmarked(bookmarkType);

    return switch (parentType) {
      case STREAM -> streamOperationsService.updateBookmarkCount(parentId, isBookmarked);
      case CHAT_SPACE -> chatSpaceService.updateBookmarkCount(parentId, isBookmarked);
      case REVIEW -> reviewOperationService.updateBookmarkCount(parentId, isBookmarked);
      case SOFT_ASK -> softAskOperationService.updateBookmarkCount(parentId, isBookmarked);
      case SOFT_ASK_REPLY -> softAskOperationService.updateBookmarkCount(otherId, parentId, isBookmarked);
      case BUSINESS, JOB_OPPORTUNITY -> 0;
    };
  }
  
  protected Optional<Bookmark> findBookmarkByParent(final Long parentId, final Long otherId, final BookmarkParentType bookmarkParentType, final Member member) {
    checkIsNullAny(List.of(parentId, member), FailedOperationException::new);
    final Long memberId = member.getMemberId();

    return switch (bookmarkParentType) {
      case CHAT_SPACE -> bookmarkRepository.findByMemberAndChatSpace(memberId, parentId, CHAT_SPACE);
      case REVIEW -> bookmarkRepository.findByMemberAndReview(memberId, parentId, REVIEW);
      case STREAM -> bookmarkRepository.findByMemberAndStream(memberId, parentId, STREAM);
      case SOFT_ASK -> bookmarkRepository.findByMemberAndSoftAsk(memberId, parentId, SOFT_ASK);
      case SOFT_ASK_REPLY -> bookmarkRepository.findByMemberAndSoftAskReply(memberId, otherId, parentId, SOFT_ASK_REPLY);
      case BUSINESS, JOB_OPPORTUNITY -> Optional.empty();
    };
  }

}
