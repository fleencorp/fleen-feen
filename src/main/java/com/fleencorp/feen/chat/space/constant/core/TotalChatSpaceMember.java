package com.fleencorp.feen.chat.space.constant.core;

import lombok.Getter;

@Getter
public enum TotalChatSpaceMember {

  TOTAL_CHAT_SPACE_MEMBER("chat.space.total.member"),
  TOTAL_CHAT_SPACE_MEMBER_REQUEST_TO_JOIN("chat.space.total.member.request.to.join");

  private final String messageCode;

  TotalChatSpaceMember(final String messageCode) {
    this.messageCode = messageCode;
  }

  public static TotalChatSpaceMember totalChatSpaceMember() {
    return TOTAL_CHAT_SPACE_MEMBER;
  }

  public static TotalChatSpaceMember totalChatSpaceMemberRequestToJoin() {
    return TOTAL_CHAT_SPACE_MEMBER_REQUEST_TO_JOIN;
  }
}
