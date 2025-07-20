package com.fleencorp.feen.stream.model.info.rating;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.review.constant.ReviewRating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

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
public class RatingInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("review_rating")
  private ReviewRating reviewRating;

  @JsonProperty("rating")
  private Integer rating;

  @JsonProperty("rating_name")
  private String ratingName;

  @JsonProperty("rating_text")
  private String ratingText;

  public static RatingInfo of(final ReviewRating reviewRating, final Integer rating, final String ratingName, final String ratingText) {
    return new RatingInfo(reviewRating, rating, ratingName, ratingText);
  }
}
