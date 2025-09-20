package com.fleencorp.feen.bookmark.service;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.stream.model.domain.FleenStream;

public interface BookmarkExternalOperationService {


  ChatSpace findChatSpaceById(Long chatSpaceId);

  Poll findPollById(Long pollId);

  Review findReviewById(Long reviewId);

  SoftAsk findSoftAskById(Long softAskId);

  SoftAskReply findSoftAskReply(Long softAskId, Long softAskReplyId);

  FleenStream findStreamById(Long streamId);

  Integer updateChatSpaceBookmarkCount(Long chatSpaceId, boolean bookmarked);

  Integer updateReviewBookmarkCount(Long chatSpaceId, boolean bookmarked);

  Integer updatePollBookmarkCount(Long chatSpaceId, boolean bookmarked);

  Integer updateSoftAskBookmarkCount(Long softAskId, boolean bookmarked);

  Integer updateSoftAskReplyBookmarkCount(Long softAskId, Long softAskReplyId, boolean bookmarked);

  Integer updateStreamBookmarkCount(Long streamId, boolean bookmarked);
}
