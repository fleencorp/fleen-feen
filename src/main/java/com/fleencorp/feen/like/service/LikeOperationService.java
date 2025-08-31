package com.fleencorp.feen.like.service;

import com.fleencorp.feen.model.contract.Likeable;
import com.fleencorp.feen.user.model.domain.Member;

import java.util.Collection;

public interface LikeOperationService {

  <T extends Likeable> void populateChatSpaceLikesFor(Collection<T> responses, Member member);

  <T extends Likeable> void populateStreamLikesFor(Collection<T> responses, Member member);

  <T extends Likeable> void populateLikesForReviews(Collection<T> reviewResponses, Member member);
}
