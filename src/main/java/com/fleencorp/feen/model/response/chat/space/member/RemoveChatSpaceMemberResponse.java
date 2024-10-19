package com.fleencorp.feen.model.response.chat.space.member;

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
public class RemoveChatSpaceMemberResponse extends ApiResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @Override
  public String getMessageCode() {
    return "remove.chat.space.member";
  }

  public static RemoveChatSpaceMemberResponse of(final Long chatSpaceId, final Long chatSpaceMemberId) {
    return RemoveChatSpaceMemberResponse.builder()
      .chatSpaceId(chatSpaceId)
      .chatSpaceMemberId(chatSpaceMemberId)
      .build();
  }
}
