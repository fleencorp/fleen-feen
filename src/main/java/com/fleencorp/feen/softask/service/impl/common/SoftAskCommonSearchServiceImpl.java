package com.fleencorp.feen.softask.service.impl.common;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import com.fleencorp.feen.softask.service.common.SoftAskCommonSearchService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.softask.service.vote.SoftAskVoteSearchService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SoftAskCommonSearchServiceImpl implements SoftAskCommonSearchService {

  private final SoftAskSearchService softAskSearchService;
  private final SoftAskReplySearchService softAskReplySearchService;
  private final SoftAskVoteSearchService softAskVoteSearchService;

  public SoftAskCommonSearchServiceImpl(
      final SoftAskSearchService softAskSearchService,
      final SoftAskReplySearchService softAskReplySearchService,
      final SoftAskVoteSearchService softAskVoteSearchService) {
    this.softAskSearchService = softAskSearchService;
    this.softAskReplySearchService = softAskReplySearchService;
    this.softAskVoteSearchService = softAskVoteSearchService;
  }

  @Override
  public SoftAsk findSoftAsk(Long softAskId) {
    return softAskSearchService.findSoftAsk(softAskId);
  }

  @Override
  public SoftAskReply findSoftAskReply(Long softAskId, Long softAskReplyId) throws SoftAskReplyNotFoundException {
    return softAskReplySearchService.findSoftAskReply(softAskId, softAskReplyId);
  }

  @Override
  public SoftAskReplySearchResult findSoftAskReplies(SoftAskSearchRequest searchRequest, IsAMember member) {
    return softAskReplySearchService.findSoftAskReplies(searchRequest, member);
  }

  @Override
  public <T extends SoftAskCommonResponse> void processVotesForResponses(Collection<T> softAskCommonResponses, IsAMember member) {
    softAskVoteSearchService.processVotesForResponses(softAskCommonResponses, member);
  }
}
