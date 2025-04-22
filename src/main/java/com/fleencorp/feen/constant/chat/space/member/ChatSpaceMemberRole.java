package com.fleencorp.feen.constant.chat.space.member;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enum representing the roles of members within a chat space.
 *
 * <p>This enum defines the various roles that a member can have in a chat space,
 * which determines their permissions and responsibilities within the space.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ChatSpaceMemberRole implements ApiParameter {

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
