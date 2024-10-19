package com.fleencorp.feen.model.search.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.base.model.view.search.SearchResultView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.function.Supplier;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "result"
})
public class EventSearchResult extends ApiResponse {

  @JsonProperty("result")
  private SearchResultView result;

  @Override
  public String getMessageCode() {
    return "event.search";
  }

  public static Supplier<EventSearchResult> of(final SearchResultView result) {
    return () -> new EventSearchResult(result);
  }
}