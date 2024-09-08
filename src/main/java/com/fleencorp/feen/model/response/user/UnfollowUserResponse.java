package com.fleencorp.feen.model.response.user;

import com.fleencorp.base.model.response.base.ApiResponse;
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
