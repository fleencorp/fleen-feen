package com.fleencorp.feen.model.domain.chat;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.join.request.AlreadyJoinedChatSpaceException;
import com.fleencorp.feen.exception.chat.space.join.request.RequestToJoinChatSpacePendingException;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberRemovedException;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class ChatSpaceMember extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "chat_space_member_id", nullable = false, updatable = false, unique = true)
  private Long chatSpaceMemberId;

  @Column(name = "parent_external_id_or_name", length = 1000)
  private String parentExternalIdOrName;

  @Column(name = "external_id_or_name", length = 1000)
  private String externalIdOrName;

  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = ChatSpace.class)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id", nullable = false, updatable = false)
  private ChatSpace chatSpace;

  @Column(name = "member_id", insertable = false, updatable = false)
  private Long memberId;

  @CreatedBy
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

  /**
   * Retrieves the email address of the member.
   *
   * <p>If the member is not null, returns the member's email address; otherwise, returns null.</p>
   *
   * @return the email address of the member or null if the member is not set.
   */
  public String getEmailAddress() {
    return nonNull(member) ? member.getEmailAddress() : null;
  }

  /**
   * Retrieves the full name of the member.
   *
   * <p>If the member is not null, returns the member's full name; otherwise, returns null.</p>
   *
   * @return the full name of the member or null if the member is not set.
   */
  public String getFullName() {
    return nonNull(member) ? member.getFullName() : null;
  }

  /**
   * Retrieves the username of the member.
   * If the member object is non-null, the username is returned. Otherwise, null is returned.
   *
   * @return the username of the member if the member is not null, otherwise null.
   */
  public String getUsername() {
    return nonNull(member) ? member.getUsername() : null;
  }

  /**
   * Retrieves the photo of the member.
   * If the member object is non-null, the photo is returned. Otherwise, null is returned.
   *
   * @return the photo of the member if the member is not null, otherwise null.
   */
  public String getProfilePhoto() {
    return nonNull(member) ? member.getProfilePhotoUrl() : null;
  }

  /**
   * Checks if the member has left the chat space.
   *
   * <p>This method checks if the member has left the chat space by verifying if the `left` field
   * is non-null and true. If the member has left, the field will be non-null, and the method will return true.</p>
   *
   * @return true if the member has left the chat space, false otherwise
   */
  public boolean hasLeft() {
    return nonNull(left) && left;
  }

  /**
   * Checks if the member is currently a member of the chat space.
   *
   * <p>This method checks if the member is still part of the chat space by ensuring they haven't been
   * removed and haven't left. If both conditions are satisfied (i.e., the member is not removed and
   * hasn't left), it returns true.</p>
   *
   * <p>This is useful to determine the current membership status of a user in the chat space.</p>
   *
   * @return true if the user is still a member of the chat space, false otherwise
   */
  public boolean isAMember() {
    return isRequestToJoinApproved() && !isRemoved() && !hasLeft();
  }

  /**
   * Checks if the member has been removed from the chat space.
   *
   * <p>This method checks if the member has been removed from the chat space by verifying if the `removed`
   * field is non-null and true. If the member has been removed, the field will be non-null, and the method will return true.</p>
   *
   * @return true if the member has been removed from the chat space, false otherwise
   */
  public boolean isRemoved() {
    return nonNull(removed) && removed;
  }

  /**
   * Checks if the member has not been removed from the chat space.
   *
   * <p>This method checks if the member has not been removed from the chat space by verifying if the `removed`
   * field is false. If the member has not been removed, the field will be false, and the method will return true.</p>
   *
   * @return true if the member has not been removed from the chat space, false otherwise
   */
  public boolean isNotRemoved() {
    return !isRemoved();
  }

  /**
   * Checks if the member is an admin of the chat space.
   *
   * <p>This method checks if the member has admin privileges within the chat space by verifying
   * if the `admin` field is non-null. If the user is an admin, the field will be non-null, and
   * the method will return true.</p>
   *
   * @return true if the member is an admin of the chat space, false otherwise
   */
  public boolean isAdmin() {
    return ChatSpaceMemberRole.isAdmin(role);
  }

  /**
   * Approves the join status of the member with an associated comment.
   *
   * <p>Sets the space admin comment to the provided comment and changes the join status to approved.</p>
   *
   */
  public void approveJoinRequest() {
    approveJoinStatus();
  }

  /**
   * Approves the join status of the member.
   *
   * <p>Changes the join status to approved without any comments.</p>
   */
  public void approveJoinStatus() {
    requestToJoinStatus = ChatSpaceRequestToJoinStatus.APPROVED;
    left = false;
    removed = false;
  }

  /**
   * Sets the join status to pending with an associated comment.
   *
   * <p>Stores the provided comment and changes the join status to pending.</p>
   *
   * @param comment the comment to associate with the pending status.
   */
  public void markJoinRequestAsPendingWithComment(final String comment) {
    memberComment = comment;
    markJoinRequestAsPending();
  }

  /**
   * Sets the join status to pending.
   *
   * <p>Changes the join status to indicate that the request to join is pending.</p>
   */
  public void markJoinRequestAsPending() {
    requestToJoinStatus = ChatSpaceRequestToJoinStatus.PENDING;
  }

  /**
   * Disapproves the request to join the chat space.
   *
   * <p>Changes the join status to indicate that the request to join has been disapproved.</p>
   */
  public void disapprovedJoinRequest() {
    requestToJoinStatus = ChatSpaceRequestToJoinStatus.DISAPPROVED;
  }

  /**
   * Handles the approval or disapproval of a join request based on the provided status.
   *
   * <p>If the status indicates approval, it proceeds to approve the join request.
   * If the status indicates disapproval, it proceeds to disapprove the join request.</p>
   *
   * @param chatSpaceRequestToJoinStatus the status of the join request (approved or disapproved)
   * @throws IllegalArgumentException if the status is neither approved nor disapproved
   */
  public void approveOrDisapproveJoinRequest(final ChatSpaceRequestToJoinStatus chatSpaceRequestToJoinStatus) {
    if (ChatSpaceRequestToJoinStatus.isApproved(chatSpaceRequestToJoinStatus)) {
      approveJoinRequest();
    } else if (ChatSpaceRequestToJoinStatus.isDisapproved(chatSpaceRequestToJoinStatus)) {
      disapprovedJoinRequest();
    }
  }

  /**
   * Checks if the request to join is pending.
   *
   * <p>Returns true if the current join status is pending; otherwise, returns false.</p>
   *
   * @return true if the request to join is pending, false otherwise.
   */
  public boolean isRequestToJoinPending() {
    return requestToJoinStatus == ChatSpaceRequestToJoinStatus.PENDING;
  }


  /**
   * Checks if the request to join has been approved.
   *
   * <p>Returns true if the current join status is approved; otherwise, returns false.</p>
   *
   * @return true if the request to join is approved, false otherwise.
   */
  public boolean isRequestToJoinApproved() {
    return requestToJoinStatus == ChatSpaceRequestToJoinStatus.APPROVED;
  }

  /**
   * Checks if the request to join has been disapproved.
   *
   * <p>Returns true if the current join status is disapproved; otherwise, returns false.</p>
   *
   * @return true if the request to join is disapproved, false otherwise.
   */
  public boolean isRequestToJoinDisapproved() {
    return requestToJoinStatus == ChatSpaceRequestToJoinStatus.DISAPPROVED;
  }

  /**
   * Checks if the request to join is either disapproved or pending.
   *
   * <p>Returns true if the current join status is disapproved or pending; otherwise, returns false.</p>
   *
   * @return true if the request to join is disapproved or pending, false otherwise.
   */
  public boolean isRequestToJoinDisapprovedOrPending() {
    return ChatSpaceRequestToJoinStatus.isDisapprovedOrPending(requestToJoinStatus);
  }

  /**
   * Updates the details of the chat space member.
   *
   * <p>Sets the parent external ID or name and the external ID or name for this member.</p>
   *
   * @param parentExternalIdOrName the external ID or name of the parent.
   * @param externalIdOrName the external ID or name of this member.
   */
  public void updateDetails(final String parentExternalIdOrName, final String externalIdOrName) {
    this.parentExternalIdOrName = parentExternalIdOrName;
    this.externalIdOrName = externalIdOrName;
  }

  /**
   * Upgrades the member's role to admin in the chat space.
   *
   * <p>This method changes the member's role to {@link ChatSpaceMemberRole#ADMIN} and sets the `admin`
   * field to true. It effectively grants the member admin privileges within the chat space.</p>
   *
   * <p>Use this method to promote a member to an admin in the chat space.</p>
   */
  public void upgradeRole() {
    role = ChatSpaceMemberRole.ADMIN;
  }

  /**
   * Downgrades the member's role to a regular member in the chat space.
   *
   * <p>This method changes the member's role to {@link ChatSpaceMemberRole#MEMBER} and sets the `admin`
   * field to false. It effectively revokes admin privileges and returns the member to a regular member
   * status within the chat space.</p>
   *
   * <p>Use this method to demote an admin back to a regular member in the chat space.</p>
   */
  public void downgradeRole() {
    role = ChatSpaceMemberRole.MEMBER;
  }

  /**
   * Marks the member as removed from the chat space.
   *
   * <p>This method sets the `removed` field to true, indicating that the member has been removed
   * from the chat space. This can be used to update the removal status of a user within the chat space.</p>
   */
  public void markAsRemoved() {
    removed = true;
  }

  /**
   * Marks the member as having left the chat space.
   *
   * <p>This method sets the `left` field to true, indicating that the member has voluntarily left
   * the chat space. This is useful for tracking users who have exited the chat space.</p>
   */
  public void leave() {
    left = true;
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

  /**
   * Ensures the chat space member has not been removed.
   *
   * @throws ChatSpaceMemberRemovedException if the member has been removed from the chat space
   */
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
    chatSpaceMember.setChatSpace(chatSpace);
    chatSpaceMember.setMember(member);
    chatSpaceMember.setRole(ChatSpaceMemberRole.MEMBER);

    return chatSpaceMember;
  }

  public static ChatSpaceMember ofOrganizer(final ChatSpace chatSpace, final Member member) {
    final ChatSpaceMember chatSpaceMember = new ChatSpaceMember();
    chatSpaceMember.setChatSpace(chatSpace);
    chatSpaceMember.setMember(member);
    chatSpaceMember.setRole(ChatSpaceMemberRole.ADMIN);
    chatSpaceMember.approveJoinStatus();

    return chatSpaceMember;
  }
}
