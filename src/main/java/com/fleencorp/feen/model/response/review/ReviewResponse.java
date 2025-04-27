package com.fleencorp.feen.model.response.review;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.constant.review.ReviewType;
import com.fleencorp.feen.model.contract.SetIsUpdatable;
import com.fleencorp.feen.model.info.stream.rating.RatingInfo;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "review",
  "review_type",
  "reviewer_name",
  "reviewer_photo",
  "rating_info",
  "parent_id",
  "parent_title",
  "is_updatable"
})
public class ReviewResponse extends FleenFeenResponse
    implements SetIsUpdatable {

  @JsonProperty("review")
  private String review;

  @JsonFormat(shape = STRING)
  @JsonProperty("review_type")
  private ReviewType reviewType;

  @JsonProperty("rating_info")
  private RatingInfo ratingInfo;

  @JsonProperty("reviewer_name")
  private String reviewerName;

  @JsonProperty("reviewer_photo")
  private String reviewerPhoto;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @JsonProperty("parent_id")
  private Long parentId;

  @JsonProperty("parent_title")
  private String parentTitle;

  @JsonIgnore
  private Long memberId;

  @Override
  public void setIsUpdatable(final boolean isUpdatable) {
    this.isUpdatable = isUpdatable;
  }

  @Override
  public void markAsUpdatable() {
    setIsUpdatable(true);
  }
}
