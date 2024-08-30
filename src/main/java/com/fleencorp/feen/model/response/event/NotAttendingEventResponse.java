package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.base.ApiResponse;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message"
})
public class NotAttendingEventResponse extends ApiResponse {

  @Builder.Default
  @JsonProperty("message")
  private String message = "Not attending request processed successfully";

  @Override
  public String getMessageKey() {
    return "not.attending.event";
  }

  public static NotAttendingEventResponse of() {
    return new NotAttendingEventResponse();
  }
}
