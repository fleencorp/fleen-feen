package com.fleencorp.feen.stream.service.impl.core;

import com.fleencorp.feen.bookmark.service.BookmarkOperationService;
import com.fleencorp.feen.like.service.LikeOperationService;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.model.contract.HasId;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.model.holder.ReviewParentCountHolder;
import com.fleencorp.feen.review.model.info.ReviewCountInfo;
import com.fleencorp.feen.review.service.ReviewCommonService;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.info.attendance.AttendeeCountInfo;
import com.fleencorp.feen.stream.model.projection.StreamAttendeeSelect;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.stream.service.attendee.StreamAttendeeOperationsService;
import com.fleencorp.feen.stream.service.core.CommonStreamOtherService;
import com.fleencorp.feen.stream.util.StreamServiceUtil;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.determineIfUserIsTheOrganizerOfEntity;
import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.setEntityUpdatableByUser;
import static com.fleencorp.feen.common.util.CommonUtil.allNonNull;
import static com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.stream.service.impl.attendee.StreamAttendeeServiceImpl.DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_STREAM;
import static java.util.Objects.nonNull;

@Service
public class CommonStreamOtherServiceImpl implements CommonStreamOtherService {

  private final BookmarkOperationService bookmarkOperationService;
  private final LikeOperationService likeOperationService;
  private final ReviewCommonService reviewCommonService;
  private final StreamAttendeeOperationsService streamAttendeeOperationsService;
  private final UnifiedMapper unifiedMapper;

  public CommonStreamOtherServiceImpl(
      final BookmarkOperationService bookmarkOperationService,
      final LikeOperationService likeOperationService,
      final ReviewCommonService reviewCommonService,
      @Lazy final StreamAttendeeOperationsService streamAttendeeOperationsService,
      final UnifiedMapper unifiedMapper) {
    this.bookmarkOperationService = bookmarkOperationService;
    this.likeOperationService = likeOperationService;
    this.reviewCommonService = reviewCommonService;
    this.streamAttendeeOperationsService = streamAttendeeOperationsService;
    this.unifiedMapper = unifiedMapper;
  }

  /**
   * Processes a collection of stream responses with additional user-specific and attendance-related information.
   *
   * <p>For each stream response, this method sets user attendance details, likes,
   * attendee lists, organizer checks, timezone-based schedule adjustments, and updatable flags.</p>
   *
   * @param streamResponses the list of stream responses to process
   * @param member a member or user in the system
   */
  @Override
  public void processOtherStreamDetails(final Collection<StreamResponse> streamResponses, final Member member) {
    if (allNonNull(streamResponses, member) && !streamResponses.isEmpty()) {
      final Map<Long, StreamAttendeeSelect> attendanceDetailsMap = getUserAttendanceDetailsMap(streamResponses, member);

      final Collection<Long> streamsIds = StreamServiceUtil.getStreamsIds(streamResponses);
      final ReviewParentCountHolder reviewParentCountHolder = reviewCommonService.getTotalReviewsByParent(ReviewParentType.STREAM, streamsIds);

      bookmarkOperationService.populateStreamBookmarksFor(streamResponses, member);
      likeOperationService.populateStreamLikesFor(streamResponses, member);

      streamResponses.stream()
        .filter(Objects::nonNull)
        .forEach(streamResponse -> processStreamResponse(streamResponse, attendanceDetailsMap, reviewParentCountHolder, member));
    }
  }

  /**
   * Processes and enriches a {@link StreamResponse} with user-specific, attendance-related,
   * and review-related information.
   *
   * <p>This includes updating join status, adjusting schedule based on the user's timezone,
   * setting attendee counts and lists, checking if the user is the stream organizer or can update the stream,
   * and enriching the response with the total number of reviews associated with the stream.</p>
   *
   * @param streamResponse        the stream response to enrich
   * @param attendanceDetailMap   a map of stream IDs to attendance details for the user
   * @param reviewParentCountHolder a holder containing review counts grouped by parent ID
   * @param member                the currently authenticated user or member
   */
  protected void processStreamResponse(final StreamResponse streamResponse, final Map<Long, StreamAttendeeSelect> attendanceDetailMap, final ReviewParentCountHolder reviewParentCountHolder, final Member member) {
    // Update join status, attendance, and speaker info
    updateJoinStatusInResponses(streamResponse, attendanceDetailMap);
    // Adjust schedule to user's timezone
    StreamServiceUtil.setOtherScheduleBasedOnUserTimezone(streamResponse, member);
    // Set the total number of attendees attending this stream
    setStreamAttendeesAndTotalAttendeesAttending(streamResponse);
    // Set the first 10 attendees attending the stream (unordered)
    setFirst10AttendeesAttendingInAnyOrderOnStreams(streamResponse);
    // Determine if the user is the organizer of the stream
    determineIfUserIsTheOrganizerOfEntity(streamResponse, member);
    // Determine if the user can update this stream
    setEntityUpdatableByUser(streamResponse, member.getMemberId());
    // Set review count info
    setReviewCount(streamResponse, reviewParentCountHolder.countOf(streamResponse.getNumberId()));
  }

