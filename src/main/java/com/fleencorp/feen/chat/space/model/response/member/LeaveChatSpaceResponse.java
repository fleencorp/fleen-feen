package com.fleencorp.feen.chat.space.model.response.member;

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
  "membership_info",
  "total_members"
})
public class LeaveChatSpaceResponse extends LocalizedResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("membership_info")
  private ChatSpaceMembershipInfo membershipInfo;

  @JsonProperty("total_members")
  private Long totalMembers;

  @Override
  public String getMessageCode() {
    return "leave.chat.space";
  }

  public static LeaveChatSpaceResponse of(final Long chatSpaceId, final ChatSpaceMembershipInfo membershipInfo, final Long totalMembers) {
    return new LeaveChatSpaceResponse(chatSpaceId, membershipInfo, totalMembers);
  }
}
