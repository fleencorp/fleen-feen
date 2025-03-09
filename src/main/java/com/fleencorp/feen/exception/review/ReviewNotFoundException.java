package com.fleencorp.feen.exception.review;

import com.fleencorp.localizer.model.exception.ApiException;

import java.util.function.Supplier;

public class ReviewNotFoundException extends ApiException {

  @Override
  public String getMessageCode() {
    return "review.not.found";
  }

  public ReviewNotFoundException(final Object...params) {
    super(params);
  }

  public static Supplier<ReviewNotFoundException> of(final Object reviewId) {
    return () -> new ReviewNotFoundException(reviewId);
  }
}
