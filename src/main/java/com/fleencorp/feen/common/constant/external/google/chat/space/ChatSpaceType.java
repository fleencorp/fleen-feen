package com.fleencorp.feen.common.constant.external.google.chat.space;

/**
 * Enumeration representing the different types of chat spaces.
 *
 * <p>This enum defines the various types of chat spaces available.
 * Each constant can be used to identify the nature of the chat space being interacted with.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public enum ChatSpaceType {

  SPACE_TYPE_UNSPECIFIED,
  SPACE,
  GROUP_CHAT,
  DIRECT_MESSAGE;

  public String getValue() {
    return this.name();
  }
}
