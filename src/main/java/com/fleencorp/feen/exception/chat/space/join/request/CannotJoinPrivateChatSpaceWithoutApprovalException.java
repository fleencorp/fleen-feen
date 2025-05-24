package com.fleencorp.feen.exception.chat.space.join.request;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class CannotJoinPrivateChatSpaceWithoutApprovalException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "cannot.join.private.chat.space.without.approval";
  }

  public CannotJoinPrivateChatSpaceWithoutApprovalException(final Object...params) {
    super(params);
  }

  public static CannotJoinPrivateChatSpaceWithoutApprovalException of(final Object chatSpaceId) {
    return new CannotJoinPrivateChatSpaceWithoutApprovalException(chatSpaceId);
  }
}
