package com.fleencorp.feen.stream.model.search.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_type_info",
  "result"
})
public class StreamSearchResult extends LocalizedResponse {

  @JsonProperty("result")
  protected SearchResult result;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    final boolean isEvent = StreamType.isEvent(getStreamType());
    final boolean hasResult = nonNull(result) && result.hasValue();

    if (isEvent) {
      return hasResult ? "event.search" : "event.empty.search";
    } else {
      return hasResult ? "live.broadcast.search" : "live.broadcast.empty.search";
    }
  }

  public static StreamSearchResult of(final SearchResult result, final StreamTypeInfo streamTypeInfo) {
    return new StreamSearchResult(result, streamTypeInfo);
  }
}