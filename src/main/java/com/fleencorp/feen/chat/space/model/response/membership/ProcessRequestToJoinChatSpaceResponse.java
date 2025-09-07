package com.fleencorp.feen.chat.space.model.response.membership;

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
  "membership_info",
  "total_members",
  "total_request_to_join"
})
public class ProcessRequestToJoinChatSpaceResponse extends LocalizedResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @JsonProperty("membership_info")
  private ChatSpaceMembershipInfo membershipInfo;

  @JsonProperty("total_members")
  private Integer totalMembers;

  @JsonProperty("total_request_to_join")
  private Integer totalRequestToJoin;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "process.request.to.join.chat.space";
  }

  public static ProcessRequestToJoinChatSpaceResponse of(final Long chatSpaceId, final Long chatSpaceMemberId, final ChatSpaceMembershipInfo membershipInfo, final Integer totalMembers, final Integer totalRequestToJoin) {
    return new ProcessRequestToJoinChatSpaceResponse(chatSpaceId, chatSpaceMemberId, membershipInfo, totalMembers, totalRequestToJoin);
  }
}
