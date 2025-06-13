package com.fleencorp.feen.like.service;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.model.contract.Likeable;
import com.fleencorp.feen.like.model.dto.LikeDto;
import com.fleencorp.feen.like.model.response.LikeResponse;
import com.fleencorp.feen.review.model.response.ReviewResponse;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;

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
