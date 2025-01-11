package com.fleencorp.feen.model.info.chat.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "request_to_join_status",
  "request_to_join_status_text"
})
public class ChatSpaceRequestToJoinStatusInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("request_to_join_status")
  private ChatSpaceRequestToJoinStatus requestToJoinStatus;

  @JsonProperty("request_to_join_status_text")
  private String requestToJoinStatusText;

  public static ChatSpaceRequestToJoinStatusInfo of(final ChatSpaceRequestToJoinStatus requestToJoinStatus, final String requestToJoinStatusText) {
    return new ChatSpaceRequestToJoinStatusInfo(requestToJoinStatus, requestToJoinStatusText);
  }

  public static ChatSpaceRequestToJoinStatusInfo of() {
    return new ChatSpaceRequestToJoinStatusInfo();
  }
}
