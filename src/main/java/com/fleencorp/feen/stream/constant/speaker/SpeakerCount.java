package com.fleencorp.feen.stream.constant.speaker;

import lombok.Getter;

@Getter
public enum SpeakerCount {

  TOTAL_SPEAKER("total.speaker.count");

  private final String messageCode;

  SpeakerCount(final String messageCode) {
    this.messageCode = messageCode;
  }

  public static SpeakerCount totalSpeakerCount() {
    return TOTAL_SPEAKER;
  }
}
