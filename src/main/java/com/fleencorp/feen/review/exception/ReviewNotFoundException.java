package com.fleencorp.feen.review.exception;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class ReviewNotFoundException extends LocalizedException {

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
