package com.fleencorp.feen.model.response.chat.space.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceMemberRoleInfo;
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
  "chat_space_id",
  "chat_space_member_id",
  "role_info"
})
public class DowngradeChatSpaceAdminToMemberResponse extends ApiResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @JsonProperty("role_info")
  private ChatSpaceMemberRoleInfo roleInfo;

  @Override
  public String getMessageCode() {
    return "downgrade.chat.space.admin.to.member";
  }

  public static DowngradeChatSpaceAdminToMemberResponse of(final Long chatSpaceId, final Long chatSpaceMemberId, final ChatSpaceMemberRoleInfo roleInfo) {
    return DowngradeChatSpaceAdminToMemberResponse.builder()
      .chatSpaceId(chatSpaceId)
      .chatSpaceMemberId(chatSpaceMemberId)
      .roleInfo(roleInfo)
      .build();
  }
}
