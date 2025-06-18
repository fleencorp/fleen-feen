package com.fleencorp.feen.poll.model.holder;

import com.fleencorp.feen.poll.model.domain.PollOption;
import com.fleencorp.feen.poll.model.projection.PollOptionEntry;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public record PollOptionEntriesHolder(Collection<PollOptionEntry> pollOptionEntries) {

  public Map<Long, Integer> pollOptionsEntries() {
    return pollOptionEntries.stream()
      .collect(Collectors.toMap(
        PollOptionEntry::optionId,
        PollOptionEntry::totalEntries
    ));
  }

  public Integer pollOptionTotalEntries(final Long optionId) {
    return pollOptionsEntries().getOrDefault(optionId, 0);
  }

  public Integer totalVotes() {
    return pollOptionsEntries().values().stream().mapToInt(Integer::intValue).sum();
  }

  public boolean isZeroTotalVotes() {
    return totalVotes() == 0;
  }

  public static PollOptionEntriesHolder of(final Collection<PollOptionEntry> pollOptionEntries) {
    return new PollOptionEntriesHolder(pollOptionEntries);
  }

  public static PollOptionEntriesHolder from(final Collection<PollOption> pollOptions) {
    final Collection<PollOptionEntry> pollOptionEntries = pollOptions.stream()
      .map(pollOption -> PollOptionEntry.of(pollOption.getPollOptionId(), pollOption.getVoteCount()))
      .collect(Collectors.toList());

    return new PollOptionEntriesHolder(pollOptionEntries);
  }
}
