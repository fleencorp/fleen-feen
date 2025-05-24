package com.fleencorp.feen.model.response.chat.space.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "chat_space"
})
public class UpdateChatSpaceResponse extends LocalizedResponse {

  @JsonProperty("chat_space")
  private ChatSpaceResponse chatSpace;

  @Override
  public String getMessageCode() {
    return "update.chat.space";
  }

  public static UpdateChatSpaceResponse of(final ChatSpaceResponse chatSpaceResponse) {
    return new UpdateChatSpaceResponse(chatSpaceResponse);
  }
}
