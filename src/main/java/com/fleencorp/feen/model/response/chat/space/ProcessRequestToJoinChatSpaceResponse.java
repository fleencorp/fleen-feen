package com.fleencorp.feen.model.response.chat.space;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "chat_space_id",
  "chat_space_member_id"
})
public class ProcessRequestToJoinChatSpaceResponse extends ApiResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @Override
  public String getMessageCode() {
    return "process.request.to.join.chat.space";
  }

  public static ProcessRequestToJoinChatSpaceResponse of(final Long chatSpaceId, final Long chatSpaceMemberId) {
    return ProcessRequestToJoinChatSpaceResponse.builder()
      .chatSpaceId(chatSpaceId)
      .chatSpaceMemberId(chatSpaceMemberId)
      .build();
  }
}
