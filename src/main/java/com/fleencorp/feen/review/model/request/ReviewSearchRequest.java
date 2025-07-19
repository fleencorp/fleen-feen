package com.fleencorp.feen.review.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.review.constant.ReviewParentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ReviewSearchRequest extends SearchRequest {

  @ToUpperCase
  @JsonProperty("parent_type")
  protected String reviewParentType;

  @IsNumber
  @JsonProperty("parent_id")
  protected String parentId;

  public ReviewParentType getReviewParentType() {
    return ReviewParentType.of(reviewParentType);
  }

  public Long getParentId() {
    return nonNull(parentId) ? Long.parseLong(parentId) : 0L;
  }

  public boolean isStreamReviewSearchRequest() {
    return ReviewParentType.isStream(getReviewParentType());
  }
}
