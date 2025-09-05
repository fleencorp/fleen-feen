package com.fleencorp.feen.review.model.holder;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.model.contract.HasTitle;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.stream.model.domain.FleenStream;

import static java.util.Objects.nonNull;

public record ReviewParentDetailHolder(ChatSpace chatSpace, FleenStream stream, ReviewParentType parentType) {

  public String parentTitle() {
    if (nonNull(parentType)) {
      return switch (parentType) {
        case CHAT_SPACE -> HasTitle.getTitle(chatSpace);
        case STREAM -> HasTitle.getTitle(stream);
      };
    }

    return null;
  }

  public static ReviewParentDetailHolder of(final ChatSpace chatSpace, final FleenStream stream, final ReviewParentType parentType) {
    return new ReviewParentDetailHolder(chatSpace, stream, parentType);
  }
}
