package com.fleencorp.feen.softask.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.softask.model.response.vote.core.SoftAskVoteResponse;
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
public class SoftAskVoteSearchResult extends LocalizedResponse {

  @JsonProperty("result")
  private SearchResult<SoftAskVoteResponse> result;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return nonNull(result) && result.hasValue() ? "soft.ask.vote.search" : "soft.ask.vote.empty.search";
  }

  public static SoftAskVoteSearchResult of(final SearchResult<SoftAskVoteResponse> result) {
    return new SoftAskVoteSearchResult(result);
  }

  public static SoftAskVoteSearchResult empty() {
    final SearchResult<SoftAskVoteResponse> result = new SearchResult<>();
    return new SoftAskVoteSearchResult(result);
  }
}
