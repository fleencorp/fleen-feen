package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stream_attendee")
public class StreamAttendee extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "stream_attendee_id", nullable = false, updatable = false, unique = true)
  private Long streamAttendeeId;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = FleenStream.class)
  @JoinColumn(name = "fleen_stream_id", referencedColumnName = "fleen_stream_id", nullable = false, updatable = false)
  private FleenStream fleenStream;

  @ManyToOne(fetch = EAGER, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @Enumerated(STRING)
  @Column(name = "request_to_join_status", nullable = false)
  private StreamAttendeeRequestToJoinStatus requestToJoinStatus;

  @Builder.Default
  @Column(name = "is_attending", nullable = false)
  private Boolean isAttending = false;

  @Column(name = "attendee_comment", length = 1000)
  private String attendeeComment;

  @Column(name = "organizer_comment", length = 1000)
  private String organizerComment;

  /**
   * Retrieves the member ID of the attendee.
   *
   * @return the member ID if the member is not null; otherwise, null.
   */
  public Long getMemberId() {
    // Return the member ID of the attendee if the member is not null
    return nonNull(member) ? member.getMemberId() : null;
  }

  /**
   * Retrieves the email address of the associated member.
   * This method returns the email address of the member if the {@code member} object is not {@code null}.
   * If the member is {@code null}, the method returns {@code null}.
   *
   * @return the email address of the member, or {@code null} if the member is not available.
   */
  public String getEmailAddress() {
    return nonNull(member) ? member.getEmailAddress() : null;
  }

  /**
   * Retrieves the full name of the member.
   *
   * @return the full name if the member is not null; otherwise, null.
   */
  public String getFullName() {
    // Return the full name of the member if the member is not null
    return nonNull(member) ? member.getFullName() : null;
  }

  public static StreamAttendee of(final Member member, final FleenStream stream) {
    return StreamAttendee.builder()
      .member(member)
      .fleenStream(stream)
      .build();
  }

  /**
   * Updates the request status for joining a event or stream and sets the organizer's comment.
   * This method updates the status of the user's request to join by setting it to the provided
   * {@link StreamAttendeeRequestToJoinStatus}. It also sets the organizer's comment to the provided string.
   *
   * @param requestToJoinStatus The new status of the user's request to join, represented by
   *                            {@link StreamAttendeeRequestToJoinStatus}.
   * @param organizerComment The comment provided by the organizer regarding the user's request.
   */
  public void updateRequestStatusAndSetOrganizerComment(final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final String organizerComment) {
    this.requestToJoinStatus = requestToJoinStatus;
    this.organizerComment = organizerComment;
  }

  /**
   * Approves the user's attendance for a event or stream.
   * This method sets the user's request to join status to {@link StreamAttendeeRequestToJoinStatus#APPROVED}
   * and marks the user as attending by setting {@code isAttending} to {@code true}.
   */
  public void approveUserAttendance() {
    requestToJoinStatus = StreamAttendeeRequestToJoinStatus.APPROVED;
    isAttending = true;
  }

  /**
   * Determines if the attendee's request to join status is pending.
   *
   * @return {@code true} if the attendee's request to join status is {@link StreamAttendeeRequestToJoinStatus#PENDING},
   *         otherwise {@code false}.
   */
  public boolean isRequestToJoinPending() {
    return StreamAttendeeRequestToJoinStatus.isPending(requestToJoinStatus);
  }

  /**
   * Checks if the status of the stream attendee request is DISAPPROVED.
   *
   * @return {@code true} if the {@code streamAttendeeRequestToJoinStatus} is DISAPPROVED; {@code false} otherwise
   */
  public boolean isRequestToJoinDisapproved() {
    return StreamAttendeeRequestToJoinStatus.isDisapproved(requestToJoinStatus);
  }

  /**
   * Checks if the request to join a stream or event has been approved.
   *
   * <p>This method evaluates the current status of the request to join and returns
   * {@code true} if the request has been approved, otherwise it returns {@code false}.</p>
   *
   * @return {@code true} if the request to join has been approved, {@code false} otherwise.
   */
  public boolean isRequestToJoinApproved() {
    return StreamAttendeeRequestToJoinStatus.isApproved(requestToJoinStatus);
  }

  /**
   * Sets the attendance status of the current user to indicate they are not attending.
   */
  public void markAsNotAttending() {
    // Update the attendance status to false
    isAttending = false;
  }

  /**
   * Marks the request-to-join status as pending.
   *
   * <p>This method sets the current {@code requestToJoinStatus} of the attendee to
   * {@code StreamAttendeeRequestToJoinStatus.PENDING}, indicating that their request is pending approval.</p>
   */
  public void markRequestAsPending() {
    requestToJoinStatus = StreamAttendeeRequestToJoinStatus.PENDING;
  }

  /**
   * Returns whether the attendee is currently attending.
   *
   * @return true if the attendee is attending, false otherwise.
   */
  public boolean isAttending() {
    return getIsAttending();
  }

}
