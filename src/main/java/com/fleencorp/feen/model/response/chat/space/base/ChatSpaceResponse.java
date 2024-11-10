package com.fleencorp.feen.model.response.chat.space.base;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import com.fleencorp.feen.constant.security.mask.MaskedChatSpaceUri;
import com.fleencorp.feen.constant.stream.JoinStatus;
import com.fleencorp.feen.model.other.Organizer;
import com.fleencorp.feen.model.response.base.FleenFeenResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.nonNull;

@SuperBuilder
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
  "is_active",
  "total_members",
  "total_request_to_join",
  "visibility",
  "organizer",
  "request_to_join_status",
  "join_status",
  "created_on",
  "updated_on"
})
@Slf4j
public class ChatSpaceResponse extends FleenFeenResponse {

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

  @JsonIgnore
  private String spaceLinkUnMasked;

  @JsonProperty("is_active")
  private Boolean isActive;

  @JsonProperty("total_members")
  private Long totalMembers;

  @JsonProperty("total_request_to_join")
  private Long totalRequestToJoin;

  @JsonFormat(shape = STRING)
  @JsonProperty("visibility")
  private ChatSpaceVisibility visibility;

  @JsonProperty("organizer")
  private Organizer organizer;

  @JsonFormat(shape = STRING)
  @JsonProperty("request_to_join_status")
  private ChatSpaceRequestToJoinStatus requestToJoinStatus;

  @Builder.Default
  @JsonProperty("join_status")
  private String joinStatus = JoinStatus.NOT_JOINED.getValue();

  @JsonProperty("space_link_unmasked")
  public String getSpaceLinkUnmasked() {
    disableAndResetUnmaskedLinkIfNotApproved();
    return spaceLinkUnMasked;
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
    if (nonNull(joinStatus) && JoinStatus.isNotApproved(joinStatus)) {
      spaceLinkUnMasked = null;
    }
  }
}
