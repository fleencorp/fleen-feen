package com.fleencorp.feen.exception.chat.space.join.request;

import com.fleencorp.localizer.model.exception.ApiException;

public class CannotJoinPrivateChatSpaceWithoutApprovalException extends ApiException {

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
