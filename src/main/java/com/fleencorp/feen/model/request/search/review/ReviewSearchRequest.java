package com.fleencorp.feen.model.request.search.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.util.FleenUtil;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.constant.review.ReviewType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ReviewSearchRequest extends SearchRequest {

  @JsonProperty("review_type")
  @ToUpperCase
  protected String reviewType;

  @JsonProperty("parent_id")
  @IsNumber
  protected String parentId;

  public ReviewType getReviewType() {
    return ReviewType.of(reviewType);
  }

  public Long getParentId() {
    if (FleenUtil.isValidNumber(parentId)) {
      return Long.parseLong(parentId);
    }
    return null;
  }

  public boolean isStreamReviewSearchRequest() {
    return ReviewType.isStream(getReviewType());
  }
}
