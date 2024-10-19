package com.fleencorp.feen.service.user;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.model.dto.social.follow.FollowOrUnfollowUserDto;
import com.fleencorp.feen.model.response.user.FollowUserResponse;
import com.fleencorp.feen.model.response.user.UnfollowUserResponse;
import com.fleencorp.feen.model.search.social.follower.follower.FollowerSearchResult;
import com.fleencorp.feen.model.search.social.follower.following.FollowingSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface FollowerService {

  FollowUserResponse followUser(FollowOrUnfollowUserDto followUserDto, FleenUser follower);

  UnfollowUserResponse unfollowUser(FollowOrUnfollowUserDto unfollowUserDto, FleenUser follower);

  FollowerSearchResult getFollowers(SearchRequest searchRequest, FleenUser followed);

  FollowingSearchResult getFollowings(SearchRequest searchRequest, FleenUser user);
}
