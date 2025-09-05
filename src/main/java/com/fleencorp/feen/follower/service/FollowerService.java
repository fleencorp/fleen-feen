package com.fleencorp.feen.follower.service;

import com.fleencorp.feen.follower.model.dto.FollowOrUnfollowUserDto;
import com.fleencorp.feen.follower.model.request.FollowerSearchRequest;
import com.fleencorp.feen.follower.model.response.FollowUserResponse;
import com.fleencorp.feen.follower.model.response.UnfollowUserResponse;
import com.fleencorp.feen.follower.model.search.FollowerSearchResult;
import com.fleencorp.feen.follower.model.search.FollowingSearchResult;
import com.fleencorp.feen.model.contract.UserFollowStat;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.model.domain.Member;

public interface FollowerService {

  FollowUserResponse followUser(FollowOrUnfollowUserDto followUserDto, RegisteredUser follower);

  UnfollowUserResponse unfollowUser(FollowOrUnfollowUserDto unfollowUserDto, RegisteredUser follower);

  FollowerSearchResult getFollowers(FollowerSearchRequest searchRequest);

  FollowingSearchResult getFollowings(FollowerSearchRequest searchRequest);

  void setFollowerDetails(Member targetMember, UserFollowStat userFollowStat);
}
