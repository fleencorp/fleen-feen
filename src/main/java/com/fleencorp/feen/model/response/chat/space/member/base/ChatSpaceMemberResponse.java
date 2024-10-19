package com.fleencorp.feen.model.response.chat.space.member.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

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
  "role",
  "request_to_join_status"
})
public class ChatSpaceMemberResponse {

  @JsonProperty("member_id")
  private Long memberId;

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @JsonProperty("member_name")
  private String memberName;

  @JsonFormat(shape = STRING)
  @JsonProperty("role")
  private ChatSpaceMemberRole chatSpaceMemberRole;

  @JsonFormat(shape = STRING)
  @JsonProperty("request_to_join_status")
  private ChatSpaceRequestToJoinStatus requestToJoinStatus;
}
