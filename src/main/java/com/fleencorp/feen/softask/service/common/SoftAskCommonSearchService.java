package com.fleencorp.feen.softask.service.common;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;

import java.util.Collection;

public interface SoftAskCommonSearchService {

  SoftAsk findSoftAsk(Long softAskId);

  SoftAskReply findSoftAskReply(Long softAskId, Long softAskReplyId) throws SoftAskReplyNotFoundException;

  SoftAskReplySearchResult findSoftAskReplies(SoftAskSearchRequest searchRequest, IsAMember member);

  <T extends SoftAskCommonResponse> void processVotesForResponses(Collection<T> softAskCommonResponses, IsAMember member);
}
