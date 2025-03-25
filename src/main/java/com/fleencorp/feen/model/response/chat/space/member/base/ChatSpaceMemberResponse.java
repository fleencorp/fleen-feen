package com.fleencorp.feen.model.response.chat.space.member.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.chat.space.membership.ChatSpaceMembershipInfo;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "chat_space_member_id",
  "member_name",
  "username",
  "request_to_join_status_info",
  "join_status_info"
})
public class ChatSpaceMemberResponse {

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @JsonProperty("member_name")
  private String memberName;

  @JsonProperty("username")
  private String username;

  @JsonProperty("membership_info")
  private ChatSpaceMembershipInfo membershipInfo;
}
