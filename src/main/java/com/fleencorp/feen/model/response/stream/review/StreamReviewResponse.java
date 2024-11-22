package com.fleencorp.feen.model.response.stream.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "review",
  "rating",
  "rating_name",
  "reviewer_name",
  "reviewer_photo",
  "stream_id",
  "stream_title",
})
public class StreamReviewResponse extends FleenFeenResponse {

  @JsonProperty("review")
  private String review;

  @JsonProperty("rating")
  private Integer rating;

  @JsonProperty("rating_name")
  private String ratingName;

  @JsonProperty("reviewer_name")
  private String reviewerName;

  @JsonProperty("reviewer_photo")
  private String reviewerPhoto;

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("stream_title")
  private String streamTitle;
}
