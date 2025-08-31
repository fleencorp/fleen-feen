package com.fleencorp.feen.poll.constant;

import lombok.Getter;

@Getter
public enum TotalVoteEntries {

  TOTAL_VOTE_ENTRIES("total.vote.entries", "total.vote.entries.2", "total.vote.entries.otherText");

  private final String messageCode;
  private final String messageCode2;
  private final String messageCode3;

  TotalVoteEntries(
      final String messageCode,
      final String messageCode2,
      final String messageCode3) {
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
    this.messageCode3 = messageCode3;
  }

  public static TotalVoteEntries totalVoteEntries() {
    return TOTAL_VOTE_ENTRIES;
  }
}
