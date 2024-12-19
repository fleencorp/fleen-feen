package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.base.converter.impl.security.StringCryptoConverter;
import com.fleencorp.feen.constant.security.mask.MaskedStreamLinkUri;
import com.fleencorp.feen.constant.stream.*;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.fleencorp.feen.constant.stream.StreamVisibility.*;
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

  @Column(name = "stream_link", length = 1000)
  @Convert(converter = StringCryptoConverter.class)
  private String streamLink;

  @Column(name = "thumbnail_link", length = 1000)
  private String thumbnailLink;

  @Enumerated(STRING)
  @Column(name = "stream_source", nullable = false)
  private StreamSource streamSource;

  @Enumerated(STRING)
  @Column(name = "stream_type", nullable = false)
  private StreamType streamType;

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

  @Column(name = "other_details", length = 3000)
  private String otherDetails;

  @Column(name = "other_link", length = 1000)
  private String otherLink;

  @Column(name = "group_or_organization_name", length = 1000)
  private String groupOrOrganizationName;

  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @Builder.Default
  @OneToMany(fetch = EAGER, cascade = ALL, targetEntity = StreamAttendee.class, mappedBy = "fleenStream")
  private Set<StreamAttendee> attendees = new HashSet<>();

  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = ChatSpace.class)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id", nullable = false, updatable = false)
  private ChatSpace chatSpace;

  @Builder.Default
  @Column(name = "made_for_kids", nullable = false)
  private Boolean forKids = false;

  @Builder.Default
  @Column(name = "is_deleted", nullable = false)
  private Boolean deleted = false;

  @Builder.Default
  @Column(name = "total_attendees", nullable = false)
  private Long totalAttendees = 0L;

  public boolean isForKids() {
    return nonNull(forKids);
  }

  /**
   * Retrieves the stream ID.
   *
   * @return the stream ID as a {@link Long}.
   */
  public Long getStreamId() {
    return fleenStreamId;
  }

  public Long getChatSpaceId() {
    return nonNull(chatSpace) ? chatSpace.getChatSpaceId() : null;
  }

  /**
   * Retrieves the set of stream attendees.
   *
   * @return a {@link Set} of {@link StreamAttendee}, or an empty set if attendees are null.
   */
  public Set<StreamAttendee> getAttendees() {
    return nonNull(attendees) ? attendees : new HashSet<>();
  }

  public String getSpaceIdOrName() {
    return nonNull(chatSpace) ? chatSpace.getExternalIdOrName() : null;
  }

  public Member getOrganizer() {
    return member;
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
   * Marks the entity as deleted by setting the {@code deleted} flag to {@code true}.
   */
  public void delete() {
    this.deleted = true;
  }

  /**
   * Cancels the stream by setting its status to {@link StreamStatus#CANCELED}.
   */
  public void cancel() {
    this.streamStatus = StreamStatus.CANCELED;
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
   * Checks if the stream visibility is set to public.
   *
   * @return {@code true} if the stream visibility is {@link StreamVisibility#PUBLIC}, {@code false} otherwise.
   */
  public boolean isPublic() {
    return streamVisibility == PUBLIC;
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
   * {@link StreamStatus#CANCELED}; otherwise, it returns {@code false}.
   *
   * @return {@code true} if the stream is canceled; {@code false} otherwise
   */
  public boolean isCanceled() {
    return streamStatus == StreamStatus.CANCELED;
  }

  /**
   * Checks if the stream is deleted.
   * This method returns {@code true} if the stream's deleted is set to
   * true; otherwise, it returns {@code false}.
   *
   * @return {@code true} if the stream is deleted; {@code false} otherwise
   */
  public boolean isDeleted() {
    return deleted;
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

  /**
   * Retrieves the member ID if the member is not null.
   *
   * @return the member ID, or null if the member is not available.
   */
  public Long getMemberId() {
    return nonNull(member) ? member.getMemberId() : null;
  }

  /**
   * Checks if the stream source is an event hosted via Google Meet.
   *
   * @return true if the stream source is Google Meet, false otherwise.
   */
  public boolean isAnEvent() {
    return streamType == StreamType.EVENT;
  }

  /**
   * Determines if the current stream is a live stream.
   *
   * <p>This method checks whether the stream source is associated with a live broadcast.
   * Specifically, it returns true if the stream is sourced from YouTube Live, indicating
   * that the stream is a live broadcast.</p>
   *
   * @return {@code true} if the stream source is YouTube Live, otherwise {@code false}.
   */
  public boolean isALiveStream() {
    return streamType == StreamType.LIVE_STREAM;
  }

  /**
   * Increments the total number of members in the event or stream by one.
   */
  public void increaseTotalAttendees() {
    totalAttendees++;
  }

  /**
   * Decrements the total number of attendees in the event or stream by one.
   */
  public void decreaseTotalAttendees() {
    totalAttendees--;
  }

  /**
   * Gets the stream schedule based on the current time.
   *
   * @return The stream time type (UPCOMING, LIVE, or PAST) based on the current time.
   */
  public StreamTimeType getStreamSchedule() {
    // Return the stream schedule based on the current time
    return getStreamSchedule(LocalDateTime.now());
  }

  /**
   * Gets the stream schedule based on the given time.
   *
   * @param currentTime The time to check against the scheduled start date.
   * @return The stream time type (UPCOMING, LIVE, or PAST) based on the current time.
   */
  public StreamTimeType getStreamSchedule(final LocalDateTime currentTime) {
    // Check if the current time is before the scheduled start date
    if (currentTime.isBefore(scheduledStartDate)) {
      return StreamTimeType.UPCOMING;
    }
    // Check if the current time is after the scheduled start date
    else if (currentTime.isAfter(scheduledStartDate)) {
      return StreamTimeType.PAST;
    }
    // If current time equals the scheduled start time
    else {
      return StreamTimeType.LIVE;
    }
  }

  /**
   * Gets the masked stream link URI.
   *
   * @return The masked stream link URI if the stream link is not null, otherwise null.
   */
  public MaskedStreamLinkUri getMaskedStreamLink() {
    // Return masked stream link if streamLink is not null
    return nonNull(streamLink)
      ? MaskedStreamLinkUri.of(streamLink, streamSource) // Create and return masked stream link URI
      : null; // Return null if streamLink is null
  }

  public static FleenStream of(final Long streamId) {
    return FleenStream.builder()
        .fleenStreamId(streamId)
        .build();
  }
}
