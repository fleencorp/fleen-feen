package com.fleencorp.feen.model.search.stream.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Supplier;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "result"
})
public class StreamReviewSearchResult extends ApiResponse {

  @JsonProperty("result")
  private SearchResultView result;

  @Override
  public String getMessageCode() {
    return "stream.review.search";
  }

  public static Supplier<StreamReviewSearchResult> of(final SearchResultView result) {
    return () -> new StreamReviewSearchResult(result);
  }
}