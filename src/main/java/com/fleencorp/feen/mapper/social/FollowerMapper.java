package com.fleencorp.feen.mapper.social;

import com.fleencorp.feen.model.domain.user.Follower;
import com.fleencorp.feen.model.response.user.UserResponse;

import java.util.List;

public interface FollowerMapper {

  List<UserResponse> toFollowerResponses(List<Follower> entries);

  List<UserResponse> toFollowingResponses(List<Follower> entries);
}
