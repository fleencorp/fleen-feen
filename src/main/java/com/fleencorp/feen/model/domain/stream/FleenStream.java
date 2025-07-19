package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.base.converter.impl.security.StringCryptoConverter;
import com.fleencorp.feen.constant.security.mask.MaskedStreamLinkUri;
import com.fleencorp.feen.constant.stream.*;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.core.*;
import com.fleencorp.feen.exception.stream.join.request.CannotJoinPrivateStreamWithoutApprovalException;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.review.exception.core.CannotAddReviewIfStreamHasNotStartedException;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.fleencorp.feen.constant.stream.StreamVisibility.PRIVATE;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stream")
public class FleenStream extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "stream_id", nullable = false, updatable = false, unique = true)
  private Long streamId;

  @Column(name = "external_id")
  private String externalId;

  @Column(name = "title", nullable = false, length = 500)
  private String title;

  @Column(name = "description", nullable = false, length = 3000)
  private String description;

  @Column(name = "summary")
  private String summary;

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

  @Column(name = "music_link", length = 1000)
  private String musicLink;

  @Column(name = "group_or_organization_name", length = 1000)
  private String groupOrOrganizationName;

  /** Use for query purpose in repositories **/
  @Column(name = "member_id", nullable = false, updatable = false, insertable = false)
  private Long organizerId;

  @Column(name = "member_id", nullable = false, updatable = false, insertable = false)
  private Long memberId;

  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @OneToMany(fetch = EAGER, cascade = ALL, targetEntity = StreamAttendee.class, mappedBy = "stream")
  private Set<StreamAttendee> attendees = new HashSet<>();

  @Column(name = "chat_space_id", nullable = false, updatable = false, insertable = false)
  private Long chatSpaceId;

  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = ChatSpace.class)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id", nullable = false, updatable = false)
  private ChatSpace chatSpace;

  @Column(name = "made_for_kids", nullable = false)
  private Boolean forKids = false;

  @Column(name = "is_deleted", nullable = false)
  private Boolean deleted = false;

  @Column(name = "total_attendees", nullable = false)
  private Integer totalAttendees = 0;

  @Column(name = "total_speakers", nullable = false)
  private Integer totalSpeakers = 0;

  @Column(name = "like_count", nullable = false)
  private Integer likeCount = 0;

  public boolean isForKids() {
    return nonNull(forKids) && forKids;
  }

  public String getExternalSpaceIdOrName() {
    return nonNull(chatSpace) ? chatSpace.getExternalIdOrName() : null;
  }

  public Member getOrganizer() {
    return member;
  }

  public Long getOrganizerId() {
    return memberId;
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
   * Updates the stream's additional information fields.
   *
   * <p>This method assigns new values to the stream's otherDetails, otherLink, and groupOrOrganizationName fields.</p>
   *
   * <p>It is typically used when a user wants to modify auxiliary information associated with a stream, such as descriptive text, an external link, or the name of the hosting group or organization.</p>
   *
   * @param otherDetails Text providing additional information about the stream.
   * @param otherLink A supplementary link associated with the stream.
   * @param groupOrOrganizationName The name of the group or organization linked to the stream.
   */
  public void updateOtherDetail(final String otherDetails, final String otherLink, final String groupOrOrganizationName) {
    this.otherDetails = otherDetails;
    this.otherLink = otherLink;
    this.groupOrOrganizationName = groupOrOrganizationName;
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
  public void update(final String externalId, final String streamLink) {
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
  public void update(final String organizerName, final String organizerEmail, final String organizerPhone) {
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
  public void reschedule(final LocalDateTime scheduledStartDate, final LocalDateTime scheduledEndDate, final String timezone) {
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
  public boolean isPrivateOrProtected() {
    return StreamVisibility.isPrivateOrProtected(streamVisibility);
  }

  /**
   * Checks if the stream visibility is set to public.
   *
   * @return {@code true} if the stream visibility is {@link StreamVisibility#PUBLIC}, {@code false} otherwise.
   */
  public boolean isPublic() {
    return StreamVisibility.isPublic(streamVisibility);
  }

  /**
   * Checks if the stream visibility is exactly PRIVATE.
   *
   * @return {@code true} if the {@code streamVisibility} is PRIVATE; {@code false} otherwise
   */
  public boolean isPrivate() {
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
    return now.isAfter(scheduledStartDate) && now.isBefore(scheduledEndDate);
  }

  /**
   * Checks if the stream or event has not yet started based on the scheduled start date.
   *
   * <p>This method compares the current time with the scheduled start date of the stream. If the
   * scheduled start date is in the future, the stream is considered to have not started.</p>
   *
   * @return {@code true} if the stream has not started (i.e., the scheduled start date is in the future),
   *         {@code false} otherwise
   */
  public boolean hasNotStarted() {
    final LocalDateTime now = LocalDateTime.now();
    return nonNull(scheduledStartDate) && scheduledStartDate.isAfter(now);
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
   * Checks whether the chat space and its ID are present.
   *
   * @return {@code true} if the {@code chatSpace} object is not null
   *         and the chat space ID obtained from {@link #getChatSpaceId()}
   *         is not null; {@code false} otherwise.
   */
  public boolean hasChatSpaceId() {
    return nonNull(getChatSpaceId());
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
    if (totalAttendees >= 1) {
      totalAttendees--;
    }
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
    // If scheduled start date is not set, assume the stream is UPCOMING
    if (isNull(scheduledStartDate)) {
      return StreamTimeType.UPCOMING;
    }

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
   * Verifies if the provided stream type is equal to the original stream type.
   *
   * <p>This method checks whether the given {@code streamType} is equal to
   * the {@code originalStreamType}. If the {@code originalStreamType} is null or
   * not equal to the {@code originalStreamType}, a {@link FailedOperationException}
   * is thrown.</p>
   *
   * @param streamType the {@link StreamType} to verify
   * @throws FailedOperationException if the {@code streamType} is null or not equal
   *                                  to the {@code originalStreamType}
   */
  public void checkStreamTypeNotEqual(final StreamType streamType) {
    if (isNull(this.streamType) || this.streamType != streamType) {
      throw new FailedOperationException();
    }
  }

  /**
   * Validates if the current user is the organizer of the stream.
   *
   * @param memberOrUserId the ID of the user or member to check
   * @throws StreamNotCreatedByUserException if the user is not the organizer of the stream
   */
  public void checkIsOrganizer(final Long memberOrUserId) {
    // Check if the stream organizer's ID matches the user's ID
    final boolean isSame = Objects.equals(getOrganizerId(), memberOrUserId);
    if (!isSame) {
      throw StreamNotCreatedByUserException.of(memberOrUserId);
    }
  }

  /**
   * Validates that the user is not the organizer of the stream.
   *
   * @param memberOrUserId the ID of the user or member to check
   * @throws FailedOperationException if the user is the organizer of the stream
   */
  public void checkIsNotOrganizer(final Long memberOrUserId) {
    // Check if the stream organizer's ID matches the user's ID
    final boolean isSame = Objects.equals(getOrganizerId(), memberOrUserId);
    if (isSame) {
      throw FailedOperationException.of();
    }
  }

  /**
   * Ensures the stream has not been canceled.
   *
   * @throws StreamAlreadyCanceledException if the stream has been canceled
   */
  public void checkNotCancelled() {
    if (isCanceled()) {
      throw StreamAlreadyCanceledException.of(streamId);
    }
  }

  /**
   * Ensures the stream has not already ended.
   *
   * @throws StreamAlreadyHappenedException if the stream has already ended
   */
  public void checkNotEnded() {
    if (hasEnded()) {
      throw new StreamAlreadyHappenedException(streamId, scheduledEndDate);
    }
  }

  /**
   * Ensures the stream is not private for joining without approval.
   *
   * @throws CannotJoinPrivateStreamWithoutApprovalException if the stream is private and cannot be joined without approval
   */
  public void checkNotPrivateForJoining() {
    if (isPrivate()) {
      throw CannotJoinPrivateStreamWithoutApprovalException.of(streamId);
    }
  }

  /**
   * Ensures the stream is not ongoing for cancellation, deletion, or update.
   *
   * @throws CannotCancelOrDeleteOngoingStreamException if the stream is ongoing and cannot be canceled, deleted, or updated
   */
  public void checkNotOngoingForCancelOrDeleteOrUpdate() {
    if (isOngoing()) {
      throw CannotCancelOrDeleteOngoingStreamException.of(streamId);
    }
  }

  /**
   * Ensures the stream is not ongoing before allowing updates.
   *
   * @throws CannotCancelOrDeleteOngoingStreamException if the stream is ongoing and cannot be updated
   */
  public void checkNotOngoingForUpdate() {
    if (isOngoing()) {
      throw CannotUpdateOngoingStreamException.of(streamId);
    }
  }

  /**
   * Ensures the stream is not public for requesting to join.
   *
   * @throws FailedOperationException if the stream is public and cannot accept join requests
   */
  public void checkIsPublicForRequestToJoin() {
    if (isPublic()) {
      throw FailedOperationException.of();
    }
  }

  /**
   * Checks the eligibility for adding a review based on the review type and the stream status.
   *
   * <p>This method ensures that a review can only be added for streams that are ongoing or completed.
   * If the stream has not started yet, it throws a {@link CannotAddReviewIfStreamHasNotStartedException}.</p>
   *
   * @throws CannotAddReviewIfStreamHasNotStartedException if the stream has not started yet
   */
  public void checkAddReviewEligibility() {
    // Only streams that are ongoing or completed can be reviewed
    if (hasNotStarted()) {
      throw CannotAddReviewIfStreamHasNotStartedException.of();
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
    final FleenStream stream = new FleenStream();
    stream.setStreamId(streamId);

    return stream;
  }

  public static FleenStream empty() {
    return null;
  }
}
