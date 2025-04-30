package com.fleencorp.feen.model.search.stream.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.localizer.model.response.ApiResponse;
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
public class StreamSearchResult extends ApiResponse {

  @JsonProperty("result")
  protected SearchResultView result;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType())
          ? nonNull(result) && result.hasValue() ? "event.search" : "event.empty.search"
          : nonNull(result) && result.hasValue() ? "live.broadcast.search" : "live.broadcast.empty.search";
  }

  public static StreamSearchResult of(final SearchResultView result, final StreamTypeInfo streamTypeInfo) {
    return new StreamSearchResult(result, streamTypeInfo);
  }
}