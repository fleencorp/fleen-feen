package com.fleencorp.feen.softask.model.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResult;
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
  "parent_id",
  "result"
})
public class SoftAskAnswerSearchResult extends LocalizedResponse {

  @JsonProperty("parent_id")
  private Long parentId;

  @JsonProperty("result")
  private SearchResult result;

  @Override
  public String getMessageCode() {
    return nonNull(result) && result.hasValue() ? "soft.ask.answer.search" : "soft.ask.answer.empty.search";
  }

  public static SoftAskAnswerSearchResult of(final Long parentId, final SearchResult result) {
    return new SoftAskAnswerSearchResult(parentId, result);
  }

  public static SoftAskAnswerSearchResult empty(final Long parentId) {
    final SearchResult result = SearchResult.empty();
    return new SoftAskAnswerSearchResult(parentId, result);
  }

  public static SoftAskAnswerSearchResult empty() {
    return empty(null);
  }
}
