package com.fleencorp.feen.like.service;

import com.fleencorp.feen.like.model.request.search.LikeSearchRequest;
import com.fleencorp.feen.like.model.search.LikeSearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface LikeSearchService {

  LikeSearchResult findLikes(LikeSearchRequest searchRequest, RegisteredUser user);
}
