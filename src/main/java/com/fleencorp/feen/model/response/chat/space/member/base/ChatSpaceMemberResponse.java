package com.fleencorp.feen.model.response.chat.space.member.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.contract.SetIsOrganizer;
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
  "display_photo",
  "request_to_join_status_info",
  "join_status_info",
  "is_organizer"
})
public class ChatSpaceMemberResponse
    implements SetIsOrganizer {

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

  @JsonIgnore
  private Long organizerId;

  @Override
  public void setIsOrganizer(final boolean isOrganizer) {
    this.isOrganizer = isOrganizer;
  }
}
