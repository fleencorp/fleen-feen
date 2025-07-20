package com.fleencorp.feen.softask.model.holder;

import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public record SoftAskUserVoteHolder(Collection<SoftAskVote> userVotes) {

  public Map<Long, SoftAskVote> groupVotes() {
    return userVotes.stream()
      .filter(Objects::nonNull)
      .collect(Collectors.toMap(SoftAskVote::getParentId, vote -> vote));
  }

  public static SoftAskUserVoteHolder of(final Collection<SoftAskVote> votes) {
    return new SoftAskUserVoteHolder(votes);
  }

  public static <T extends SoftAskCommonResponse> Collection<Long> getParentIdsToScanForVotes(final Collection<T> softAskCommonResponses) {
    return softAskCommonResponses.stream()
      .filter(Objects::nonNull)
      .map(SoftAskCommonResponse::getParentId)
      .toList();
  }
}
