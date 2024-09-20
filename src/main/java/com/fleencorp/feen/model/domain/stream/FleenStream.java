package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.base.converter.impl.security.StringCryptoConverter;
import com.fleencorp.feen.constant.stream.StreamCreationType;
import com.fleencorp.feen.constant.stream.StreamSource;
import com.fleencorp.feen.constant.stream.StreamStatus;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.fleencorp.feen.constant.stream.StreamVisibility.PRIVATE;
import static com.fleencorp.feen.constant.stream.StreamVisibility.PROTECTED;
import static jakarta.persistence.CascadeType.ALL;
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
@Table(name = "fleen_stream")
public class FleenStream extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "fleen_stream_id", nullable = false, updatable = false, unique = true)
  private Long fleenStreamId;

  @Column(name = "external_id")
  private String externalId;

  @Column(name = "title", nullable = false, length = 500)
  private String title;

  @Column(name = "description", nullable = false, length = 3000)
  private String description;

  @Column(name = "tags", length = 300)
  private String tags;

  @Column(name = "location", nullable = false, length = 100)
  private String location;

  @Column(name = "timezone", nullable = false, length = 30)
  private String timezone;

  @Column(name = "organizer_name", nullable = false, updatable = false, length = 100)
  private String organizerName;

  @Column(name = "organizer_email", nullable = false, updatable = false, length = 50)
  private String organizerEmail;

  @Column(name = "organizer_phone", nullable = false, updatable = false, length = 20)
  private String organizerPhone;

  @Default
  @Column(name = "made_for_kids", nullable = false)
  private Boolean forKids = false;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;

  @Column(name = "stream_link", length = 1000)
  @Convert(converter = StringCryptoConverter.class)
  private String streamLink;

  @Column(name = "thumbnail_link", length = 1000)
  private String thumbnailLink;

  @Enumerated(STRING)
  @Column(name = "stream_source", nullable = false)
  private StreamSource streamSource;

  @Enumerated(STRING)
  @Column(name = "stream_creation_type", nullable = false)
  private StreamCreationType streamCreationType;

  @Enumerated(STRING)
  @Column(name = "stream_visibility", nullable = false)
  private StreamVisibility streamVisibility;

  @Enumerated(STRING)
  @Column(name = "stream_status", nullable = false)
  private StreamStatus streamStatus;

  @Column(name = "scheduled_start_date", nullable = false)
  private LocalDateTime scheduledStartDate;

  @Column(name = "scheduled_end_date", nullable = false)
  private LocalDateTime scheduledEndDate;

  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @Default
  @OneToMany(fetch = EAGER, cascade = ALL, targetEntity = StreamAttendee.class, mappedBy = "fleenStream")
  private Set<StreamAttendee> attendees = new HashSet<>();

  public Set<StreamAttendee> getAttendees() {
    return nonNull(attendees) ? attendees : new HashSet<>();
  }

  /**
   * Updates the details of the current stream with the provided values.
   *
   * <p>This method sets the title, description, tags, and location of the current stream to the given values.
   * It can be used to modify the properties of an existing object.</p>
   *
   * @param title       the new title to be set
   * @param description the new description to be set
   * @param tags        the new tags to be set
   * @param location    the new location to be set
   */
  public void update(final String title, final String description, final String tags, final String location) {
    this.title = title;
    this.description = description;
    this.tags = tags;
    this.location = location;
  }

  /**
   * Updates the external ID and stream link of the current stream with the provided values.
   *
   * <p>This method sets the externalId and streamLink fields of the current instance to the given values.
   * It can be used to modify these properties of an existing object.</p>
   *
   * @param externalId the new external ID to be set
   * @param streamLink the new stream link to be set
   */
  public void updateDetails(final String externalId, final String streamLink) {
    this.externalId = externalId;
    this.streamLink = streamLink;
  }

  /**
   * Updates the organizer details of the current stream with the provided values.
   *
   * <p>This method sets the organizerName, organizerEmail, and organizerPhone fields of the current instance
   * to the given values. It can be used to modify these properties of an existing object.</p>
   *
   * @param organizerName the new name of the organizer to be set
   * @param organizerEmail the new email of the organizer to be set
   * @param organizerPhone the new phone number of the organizer to be set
   */
  public void updateDetails(final String organizerName, final String organizerEmail, final String organizerPhone) {
    this.organizerName = organizerName;
    this.organizerEmail = organizerEmail;
    this.organizerPhone = organizerPhone;
  }

  /**
   * Updates the schedule details of the current instance with the provided values.
   *
   * <p>This method sets the scheduledStartDate, scheduledEndDate, and timezone fields of the current instance
   * to the given values. It can be used to modify the schedule of an existing object.</p>
   *
   * @param scheduledStartDate the new start date and time of the schedule to be set
   * @param scheduledEndDate the new end date and time of the schedule to be set
   * @param timezone the new timezone of the schedule to be set
   */
  public void updateSchedule(final LocalDateTime scheduledStartDate, final LocalDateTime scheduledEndDate, final String timezone) {
    this.scheduledStartDate = scheduledStartDate;
    this.scheduledEndDate = scheduledEndDate;
    this.timezone = timezone;
  }

  /**
   * Marks the entity as deleted by setting the {@code isDeleted} flag to {@code true}.
   */
  public void delete() {
    this.isDeleted = true;
  }

  /**
   * Cancels the stream by setting its status to {@link StreamStatus#CANCELLED}.
   */
  public void cancel() {
    this.streamStatus = StreamStatus.CANCELLED;
  }

  /**
   * Checks if the stream has restricted visibility.
   *
   * @return {@code true} if the stream's visibility is either {@link StreamVisibility#PRIVATE}
   *         or {@link StreamVisibility#PROTECTED}; {@code false} otherwise.
   */
  public boolean isPrivate() {
    return streamVisibility == PRIVATE || streamVisibility == PROTECTED;
  }

  /**
   * Checks if the stream visibility is exactly PRIVATE.
   *
   * @return {@code true} if the {@code streamVisibility} is PRIVATE; {@code false} otherwise
   */
  public boolean isJustPrivate() {
    return streamVisibility == PRIVATE;
  }

  /**
   * Checks if the stream is canceled.
   * This method returns {@code true} if the stream's status is set to
   * {@link StreamStatus#CANCELLED}; otherwise, it returns {@code false}.
   *
   * @return {@code true} if the stream is canceled; {@code false} otherwise
   */
  public boolean isCanceled() {
    return streamStatus == StreamStatus.CANCELLED;
  }

  /**
   * Checks if the current time is within the scheduled start and end dates, indicating the event is ongoing.
   *
   * @return {@code true} if the current time is equal to or after the {@code scheduledStartDate}
   *         and before the {@code scheduledEndDate}; {@code false} otherwise
   */
  public boolean isOngoing() {
    final LocalDateTime now = LocalDateTime.now();
    return (now.isEqual(scheduledStartDate) || now.isAfter(scheduledStartDate)) && now.isBefore(scheduledEndDate);
  }

  /**
   * Checks if the current time is after the scheduled end date, indicating the event has ended.
   *
   * @return {@code true} if the current time is after the {@code scheduledEndDate}; {@code false} otherwise
   */
  public boolean hasEnded() {
    final LocalDateTime now = LocalDateTime.now();
    return now.isAfter(scheduledEndDate);
  }

  public Long getMemberId() {
    return nonNull(member) ? member.getMemberId() : null;
  }

  public boolean isAnEvent() {
    return streamSource == StreamSource.GOOGLE_MEET;
  }

  public static FleenStream of(final Long streamId) {
    return FleenStream.builder()
        .fleenStreamId(streamId)
        .build();
  }
}
