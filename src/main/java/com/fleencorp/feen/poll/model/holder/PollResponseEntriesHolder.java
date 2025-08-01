package com.fleencorp.feen.poll.model.holder;

import com.fleencorp.feen.poll.model.response.base.PollResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

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
  public Collection<Long> getPollIds() {
    return pollResponses.stream()
      .map(PollResponse::getNumberId)
      .filter(Objects::nonNull)
      .toList();
  }

  public boolean hasNoPolls() {
    return nonNull(pollResponses) && pollResponses.isEmpty();
  }

  public static PollResponseEntriesHolder of(final Collection<PollResponse> pollResponses) {
    return new PollResponseEntriesHolder(pollResponses);
  }

  public static PollResponseEntriesHolder of(final PollResponse pollResponse) {
    final Collection<PollResponse> pollResponses = Collections.singletonList(pollResponse);
    return new PollResponseEntriesHolder(pollResponses);
  }
}
