package com.fleencorp.feen.constant.base.external.google.chat;

import java.util.HashSet;
import java.util.Set;

public class HangoutsChatScopes {
  public static final String CHAT_BOT = "https://www.googleapis.com/auth/chat.bot";
  public static final String CHAT_DELETE = "https://www.googleapis.com/auth/chat.delete";
  public static final String CHAT_IMPORT = "https://www.googleapis.com/auth/chat.import";
  public static final String CHAT_MEMBERSHIPS = "https://www.googleapis.com/auth/chat.memberships";
  public static final String CHAT_MEMBERSHIPS_APP = "https://www.googleapis.com/auth/chat.memberships.app";
  public static final String CHAT_MEMBERSHIPS_READONLY = "https://www.googleapis.com/auth/chat.memberships.readonly";
  public static final String CHAT_MESSAGES = "https://www.googleapis.com/auth/chat.messages";
  public static final String CHAT_MESSAGES_CREATE = "https://www.googleapis.com/auth/chat.messages.create";
  public static final String CHAT_MESSAGES_REACTIONS = "https://www.googleapis.com/auth/chat.messages.reactions";
  public static final String CHAT_MESSAGES_REACTIONS_CREATE = "https://www.googleapis.com/auth/chat.messages.reactions.create";
  public static final String CHAT_MESSAGES_REACTIONS_READONLY = "https://www.googleapis.com/auth/chat.messages.reactions.readonly";
  public static final String CHAT_MESSAGES_READONLY = "https://www.googleapis.com/auth/chat.messages.readonly";
  public static final String CHAT_SPACES = "https://www.googleapis.com/auth/chat.spaces";
  public static final String CHAT_SPACES_CREATE = "https://www.googleapis.com/auth/chat.spaces.create";
  public static final String CHAT_SPACES_READONLY = "https://www.googleapis.com/auth/chat.spaces.readonly";

  public static Set<String> all() {
    final Set<String> set = new HashSet<>();
    set.add("https://www.googleapis.com/auth/chat.bot");
    set.add("https://www.googleapis.com/auth/chat.delete");
    set.add("https://www.googleapis.com/auth/chat.import");
    set.add("https://www.googleapis.com/auth/chat.memberships");
    set.add("https://www.googleapis.com/auth/chat.memberships.app");
    set.add("https://www.googleapis.com/auth/chat.memberships.readonly");
    set.add("https://www.googleapis.com/auth/chat.messages");
    set.add("https://www.googleapis.com/auth/chat.messages.create");
    set.add("https://www.googleapis.com/auth/chat.messages.reactions");
    set.add("https://www.googleapis.com/auth/chat.messages.reactions.create");
    set.add("https://www.googleapis.com/auth/chat.messages.reactions.readonly");
    set.add("https://www.googleapis.com/auth/chat.messages.readonly");
    set.add("https://www.googleapis.com/auth/chat.spaces");
    set.add("https://www.googleapis.com/auth/chat.spaces.create");
    set.add("https://www.googleapis.com/auth/chat.spaces.readonly");
    return set;
  }

}
