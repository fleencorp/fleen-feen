package com.fleencorp.feen.chat.space.model.request.external.membership;

import com.fleencorp.feen.common.constant.external.google.chat.space.membership.MemberUserType;
import com.fleencorp.feen.common.constant.external.google.chat.space.membership.MembershipRole;
import com.fleencorp.feen.common.constant.external.google.chat.space.membership.MembershipState;
import lombok.*;

import java.time.LocalDateTime;

import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getChatSpaceIdOrNameRequiredPattern;
import static com.fleencorp.feen.common.util.external.google.GoogleApiUtil.getChatSpaceUserIdOrNameRequiredPattern;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddChatSpaceMemberRequest extends ChatSpaceMemberRequest {

  private String userEmailAddress;
  private String memberSpaceIdOrName;
  private MemberUserType userType;
  private MembershipRole membershipRole;
  private MembershipState membershipState;

  public LocalDateTime getCreateTime() {
    return LocalDateTime.now();
  }

  public String getUsername() {
    return getChatSpaceUserIdOrNameRequiredPattern(userEmailAddress);
  }

  public String getRole() {
    return nonNull(membershipRole) ? membershipRole.getValue() : null;
  }

  public String getUserType() {
    return nonNull(userType) ? userType.getValue() : null;
  }

  public static AddChatSpaceMemberRequest of(final String spaceIdOrName, final String userEmailAddress) {
    final String formattedSpaceName = nonNull(spaceIdOrName) ? getChatSpaceIdOrNameRequiredPattern(spaceIdOrName) : null;

    final AddChatSpaceMemberRequest addChatSpaceMemberRequest = new AddChatSpaceMemberRequest();
    addChatSpaceMemberRequest.setUserEmailAddress(userEmailAddress);
    addChatSpaceMemberRequest.setMemberSpaceIdOrName(formattedSpaceName);
    addChatSpaceMemberRequest.setUserType(MemberUserType.HUMAN);
    addChatSpaceMemberRequest.setMembershipRole(MembershipRole.ROLE_MEMBER);

    return addChatSpaceMemberRequest;
  }
}
