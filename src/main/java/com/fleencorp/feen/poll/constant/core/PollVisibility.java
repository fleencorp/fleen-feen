package com.fleencorp.feen.poll.constant.core;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum PollVisibility implements ApiParameter {

  CHAT_SPACE_MEMBERS_ONLY("poll.visibility.chat.space.member.label", "poll.visibility.chat.space.members.only"),
  FOLLOWERS_ONLY("poll.visibility.followers.label", "poll.visibility.followers.only"),
  PRIVATE("poll.visibility.private.label", "poll.visibility.private.only"),
  PUBLIC("poll.visibility.public.label", "poll.visibility.public.only"),
  STREAM_ATTENDEES_ONLY("poll.visibility.stream.attendees.label", "poll.visibility.stream.attendees.only");

  private final String value;
  private final String messageCode;

  PollVisibility(
      final String value,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
  }

  public String getLabelCode() {
    return this.value;
  }

  public static PollVisibility of(final String value) {
    return parseEnumOrNull(value, PollVisibility.class);
  }
}
