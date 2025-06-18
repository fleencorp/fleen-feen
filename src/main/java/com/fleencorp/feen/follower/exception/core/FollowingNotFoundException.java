package com.fleencorp.feen.follower.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class FollowingNotFoundException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "following.not.found";
  }
}
