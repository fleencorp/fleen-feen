package com.fleencorp.feen.model.response.review;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.constant.review.ReviewType;
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
public class ReviewResponse extends FleenFeenResponse {

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
  private Long getParentId() {
    return ReviewType.isStream(reviewType) ? streamId : null;
  }

  @JsonProperty("parent_title")
  private String getParentTitle() {
    return ReviewType.isStream(reviewType) ? streamTitle : null;
  }

  @JsonIgnore
  private Long streamId;

  @JsonIgnore
  private String streamTitle;

  @JsonIgnore
  private Long memberId;

  public void markAsUpdatable() {
    this.isUpdatable = true;
  }
}
