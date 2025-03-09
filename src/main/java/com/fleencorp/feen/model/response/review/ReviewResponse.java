package com.fleencorp.feen.model.response.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.review.ReviewType;
import com.fleencorp.feen.model.info.stream.rating.StreamRatingInfo;
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
  "reviewer_name",
  "reviewer_photo",
  "rating_info",
  "stream_id",
  "stream_title",
})
public class ReviewResponse extends FleenFeenResponse {

  @JsonProperty("review")
  private String review;

  @JsonFormat(shape = STRING)
  @JsonProperty("review_type")
  private ReviewType reviewType;

  @JsonProperty("rating_info")
  private StreamRatingInfo ratingInfo;

  @JsonProperty("reviewer_name")
  private String reviewerName;

  @JsonProperty("reviewer_photo")
  private String reviewerPhoto;

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("stream_title")
  private String streamTitle;
}
