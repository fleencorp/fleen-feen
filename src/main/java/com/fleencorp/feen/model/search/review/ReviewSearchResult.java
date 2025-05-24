package com.fleencorp.feen.model.search.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "result"
})
public class ReviewSearchResult extends LocalizedResponse {

  @JsonProperty("result")
  private SearchResultView result;

  @Override
  public String getMessageCode() {
    return nonNull(result) && result.hasValue() ? "review.search" : "review.empty.search";
  }

  public static ReviewSearchResult of(final SearchResultView result) {
    return new ReviewSearchResult(result);
  }

  public static ReviewSearchResult empty() {
    return new ReviewSearchResult(SearchResultView.empty());
  }
}