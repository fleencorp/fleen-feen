package com.fleencorp.feen.stream.model.response.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_id",
  "email_address",
  "stream"
})
public class AddNewStreamAttendeeResponse extends LocalizedResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("email_address")
  private String emailAddress;

  @JsonProperty("stream")
  private StreamResponse stream;

  @Override
  public String getMessageCode() {
    return "add.new.stream.attendee";
  }

  public static AddNewStreamAttendeeResponse of(final Long streamId, final String emailAddress, final StreamResponse stream) {
    return new AddNewStreamAttendeeResponse(streamId, emailAddress, stream);
  }
}
