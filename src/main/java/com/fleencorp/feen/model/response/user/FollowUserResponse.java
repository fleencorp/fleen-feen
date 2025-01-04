package com.fleencorp.feen.model.response.user;

import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FollowUserResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "follow.user";
  }

  public static FollowUserResponse of() {
    return new FollowUserResponse();
  }
}
