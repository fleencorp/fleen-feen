package com.fleencorp.feen.stream.model.domain;

import com.fleencorp.base.converter.impl.security.StringCryptoConverter;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.common.constant.mask.MaskedStreamLinkUri;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.model.contract.HasTitle;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.review.exception.core.CannotAddReviewIfStreamHasNotStartedException;
import com.fleencorp.feen.stream.constant.core.*;
import com.fleencorp.feen.stream.exception.core.*;
import com.fleencorp.feen.stream.exception.request.CannotJoinPrivateStreamWithoutApprovalException;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.fleencorp.feen.common.util.common.HybridSlugGenerator.generateHybridSlug;
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
public class FleenStream extends FleenFeenEntity
  implements HasTitle {

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

  @ToString.Exclude
  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @ToString.Exclude
  @OneToMany(fetch = EAGER, cascade = ALL, targetEntity = StreamAttendee.class, mappedBy = "stream")
  private Set<StreamAttendee> attendees = new HashSet<>();

  @Column(name = "chat_space_id", nullable = false, updatable = false, insertable = false)
  private Long chatSpaceId;

  @ToString.Exclude
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

  @Column(name = "bookmark_count", nullable = false)
  private Integer bookmarkCount = 0;

  @Column(name = "share_count", nullable = false)
  private Integer shareCount = 0;

  @Column(name = "slug", nullable = false, unique = true, updatable = false)
  private String slug;

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

  public void delete() {
    this.deleted = true;
  }

  public void cancel() {
    this.streamStatus = StreamStatus.CANCELED;
  }

  public boolean isPrivateOrProtected() {
    return StreamVisibility.isPrivateOrProtected(streamVisibility);
  }

  public boolean isPublic() {
    return StreamVisibility.isPublic(streamVisibility);
  }

  public boolean isPrivate() {
    return StreamVisibility.isPrivate(streamVisibility);
  }

  public boolean isCanceled() {
    return StreamStatus.isCanceled(streamStatus);
  }

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

  public boolean isAnEvent() {
    return StreamType.isEvent(streamType);
  }

  public boolean isALiveStream() {
    return StreamType.isLiveStream(streamType);
  }

  public boolean hasChatSpaceId() {
    return nonNull(getChatSpaceId());
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

  public void checkIsOrganizer(final Long memberOrUserId) {
    final boolean isSame = Objects.equals(getOrganizerId(), memberOrUserId);
    if (!isSame) {
      throw StreamNotCreatedByUserException.of(memberOrUserId);
    }
  }

  public void checkIsNotOrganizer(final Long memberOrUserId) {
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

  public void checkNotEnded() {
    if (hasEnded()) {
      throw new StreamAlreadyHappenedException(streamId, scheduledEndDate);
    }
  }

  public void checkNotPrivateForJoining() {
    if (isPrivate()) {
      throw CannotJoinPrivateStreamWithoutApprovalException.of(streamId);
    }
  }

  public void checkNotOngoingForCancelOrDeleteOrUpdate() {
    if (isOngoing()) {
      throw CannotCancelOrDeleteOngoingStreamException.of(streamId);
    }
  }

  public void checkNotOngoingForUpdate() {
    if (isOngoing()) {
      throw CannotUpdateOngoingStreamException.of(streamId);
    }
  }

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

  public MaskedStreamLinkUri getMaskedStreamLink() {
    return MaskedStreamLinkUri.of(streamLink, streamSource);
  }

  public static FleenStream of(final Long streamId) {
    final FleenStream stream = new FleenStream();
    stream.setStreamId(streamId);

    return stream;
  }

  public static FleenStream empty() {
    return null;
  }

  @PrePersist
  public void prePersist() {
    slug = generateHybridSlug(title);
  }

}
