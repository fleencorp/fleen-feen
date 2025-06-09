package com.fleencorp.feen.service.social;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.model.contract.UserFollowStat;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.model.dto.social.follow.FollowOrUnfollowUserDto;
import com.fleencorp.feen.model.response.social.follower.FollowUserResponse;
import com.fleencorp.feen.model.response.social.follower.UnfollowUserResponse;
import com.fleencorp.feen.model.search.social.follower.follower.FollowerSearchResult;
import com.fleencorp.feen.model.search.social.follower.following.FollowingSearchResult;
import com.fleencorp.feen.user.security.RegisteredUser;

public interface FollowerService {

  FollowUserResponse followUser(FollowOrUnfollowUserDto followUserDto, RegisteredUser follower);

  UnfollowUserResponse unfollowUser(FollowOrUnfollowUserDto unfollowUserDto, RegisteredUser follower);

  FollowerSearchResult getFollowers(SearchRequest searchRequest, RegisteredUser followed);

  FollowingSearchResult getFollowings(SearchRequest searchRequest, RegisteredUser user);

  void setFollowerDetails(Member targetMember, UserFollowStat userFollowStat);
}
