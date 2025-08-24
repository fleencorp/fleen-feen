package com.fleencorp.feen.like.model.holder;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.like.constant.LikeParentType;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.stream.model.domain.FleenStream;

import java.util.Optional;

public record LikeParentDetailHolder(ChatSpace chatSpace, Review review, FleenStream stream, LikeParentType parentType) {

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

      case STREAM -> Optional
        .ofNullable(stream)
        .map(FleenStream::getTitle)
        .orElse(null);

      default -> null;
    };
  }

  public static LikeParentDetailHolder of(final ChatSpace chatSpace, final Review review, final FleenStream stream, final LikeParentType likeParentType) {
    return new LikeParentDetailHolder(chatSpace, review, stream, likeParentType);
  }
}
