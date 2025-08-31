package com.fleencorp.feen.chat.space.model.response.membership;

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
public class RequestToJoinChatSpaceResponse extends LocalizedResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("membership_info")
  private ChatSpaceMembershipInfo membershipInfo;

  @JsonProperty("total_members")
  private Integer totalMembers;

  @Override
  public String getMessageCode() {
    return "request.to.join.chat.space";
  }

  public static RequestToJoinChatSpaceResponse of(final Long chatSpaceId, final ChatSpaceMembershipInfo membershipInfo, final Integer totalMembers) {
    return new RequestToJoinChatSpaceResponse(chatSpaceId, membershipInfo, totalMembers);
  }
}
