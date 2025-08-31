package com.fleencorp.feen.stream.model.domain;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;

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

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = FleenStream.class)
  @JoinColumn(name = "stream_id", referencedColumnName = "stream_id", nullable = false, updatable = false)
  private FleenStream stream;

  @Column(name = "member_id", nullable = false, updatable = false, insertable = false)
  private Long memberId;

  @ToString.Exclude
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

  public String getEmailAddress() {
    return nonNull(member) ? member.getEmailAddress() : null;
  }

  public String getFullName() {
    return nonNull(member) ? member.getFullName() : null;
  }

  public String getUsername() {
    return nonNull(member) ? member.getUsername() : null;
  }

  public String getProfilePhoto() {
    return nonNull(member) ? member.getProfilePhotoUrl() : null;
  }

  public void approveUserAttendance() {
    requestToJoinStatus = StreamAttendeeRequestToJoinStatus.APPROVED;
    attending = true;
  }

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

  public boolean isRequestToJoinPending() {
    return StreamAttendeeRequestToJoinStatus.isPending(requestToJoinStatus);
  }

  public boolean isRequestToJoinDisapproved() {
    return StreamAttendeeRequestToJoinStatus.isDisapproved(requestToJoinStatus);
  }

  public boolean isRequestToJoinDisapprovedOrPending() {
    return isRequestToJoinDisapproved() || isRequestToJoinPending();
  }

  public boolean isRequestToJoinApproved() {
    return StreamAttendeeRequestToJoinStatus.isApproved(requestToJoinStatus);
  }

  public void markAsNotAttending() {
    // Update the attendance status to false
    attending = false;
  }

  public void markRequestAsPending() {
    if (stream.isPrivateOrProtected()) {
      requestToJoinStatus = StreamAttendeeRequestToJoinStatus.PENDING;
    }
  }

  public void markAsOrganizer() {
    aSpeaker = false;
    isOrganizer = true;
  }

  public void markAsNonSpeaker() {
    aSpeaker = false;
  }

  public boolean isAttending() {
    return nonNull(attending) && attending;
  }

  public boolean isASpeaker() {
    return nonNull(aSpeaker) && aSpeaker;
  }

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
