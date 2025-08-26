package com.fleencorp.feen.like.mapper;

import com.fleencorp.feen.like.model.domain.Like;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.like.model.response.LikeResponse;

import java.util.Collection;

public interface LikeMapper {

  LikeResponse toLikeResponse(Like entry);

  Collection<LikeResponse> toLikeResponses(Collection<Like> entries);

  UserLikeInfo toLikeInfo(boolean isLiked);
}
