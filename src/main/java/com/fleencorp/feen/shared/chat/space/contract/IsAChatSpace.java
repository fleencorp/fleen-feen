package com.fleencorp.feen.shared.chat.space.contract;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceVisibility;
import com.fleencorp.feen.model.contract.HasTitle;

import static java.util.Objects.nonNull;

public interface IsAChatSpace extends HasTitle {

  Long getChatSpaceId();

  String getExternalIdOrName();

  String getTitle();

  String getDescription();

  String getTags();

  String getGuidelinesOrRules();

  String getSpaceLink();

  Long getOrganizerId();

  String getOrganizerName();

  ChatSpaceVisibility getSpaceVisibility();

  ChatSpaceStatus getStatus();

  Integer getTotalMembers();

  Boolean getDeleted();

  Integer getLikeCount();

  Integer getBookmarkCount();

  Integer getShareCount();

  String getSlug();

  default boolean isDeleted() {
    return nonNull(getDeleted()) && getDeleted();
  }
}
