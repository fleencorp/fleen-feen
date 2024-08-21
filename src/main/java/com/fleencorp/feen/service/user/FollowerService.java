package com.fleencorp.feen.service.user;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.model.response.user.FollowUserResponse;
import com.fleencorp.feen.model.response.user.FollowersResponse;
import com.fleencorp.feen.model.response.user.FollowingsResponse;
import com.fleencorp.feen.model.response.user.UnfollowUserResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface FollowerService {

  FollowUserResponse followUser(Long userId, FleenUser follower);

  UnfollowUserResponse unfollowUser(Long userId, FleenUser follower);

  FollowersResponse getFollowers(FleenUser followed, SearchRequest searchRequest);

  FollowingsResponse getUsersFollowing(FleenUser follower, SearchRequest searchRequest);
}
