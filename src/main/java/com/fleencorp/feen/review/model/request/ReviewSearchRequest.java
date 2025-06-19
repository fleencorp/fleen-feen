package com.fleencorp.feen.review.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.review.constant.ReviewParentType;
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
  @JsonProperty("review_parent_type")
  protected String reviewParentType;

  @IsNumber
  @JsonProperty("parent_id")
  protected String parentId;

  public ReviewParentType getReviewParentType() {
    return ReviewParentType.of(reviewParentType);
  }

  public Long getParentId() {
    if (isValidNumber(parentId)) {
      return Long.parseLong(parentId);
    }

    throw FailedOperationException.of();
  }

  public boolean isStreamReviewSearchRequest() {
    return ReviewParentType.isStream(getReviewParentType());
  }
}
