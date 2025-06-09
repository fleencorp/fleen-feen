package com.fleencorp.feen.service.like;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.model.contract.Likeable;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.model.dto.like.LikeDto;
import com.fleencorp.feen.model.response.like.LikeResponse;
import com.fleencorp.feen.model.response.review.ReviewResponse;
import com.fleencorp.feen.user.security.RegisteredUser;

import java.util.Collection;
import java.util.Map;

public interface LikeService {

  LikeResponse like(LikeDto likeDto, RegisteredUser user) throws StreamNotFoundException, ChatSpaceNotFoundException, FailedOperationException;

  <T extends Likeable> void populateChatSpaceLikesForNonMembership(Collection<T> responses, Map<Long, ?> membershipMap, Member member);

  <T extends Likeable> void populateChatSpaceLikesForMembership(Collection<T> responses, Map<Long, ?> membershipMap, Member member);

  <T extends Likeable> void populateStreamLikesForNonAttendance(Collection<T> responses, Map<Long, ?> membershipMap, Member member);

  <T extends Likeable> void populateStreamLikesForAttendance(Collection<T> responses, Map<Long, ?> membershipMap, Member member);

  void populateLikesForReviews(Collection<ReviewResponse> reviewResponses, Member member);
}
