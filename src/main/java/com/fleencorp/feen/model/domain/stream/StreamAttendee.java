package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

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

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @Enumerated(STRING)
  @Column(name = "request_to_join_status", nullable = false)
  private StreamAttendeeRequestToJoinStatus streamAttendeeRequestToJoinStatus;

  @Builder.Default
  @Column(name = "is_attending", nullable = false)
  private Boolean isAttending = false;

  @Column(name = "attendee_comment", length = 1000)
  private String attendeeComment;

  @Column(name = "organizer_comment", length = 1000)
  private String organizerComment;

  public static StreamAttendee of(final Member member, final FleenStream stream) {
    return StreamAttendee.builder()
      .member(member)
      .fleenStream(stream)
      .build();
  }
}
