package com.fleencorp.feen.softask.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.softask.model.response.reply.core.SoftAskReplyResponse;
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
public class SoftAskReplySearchResult extends LocalizedResponse {

  @JsonProperty("parent_id")
  private Long parentId;

  @JsonProperty("result")
  private SearchResult<SoftAskReplyResponse> result;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return nonNull(result) && result.hasValue() ? "soft.ask.reply.search" : "soft.ask.reply.empty.search";
  }

  public static SoftAskReplySearchResult of(final Long parentId, final SearchResult<SoftAskReplyResponse> result) {
    return new SoftAskReplySearchResult(parentId, result);
  }

  public static SoftAskReplySearchResult empty(final Long parentId) {
    final SearchResult<SoftAskReplyResponse> result = SearchResult.empty();
    return new SoftAskReplySearchResult(parentId, result);
  }

  public static SoftAskReplySearchResult empty() {
    return empty(null);
  }
}
