package com.fleencorp.feen.follower.model.response;

import com.fleencorp.feen.follower.model.info.IsFollowingInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowUserResponse extends FollowResponse {

  public FollowUserResponse(final IsFollowingInfo isFollowingInfo) {
    super(isFollowingInfo, null, null);
  }

  @Override
  public String getMessageCode() {
    return "follow.user";
  }

  public static FollowUserResponse of(final IsFollowingInfo isFollowingInfo) {
    return new FollowUserResponse(isFollowingInfo);
  }
}
