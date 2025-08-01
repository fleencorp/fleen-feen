package com.fleencorp.feen.chat.space.model.response.core;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceVisibility;
import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.common.constant.mask.MaskedChatSpaceUri;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.link.model.response.base.LinkResponse;
import com.fleencorp.feen.model.contract.HasId;
import com.fleencorp.feen.model.contract.HasOrganizer;
import com.fleencorp.feen.model.contract.Likeable;
import com.fleencorp.feen.model.contract.Updatable;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.common.model.info.JoinStatusInfo;
import com.fleencorp.feen.chat.space.model.info.core.ChatSpaceStatusInfo;
import com.fleencorp.feen.chat.space.model.info.core.ChatSpaceVisibilityInfo;
import com.fleencorp.feen.chat.space.model.info.member.ChatSpaceRequestToJoinStatusInfo;
import com.fleencorp.feen.chat.space.model.info.membership.ChatSpaceMembershipInfo;
import com.fleencorp.feen.like.model.info.LikeCountInfo;
import com.fleencorp.feen.stream.model.other.Organizer;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.chat.space.model.response.member.base.ChatSpaceMemberResponse;
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
  "is_deleted_info",
  "organizer",
  "some_members",
  "links",
  "request_to_join_status_info",
  "join_status_info",
  "membership_info",
  "user_like_info",
  "like_count_info",
  "is_updatable",
  "created_on",
  "updated_on"
})
public class ChatSpaceResponse extends FleenFeenResponse
    implements HasId, HasOrganizer, Updatable, Likeable {

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

  @JsonProperty("is_deleted_info")
  private IsDeletedInfo deletedInfo;

  @JsonProperty("organizer")
  private Organizer organizer;

  @JsonProperty("some_members")
  private Set<ChatSpaceMemberResponse> someMembers = new HashSet<>();

  @JsonProperty("links")
  private Set<LinkResponse> links = new HashSet<>();

  @JsonProperty("request_to_join_status_info")
  private ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo;

  @JsonProperty("membership_info")
  private ChatSpaceMembershipInfo membershipInfo;

  @JsonProperty("user_like_info")
  private UserLikeInfo userLikeInfo;

  @JsonProperty("like_count_info")
  private LikeCountInfo likeCountInfo;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @JsonProperty("is_private")
  public boolean isPrivate() {
    return ChatSpaceVisibility.isPrivate(visibilityInfo.getVisibility());
  }

  @JsonIgnore
  private Long organizerId;

  @JsonIgnore
  private String spaceLinkUnMasked;

  @Override
  @JsonIgnore
  public Long getAuthorId() {
    return getOrganizerId();
  }

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

  @Override
  public void setIsOrganizer(final boolean isOrganizer) {
    this.organizer.setIsOrganizer(isOrganizer);
  }

  @Override
  public void setIsUpdatable(final boolean isUpdatable) {
    this.isUpdatable = isUpdatable;
  }

  @Override
  public void markAsUpdatable() {
    setIsUpdatable(true);
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
