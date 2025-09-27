package com.fleencorp.feen.chat.space.model.domain;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.member.ChatSpaceMemberRole;
import com.fleencorp.feen.chat.space.exception.member.ChatSpaceMemberRemovedException;
import com.fleencorp.feen.chat.space.exception.request.AlreadyJoinedChatSpaceException;
import com.fleencorp.feen.chat.space.exception.request.RequestToJoinChatSpacePendingException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpaceMember;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_space_member")
public class ChatSpaceMember extends FleenFeenEntity
  implements IsAChatSpaceMember {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "chat_space_member_id", nullable = false, updatable = false, unique = true)
  private Long chatSpaceMemberId;

  @Column(name = "parent_external_id_or_name", length = 1000)
  private String parentExternalIdOrName;

  @Column(name = "external_id_or_name", length = 1000)
  private String externalIdOrName;

  @Column(name = "chat_space_id", insertable = false, updatable = false)
  private Long chatSpaceId;

  @ToString.Exclude
  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = ChatSpace.class)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id", nullable = false, updatable = false)
  private ChatSpace chatSpace;

  @Column(name = "member_id", insertable = false, updatable = false)
  private Long memberId;

  @ToString.Exclude
  @ManyToOne(fetch = EAGER, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @Enumerated(STRING)
  @Column(name = "role", nullable = false)
  private ChatSpaceMemberRole role;

  @Enumerated(STRING)
  @Column(name = "request_to_join_status", nullable = false)
  private ChatSpaceRequestToJoinStatus requestToJoinStatus;

  @Column(name = "has_left", nullable = false)
  private Boolean left = false;

  @Column(name = "is_removed", nullable = false)
  private Boolean removed = false;

  @Column(name = "member_comment", length = 1000)
  private String memberComment;

  @Column(name = "space_admin_comment", length = 1000)
  private String spaceAdminComment;

  @Override
  public String getEmailAddress() {
    return nonNull(member) ? member.getEmailAddress() : null;
  }

  @Override
  public String getFullName() {
    return nonNull(member) ? member.getFullName() : null;
  }

  @Override
  public String getUsername() {
    return nonNull(member) ? member.getUsername() : null;
  }

  @Override
  public String getProfilePhoto() {
    return nonNull(member) ? member.getProfilePhotoUrl() : null;
  }

  @Override
  public Boolean hasLeft() {
    return nonNull(left) && left;
  }

  @Override
  public boolean isAMember() {
    return isRequestToJoinApproved() && !isRemoved() && !hasLeft();
  }

  @Override
  public boolean isRemoved() {
    return nonNull(removed) && removed;
  }

  @Override
  public boolean isAdmin() {
    return ChatSpaceMemberRole.isAdmin(role);
  }

  private boolean isRequestToJoinPending() {
    return ChatSpaceRequestToJoinStatus.isPending(requestToJoinStatus);
  }

  private boolean isRequestToJoinApproved() {
    return ChatSpaceRequestToJoinStatus.isApproved(requestToJoinStatus);
  }

  public boolean isRequestToJoinDisapproved() {
    return ChatSpaceRequestToJoinStatus.isDisapproved(requestToJoinStatus);
  }

  public boolean isRequestToJoinDisapprovedOrPending() {
    return ChatSpaceRequestToJoinStatus.isDisapprovedOrPending(requestToJoinStatus);
  }

  public void approveJoinRequest() {
    approveAndUpdateJoinStatus();
  }

  private void approveAndUpdateJoinStatus() {
    requestToJoinStatus = ChatSpaceRequestToJoinStatus.APPROVED;
    left = false;
    removed = false;
  }

  public void markJoinRequestAsPendingWithComment(final String comment) {
    memberComment = comment;
    markJoinRequestAsPending();
  }

  private void markJoinRequestAsPending() {
    requestToJoinStatus = ChatSpaceRequestToJoinStatus.PENDING;
  }

  private void disapprovedJoinRequest() {
    requestToJoinStatus = ChatSpaceRequestToJoinStatus.DISAPPROVED;
  }

  public void approveOrDisapproveJoinRequest(final ChatSpaceRequestToJoinStatus chatSpaceRequestToJoinStatus) {
    if (ChatSpaceRequestToJoinStatus.isApproved(chatSpaceRequestToJoinStatus)) {
      approveJoinRequest();
    } else if (ChatSpaceRequestToJoinStatus.isDisapproved(chatSpaceRequestToJoinStatus)) {
      disapprovedJoinRequest();
    }
  }

  public void upgradeRole() {
    role = ChatSpaceMemberRole.ADMIN;
  }

  public void downgradeRole() {
    role = ChatSpaceMemberRole.MEMBER;
  }

  public void markAsRemoved() {
    removed = true;
  }

  public void leave() {
    left = true;
  }

  public void updateDetails(final String parentExternalIdOrName, final String externalIdOrName) {
    this.parentExternalIdOrName = parentExternalIdOrName;
    this.externalIdOrName = externalIdOrName;
  }

  /**
   * Checks if the member is not the admin (organizer) of the chat space.
   *
   * <p>This method verifies whether the current member is part of the chat space and
   * whether their `memberId` does not match the `organizerId`, implying that the member
   * is not the admin (organizer).</p>
   *
   * <p>It returns true if the member is part of the chat space and their `memberId` is
   * different from the `organizerId`. Otherwise, it returns false if the member is either
   * not part of the chat space or they are the organizer.</p>
   *
   * @param organizerId the ID of the chat space organizer to compare with the member's ID.
   * @throws FailedOperationException if the user trying to leave is the organizer of the chat space
   */
  public void checkIsEligibleToLeave(final Long organizerId) throws FailedOperationException {
    if (isAMember() && nonNull(memberId) && memberId.equals(organizerId)) {
      throw FailedOperationException.of();
    }
  }

  public void checkNotRemoved() {
    if (isRemoved()) {
      throw ChatSpaceMemberRemovedException.of(chatSpaceMemberId);
    }
  }

  public void checkIsNotApprovedOrPending() {
    // Check if the join request has been approved
    if (isRequestToJoinApproved() && isAMember()) {
      throw new AlreadyJoinedChatSpaceException();
    }
    // Check if the join request is still pending
    else if (isRequestToJoinPending()) {
      throw new RequestToJoinChatSpacePendingException();
    }
  }

  public static ChatSpaceMember of(final Long chatSpaceMemberId) {
    final ChatSpaceMember chatSpaceMember = new ChatSpaceMember();
    chatSpaceMember.setChatSpaceMemberId(chatSpaceMemberId);

    return chatSpaceMember;
  }

  public static ChatSpaceMember of(final ChatSpace chatSpace, final Member member) {
    final ChatSpaceMember chatSpaceMember = new ChatSpaceMember();
    chatSpaceMember.setChatSpaceId(chatSpace.getChatSpaceId());
    chatSpaceMember.setChatSpace(chatSpace);
    chatSpaceMember.setMemberId(member.getMemberId());
    chatSpaceMember.setMember(member);
    chatSpaceMember.setRole(ChatSpaceMemberRole.MEMBER);

    return chatSpaceMember;
  }

  public static ChatSpaceMember ofOrganizer(final ChatSpace chatSpace, final Member member) {
    final ChatSpaceMember chatSpaceMember = new ChatSpaceMember();
    chatSpaceMember.setChatSpaceId(chatSpace.getChatSpaceId());
    chatSpaceMember.setChatSpace(chatSpace);
    chatSpaceMember.setMemberId(member.getMemberId());
    chatSpaceMember.setMember(member);
    chatSpaceMember.setRole(ChatSpaceMemberRole.ADMIN);
    chatSpaceMember.approveAndUpdateJoinStatus();

    return chatSpaceMember;
  }
}
