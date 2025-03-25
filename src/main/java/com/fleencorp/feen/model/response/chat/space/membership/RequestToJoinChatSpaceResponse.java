package com.fleencorp.feen.model.response.chat.space.membership;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.chat.space.membership.ChatSpaceMembershipInfo;
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
  "membership_info",
  "total_members"
})
public class RequestToJoinChatSpaceResponse extends ApiResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("membership_info")
  private ChatSpaceMembershipInfo membershipInfo;

  @JsonProperty("total_members")
  private Long totalMembers;

  @Override
  public String getMessageCode() {
    return "request.to.join.chat.space";
  }

  public static RequestToJoinChatSpaceResponse of(final Long chatSpaceId, final ChatSpaceMembershipInfo membershipInfo, final Long totalMembers) {
    return new RequestToJoinChatSpaceResponse(chatSpaceId, membershipInfo, totalMembers);
  }
}
