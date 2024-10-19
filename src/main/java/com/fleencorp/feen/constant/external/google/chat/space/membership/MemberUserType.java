package com.fleencorp.feen.constant.external.google.chat.space.membership;

/**
 * Enum representing the type of a member user in a chat space.
 *
 * <p>This enum defines two possible user types: {@code HUMAN} and {@code BOT}.
 * The user type can be retrieved in its string form using the {@code getValue} method.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public enum MemberUserType {

  HUMAN,
  BOT;

  public String getValue() {
    return name();
  }
}
