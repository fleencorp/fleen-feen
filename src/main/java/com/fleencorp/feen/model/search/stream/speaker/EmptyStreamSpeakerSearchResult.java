package com.fleencorp.feen.model.search.stream.speaker;

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
public class EmptyStreamSpeakerSearchResult extends StreamSpeakerSearchResult {

  @JsonProperty("result")
  private SearchResultView result;

  @Override
  public String getMessageCode() {
    return "empty.stream.speaker.search";
  }

  public static Supplier<StreamSpeakerSearchResult> of(final SearchResultView result) {
    return () -> new EmptyStreamSpeakerSearchResult(result);
  }
}

