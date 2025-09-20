package com.fleencorp.feen.softask.service.vote;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
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

import static org.junit.jupiter.api.Assertions.*;
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
  private SoftAskVoteDto softAskVoteDtoForAskUnvote;
  private SoftAskVoteDto softAskVoteDtoForReplyUpvote;
  private SoftAskVoteDto softAskVoteDtoForReplyUnvote;


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
    softAskVoteDtoForAskUnvote = SoftAskMother.createSoftAskVoteDtoNotVoted();
    softAskVoteDtoForReplyUpvote = SoftAskMother.createSoftAskReplyVoteDtoVoted();
    softAskVoteDtoForReplyUnvote = SoftAskMother.createSoftAskReplyVoteDtoNotVoted();

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
  @DisplayName("Vote soft successfully")
  void vote_soft_ask_successfully() {
    // parameters
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;

    SoftAskVote newVote = SoftAskMother.createSoftAskVoteForVoted();
    SoftAskVoteResponse expectedResponse = new SoftAskVoteResponse();
    expectedResponse.setParentTotalVotes(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES_1);

    // when
    when(softAskSearchService.findSoftAsk(softAskId)).thenReturn(softAsk);
    when(softAskVoteRepository.findByMemberAndSoftAsk(memberId, softAskId)).thenReturn(Optional.empty());
    when(softAskVoteRepository.save(any(SoftAskVote.class))).thenReturn(newVote);
    when(softAskOperationService.updateVoteCount(any(), eq(true))).thenReturn(1);
    when(softAskMapper.toSoftAskVoteResponse(any(SoftAskVote.class), eq(true))).thenReturn(expectedResponse);

    // act
    SoftAskVoteUpdateResponse response = softAskVoteService.vote(softAskVoteDtoForAskUpvote, user);

    // assert
    assertNotNull(response);
    assertEquals(SoftAskFeatureTestConstant.SoftAskVoteDefaultTestConstants.ID_1, response.getVoteId());
    assertEquals(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES_1, response.getVoteResponse().getParentTotalVotes());

    // verify
    verify(softAskVoteRepository, times(1)).findByMemberAndSoftAsk(memberId, softAskId);
    verify(softAskVoteRepository, times(1)).save(any(SoftAskVote.class));
    verify(softAskOperationService, times(1)).updateVoteCount(softAskId, true);
    verify(softAskOperationService, times(1)).updateVoteCount(eq(softAskId), eq(true));
    verify(softAskMapper, times(1)).toSoftAskVoteResponse(any(SoftAskVote.class), eq(true));
  }

  @Test
  @DisplayName("Unvote soft ask successfully")
  void unvote_soft_ask_successfully() {
    // parameters
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;

    SoftAskVote existingVote = SoftAskMother.createSoftAskVoteForVoted();
    SoftAskVote updatedVote = SoftAskMother.createSoftAskVoteForNotVoted();
    SoftAskVoteResponse expectedResponse = new SoftAskVoteResponse();
    expectedResponse.setParentTotalVotes(0);

    // when
    when(softAskSearchService.findSoftAsk(softAskId)).thenReturn(softAsk);
    when(softAskVoteRepository.findByMemberAndSoftAsk(memberId, softAskId)).thenReturn(Optional.of(existingVote));
    when(softAskVoteRepository.save(any(SoftAskVote.class))).thenReturn(updatedVote);
    when(softAskOperationService.updateVoteCount(any(), eq(false))).thenReturn(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES_0);
    when(softAskMapper.toSoftAskVoteResponse(any(SoftAskVote.class), eq(false))).thenReturn(expectedResponse);

    // act
    SoftAskVoteUpdateResponse response = softAskVoteService.vote(softAskVoteDtoForAskUnvote, user);

    // assert
    assertNotNull(response);
    assertEquals(SoftAskFeatureTestConstant.SoftAskVoteDefaultTestConstants.ID_1, response.getVoteId());
    assertEquals(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES_0, response.getVoteResponse().getParentTotalVotes());

    // verify
    verify(softAskVoteRepository, times(1)).findByMemberAndSoftAsk(memberId, softAskId);
    verify(softAskVoteRepository, times(1)).save(any(SoftAskVote.class));
    verify(softAskOperationService, times(1)).updateVoteCount(softAskId, false);
    verify(softAskMapper, times(1)).toSoftAskVoteResponse(any(SoftAskVote.class), eq(false));
  }

  @Test
  @DisplayName("Vote soft ask reply and vote successfully")
  void vote_soft_ask_reply_and_vote_successfully() {
    // parameters
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;
    Long softAskReplyId = SoftAskFeatureTestConstant.SoftAskReplyDefaultTestConstants.ID_1;

    SoftAskVote newVote = SoftAskMother.createSoftAskReplyVoteForVoted();
    SoftAskVoteResponse expectedResponse = new SoftAskVoteResponse();
    expectedResponse.setParentTotalVotes(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES_1);

    // when
    when(softAskSearchService.findSoftAsk(softAskId)).thenReturn(softAsk);
    when(softAskReplySearchService.findSoftAskReply(softAskId, softAskReplyId)).thenReturn(softAskReply);
    when(softAskVoteRepository.findByMemberAndSoftAskAndSoftAskReply(memberId, softAskId, softAskReplyId)).thenReturn(Optional.empty());
    when(softAskVoteRepository.save(any(SoftAskVote.class))).thenReturn(newVote);
    when(softAskOperationService.updateVoteCount(anyLong(), anyLong(), eq(true))).thenReturn(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES_1);
    when(softAskMapper.toSoftAskVoteResponse(any(SoftAskVote.class), eq(true))).thenReturn(expectedResponse);

    // act
    SoftAskVoteUpdateResponse response = softAskVoteService.vote(softAskVoteDtoForReplyUpvote, user);

    // assert
    assertNotNull(response);
    assertEquals(SoftAskFeatureTestConstant.SoftAskVoteDefaultTestConstants.ID_1, response.getVoteId());
    assertEquals(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES_1, response.getVoteResponse().getParentTotalVotes());

    // verify
    verify(softAskVoteRepository, times(1)).findByMemberAndSoftAskAndSoftAskReply(memberId, softAskId, softAskReplyId);
    verify(softAskVoteRepository, times(1)).save(any(SoftAskVote.class));
    verify(softAskOperationService, times(1)).updateVoteCount(softAskId, softAskReplyId, true);
    verify(softAskMapper, times(1)).toSoftAskVoteResponse(any(SoftAskVote.class), eq(true));
  }

  @Test
  @DisplayName("Unvote soft ask reply successfully")
  void unvote_soft_ask_reply_successfully() {
    // parameters
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;
    Long softAskReplyId = SoftAskFeatureTestConstant.SoftAskReplyDefaultTestConstants.ID_1;

    SoftAskVote existingVote = SoftAskMother.createSoftAskReplyVoteForVoted();
    SoftAskVote updatedVote = SoftAskMother.createSoftAskReplyVoteForVoted();
    SoftAskVoteResponse expectedResponse = new SoftAskVoteResponse();
    expectedResponse.setParentTotalVotes(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES_0);

    // when
    when(softAskSearchService.findSoftAsk(softAskId)).thenReturn(softAsk);
    when(softAskReplySearchService.findSoftAskReply(softAskId, softAskReplyId)).thenReturn(softAskReply);
    when(softAskVoteRepository.findByMemberAndSoftAskAndSoftAskReply(memberId, softAskId, softAskReplyId)).thenReturn(Optional.of(existingVote));
    when(softAskVoteRepository.save(any(SoftAskVote.class))).thenReturn(updatedVote);
    when(softAskOperationService.updateVoteCount(anyLong(), anyLong(), eq(false))).thenReturn(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES_0);
    when(softAskMapper.toSoftAskVoteResponse(any(SoftAskVote.class), eq(false))).thenReturn(expectedResponse);

    // act
    SoftAskVoteUpdateResponse response = softAskVoteService.vote(softAskVoteDtoForReplyUnvote, user);

    // assert
    assertNotNull(response);
    assertEquals(SoftAskFeatureTestConstant.SoftAskVoteDefaultTestConstants.ID_1, response.getVoteId());
    assertEquals(SoftAskFeatureTestConstant.OtherTestConstants.PARENT_TOTAL_VOTES_0, response.getVoteResponse().getParentTotalVotes());

    // verify
    verify(softAskVoteRepository, times(1)).findByMemberAndSoftAskAndSoftAskReply(memberId, softAskId, softAskReplyId);
    verify(softAskVoteRepository, times(1)).save(any(SoftAskVote.class));
    verify(softAskOperationService, times(1)).updateVoteCount(softAskId, softAskReplyId, false);
    verify(softAskMapper, times(1)).toSoftAskVoteResponse(any(SoftAskVote.class), eq(false));
  }

  @Test
  @DisplayName("Vote on an ask fails if soft ask not found")
  void vote_ask_fails_if_soft_ask_not_found() {
    // parameters
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;
    Long softAskReplyId = SoftAskFeatureTestConstant.SoftAskReplyDefaultTestConstants.ID_1;

    // when
    when(softAskSearchService.findSoftAsk(softAskId)).thenThrow(new SoftAskNotFoundException(softAskId));

    // act & assert
    assertThrows(SoftAskNotFoundException.class, () -> softAskVoteService.vote(softAskVoteDtoForAskUpvote, user));

    // verify
    verify(softAskSearchService, never()).findSoftAsk(softAskId);
    verify(softAskReplySearchService, never()).findSoftAskReply(softAskId, softAskReplyId);
    verifyNoInteractions(softAskVoteRepository, softAskOperationService, softAskMapper);
  }

  @Test
  @DisplayName("Vote on a reply fails if soft ask reply not found")
  void vote_reply_fails_if_soft_ask_reply_not_found() {
    // parameters
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;
    Long softAskReplyId = SoftAskFeatureTestConstant.SoftAskReplyDefaultTestConstants.ID_1;

    // when
    when(softAskReplySearchService.findSoftAskReply(softAskId, softAskReplyId)).thenThrow(new SoftAskReplyNotFoundException(softAskReplyId));

    // act & assert
    assertThrows(SoftAskReplyNotFoundException.class, () -> softAskVoteService.vote(softAskVoteDtoForReplyUpvote, user));

    // verify
    verify(softAskSearchService, times(0)).findSoftAsk(softAskId);
    verify(softAskReplySearchService, times(1)).findSoftAskReply(softAskId, softAskReplyId);
    verifyNoInteractions(softAskVoteRepository, softAskOperationService, softAskMapper);
  }

  @Test
  @DisplayName("Attempt to vote with same vote type on existing vote should throw exception")
  void vote_with_same_type_on_existing_vote_throws_exception() {
    // parameters
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;

    SoftAskVote existingVote = SoftAskMother.createSoftAskVoteForVoted();

    // when
    when(softAskSearchService.findSoftAsk(softAskId)).thenReturn(softAsk);
    when(softAskVoteRepository.findByMemberAndSoftAsk(memberId, softAskId)).thenReturn(Optional.of(existingVote));

    // act & assert
    assertThrows(SoftAskUpdateDeniedException.class, () -> softAskVoteService.vote(softAskVoteDtoForAskUpvote, user));

    // verify
    verify(softAskVoteRepository, times(1)).findByMemberAndSoftAsk(memberId, softAskId);
    verifyNoMoreInteractions(softAskVoteRepository);
    verifyNoInteractions(softAskOperationService, softAskMapper);
  }

  @Test
  @DisplayName("Attempt to unvote a non-existent vote throws exception")
  void unvote_non_existent_vote_throws_exception() {
    // parameters
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;

    // when
    when(softAskSearchService.findSoftAsk(softAskId)).thenReturn(softAsk);
    when(softAskVoteRepository.findByMemberAndSoftAsk(memberId, softAskId)).thenReturn(Optional.empty());

    // act & assert
    assertThrows(SoftAskUpdateDeniedException.class, () -> softAskVoteService.vote(softAskVoteDtoForAskUnvote, user));

    // verify
    verify(softAskVoteRepository, times(1)).findByMemberAndSoftAsk(memberId, softAskId);
    verifyNoMoreInteractions(softAskVoteRepository);
    verifyNoInteractions(softAskOperationService, softAskMapper);
  }

  @Test
  @DisplayName("Finding SoftAskVoteParentDetailsHolder fails if parentType is null")
  void findSoftAskVoteParentDetailsHolder_fails_if_parentType_is_null() {
    // parameters
    SoftAskVoteDto dtoWithNullParentType = SoftAskMother.createSoftAskVoteDtoNoParentType();

    // act & assert
    assertThrows(FailedOperationException.class, () -> softAskVoteService.vote(dtoWithNullParentType, user));

    // verify
    verifyNoInteractions(softAskSearchService, softAskReplySearchService, softAskVoteRepository, softAskOperationService, softAskMapper);
  }



}