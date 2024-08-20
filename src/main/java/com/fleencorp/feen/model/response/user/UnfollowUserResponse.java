package com.fleencorp.feen.model.response.user;

import com.fleencorp.feen.model.response.base.ApiResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnfollowUserResponse extends ApiResponse {

  @Override
  public String getMessageKey() {
    return "unfollow.user";
  }

  public static UnfollowUserResponse of() {
    return new UnfollowUserResponse();
  }
}
