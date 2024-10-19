package com.fleencorp.feen.model.response.chat.space;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "chat_space"
})
public class RetrieveChatSpaceResponse extends ApiResponse {

  @JsonProperty("chat_space")
  private ChatSpaceResponse chatSpace;

  @Override
  public String getMessageCode() {
    return "retrieve.chat.space";
  }

  public static RetrieveChatSpaceResponse of(final ChatSpaceResponse response) {
    return RetrieveChatSpaceResponse.builder()
      .chatSpace(response)
      .build();
  }
}
