package com.fleencorp.feen.chat.space.exception.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.localizer.model.exception.LocalizedException;

public class CannotJoinPrivateChatSpaceWithoutApprovalException extends LocalizedException {

  @Override
  @JsonIgnore
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
