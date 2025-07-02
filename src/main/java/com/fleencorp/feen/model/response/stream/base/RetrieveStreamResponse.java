package com.fleencorp.feen.model.response.stream.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_id",
  "total_attending",
  "stream_type_info",
  "stream",
  "attendees",
})
public class RetrieveStreamResponse extends LocalizedResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("stream")
  private StreamResponse stream;

  @JsonProperty("attendees")
  private Collection<StreamAttendeeResponse> attendees = new HashSet<>();

  @JsonProperty("total_attending")
  private Integer totalAttending;

  @JsonProperty("stream_type_info")
  protected StreamTypeInfo streamTypeInfo;

  @JsonIgnore
  protected StreamType getStreamType() {
    return nonNull(streamTypeInfo) ? streamTypeInfo.getStreamType() : null;
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "retrieve.event" : "retrieve.live.broadcast";
  }

  public static RetrieveStreamResponse of(final Long streamId, final StreamResponse stream, final Collection<StreamAttendeeResponse> attendees, final Integer totalAttending, final StreamTypeInfo streamTypeInfo) {
    return new RetrieveStreamResponse(streamId, stream, attendees, totalAttending, streamTypeInfo);
  }
}
