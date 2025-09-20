package com.fleencorp.feen.bookmark.service.impl;

import com.fleencorp.feen.bookmark.service.BookmarkExternalOperationService;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceService;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.service.ReviewOperationService;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class BookmarkExternalOperationServiceImpl implements BookmarkExternalOperationService {

  private final ChatSpaceService chatSpaceService;
  private final ReviewOperationService reviewOperationService;
  private final SoftAskOperationService softAskOperationService;
  private final StreamOperationsService streamOperationsService;
  private final PollOperationsService pollOperationsService;

  public BookmarkExternalOperationServiceImpl(
      final ChatSpaceService chatSpaceService,
      @Lazy final PollOperationsService pollOperationsService,
      final ReviewOperationService reviewOperationService,
      final SoftAskOperationService softAskOperationService,
      @Lazy final StreamOperationsService streamOperationsService) {
    this.chatSpaceService = chatSpaceService;
    this.reviewOperationService = reviewOperationService;
    this.softAskOperationService = softAskOperationService;
    this.streamOperationsService = streamOperationsService;
    this.pollOperationsService = pollOperationsService;
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
  public SoftAsk findSoftAskById(Long softAskId) {
    return softAskOperationService.findSoftAsk(softAskId);
  }

  @Override
  public SoftAskReply findSoftAskReply(Long softAskId, Long softAskReplyId) {
    return softAskOperationService.findSoftAskReply(softAskReplyId, softAskId);
  }

  @Override
  public FleenStream findStreamById(Long streamId) {
    return streamOperationsService.findStream(streamId);
  }

  @Override
  public Integer updateChatSpaceBookmarkCount(final Long chatSpaceId, final boolean bookmarked) {
    return chatSpaceService.updateBookmarkCount(chatSpaceId, bookmarked);
  }

  @Override
  public Integer updateReviewBookmarkCount(final Long chatSpaceId, final boolean bookmarked) {
    return reviewOperationService.updateBookmarkCount(chatSpaceId, bookmarked);
  }

  @Override
  public Integer updatePollBookmarkCount(final Long chatSpaceId, final boolean bookmarked) {
    return pollOperationsService.updateBookmarkCount(chatSpaceId, bookmarked);
  }

  @Override
  public Integer updateSoftAskBookmarkCount(final Long softAskId, final boolean bookmarked) {
    return softAskOperationService.updateBookmarkCount(softAskId, bookmarked);
  }

  @Override
  public Integer updateSoftAskReplyBookmarkCount(final Long softAskId, final Long softAskReplyId, final boolean bookmarked) {
    return softAskOperationService.updateBookmarkCount(softAskId, softAskReplyId, bookmarked);
  }

  @Override
  public Integer updateStreamBookmarkCount(final Long streamId, final boolean bookmarked) {
    return streamOperationsService.updateBookmarkCount(streamId, bookmarked);
  }
}
