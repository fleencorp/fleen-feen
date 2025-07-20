package com.fleencorp.feen.common.constant.external.google.chat.space;

/**
 * Enum representing the chat history state in a chat space.
 *
 * <p>This enum defines two possible states for chat history: {@code HISTORY_OFF} and {@code HISTORY_ON}.
 * These states indicate whether the chat history is disabled or enabled, respectively.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public enum ChatHistoryState {

  HISTORY_OFF,
  HISTORY_ON;

  public String getValue() {
    return name();
  }
}
