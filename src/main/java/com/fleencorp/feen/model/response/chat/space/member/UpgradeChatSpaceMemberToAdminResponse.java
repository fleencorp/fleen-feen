package com.fleencorp.feen.model.response.chat.space.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceMemberRoleInfo;
import com.fleencorp.localizer.model.response.ApiResponse;
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
  "chat_space_member_id",
  "role_info"
})
public class UpgradeChatSpaceMemberToAdminResponse extends ApiResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @JsonProperty("role_info")
  private ChatSpaceMemberRoleInfo roleInfo;

  @Override
  public String getMessageCode() {
    return "upgrade.chat.space.member.to.admin";
  }

  public static UpgradeChatSpaceMemberToAdminResponse of(final Long chatSpaceId, final Long chatSpaceMemberId, final ChatSpaceMemberRoleInfo chatSpaceMemberRoleInfo) {
    return new UpgradeChatSpaceMemberToAdminResponse(chatSpaceId, chatSpaceMemberId, chatSpaceMemberRoleInfo);
  }
}
