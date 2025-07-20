package com.fleencorp.feen.stream.model.domain;

import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Table(name = "stream_attendee")
public class StreamAttendee extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "stream_attendee_id", nullable = false, updatable = false, unique = true)
  private Long attendeeId;

  @Column(name = "stream_id", nullable = false, updatable = false, insertable = false)
  private Long streamId;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = FleenStream.class)
  @JoinColumn(name = "stream_id", referencedColumnName = "stream_id", nullable = false, updatable = false)
  private FleenStream stream;

  @Column(name = "member_id", nullable = false, updatable = false, insertable = false)
  private Long memberId;

  @ManyToOne(fetch = EAGER, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @Enumerated(STRING)
  @Column(name = "request_to_join_status", nullable = false)
  private StreamAttendeeRequestToJoinStatus requestToJoinStatus;

  @Column(name = "is_attending", nullable = false)
  private Boolean attending = false;

  @Column(name = "is_a_speaker", nullable = false)
  private Boolean aSpeaker = false;

  @Column(name = "is_organizer", nullable = false)
  private Boolean isOrganizer = false;

  @Column(name = "attendee_comment", length = 1000)
  private String attendeeComment;

  @Column(name = "organizer_comment", length = 1000)
  private String organizerComment;

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

  /**
   * Retrieves the username of the member.
   *
   * @return the username if the member is not null; otherwise, null.
   */
  public String getUsername() {
    // Return the username of the member if the member is not null
    return nonNull(member) ? member.getUsername() : null;
  }

  public String getProfilePhoto() {
    return nonNull(member) ? member.getProfilePhotoUrl() : null;
  }

  /**
   * Approves the user's attendance for a event or stream.
   * This method sets the user's request to join status to {@link StreamAttendeeRequestToJoinStatus#APPROVED}
   * and marks the user as attending by setting {@code attending} to {@code true}.
   */
  public void approveUserAttendance() {
    requestToJoinStatus = StreamAttendeeRequestToJoinStatus.APPROVED;
    attending = true;
  }

  /**
   * Disapprove the user's attendance for a event or stream.
   * This method sets the user's request to join status to {@link StreamAttendeeRequestToJoinStatus#DISAPPROVED}
   * and marks the user as not attending by setting {@code attending} to {@code false}.
   */
  public void disapproveUserAttendance() {
    requestToJoinStatus = StreamAttendeeRequestToJoinStatus.DISAPPROVED;
    attending = false;
  }

  /**
   * Approves the attendee's request to join by the organizer.
   *
   * <p></p>This method sets the attendee's request-to-join status to {@code APPROVED}
   * when the organizer manually decides to add the attendee. The organizer
   * can provide a comment, which will be stored as part of the action.</p>
   *
   * @param organizerComment the comment provided by the organizer regarding
   *                         the approval of the attendee's request to join.
   */
  public void approvedByOrganizer(final String organizerComment) {
    requestToJoinStatus = StreamAttendeeRequestToJoinStatus.APPROVED;
    this.organizerComment = organizerComment;
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
   * Checks whether the request to join the stream is either disapproved or still pending.
   *
   * <p>This method returns {@code true} if the request to join the stream has been disapproved or is still awaiting
   * approval. Otherwise, it returns {@code false}.</p>
   *
   * @return {@code true} if the request is disapproved or pending, {@code false} otherwise
   */
  public boolean isRequestToJoinDisapprovedOrPending() {
    return isRequestToJoinDisapproved() || isRequestToJoinPending();
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
    attending = false;
  }

  /**
   * Marks the request-to-join status as pending.
   *
   * <p>This method sets the current {@code requestToJoinStatus} of the attendee to
   * {@code StreamAttendeeRequestToJoinStatus.PENDING}, indicating that their request is pending approval.</p>
   */
  public void markRequestAsPending() {
    if (stream.isPrivateOrProtected()) {
      requestToJoinStatus = StreamAttendeeRequestToJoinStatus.PENDING;
    }
  }

  /**
   * Marks the user as both an organizer and a speaker.
   *
   * <p>This method sets the internal flag indicating that the user is a speaker.
   * It assumes that the user is already designated as an organizer.</p>
   */
  public void markAsOrganizer() {
    aSpeaker = false;
    isOrganizer = true;
  }

  /**
   * Marks the entity as a non-speaker.
   *
   * <p>This method updates the speaker status of the entity by setting the
   * {@code aSpeaker} flag to {@code false}, indicating that the entity
   * is no longer a speaker.</p>
   */
  public void markAsNonSpeaker() {
    aSpeaker = false;
  }

  /**
   * Returns whether the attendee is currently attending.
   *
   * @return true if the attendee is attending, false otherwise.
   */
  public boolean isAttending() {
    return nonNull(attending) && attending;
  }

  /**
   * Returns whether the attendee is a speaker.
   *
   * @return true if the attendee is a speaker, false otherwise.
   */
  public boolean isASpeaker() {
    return nonNull(aSpeaker) && aSpeaker;
  }

  /**
   * Returns whether the attendee is the organizer.
   *
   * @return true if the attendee is the organize of the stream, false otherwise.
   */
  public boolean isOrganizer() {
    return nonNull(isOrganizer) && isOrganizer;
  }

  public static StreamAttendee of(final String attendeeId) {
    final StreamAttendee attendee = new StreamAttendee();
    attendee.setAttendeeId(Long.valueOf(attendeeId));

    return attendee;
  }

  public static StreamAttendee of(final Member member, final FleenStream stream) {
    final StreamAttendee attendee = new StreamAttendee();
    attendee.setMember(member);
    attendee.setStream(stream);

    return attendee;
  }

  public static StreamAttendee of(final Member member, final FleenStream stream, final String comment) {
    final StreamAttendee streamAttendee = of(member, stream);
    streamAttendee.setAttendeeComment(comment);

    return streamAttendee;
  }

  public static StreamAttendee empty() {
    return null;
  }

}
