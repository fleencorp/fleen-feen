package com.fleencorp.feen.softask.service.vote;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.dto.vote.SoftAskVoteDto;
import com.fleencorp.feen.softask.model.response.vote.SoftAskVoteUpdateResponse;
import com.fleencorp.feen.softask.model.response.vote.core.SoftAskVoteResponse;
import com.fleencorp.feen.softask.mother.SoftAskMother;
import com.fleencorp.feen.softask.repository.vote.SoftAskVoteRepository;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.impl.vote.SoftAskVoteServiceImpl;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.softask.util.SoftAskFeatureTestConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SoftAskVoteServiceTest {

  private SoftAskSearchService softAskSearchService;
  private SoftAskReplySearchService softAskReplySearchService;
  private SoftAskOperationService softAskOperationService;
  private SoftAskVoteRepository softAskVoteRepository;
  private SoftAskMapper softAskMapper;

  private RegisteredUser user;
  private IsAMember member;
  private SoftAsk softAsk;
  private SoftAskReply softAskReply;
  private SoftAskVoteDto softAskVoteDtoForAskUpvote;
  private SoftAskVoteDto softAskVoteDtoForReplyUnvote;
  private SoftAskVoteDto softAskVoteDtoForAskUnvote;


  private SoftAskVoteService softAskVoteService;

  @BeforeEach
  void setup() {
    softAskSearchService = Mockito.mock(SoftAskSearchService.class);
    softAskReplySearchService = Mockito.mock(SoftAskReplySearchService.class);
    softAskOperationService = Mockito.mock(SoftAskOperationService.class);
    softAskVoteRepository = Mockito.mock(SoftAskVoteRepository.class);
    softAskMapper = Mockito.mock(SoftAskMapper.class);

    user = SoftAskMother.createRegisteredUser();
    member = SoftAskMother.createIsAMember();
    softAskVoteDtoForAskUpvote = SoftAskMother.createSoftAskVoteDtoVoted();

    softAsk = SoftAskMother.createSoftAsk();
    softAskReply = SoftAskMother.createSoftAskReply();

    softAskVoteService = new SoftAskVoteServiceImpl(
      softAskOperationService,
      softAskReplySearchService,
      softAskSearchService,
      softAskVoteRepository,
      softAskMapper
    );
  }

  @Test
  @DisplayName("Vote soft ask and vote successfully")
  void vote_soft_ask_and_vote_successfully() {
    // parameters
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;

    SoftAskVote newVote = SoftAskMother.createSoftAskVoteForVoted();
    SoftAskVoteResponse expectedResponse = new SoftAskVoteResponse();
    expectedResponse.setParentTotalVotes(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES);

    // when
    when(softAskSearchService.findSoftAsk(softAskId)).thenReturn(softAsk);
    when(softAskVoteRepository.findByMemberAndSoftAsk(memberId, softAskId)).thenReturn(Optional.empty());
    when(softAskVoteRepository.save(any(SoftAskVote.class))).thenReturn(newVote);
    when(softAskOperationService.updateVoteCount(any(), eq(true))).thenReturn(1);
    when(softAskMapper.toSoftAskVoteResponse(any(SoftAskVote.class), eq(true))).thenReturn(expectedResponse);

    // act
    SoftAskVoteUpdateResponse response = softAskVoteService.vote(softAskVoteDtoForAskUpvote, user);

    // assert
    // verify
    assertNotNull(response);
    assertEquals(SoftAskFeatureTestConstant.SoftAskVoteDefaultTestConstants.ID_1, response.getVoteId());
    assertEquals(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES, response.getVoteResponse().getParentTotalVotes());

    verify(softAskVoteRepository, times(1)).findByMemberAndSoftAsk(memberId, softAskId);
    verify(softAskVoteRepository, times(1)).save(any(SoftAskVote.class));
    verify(softAskOperationService, times(1)).updateVoteCount(softAskId, true);
    verify(softAskOperationService, times(1)).updateVoteCount(eq(softAskId), eq(true));
    verify(softAskMapper, times(1)).toSoftAskVoteResponse(any(SoftAskVote.class), eq(true));
  }

}