  /**
   * Retrieves the user's attendance details for a list of stream responses and maps them by stream ID.
   *
   * <p>This method extracts stream IDs from the given responses, fetches the corresponding attendance
   * records for the user, and returns them grouped by stream ID.</p>
   *
   * @param streamResponses the collection of stream responses
   * @param member the user whose attendance details are to be fetched
   * @return a map of stream IDs to the user's corresponding attendance records
   */
  protected Map<Long, StreamAttendeeSelect> getUserAttendanceDetailsMap(final Collection<StreamResponse> streamResponses, final Member member) {
    // Extract the stream IDs from the search result views
    final List<Long> streamIds = HasId.getIds(streamResponses);
    // Retrieve the user's attendance records for the provided stream IDs
    final List<StreamAttendeeSelect> attendeeAttendance = streamAttendeeOperationsService.findByMemberAndStreamIds(member, streamIds);
    // Group the attendance details by stream ID and return as a map
    return HasId.groupMembershipByEntriesId(attendeeAttendance);
  }

  /**
   * Updates the join status fields in the given stream response based on attendance details.
   *
   * <p>This method looks up the corresponding attendance record for the given stream,
   * and if found, updates the response with join status, request-to-join status,
   * attending flag, and speaker status.</p>
   *
   * @param streamResponse the stream response to update
   * @param attendanceDetailMap map of stream IDs to their corresponding attendance records
   */
  protected void updateJoinStatusInResponses(final StreamResponse streamResponse, final Map<Long, StreamAttendeeSelect> attendanceDetailMap) {
    if (allNonNull(streamResponse, attendanceDetailMap)) {
      // Retrieve the attendee status for a specific ID which can be null because the member has not join or requested to join the stream
      final Optional<StreamAttendeeSelect> existingAttendance = Optional.ofNullable(attendanceDetailMap.get(streamResponse.getNumberId()));
      // If member is an attendee, retrieve the status and set view label
      existingAttendance.ifPresent(attendee -> {
        // Update the request to join status, join status and is attending info
        unifiedMapper.update(
          streamResponse,
          attendee.getRequestToJoinStatus(),
          attendee.getJoinStatus(),
          attendee.isAttending(),
          attendee.isASpeaker()
        );
      });
    }
  }

  /**
   * Sets the total number of attendees who are approved and attending the given stream.
   *
   * <p>This method queries the repository to count how many users with approved join requests
   * are currently attending the stream. The result is then set in the {@code totalAttending}
   * field of the given stream response.</p>
   *
   * @param streamResponse the stream response object to populate with attendee count
   */
  public void setStreamAttendeesAndTotalAttendeesAttending(final StreamResponse streamResponse) {
    if (nonNull(streamResponse)) {
      final Long streamId = streamResponse.getNumberId();
      // Count total attendees whose request to join stream is approved and are attending the stream because they are interested
      final int totalAttendees = streamAttendeeOperationsService.countByStreamAndRequestToJoinStatusAndAttending(FleenStream.of(streamId), APPROVED, true);
      final AttendeeCountInfo attendeeCountInfo = unifiedMapper.toAttendeeCountInfo(totalAttendees);
      streamResponse.setAttendeeCountInfo(attendeeCountInfo);
    }
  }

  /**
   * Sets up to 10 attendees who are approved and attending for the given stream.
   *
   * <p>This method retrieves the first 10 attendees (in any order) who have
   * been approved and are currently attending the stream. The list is then
   * converted into response objects and assigned to the stream response.</p>
   *
   * @param streamResponse the response object representing the stream
   */
  public void setFirst10AttendeesAttendingInAnyOrderOnStreams(final StreamResponse streamResponse) {
    if (nonNull(streamResponse)) {
      final Long streamId = streamResponse.getNumberId();
      // Create a pageable request to get the first 10 attendees
      final Pageable pageable = PageRequest.of(1, DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_STREAM);
      // Fetch attendees who are approved and attending the stream
      final Page<StreamAttendee> page = streamAttendeeOperationsService
        .findAllByStreamAndRequestToJoinStatusAndAttending(FleenStream.of(streamId), APPROVED, true, pageable);
      // Convert the list of stream attendees to list of stream attendee responses
      final Collection<StreamAttendeeResponse> streamAttendees = unifiedMapper.toStreamAttendeeResponsesPublic(page.getContent(), streamResponse);
      // Set the attendees on the response
      streamResponse.setSomeAttendees(streamAttendees);
    }
  }

  /**
   * Sets the review count information on the given {@link StreamResponse}.
   *
   * <p>Converts the raw review count into a {@link ReviewCountInfo} using the {@code unifiedMapper},
   * and attaches it to the stream response.</p>
   *
   * @param streamResponse the stream response to enrich with review count info
   * @param reviewCount    the number of reviews associated with the stream
   */
  protected void setReviewCount(final StreamResponse streamResponse, final Integer reviewCount) {
    final ReviewCountInfo reviewCountInfo = unifiedMapper.toReviewCountInfo(reviewCount);
    streamResponse.setReviewCountInfo(reviewCountInfo);
  }

}
