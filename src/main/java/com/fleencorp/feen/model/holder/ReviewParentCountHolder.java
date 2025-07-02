package com.fleencorp.feen.model.holder;

import com.fleencorp.feen.model.projection.stream.review.ReviewParentCount;

import java.util.Collection;
import java.util.Objects;

public record ReviewParentCountHolder(Collection<ReviewParentCount> reviewParentCounts) {

  public int countOf(final Long parentId) {
    return reviewParentCounts.stream()
      .filter(c -> Objects.equals(c.getParentId(), parentId))
      .map(ReviewParentCount::getCount)
      .findFirst()
      .orElse(0);
  }

  public static ReviewParentCountHolder of(final Collection<ReviewParentCount> reviewParentCounts) {
    return new ReviewParentCountHolder(reviewParentCounts);
  }
}
