package com.fleencorp.feen.review.model.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "review_count",
  "review_text"
})
public class ReviewCountInfo {

  @JsonProperty("review_count")
  private Integer reviewCount;

  @JsonProperty("review_text")
  private String reviewText;

  public static ReviewCountInfo of(final Integer reviewCount, final String reviewText) {
    return new ReviewCountInfo(reviewCount, reviewText);
  }
}
