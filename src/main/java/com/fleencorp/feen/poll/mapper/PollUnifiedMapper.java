package com.fleencorp.feen.poll.mapper;

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

import java.util.Collection;
import java.util.List;

public interface PollUnifiedMapper {

  IsVotedInfo toIsVotedInfo(boolean isVoted);

  PollResponse toPollResponse(Poll entry);

  PollResponseEntriesHolder toPollResponses(List<Poll> entries);

  Collection<PollOptionResponse> toVotedPollOptionResponses(Collection<PollOption> entries);

  Collection<PollOptionResponse> toPollOptionResponses(
    Collection<PollOption> entries,
    PollOptionEntriesHolder pollOptionEntriesHolder,
    Collection<Long> votedPollOptionIds);

  Collection<UserResponse> toPollVoteResponses(Collection<Member> entries);

  TotalPollVoteEntriesInfo toTotalPollVoteEntriesInfo(Integer pollVoteEntries);
}
