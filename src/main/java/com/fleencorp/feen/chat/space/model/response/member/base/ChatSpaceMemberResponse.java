package com.fleencorp.feen.chat.space.model.response.member.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.chat.space.model.info.membership.ChatSpaceMembershipInfo;
import com.fleencorp.feen.model.contract.HasOrganizer;
import com.fleencorp.feen.model.contract.Updatable;
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
  "display_photo",
  "request_to_join_status_info",
  "join_status_info",
  "is_organizer",
  "is_updatable"
})
public class ChatSpaceMemberResponse
  implements HasOrganizer, Updatable {

  @JsonProperty("chat_space_member_id")
  private Long chatSpaceMemberId;

  @JsonProperty("member_name")
  private String memberName;

  @JsonProperty("username")
  private String username;

  @JsonProperty("display_photo")
  private String displayPhoto;

  @JsonProperty("membership_info")
  private ChatSpaceMembershipInfo membershipInfo;

  @JsonProperty("is_organizer")
  private Boolean isOrganizer;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @JsonIgnore
  private Long organizerId;

  @Override
  public void setIsOrganizer(final boolean isOrganizer) {
    this.isOrganizer = isOrganizer;
  }

  @Override
  public void setIsUpdatable(final boolean isUpdatable) {
    this.isUpdatable = isUpdatable;
  }

  @Override
  public void markAsUpdatable() {
    setIsUpdatable(true);
  }
}
