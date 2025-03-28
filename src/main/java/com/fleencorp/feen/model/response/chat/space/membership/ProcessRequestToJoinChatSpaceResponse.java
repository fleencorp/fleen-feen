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
  "chat_space_member_id",
  "membership_info",
  "total_members",
  "total_request_to_join"
})
public class ProcessRequestToJoinChatSpaceResponse extends ApiResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @JsonProperty("membership_info")
  private ChatSpaceMembershipInfo membershipInfo;

  @JsonProperty("total_members")
  private Long totalMembers;

  @JsonProperty("total_request_to_join")
  private Long totalRequestToJoin;

  @Override
  public String getMessageCode() {
    return "process.request.to.join.chat.space";
  }

  public static ProcessRequestToJoinChatSpaceResponse of(final Long chatSpaceId, final Long chatSpaceMemberId, final ChatSpaceMembershipInfo membershipInfo, final Long totalMembers, final Long totalRequestToJoin) {
    return new ProcessRequestToJoinChatSpaceResponse(chatSpaceId, chatSpaceMemberId, membershipInfo, totalMembers, totalRequestToJoin);
  }
}
