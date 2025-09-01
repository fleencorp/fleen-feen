package com.fleencorp.feen.poll.mapper.common;

import com.fleencorp.feen.poll.constant.core.PollVisibility;
import com.fleencorp.feen.poll.model.info.*;

public interface PollInfoMapper {

  PollIsEndedInfo toIsEnded(boolean ended);

  IsPollMultipleChoiceInfo toIsMultipleChoiceInfo(boolean multipleChoice);

  IsVotedInfo toIsVotedInfo(boolean voted);

  TotalPollVoteEntriesInfo toTotalPollVoteEntriesInfo(Integer pollVoteEntries);

  IsPollAnonymousInfo toIsAnonymousInfo(boolean anonymous);

  PollVisibilityInfo toPollVisibilityInfo(PollVisibility pollVisibility);
}
