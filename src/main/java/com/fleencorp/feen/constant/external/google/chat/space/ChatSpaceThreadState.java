package com.fleencorp.feen.constant.external.google.chat.space;

/**
 * Enumeration representing the different thread states of chat space messages.
 *
 * <p>This enum defines the possible states for message threading in a chat space.
 * Each constant can be used to determine how messages are organized within the chat space.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public enum ChatSpaceThreadState {

  THREADED_MESSAGES,
  GROUPED_MESSAGES,
  UNTHREADED_MESSAGES;

  public String getValue() {
    return name();
  }
}
