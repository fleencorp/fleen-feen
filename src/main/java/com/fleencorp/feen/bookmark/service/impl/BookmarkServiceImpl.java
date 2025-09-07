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
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.feen.bookmark.constant.BookmarkParentType.*;

@Slf4j
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

  /**
   * Creates or updates a {@link Bookmark} for the given user and parent entity, updates the
   * bookmark count, and returns a localized {@link BookmarkCreateResponse}. This method first
   * resolves the parent entity details, creates or updates the bookmark accordingly, and then
   * updates the total bookmark count for the parent. The result includes the bookmark data and
   * the updated count.
   *
   * @param bookmarkDto the DTO containing bookmark details including parent IDs, type, and bookmark type
   * @param user the authenticated user performing the bookmark action
   * @return a localized {@link BookmarkCreateResponse} containing the created or updated bookmark
   *         and the new total bookmark count for the parent entity
   * @throws StreamNotFoundException if the parent type is a stream and the stream is not found
   * @throws ChatSpaceNotFoundException if the parent type is a chat space and the chat space is not found
   * @throws SoftAskNotFoundException if the parent type is a soft ask and the soft ask is not found
   * @throws SoftAskReplyNotFoundException if the parent type is a soft ask reply and the reply is not found
   * @throws FailedOperationException if the parent type is null or the operation is otherwise invalid
   */
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
    final Integer parentTotalBookmarks = updateBookmarkCount(parentId, otherId, parentType, bookmarkDto.getBookmarkType());

    final BookmarkResponse bookmarkResponse = bookmarkMapper.toBookmarkResponse(bookmark);
    final BookmarkCreateResponse bookmarkCreateResponse = BookmarkCreateResponse.of(bookmarkResponse, parentTotalBookmarks);
    return localizer.of(bookmarkCreateResponse);
  }

  /**
   * Creates a new {@link Bookmark} or updates an existing one based on the provided
   * {@link BookmarkDto}. If a bookmark exists for the given parent, other identifier, and member,
   * its bookmark type must match the incoming type; otherwise, a {@link FailedOperationException}
   * is thrown. When no bookmark exists, a new bookmark is created and saved if the type is valid;
   * otherwise, a {@link FailedOperationException} is thrown to prevent creating an empty bookmark.
   *
   * @param bookmarkDto the DTO containing bookmark details including type and parent references
   * @param otherId an optional secondary identifier related to the parent entity
   * @param member the member performing the bookmark action
   * @param detailsHolder a holder containing parent entity details used to construct a new bookmark
   * @return the created or updated {@link Bookmark}
   */
  private Bookmark createOrUpdateBookmark(final BookmarkDto bookmarkDto, final Long otherId, final Member member, final BookmarkParentDetailHolder detailsHolder) {
    final BookmarkParentType parentType = bookmarkDto.getBookmarkParentType();
    final Long parentId = bookmarkDto.getParentId();
    final BookmarkType bookmarkType = bookmarkDto.getBookmarkType();

    return findBookmarkByParent(parentId, otherId, parentType, member)
      .map(existingBookmark -> {
        if (existingBookmark.getBookmarkType() == bookmarkType) {
          throw FailedOperationException.of();
        }

        existingBookmark.updateType(bookmarkType);
        return bookmarkRepository.save(existingBookmark);
      }).orElseGet(() -> {
        if (BookmarkType.isUnbookmarked(bookmarkType)) {
          throw FailedOperationException.of();
        }

        Bookmark newBookmark = BookmarkFactory.by(bookmarkDto, detailsHolder, member);
        newBookmark = bookmarkRepository.save(newBookmark);
        return newBookmark;
    });
  }

  /**
   * Retrieves a {@link BookmarkParentDetailHolder} containing the resolved parent entities
   * based on the provided {@link BookmarkParentType} and identifiers. Depending on the parent type,
   * this method fetches the corresponding {@link ChatSpace}, {@link Review}, {@link FleenStream},
   * {@link SoftAsk}, or {@link SoftAskReply}. For {@link BookmarkParentType#SOFT_ASK}, a
   * {@link SoftAsk} is created from {@code otherId} if {@code parentId} is not used. If the
   * bookmarkParentType is null, a {@link FailedOperationException} is thrown.
   *
   * @param bookmarkParentType the type of the parent entity to retrieve
   * @param parentId the ID of the main parent entity, used for lookups
   * @param otherId an optional secondary identifier, used when resolving {@link SoftAskReply} or as a fallback for {@link SoftAsk}
   * @return a {@link BookmarkParentDetailHolder} containing the resolved parent entity details
   * @throws FailedOperationException if the bookmarkParentType is null or retrieval fails
   */
  protected BookmarkParentDetailHolder retrieveBookmarkDetailsHolder(final BookmarkParentType bookmarkParentType, final Long parentId, final Long otherId) throws FailedOperationException {
    checkIsNull(bookmarkParentType, FailedOperationException::new);

    final ChatSpace chatSpace = BookmarkParentType.isChatSpace(bookmarkParentType) ? chatSpaceService.findChatSpace(parentId) : null;
    final Review review = BookmarkParentType.isReview(bookmarkParentType) ? reviewOperationService.findReview(parentId) : null;
    final FleenStream stream = BookmarkParentType.isStream(bookmarkParentType) ? streamOperationsService.findStream(parentId) : null;
    final SoftAsk softAsk = BookmarkParentType.isSoftAsk(bookmarkParentType) ? softAskOperationService.findSoftAsk(parentId) : SoftAsk.of(otherId);
    final SoftAskReply softAskReply = BookmarkParentType.isSoftAskReply(bookmarkParentType) ? softAskOperationService.findSoftAskReply(otherId, parentId) : null;

    return BookmarkParentDetailHolder.of(chatSpace, stream, review, softAsk, softAskReply, bookmarkParentType);
  }

  /**
   * Updates the bookmark count for the specified parent entity based on the given
   * {@link BookmarkType}. The count is incremented or decremented depending on whether
   * the type represents a bookmarked or unbookmarked state. The appropriate service is
   * invoked based on the {@link BookmarkParentType}. For types {@link BookmarkParentType#BUSINESS}
   * and {@link BookmarkParentType#JOB_OPPORTUNITY}, no update is performed and {@code 0} is returned.
   * If the parentId or parentType is null, a {@link FailedOperationException} is thrown.
   *
   * @param parentId the ID of the parent entity whose bookmark count should be updated
   * @param otherId an optional secondary identifier, used when updating count for {@link BookmarkParentType#SOFT_ASK_REPLY}
   * @param parentType the type of the parent entity whose bookmark count is being updated
   * @param bookmarkType the type of bookmark indicating whether to increment or decrement the count
   * @return the updated bookmark count, or {@code 0} if no update is performed
   */
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

  /**
   * Finds a {@link Bookmark} for the given parent entity and member based on the specified
   * {@link BookmarkParentType}. The lookup delegates to the appropriate repository method
   * depending on the parent type. For types {@link BookmarkParentType#BUSINESS} and
   * {@link BookmarkParentType#JOB_OPPORTUNITY}, this method always returns an empty
   * {@link Optional}. If the parentId or member is null, a {@link FailedOperationException}
   * is thrown.
   *
   * @param parentId the ID of the parent entity to search by
   * @param otherId an optional secondary identifier, used when searching by {@link BookmarkParentType#SOFT_ASK_REPLY}
   * @param bookmarkParentType the type of the parent entity
   * @param member the member whose bookmark is being retrieved
   * @return an {@link Optional} containing the matching {@link Bookmark} if found, otherwise empty
   */
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
