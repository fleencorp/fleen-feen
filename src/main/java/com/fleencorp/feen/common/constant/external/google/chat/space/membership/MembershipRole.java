package com.fleencorp.feen.common.constant.external.google.chat.space.membership;

/**
 * Enum representing the roles of members in a chat space.
 *
 * <p>This enum defines the possible membership roles that can be assigned
 * within a chat space, such as {@code ROLE_MEMBER} and {@code ROLE_MANAGER}.
 * It provides a method to retrieve the role's name in uppercase.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public enum MembershipRole {

  ROLE_MEMBER,
  ROLE_MANAGER;

  public String getValue() {
    return name();
  }
}
