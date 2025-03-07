package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stream_speaker")
public class StreamSpeaker extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "stream_speaker_id", nullable = false, updatable = false, unique = true)
  private Long speakerId;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = FleenStream.class)
  @JoinColumn(name = "fleen_stream_id", referencedColumnName = "fleen_stream_id", nullable = false, updatable = false)
  private FleenStream stream;

  @Column(name = "attendee_id", updatable = false, insertable = false)
  private Long attendeeId;

  @ManyToOne(fetch = LAZY, targetEntity = StreamAttendee.class)
  @JoinColumn(name = "attendee_id", referencedColumnName = "stream_attendee_id")
  private StreamAttendee attendee;

  @Column(name = "member_id", updatable = false, insertable = false)
  private Long memberId;

  @ManyToOne(fetch = LAZY, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id")
  private Member member;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "title", length = 100)
  private String title;

  @Column(name = "description", length = 1000)
  private String description;

  /**
   * Retrieves the name of the member.
   * If the full name is not null or blank, it returns the full name; otherwise, it returns the provided default name.
   *
   * @param defaultFullName the default name to return if the full name is null or blank
   * @return the full name if valid; otherwise, the default name.
   */
  public String getName(final String defaultFullName) {
    if (nonNull(fullName) && !(fullName.trim().isBlank())) {
      return fullName;
    }
    return defaultFullName;
  }

  public boolean hasSpeakerId() {
    return nonNull(getSpeakerId());
  }

  public boolean hasNoMember() {
    return isNull(getMemberId());
  }

  public boolean isNotOrganizer(Long organizerId) {
    return nonNull(memberId) && !(memberId.equals(organizerId));
  }

  /**
   * Updates the speaker's information with the provided full name, title, and description.
   *
   * @param fullName the updated full name of the speaker.
   * @param title the updated title of the speaker.
   * @param description the updated description of the speaker.
   */
  public void update(final String fullName, final String title, final String description) {
    this.fullName = fullName;
    this.title = title;
    this.description = description;
  }

  public static StreamSpeaker of(final Long streamSpeakerId) {
    final StreamSpeaker speaker = new StreamSpeaker();
    speaker.setSpeakerId(streamSpeakerId);
    return speaker;
  }
}
