package com.fleencorp.feen.model.response.chat.space.member.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceMemberRoleInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceRequestToJoinStatusInfo;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "member_id",
  "chat_space_member_id",
  "member_name",
  "role_info",
  "request_to_join_status_info",
  "join_status_info"
})
public class ChatSpaceMemberResponse {

  @JsonProperty("member_id")
  private Long memberId;

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @JsonProperty("member_name")
  private String memberName;

  @JsonProperty("role_info")
  private ChatSpaceMemberRoleInfo chatSpaceMemberRoleInfo;

  @JsonProperty("request_to_join_status_info")
  private ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo;

  @JsonProperty("join_status_info")
  private JoinStatusInfo joinStatusInfo;
}
