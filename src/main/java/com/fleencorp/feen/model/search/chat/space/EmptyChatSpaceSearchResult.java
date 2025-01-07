package com.fleencorp.feen.model.search.chat.space;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResultView;
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
public class EmptyChatSpaceSearchResult extends ChatSpaceSearchResult {

  @JsonProperty("result")
  private SearchResultView result;

  @Override
  public String getMessageCode() {
    return "empty.chat.space.search";
  }

  public static Supplier<ChatSpaceSearchResult> of(final SearchResultView result) {
    return () -> new EmptyChatSpaceSearchResult(result);
  }
}