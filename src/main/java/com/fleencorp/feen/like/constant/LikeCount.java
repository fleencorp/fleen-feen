package com.fleencorp.feen.like.constant;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum LikeCount implements ApiParameter {

  TOTAL_LIKES("total.like.entries");

  private final String value;

  LikeCount(final String value) {
    this.value = value;
  }

  public String getMessageCode() {
    return value;
  }

  public static LikeCount totalLikes() {
    return TOTAL_LIKES;
  }
}
