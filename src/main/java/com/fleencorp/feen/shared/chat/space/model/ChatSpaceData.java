package com.fleencorp.feen.shared.chat.space.model;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceVisibility;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSpaceData implements IsAChatSpace {

  private Long chatSpaceId;
  private String externalIdOrName;
  private String title;
  private String description;
  private String tags;
  private String guidelinesOrRules;
  private String spaceLink;
  private Long organizerId;
  private String organizerName;
  private ChatSpaceVisibility spaceVisibility;
  private ChatSpaceStatus status;
  private Integer totalMembers;
  private Boolean deleted;
  private Integer likeCount;
  private Integer bookmarkCount;
  private Integer shareCount;
  private String slug;

  public static ChatSpaceData empty() {
    return new ChatSpaceData();
  }
}

