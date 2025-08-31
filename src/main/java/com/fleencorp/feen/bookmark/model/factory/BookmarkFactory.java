package com.fleencorp.feen.bookmark.model.factory;

import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
import com.fleencorp.feen.bookmark.model.domain.Bookmark;
import com.fleencorp.feen.bookmark.model.dto.BookmarkDto;
import com.fleencorp.feen.bookmark.model.holder.BookmarkParentDetailHolder;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;

import java.util.Map;
import java.util.function.Consumer;

import static java.util.Objects.isNull;

public final class BookmarkFactory {

  private BookmarkFactory() {}

  private static final Map<BookmarkParentType, BookmarkCreator> CREATORS = Map.of(
    BookmarkParentType.CHAT_SPACE, BookmarkFactory::createChatSpaceBookmark,
    BookmarkParentType.REVIEW, BookmarkFactory::createReviewBookmark,
    BookmarkParentType.SOFT_ASK, BookmarkFactory::createSoftAskBookmark,
    BookmarkParentType.SOFT_ASK_REPLY, BookmarkFactory::createSoftAskReplyBookmark,
    BookmarkParentType.STREAM, BookmarkFactory::createStreamBookmark
  );

  public static Bookmark by(final BookmarkDto dto, final BookmarkParentDetailHolder detailsHolder, final Member member) {
    final BookmarkCreator creator = CREATORS.get(dto.getBookmarkParentType());

    if (isNull(creator)) {
      throw FailedOperationException.of();
    }

    return creator.create(dto, detailsHolder, member);
  }

  private static Bookmark createChatSpaceBookmark(final BookmarkDto dto, final BookmarkParentDetailHolder holder, final Member member) {
    final ChatSpace chatSpace = holder.chatSpace();
    return createBaseBookmark(dto, member, chatSpace.getChatSpaceId(), chatSpace.getTitle())
      .apply(bookmark -> {
        bookmark.setChatSpaceId(chatSpace.getChatSpaceId());
        bookmark.setChatSpace(chatSpace);
      });
  }

  private static Bookmark createReviewBookmark(final BookmarkDto dto, final BookmarkParentDetailHolder holder, final Member member) {
    final Review review = holder.review();
    return createBaseBookmark(dto, member, review.getReviewId(), review.getReviewText())
      .apply(bookmark -> {
        bookmark.setReviewId(review.getReviewId());
        bookmark.setReview(review);
        bookmark.setChatSpaceId(review.getChatSpaceId());
        bookmark.setStreamId(review.getStreamId());
      });
  }

  private static Bookmark createSoftAskBookmark(final BookmarkDto dto, final BookmarkParentDetailHolder holder, final Member member) {
    final SoftAsk softAsk = holder.softAsk();
    return createBaseBookmark(dto, member, softAsk.getSoftAskId(), softAsk.getDescription())
      .apply(bookmark -> {
        bookmark.setSoftAskId(softAsk.getSoftAskId());
        bookmark.setSoftAsk(softAsk);
    });
  }

  private static Bookmark createSoftAskReplyBookmark(final BookmarkDto dto, final BookmarkParentDetailHolder holder, final Member member) {
    final SoftAsk softAsk = holder.softAsk();
    final SoftAskReply reply = holder.softAskReply();
    return createBaseBookmark(dto, member, reply.getSoftAskReplyId(), reply.getContent())
      .apply(bookmark -> {
        bookmark.setSoftAskId(softAsk.getSoftAskId());
        bookmark.setSoftAsk(softAsk);
        bookmark.setSoftAskReplyId(reply.getSoftAskReplyId());
        bookmark.setSoftAskReply(reply);
      });
  }

  private static Bookmark createStreamBookmark(final BookmarkDto dto, final BookmarkParentDetailHolder holder, final Member member) {
    final FleenStream stream = holder.stream();
    return createBaseBookmark(dto, member, stream.getStreamId(), stream.getTitle())
      .apply(bookmark -> {
        bookmark.setStreamId(stream.getStreamId());
        bookmark.setStream(stream);
      });
  }

  private static BookmarkApplier createBaseBookmark(final BookmarkDto dto, final Member member, final Long parentId, final String parentSummary) {
    final Bookmark bookmark = new Bookmark();
    bookmark.setParentId(parentId);
    bookmark.setParentSummary(parentSummary);
    bookmark.setBookmarkParentType(dto.getBookmarkParentType());
    bookmark.setBookmarkType(dto.getBookmarkType());
    bookmark.setMemberId(member.getMemberId());
    bookmark.setMember(member);

    return new BookmarkApplier(bookmark);
  }

  @FunctionalInterface
  private interface BookmarkCreator {
    Bookmark create(BookmarkDto dto, BookmarkParentDetailHolder holder, Member member);
  }

  private record BookmarkApplier(Bookmark bookmark) {

    public Bookmark apply(final Consumer<Bookmark> configurer) {
      configurer.accept(bookmark);
      return bookmark;
    }
  }
}