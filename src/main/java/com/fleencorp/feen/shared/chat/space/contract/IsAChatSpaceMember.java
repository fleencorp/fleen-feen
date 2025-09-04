package com.fleencorp.feen.shared.chat.space.contract;

import static java.util.Objects.nonNull;

public interface IsAChatSpaceMember {

  Long getChatSpaceMemberId();

  String getParentExternalIdOrName();

  String getExternalIdOrName();

  Long getChatSpaceId();

  Long getMemberId();

  Boolean getLeft();

  Boolean hasLeft();

  Boolean getRemoved();

  String getMemberComment();

  String getSpaceAdminComment();

  String getEmailAddress();

  String getFullName();

  String getUsername();

  String getProfilePhoto();

  default boolean isAMember() {
    return !hasLeft() && !isRemoved();
  }

  default boolean isRemoved() {
    return nonNull(getRemoved()) && getRemoved();
  }

  default boolean isApproved() {
    return false;
  }

  default boolean isAdmin() {
    return false;
  }
}
