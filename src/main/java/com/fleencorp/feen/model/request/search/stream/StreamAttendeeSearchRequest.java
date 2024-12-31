package com.fleencorp.feen.model.request.search.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.constant.stream.StreamSource;
import com.fleencorp.feen.constant.stream.StreamType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class StreamAttendeeSearchRequest extends SearchRequest {

  protected static final int DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_SEARCH = 100;

  @JsonProperty("stream_type")
  @ToUpperCase
  protected String streamType;

  public StreamType getStreamType() {
    return StreamType.of(streamType);
  }

  @JsonIgnore
  public StreamSource googleMeet() {
    return StreamSource.GOOGLE_MEET;
  }

  @JsonIgnore
  public StreamSource youtubeLive() {
    return StreamSource.YOUTUBE_LIVE;
  }

  public void setDefaultPageSize() {
    setPageSize(DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_SEARCH);
  }
}
