package com.fleencorp.feen.model.info.chat.space.membership;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceMemberRoleInfo;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceRequestToJoinStatusInfo;
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
  "request_to_join_status_info",
  "join_status_info",
  "role_info",
  "is_a_member_info",
  "is_admin_info",
  "is_left_info",
  "is_removed_info"
})
public class ChatSpaceMembershipInfo {

  @JsonProperty("request_to_join_status_info")
  private ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo;

  @JsonProperty("join_status_info")
  private JoinStatusInfo joinStatusInfo;

  @JsonProperty("role_info")
  private ChatSpaceMemberRoleInfo memberRoleInfo;

  @JsonProperty("is_a_member_info")
  private IsAChatSpaceMemberInfo isAMemberInfo;

  @JsonProperty("is_admin_info")
  private IsAChatSpaceAdminInfo isAdminInfo;

  @JsonProperty("is_left_info")
  private IsChatSpaceMemberLeftInfo isLeftInfo;

  @JsonProperty("is_removed_info")
  private IsChatSpaceMemberRemovedInfo isRemovedInfo;

  public static ChatSpaceMembershipInfo of(
      final ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo,
      final JoinStatusInfo joinStatusInfo,
      final ChatSpaceMemberRoleInfo memberRoleInfo,
      final IsAChatSpaceMemberInfo isAMemberInfo,
      final IsAChatSpaceAdminInfo isAdminInfo,
      final IsChatSpaceMemberLeftInfo isLeftInfo,
      final IsChatSpaceMemberRemovedInfo isRemovedInfo) {
    return new ChatSpaceMembershipInfo(requestToJoinStatusInfo, joinStatusInfo, memberRoleInfo, isAMemberInfo, isAdminInfo, isLeftInfo, isRemovedInfo);
  }

  public static ChatSpaceMembershipInfo of() {
    final ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo = ChatSpaceRequestToJoinStatusInfo.of();
    final JoinStatusInfo joinStatusInfo = JoinStatusInfo.of();
    final ChatSpaceMemberRoleInfo memberRoleInfo = ChatSpaceMemberRoleInfo.of();
    final IsAChatSpaceMemberInfo isAMemberInfo = IsAChatSpaceMemberInfo.of();
    final IsAChatSpaceAdminInfo isAAdminInfo = IsAChatSpaceAdminInfo.of();
    final IsChatSpaceMemberLeftInfo isLeftInfo = IsChatSpaceMemberLeftInfo.of();
    final IsChatSpaceMemberRemovedInfo isRemovedInfo = IsChatSpaceMemberRemovedInfo.of();

    return of(requestToJoinStatusInfo, joinStatusInfo, memberRoleInfo, isAMemberInfo, isAAdminInfo, isLeftInfo, isRemovedInfo);
  }

}
