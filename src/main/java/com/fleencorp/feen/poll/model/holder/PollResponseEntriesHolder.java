package com.fleencorp.feen.poll.model.holder;

import com.fleencorp.feen.poll.model.response.base.PollResponse;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public record PollResponseEntriesHolder(Collection<PollResponse> pollResponses) {

  /**
   * Extracts non-null poll IDs from a collection of {@link PollResponse} objects.
   *
   * <p>This method maps each {@link PollResponse} to its {@code numberId} and filters out any {@code null} values.
   * The resulting list contains only the valid poll IDs.</p>
   *
   * @return a list of non-null poll IDs
   */
  public List<Long> getPollIds() {
    return pollResponses.stream()
      .map(PollResponse::getNumberId)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  public boolean hasPolls() {
    return nonNull(pollResponses) && !pollResponses.isEmpty();
  }

  public static PollResponseEntriesHolder of(final Collection<PollResponse> pollResponses) {
    return new PollResponseEntriesHolder(pollResponses);
  }
}
