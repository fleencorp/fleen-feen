package com.fleencorp.feen.model.search.chat.space.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
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
  "stream_type_info"
})
public class ChatSpaceEventSearchResult extends LocalizedResponse {

  @JsonProperty("result")
  private SearchResult result;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @Override
  public String getMessageCode() {
    return nonNull(result) && result.hasValue() ? "chat.space.event.search" : "chat.space.event.empty.search";
  }

  public static ChatSpaceEventSearchResult of(final SearchResult result, final StreamTypeInfo streamTypeInfo) {
    return new ChatSpaceEventSearchResult(result, streamTypeInfo);
  }
}