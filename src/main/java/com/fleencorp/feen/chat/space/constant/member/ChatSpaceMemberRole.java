package com.fleencorp.feen.chat.space.constant.member;

import lombok.Getter;

@Getter
public enum ChatSpaceMemberRole {

  ADMIN("admin", "chat.space.member.role.admin"),
  MEMBER("member", "chat.space.member.role.member");

  private final String value;
  private final String messageCode;

  ChatSpaceMemberRole(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public static boolean isAdmin(final ChatSpaceMemberRole role) {
    return role == ChatSpaceMemberRole.ADMIN;
  }
}
