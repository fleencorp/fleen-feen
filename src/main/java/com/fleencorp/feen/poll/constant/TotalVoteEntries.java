package com.fleencorp.feen.poll.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum TotalVoteEntries implements ApiParameter {

  TOTAL_VOTE_ENTRIES("total.vote.entries", "total.vote.entries.2", "total.vote.entries.otherText");

  private final String value;
  private final String messageCode2;
  private final String messageCode3;

  TotalVoteEntries(
      final String value,
      final String messageCode2,
      final String messageCode3) {
    this.value = value;
    this.messageCode2 = messageCode2;
    this.messageCode3 = messageCode3;
  }

  public String getMessageCode() {
    return value;
  }

  public static TotalVoteEntries totalVoteEntries() {
    return TOTAL_VOTE_ENTRIES;
  }
}
