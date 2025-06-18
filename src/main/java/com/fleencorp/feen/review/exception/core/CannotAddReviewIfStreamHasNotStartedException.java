package com.fleencorp.feen.review.exception.core;

import com.fleencorp.localizer.model.exception.LocalizedException;

public class CannotAddReviewIfStreamHasNotStartedException extends LocalizedException {

  @Override
  public String getMessageCode() {
    return "cannot.add.review.if.stream.has.not.started";
  }

  public CannotAddReviewIfStreamHasNotStartedException(final Object...params) {
    super(params);
  }

  public static CannotAddReviewIfStreamHasNotStartedException of() {
    return new CannotAddReviewIfStreamHasNotStartedException();
  }
}
