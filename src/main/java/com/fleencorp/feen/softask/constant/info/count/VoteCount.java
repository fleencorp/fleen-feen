package com.fleencorp.feen.softask.constant.info.count;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum VoteCount implements ApiParameter {

  TOTAL_VOTE("Total Vote", "soft.ask.vote.total.count", "soft.ask.vote.total.count.2");

  private final String value;
  private final String messageCode;
  private final String messageCode2;

  VoteCount(
      final String value,
      final String messageCode,
      final String messageCode2) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
  }

  public static VoteCount totalVote() {
    return TOTAL_VOTE;
  }
}
