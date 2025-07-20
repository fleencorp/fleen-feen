package com.fleencorp.feen.common.constant.external.google.chat.space.membership;

import lombok.Getter;

/**
 * Enum representing the different states of a member in a chat space.
 *
 * <p>This enum defines possible membership states such as {@code JOINED},
 * {@code INVITED}, and {@code NOT_A_MEMBER}. Each state is associated with
 * a label that describes its human-readable form. The enum provides a method
 * to retrieve the state's name in uppercase format.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum MembershipState {

  JOINED("Joined"),
  INVITED("Invited"),
  NOT_A_MEMBER("Not a Member");

  private final String label;

  MembershipState(final String label) {
    this.label = label;
  }

  public String getValue() {
    return name();
  }
}
