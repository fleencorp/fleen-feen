package com.fleencorp.feen.model.response.stream.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.stream.rating.StreamRatingInfo;
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
  "reviewer_name",
  "reviewer_photo",
  "rating_info",
  "stream_id",
  "stream_title",
})
public class StreamReviewResponse extends FleenFeenResponse {

  @JsonProperty("review")
  private String review;

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
