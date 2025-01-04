package com.fleencorp.feen.model.response.chat.space;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message"
})
public class RequestToJoinChatSpaceResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "request.to.join.chat.space";
  }

  public static RequestToJoinChatSpaceResponse of() {
    return new RequestToJoinChatSpaceResponse();
  }
}
