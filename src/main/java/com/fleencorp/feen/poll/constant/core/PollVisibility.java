package com.fleencorp.feen.poll.constant.core;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum PollVisibility {

  CHAT_SPACE_MEMBERS_ONLY("poll.visibility.chat.space.members.only", "poll.visibility.chat.space.members.only.2"),
  FOLLOWERS_ONLY("poll.visibility.followers.only", "poll.visibility.followers.only.2"),
  PRIVATE("poll.visibility.private", "poll.visibility.private.2"),
  PUBLIC("poll.visibility.public", "poll.visibility.public.2"),
  STREAM_ATTENDEES_ONLY("poll.visibility.stream.attendees", "poll.visibility.stream.attendees.2");

  private final String messageCode;
  private final String messageCode2;

  PollVisibility(
      final String messageCode,
      final String messageCode2) {
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public String getLabelCode() {
    return this.messageCode;
  }

  public static PollVisibility of(final String value) {
    return parseEnumOrNull(value, PollVisibility.class);
  }
}
