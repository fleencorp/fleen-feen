package com.fleencorp.feen.model.search.link;

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
  "result",
  "parent_id"
})
public class LinkSearchResult extends LocalizedResponse {

  @JsonProperty("result")
  private SearchResult result;

  @JsonProperty("parent_id")
  private Object parentId;

  @Override
  public String getMessageCode() {
    return nonNull(result) && result.hasValue() ? "link.search" : "link.empty.search";
  }

  public static LinkSearchResult of(final SearchResult result, final Object parentId) {
    return new LinkSearchResult(result, parentId);
  }

  public static LinkSearchResult empty() {
    return new LinkSearchResult(SearchResult.empty(), null);
  }
}
