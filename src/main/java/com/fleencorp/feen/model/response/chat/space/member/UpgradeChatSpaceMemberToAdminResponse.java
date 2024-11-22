package com.fleencorp.feen.model.response.chat.space.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceMemberRoleInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

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
  "role"
})
public class UpgradeChatSpaceMemberToAdminResponse extends ApiResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @JsonFormat(shape = STRING)
  @JsonProperty("role")
  private ChatSpaceMemberRole role;

  @JsonProperty("role_info")
  private ChatSpaceMemberRoleInfo roleInfo;

  @Override
  public String getMessageCode() {
    return "upgrade.chat.space.member.to.admin";
  }

  public static UpgradeChatSpaceMemberToAdminResponse of(final Long chatSpaceId, final Long chatSpaceMemberId, final ChatSpaceMemberRoleInfo chatSpaceMemberRoleInfo) {
    return UpgradeChatSpaceMemberToAdminResponse.builder()
      .chatSpaceId(chatSpaceId)
      .chatSpaceMemberId(chatSpaceMemberId)
      .roleInfo(chatSpaceMemberRoleInfo)
      .build();
  }
}
