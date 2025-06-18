package com.fleencorp.feen.poll.model.projection;

public record PollOptionEntry(Long optionId, int totalEntries) {

  public static PollOptionEntry of(final long optionId, final int totalEntries) {
    return new PollOptionEntry(optionId, totalEntries);
  }
}
