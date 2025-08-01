package com.fleencorp.feen.softask.service.vote;

import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.search.SoftAskVoteSearchResult;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;

import java.util.Collection;

public interface SoftAskVoteSearchService {

  <T extends SoftAskCommonResponse> void processVotesForResponses(Collection<T> softAskCommonResponses, Member member);

  SoftAskVoteSearchResult findUserVotes(SoftAskSearchRequest searchRequest, RegisteredUser user);
}
