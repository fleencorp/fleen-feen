package com.fleencorp.feen.shared.chat.space.model;

import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpaceMember;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSpaceMemberData implements IsAChatSpaceMember {

  private Long chatSpaceMemberId;
  private String parentExternalIdOrName;
  private String externalIdOrName;
  private Long chatSpaceId;
  private Long memberId;
  private Boolean left;
  private Boolean removed;
  private String memberComment;
  private String spaceAdminComment;
  private String emailAddress;
  private String fullName;
  private String username;
  private String profilePhoto;

  @Override
  public Boolean hasLeft() {
    return left;
  }

  public static ChatSpaceMemberData empty() {
    return new ChatSpaceMemberData();
  }
}

