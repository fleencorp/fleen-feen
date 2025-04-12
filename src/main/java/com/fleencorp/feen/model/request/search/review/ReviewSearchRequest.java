package com.fleencorp.feen.model.request.search.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.constant.review.ReviewType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.base.util.FleenUtil.isValidNumber;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ReviewSearchRequest extends SearchRequest {

  @ToUpperCase
  @JsonProperty("review_type")
  protected String reviewType;

  @IsNumber
  @JsonProperty("parent_id")
  protected String parentId;

  public ReviewType getReviewType() {
    return ReviewType.of(reviewType);
  }

  public Long getParentId() {
    if (isValidNumber(parentId)) {
      return Long.parseLong(parentId);
    }

    throw FailedOperationException.of();
  }

  public boolean isStreamReviewSearchRequest() {
    return ReviewType.isStream(getReviewType());
  }
}
