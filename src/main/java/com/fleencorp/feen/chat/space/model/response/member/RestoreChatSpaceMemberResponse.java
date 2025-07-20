package com.fleencorp.feen.chat.space.model.response.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
  "chat_space_id",
  "chat_space_member_id"
})
public class RestoreChatSpaceMemberResponse extends LocalizedResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @Override
  public String getMessageCode() {
    return "restore.chat.space.member";
  }

  public static RestoreChatSpaceMemberResponse of(final Long chatSpaceId, final Long chatSpaceMemberId) {
    return new RestoreChatSpaceMemberResponse(chatSpaceId, chatSpaceMemberId);
  }
}
