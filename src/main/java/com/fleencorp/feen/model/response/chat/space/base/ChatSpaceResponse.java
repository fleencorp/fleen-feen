package com.fleencorp.feen.model.response.chat.space.base;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import com.fleencorp.feen.constant.common.JoinStatus;
import com.fleencorp.feen.constant.security.mask.MaskedChatSpaceUri;
import com.fleencorp.feen.model.contract.SetIsOrganizer;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceStatusInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceVisibilityInfo;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceRequestToJoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.membership.ChatSpaceMembershipInfo;
import com.fleencorp.feen.model.other.Organizer;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import com.fleencorp.feen.model.response.chat.space.member.base.ChatSpaceMemberResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "title",
  "description",
  "tags",
  "guidelines_or_rules",
  "space_link",
  "space_link_unmasked",
  "total_members",
  "total_request_to_join",
  "visibility_info",
  "status_info",
  "organizer",
  "some_members",
  "request_to_join_status_info",
  "join_status_info",
  "membership_info",
  "created_on",
  "updated_on"
})
public class ChatSpaceResponse extends FleenFeenResponse
    implements SetIsOrganizer {

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("tags")
  private String tags;

  @JsonProperty("guidelines_or_rules")
  private String guidelinesOrRules;

  @JsonFormat(shape = STRING)
  @JsonProperty("space_link")
  private MaskedChatSpaceUri spaceLink;

  @JsonProperty("total_members")
  private Long totalMembers;

  @JsonProperty("total_request_to_join")
  private Long totalRequestToJoin;

  @JsonProperty("visibility_info")
  private ChatSpaceVisibilityInfo visibilityInfo;

  @JsonProperty("status_info")
  private ChatSpaceStatusInfo statusInfo;

  @JsonProperty("organizer")
  private Organizer organizer;

  @JsonProperty("some_members")
  private Set<ChatSpaceMemberResponse> someMembers = new HashSet<>();

  @JsonProperty("request_to_join_status_info")
  private ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo;

  @JsonProperty("membership_info")
  private ChatSpaceMembershipInfo membershipInfo;

  @JsonProperty("is_private")
  public boolean isPrivate() {
    return ChatSpaceVisibility.isPrivate(visibilityInfo.getVisibility());
  }

  @JsonIgnore
  private Long organizerId;

  @JsonIgnore
  private String spaceLinkUnMasked;

  @JsonIgnore
  public ChatSpaceVisibility getVisibility() {
    return nonNull(visibilityInfo) ? visibilityInfo.getVisibility() : null;
  }

  @JsonIgnore
  public JoinStatusInfo getJoinStatusInfo() {
    return nonNull(membershipInfo) ? membershipInfo.getJoinStatusInfo() : null;
  }

  @JsonProperty("space_link_unmasked")
  public String getSpaceLinkUnmasked() {
    disableAndResetUnmaskedLinkIfNotApproved();
    return spaceLinkUnMasked;
  }

  public void setIsOrganizer(final boolean isOrganizer) {
    this.organizer.setIsOrganizer(isOrganizer);
  }

  /**
   * Disables and resets the unmasked space link if the join status is not approved.
   *
   * <p>This method checks whether the current {@code joinStatus} is not approved.
   * If the status is not approved, the {@code spaceLinkUnMasked} field is set to {@code null},
   * effectively disabling and resetting the unmasked link.</p>
   *
   * <p>This operation ensures that users without approval cannot access unmasked space links.</p>
   */
  public void disableAndResetUnmaskedLinkIfNotApproved() {
    if (nonNull(getJoinStatusInfo()) && JoinStatus.isNotApproved(getJoinStatusInfo().getJoinStatus())) {
      spaceLinkUnMasked = null;
    }
  }
}
