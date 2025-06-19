package com.fleencorp.feen.follower.mapper;

import com.fleencorp.feen.follower.model.domain.Follower;
import com.fleencorp.feen.user.model.response.UserResponse;

import java.util.List;

public interface FollowerMapper {

  List<UserResponse> toFollowerResponses(List<Follower> entries);

  List<UserResponse> toFollowingResponses(List<Follower> entries);
}
