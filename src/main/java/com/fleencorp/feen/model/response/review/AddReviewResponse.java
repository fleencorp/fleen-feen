package com.fleencorp.feen.model.response.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
public class AddReviewResponse extends LocalizedResponse {

  @JsonProperty("review")
  private ReviewResponse review;

  @Override
  public String getMessageCode() {
    return "add.review";
  }

  public static AddReviewResponse of(final ReviewResponse review) {
    return new AddReviewResponse(review);
  }
}
