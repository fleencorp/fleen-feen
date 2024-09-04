package com.fleencorp.feen.model.response.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message"
})
public class NotAttendingStreamResponse extends ApiResponse {

  @Override
  public String getMessageKey() {
    return "not.attending.stream";
  }

  public static NotAttendingStreamResponse of() {
    return new NotAttendingStreamResponse();
  }
}
