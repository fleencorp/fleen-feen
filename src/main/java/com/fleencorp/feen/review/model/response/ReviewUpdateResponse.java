package com.fleencorp.feen.review.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;
import com.fleencorp.localizer.model.response.LocalizedResponse;
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
  "message",
  "review"
})
public class ReviewUpdateResponse extends LocalizedResponse {

  @JsonProperty("review")
  private ReviewResponse review;

  @Override
  public String getMessageCode() {
    return "update.review";
  }

  public static ReviewUpdateResponse of(final ReviewResponse review) {
    return new ReviewUpdateResponse(review);
  }
}
