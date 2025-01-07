package com.fleencorp.feen.model.response.chat.space.member;

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
public class LeaveChatSpaceResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "leave.chat.space";
  }

  public static LeaveChatSpaceResponse of() {
    return new LeaveChatSpaceResponse();
  }
}
