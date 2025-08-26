package com.fleencorp.feen.bookmark.model.holder;

import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.stream.model.domain.FleenStream;

import java.util.Optional;

public record BookmarkParentDetailHolder(
  ChatSpace chatSpace,
  FleenStream stream,
  Review review,
  SoftAsk softAsk,
  SoftAskReply softAskReply,
  BookmarkParentType parentType
) {

  public String parentTitle() {
    return switch (parentType) {
      case CHAT_SPACE -> Optional
        .ofNullable(chatSpace)
        .map(ChatSpace::getTitle)
        .orElse(null);

      case REVIEW -> Optional
        .ofNullable(review)
        .map(Review::getReviewText)
        .orElse(null);

      case SOFT_ASK -> Optional.ofNullable(softAsk)
        .map(SoftAsk::getDescription)
        .orElse(null);

      case SOFT_ASK_REPLY -> Optional
        .ofNullable(softAskReply)
        .map(SoftAskReply::getContent)
        .orElse(null);

      case STREAM -> Optional
        .ofNullable(stream)
        .map(FleenStream::getTitle)
        .orElse(null);

      case BUSINESS, JOB_OPPORTUNITY -> null;
    };
  }

  public static BookmarkParentDetailHolder of(
    final ChatSpace chatSpace,
    final FleenStream stream,
    final Review review,
    final SoftAsk softAsk,
    final SoftAskReply softAskReply,
    final BookmarkParentType bookmarkParentType
  ) {
    return new BookmarkParentDetailHolder(chatSpace, stream, review, softAsk, softAskReply, bookmarkParentType);
  }
}
