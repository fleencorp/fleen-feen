package com.fleencorp.feen.poll.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  "result"
})
public class PollVoteSearchResult extends LocalizedResponse {

  @JsonProperty("result")
  private SearchResult result;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return nonNull(result) && result.hasValue() ? "poll.voter.search" : "poll.voter.empty.search";
  }

  public static PollVoteSearchResult of(final SearchResult result) {
    return new PollVoteSearchResult(result);
  }

  public static PollVoteSearchResult empty() {
    return new PollVoteSearchResult(SearchResult.empty());
  }
}
