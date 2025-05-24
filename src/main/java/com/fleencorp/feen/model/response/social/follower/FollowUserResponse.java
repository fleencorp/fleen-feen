package com.fleencorp.feen.model.response.social.follower;

import com.fleencorp.feen.model.info.user.profile.IsFollowingInfo;
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
