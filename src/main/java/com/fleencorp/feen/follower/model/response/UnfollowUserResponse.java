package com.fleencorp.feen.follower.model.response;

import com.fleencorp.feen.follower.model.info.IsFollowingInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnfollowUserResponse extends FollowResponse {

  public UnfollowUserResponse(final IsFollowingInfo isFollowingInfo) {
    super(isFollowingInfo, null, null);
  }

  @Override
  public String getMessageCode() {
    return "unfollow.user";
  }

  public static UnfollowUserResponse of(final IsFollowingInfo isFollowingInfo) {
    return new UnfollowUserResponse(isFollowingInfo);
  }
}
