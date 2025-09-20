package com.fleencorp.feen.bookmark.model.holder;

import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.model.contract.HasTitle;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.stream.model.domain.FleenStream;

import static java.util.Objects.nonNull;

public record BookmarkParentDetailHolder(
  ChatSpace chatSpace,
  Poll poll,
  Review review,
  SoftAsk softAsk,
  SoftAskReply softAskReply,
  FleenStream stream,
  BookmarkParentType parentType
) {

  public String parentTitle() {
    if (nonNull(parentType)) {
      return switch (parentType) {
        case CHAT_SPACE -> HasTitle.getTitle(chatSpace);
        case POLL ->  HasTitle.getTitle(poll);
        case REVIEW -> HasTitle.getTitle(review);
        case SOFT_ASK -> HasTitle.getTitle(softAsk);
        case SOFT_ASK_REPLY -> HasTitle.getTitle(softAskReply);
        case STREAM ->  HasTitle.getTitle(stream);
        case BUSINESS, JOB_OPPORTUNITY -> null;
      };
    }

    return null;
  }

  public static BookmarkParentDetailHolder of(
    final ChatSpace chatSpace,
    final Poll poll,
    final Review review,
    final SoftAsk softAsk,
    final SoftAskReply softAskReply,
    final FleenStream stream,
    final BookmarkParentType bookmarkParentType
  ) {
    return new BookmarkParentDetailHolder(chatSpace, poll, review, softAsk, softAskReply, stream, bookmarkParentType);
  }
}
