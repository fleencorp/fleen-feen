package com.fleencorp.feen.softask.contract;

import com.fleencorp.feen.model.info.ParentInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskUserVoteInfo;
import com.fleencorp.feen.softask.model.info.vote.SoftAskVoteCountInfo;
import com.fleencorp.feen.softask.model.response.participant.SoftAskParticipantResponse;

public interface SoftAskCommonResponse {

  Long getParentId();

  void setVoteCountInfo(SoftAskVoteCountInfo voteCountInfo);

  void setParentInfo(ParentInfo parentInfo);

  void setSoftAskUserVoteInfo(SoftAskUserVoteInfo softAskUserVoteInfo);

  void setSoftAskParticipantResponse(SoftAskParticipantResponse softAskParticipantResponse);
}
