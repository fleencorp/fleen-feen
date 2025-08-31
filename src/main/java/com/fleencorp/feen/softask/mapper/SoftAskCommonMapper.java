package com.fleencorp.feen.softask.mapper;

import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.response.vote.core.SoftAskVoteResponse;

import java.util.Collection;

public interface SoftAskCommonMapper {

  void setOtherDetails(SoftAskCommonData entry, SoftAskCommonResponse response);

  SoftAskVoteResponse toSoftAskVoteResponse(SoftAskVote entry);

  SoftAskVoteResponse toSoftAskVoteResponse(SoftAskVote entry, boolean voted);

  Collection<SoftAskVoteResponse> toSoftAskVoteResponses(Collection<SoftAskVote> entries);
}
