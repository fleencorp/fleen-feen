package com.fleencorp.feen.block.user.model.search;

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
public class BlockUserSearchResult extends LocalizedResponse {

  @JsonProperty("result")
  private SearchResult result;

  @Override
  public String getMessageCode() {
    return nonNull(result) && result.hasValue() ? "blocking.user.search" : "blocking.user.empty.search";
  }

  public static BlockUserSearchResult of(final SearchResult result) {
    return new BlockUserSearchResult(result);
  }
}