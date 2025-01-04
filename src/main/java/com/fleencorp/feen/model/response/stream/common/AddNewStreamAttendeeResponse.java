package com.fleencorp.feen.model.response.stream.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.localizer.model.response.ApiResponse;
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
public class AddNewStreamAttendeeResponse extends ApiResponse {

  @JsonProperty("stream_id")
  private Long streamId;

  @JsonProperty("email_address")
  private String emailAddress;

  @JsonProperty("stream")
  private FleenStreamResponse stream;

  @Override
  public String getMessageCode() {
    return "add.new.stream.attendee";
  }

  public static AddNewStreamAttendeeResponse of(final Long streamId, final String emailAddress, final FleenStreamResponse stream) {
    return new AddNewStreamAttendeeResponse(streamId, emailAddress, stream);
  }
}
