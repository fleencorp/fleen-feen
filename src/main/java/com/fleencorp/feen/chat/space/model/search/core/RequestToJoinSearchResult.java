package com.fleencorp.feen.chat.space.model.search.core;

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
public class RequestToJoinSearchResult extends LocalizedResponse {

  @JsonProperty("result")
  private SearchResult result;

  @Override
  public String getMessageCode() {
    return nonNull(result) && result.hasValue() ? "request.to.join.search" : "request.to.join.empty.search";
  }

  public static RequestToJoinSearchResult of(final SearchResult result) {
    return new RequestToJoinSearchResult(result);
  }
}