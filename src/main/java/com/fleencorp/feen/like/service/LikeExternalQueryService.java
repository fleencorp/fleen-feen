package com.fleencorp.feen.like.service;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.stream.model.domain.FleenStream;

public interface LikeExternalQueryService {
  ChatSpace findChatSpaceById(Long chatSpaceId);

  Poll findPollById(Long pollId);

  Review findReviewById(Long reviewId);

  FleenStream findStreamById(Long streamId);

  Integer updateChatSpaceLikeCount(Long chatSpaceId, boolean isLiked);

  Integer updatePollLikeCount(Long pollId, boolean isLiked);

  Integer updateReviewLikeCount(Long reviewId, boolean isLiked);

  Integer updateStreamLikeCount(Long streamId, boolean isLiked);
}
