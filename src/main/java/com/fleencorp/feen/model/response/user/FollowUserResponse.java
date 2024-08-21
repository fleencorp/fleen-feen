package com.fleencorp.feen.model.response.user;

import com.fleencorp.feen.model.response.base.ApiResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FollowUserResponse extends ApiResponse {

  @Override
  public String getMessageKey() {
    return "follow.user";
  }

  public static FollowUserResponse of() {
    return new FollowUserResponse();
  }
}
