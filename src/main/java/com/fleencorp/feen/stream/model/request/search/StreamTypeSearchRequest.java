package com.fleencorp.feen.stream.model.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.stream.constant.core.StreamType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamTypeSearchRequest extends SearchRequest {

  @JsonProperty("stream_type")
  @ToUpperCase
  protected String streamType;

  public StreamType getStreamType() {
    return StreamType.of(streamType);
  }

  public boolean hasStreamType() {
    return nonNull(getStreamType());
  }
}
