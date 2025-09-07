package com.fleencorp.feen.chat.space.model.response.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.chat.space.model.info.membership.ChatSpaceMembershipInfo;
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
  "chat_space_member_id",
  "membership_info"
})
public class UpgradeChatSpaceMemberToAdminResponse extends LocalizedResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @JsonProperty("membership_info")
  private ChatSpaceMembershipInfo membershipInfo;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "upgrade.chat.space.member.to.admin";
  }

  public static UpgradeChatSpaceMemberToAdminResponse of(final Long chatSpaceId, final Long chatSpaceMemberId, final ChatSpaceMembershipInfo membershipInfo) {
    return new UpgradeChatSpaceMemberToAdminResponse(chatSpaceId, chatSpaceMemberId, membershipInfo);
  }
}
