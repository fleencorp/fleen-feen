package com.fleencorp.feen.model.domain.chat;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
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

  @Column(name = "member_comment", length = 1000)
  private String memberComment;

  @Column(name = "space_admin_comment", length = 1000)
  private String spaceAdminComment;

  /**
   * Retrieves the member ID.
   *
   * <p>If the member is not null, returns the member's ID; otherwise, returns null.</p>
   *
   * @return the member ID or null if the member is not set.
   */
  public Long getMemberId() {
    return nonNull(member) ? member.getMemberId() : null;
  }

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
   * Approves the join status of the member with an associated comment.
   *
   * <p>Sets the space admin comment to the provided comment and changes the join status to approved.</p>
   *
   * @param comment the comment to associate with the approval.
   */
  public void approveJoinStatusWithComment(final String comment) {
    spaceAdminComment = comment;
    approveJoinStatus();
  }

  /**
   * Approves the join status of the member.
   *
   * <p>Changes the join status to approved without any comments.</p>
   */
  public void approveJoinStatus() {
    requestToJoinStatus = ChatSpaceRequestToJoinStatus.APPROVED;
  }

  /**
   * Sets the join status to pending with an associated comment.
   *
   * <p>Stores the provided comment and changes the join status to pending.</p>
   *
   * @param comment the comment to associate with the pending status.
   */
  public void pendingJoinStatusWithComment(final String comment) {
    memberComment = comment;
    pendingJoinStatus();
  }

  /**
   * Sets the join status to pending.
   *
   * <p>Changes the join status to indicate that the request to join is pending.</p>
   */
  public void pendingJoinStatus() {
    requestToJoinStatus = ChatSpaceRequestToJoinStatus.PENDING;
  }

  /**
   * Disapproves the request to join the chat space.
   *
   * <p>Changes the join status to indicate that the request to join has been disapproved.</p>
   */
  public void disapprovedRequestToJoin() {
    requestToJoinStatus = ChatSpaceRequestToJoinStatus.DISAPPROVED;
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
   * Upgrades the role of the chat space member to ADMIN.
   *
   * <p>This method sets the member's role to ADMIN.</p>
   */
  public void upgradeRole() {
    role = ChatSpaceMemberRole.ADMIN;
  }

  /**
   * Downgrades the role of the chat space member to MEMBER.
   *
   * <p>This method sets the member's role to MEMBER.</p>
   */
  public void downgradeRole() {
    role = ChatSpaceMemberRole.MEMBER;
  }


  public static ChatSpaceMember of(final Long chatSpaceMemberId) {
    final ChatSpaceMember chatSpaceMember = new ChatSpaceMember();
    chatSpaceMember.setChatSpaceMemberId(chatSpaceMemberId);

    return chatSpaceMember;
  }

  public static ChatSpaceMember of(final ChatSpace chatSpace, final Member member) {
    final ChatSpaceMember chatSpaceMember = of(chatSpace, member, null);
    chatSpaceMember.setRole(ChatSpaceMemberRole.ADMIN);
    chatSpaceMember.approveJoinStatus();
    return chatSpaceMember;
  }

  public static ChatSpaceMember of(final ChatSpace chatSpace, final Member member, final String comment) {
    final ChatSpaceMember chatSpaceMember = new ChatSpaceMember();
    chatSpaceMember.setChatSpace(chatSpace);
    chatSpaceMember.setMember(member);
    chatSpaceMember.setRole(ChatSpaceMemberRole.MEMBER);
    chatSpaceMember.setMemberComment(comment);

    return chatSpaceMember;
  }
}
