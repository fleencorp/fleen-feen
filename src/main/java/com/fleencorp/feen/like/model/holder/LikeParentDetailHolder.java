package com.fleencorp.feen.like.model.holder;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.like.constant.LikeParentType;
import com.fleencorp.feen.model.contract.HasTitle;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.stream.model.domain.FleenStream;

import static java.util.Objects.nonNull;

public record LikeParentDetailHolder(
  ChatSpace chatSpace,
  Poll poll,
  Review review,
  FleenStream stream,
  LikeParentType parentType
) {

  public String parentTitle() {
    if (nonNull(parentType)) {
      return switch (parentType) {
        case CHAT_SPACE -> HasTitle.getTitle(chatSpace);
        case POLL -> HasTitle.getTitle(poll);
        case REVIEW -> HasTitle.getTitle(review);
        case STREAM -> HasTitle.getTitle(stream);
      };
    }

    return null;
  }

  public static LikeParentDetailHolder of(
    final ChatSpace chatSpace,
    final Poll poll,
    final Review review,
    final FleenStream stream,
    final LikeParentType likeParentType
  ) {
    return new LikeParentDetailHolder(chatSpace, poll, review, stream, likeParentType);
  }
}
