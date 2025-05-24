package com.fleencorp.feen.model.response.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message"
})
public class DeleteReviewResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "delete.review";
  }

  public static DeleteReviewResponse of() {
    return new DeleteReviewResponse();
  }
}
