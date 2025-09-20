package com.fleencorp.feen.like.service.impl;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceService;
import com.fleencorp.feen.like.service.LikeExternalQueryService;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.service.ReviewOperationService;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class LikeExternalQueryServiceImpl implements LikeExternalQueryService {

  private final ChatSpaceService chatSpaceService;
  private final PollOperationsService pollOperationsService;
  private final ReviewOperationService reviewOperationService;
  private final StreamOperationsService streamOperationsService;

  public LikeExternalQueryServiceImpl(
      final ChatSpaceService chatSpaceService,
      final PollOperationsService pollOperationsService,
      final ReviewOperationService reviewOperationService,
      @Lazy final StreamOperationsService streamOperationsService) {
    this.chatSpaceService = chatSpaceService;
    this.pollOperationsService = pollOperationsService;
    this.reviewOperationService = reviewOperationService;
    this.streamOperationsService = streamOperationsService;
  }

  @Override
  public ChatSpace findChatSpaceById(Long chatSpaceId) {
    return chatSpaceService.findChatSpace(chatSpaceId);
  }

  @Override
  public Poll findPollById(Long pollId) {
    return pollOperationsService.findPoll(pollId);
  }

  @Override
  public Review findReviewById(Long reviewId) {
    return reviewOperationService.findReview(reviewId);
  }


  @Override
  public FleenStream findStreamById(Long streamId) {
    return streamOperationsService.findStream(streamId);
  }

  @Override
  public Integer updateChatSpaceLikeCount(Long chatSpaceId, boolean isLiked) {
    return chatSpaceService.updateLikeCount(chatSpaceId, isLiked);
  }

  @Override
  public Integer updatePollLikeCount(Long pollId, boolean isLiked) {
    return pollOperationsService.updateLikeCount(pollId, isLiked);
  }

  @Override
  public Integer updateReviewLikeCount(Long reviewId, boolean isLiked) {
    return reviewOperationService.updateLikeCount(reviewId, isLiked);
  }

  @Override
  public Integer updateStreamLikeCount(Long streamId, boolean isLiked) {
    return streamOperationsService.updateLikeCount(streamId, isLiked);
  }

}
