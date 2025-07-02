package com.fleencorp.feen.constant.interaction;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum ReviewCount implements ApiParameter {

  TOTAL_REVIEWS("total.review.entries");

  private final String value;

  ReviewCount(final String value) {
    this.value = value;
  }

  public String getMessageCode() {
    return value;
  }

  public static ReviewCount totalReviews() {
    return TOTAL_REVIEWS;
  }
}
