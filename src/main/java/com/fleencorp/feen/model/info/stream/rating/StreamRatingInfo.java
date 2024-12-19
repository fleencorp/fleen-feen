package com.fleencorp.feen.model.info.stream.rating;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.stream.StreamReviewRating;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "review_rating",
  "rating",
  "rating_name",
  "rating_text"
})
public class StreamRatingInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("review_rating")
  private StreamReviewRating reviewRating;

  @JsonProperty("rating")
  private Integer rating;

  @JsonProperty("rating_name")
  private String ratingName;

  @JsonProperty("rating_text")
  private String ratingText;

  public static StreamRatingInfo of(final StreamReviewRating reviewRating, final Integer rating, final String ratingName, final String ratingText) {
    return StreamRatingInfo.builder()
      .reviewRating(reviewRating)
      .rating(rating)
      .ratingName(ratingName)
      .ratingText(ratingText)
      .build();
  }
}
