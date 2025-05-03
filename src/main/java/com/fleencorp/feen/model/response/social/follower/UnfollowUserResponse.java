package com.fleencorp.feen.model.response.social.follower;

import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnfollowUserResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "unfollow.user";
  }

  public static UnfollowUserResponse of() {
    return new UnfollowUserResponse();
  }
}
