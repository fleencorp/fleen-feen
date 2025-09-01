package com.fleencorp.feen.poll.mapper.impl;

import com.fleencorp.feen.poll.mapper.PollUnifiedMapper;
import com.fleencorp.feen.poll.mapper.common.PollInfoMapper;
import com.fleencorp.feen.poll.mapper.poll.PollMapper;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollOption;
import com.fleencorp.feen.poll.model.holder.PollOptionEntriesHolder;
import com.fleencorp.feen.poll.model.holder.PollResponseEntriesHolder;
import com.fleencorp.feen.poll.model.info.IsVotedInfo;
import com.fleencorp.feen.poll.model.info.TotalPollVoteEntriesInfo;
import com.fleencorp.feen.poll.model.response.core.PollOptionResponse;
import com.fleencorp.feen.poll.model.response.core.PollResponse;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.response.UserResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class PollUnifiedMapperImpl implements PollUnifiedMapper {

  private final PollInfoMapper pollInfoMapper;
  private final PollMapper pollMapper;

  public PollUnifiedMapperImpl(
      final PollInfoMapper pollInfoMapper,
      final PollMapper pollMapper) {
    this.pollInfoMapper = pollInfoMapper;
    this.pollMapper = pollMapper;
  }

  @Override
  public IsVotedInfo toIsVotedInfo(final boolean isVoted) {
    return pollInfoMapper.toIsVotedInfo(isVoted);
  }

  @Override
  public PollResponse toPollResponse(Poll entry) {
    return pollMapper.toPollResponse(entry);
  }

  @Override
  public PollResponseEntriesHolder toPollResponses(List<Poll> entries) {
    return pollMapper.toPollResponses(entries);
  }

  @Override
  public Collection<PollOptionResponse> toVotedPollOptionResponses(Collection<PollOption> entries) {
    return pollMapper.toVotedPollOptionResponses(entries);
  }

  @Override
  public Collection<PollOptionResponse> toPollOptionResponses(
      Collection<PollOption> entries,
      PollOptionEntriesHolder pollOptionEntriesHolder,
      Collection<Long> votedPollOptionIds) {
    return pollMapper.toPollOptionResponses(entries, pollOptionEntriesHolder, votedPollOptionIds);
  }

  @Override
  public Collection<UserResponse> toPollVoteResponses(Collection<Member> entries) {
    return pollMapper.toPollVoteResponses(entries);
  }

  @Override
  public TotalPollVoteEntriesInfo toTotalPollVoteEntriesInfo(Integer pollVoteEntries) {
    return pollMapper.toTotalPollVoteEntriesInfo(pollVoteEntries);
  }
}
