package com.fleencorp.feen.follower.service;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.model.contract.UserFollowStat;
import com.fleencorp.feen.follower.model.dto.FollowOrUnfollowUserDto;
import com.fleencorp.feen.follower.model.response.FollowUserResponse;
import com.fleencorp.feen.follower.model.response.UnfollowUserResponse;
import com.fleencorp.feen.follower.model.search.FollowerSearchResult;
import com.fleencorp.feen.follower.model.search.FollowingSearchResult;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface FollowerService {

  FollowUserResponse followUser(FollowOrUnfollowUserDto followUserDto, RegisteredUser follower);

  UnfollowUserResponse unfollowUser(FollowOrUnfollowUserDto unfollowUserDto, RegisteredUser follower);

  FollowerSearchResult getFollowers(SearchRequest searchRequest, RegisteredUser followed);

  FollowingSearchResult getFollowings(SearchRequest searchRequest, RegisteredUser user);

  void setFollowerDetails(Member targetMember, UserFollowStat userFollowStat);
}
