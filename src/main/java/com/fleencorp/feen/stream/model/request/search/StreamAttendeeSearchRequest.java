package com.fleencorp.feen.stream.model.request.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.ValidBoolean;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamSource;
import com.fleencorp.feen.stream.constant.core.StreamType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import static com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus.DISAPPROVED;
import static com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus.PENDING;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
public class StreamAttendeeSearchRequest extends SearchRequest {

  protected static final int DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_SEARCH = 100;

  @JsonProperty("stream_type")
  @ToUpperCase
  protected String streamType;

  @JsonProperty("disapproved")
  @ValidBoolean
  protected String disapproved;

  public StreamType getStreamType() {
    return StreamType.of(streamType);
  }

  public boolean isDisapproved() {
    return nonNull(disapproved) && Boolean.parseBoolean(disapproved);
  }

  public Set<StreamAttendeeRequestToJoinStatus> forPendingOrDisapprovedRequestToJoinStatus() {
    return isDisapproved() ? Set.of(DISAPPROVED) : Set.of(PENDING);
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
