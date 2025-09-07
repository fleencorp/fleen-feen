package com.fleencorp.feen.link.model.search;

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
  "result",
  "parent_id"
})
public class LinkSearchResult extends LocalizedResponse {

  @JsonProperty("result")
  private SearchResult result;

  @JsonProperty("parent_id")
  private Long parentId;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return nonNull(result) && result.hasValue() ? "link.search" : "link.empty.search";
  }

  public static LinkSearchResult of(final SearchResult result, final Long parentId) {
    return new LinkSearchResult(result, parentId);
  }

  public static LinkSearchResult empty() {
    return new LinkSearchResult(SearchResult.empty(), null);
  }
}
