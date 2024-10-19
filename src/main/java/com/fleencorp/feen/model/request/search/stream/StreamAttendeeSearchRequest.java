package com.fleencorp.feen.model.request.search.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.constant.stream.StreamSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class StreamAttendeeSearchRequest extends SearchRequest {

  @JsonIgnore
  public StreamSource googleMeet() {
    return StreamSource.GOOGLE_MEET;
  }

  @JsonIgnore
  public StreamSource youtubeLive() {
    return StreamSource.YOUTUBE_LIVE;
  }
}
