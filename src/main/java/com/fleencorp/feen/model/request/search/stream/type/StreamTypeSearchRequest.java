package com.fleencorp.feen.model.request.search.stream.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.constant.stream.StreamType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
