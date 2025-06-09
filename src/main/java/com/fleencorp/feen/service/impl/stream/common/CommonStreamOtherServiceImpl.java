package com.fleencorp.feen.service.impl.stream.common;

import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.model.other.Schedule;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeSelect;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.service.like.LikeService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeOperationsService;
import com.fleencorp.feen.service.stream.common.CommonStreamOtherService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.service.impl.common.MiscServiceImpl.*;
import static com.fleencorp.feen.service.impl.stream.attendee.StreamAttendeeServiceImpl.DEFAULT_NUMBER_OF_ATTENDEES_TO_GET_FOR_STREAM;
import static com.fleencorp.feen.util.CommonUtil.allNonNull;
import static com.fleencorp.feen.util.DateTimeUtil.convertToTimezone;
import static java.util.Objects.nonNull;

@Service
public class CommonStreamOtherServiceImpl implements CommonStreamOtherService {

  private final LikeService likeService;
  private final StreamAttendeeOperationsService streamAttendeeOperationsService;
  private final UnifiedMapper unifiedMapper;

  public CommonStreamOtherServiceImpl(
      final LikeService likeService,
      @Lazy final StreamAttendeeOperationsService streamAttendeeOperationsService,
      final UnifiedMapper unifiedMapper) {
    this.likeService = likeService;
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
    // Ensure inputs are valid and the response list is not empty
    if (allNonNull(streamResponses, member) && !streamResponses.isEmpty()) {
      // Get attendance details for the streams, grouped by stream ID
      final Map<Long, StreamAttendeeSelect> attendanceDetailsMap = getUserAttendanceDetailsMap(streamResponses, member);

      // Populate like status for streams where the user has not joined or requested to join
      likeService.populateStreamLikesForNonAttendance(streamResponses, attendanceDetailsMap, member);
      // Populate like status for streams where the user has joined or requested to join
      likeService.populateStreamLikesForAttendance(streamResponses, attendanceDetailsMap, member);

      // Process each stream response with detailed enrichment
      streamResponses.stream()
        .filter(Objects::nonNull)
        .forEach(streamResponse -> processStreamResponse(streamResponse, attendanceDetailsMap, member));
    }
  }


  /**
   * Processes and enriches a stream response with user-specific and attendance-related information.
   *
   * <p>This includes updating join status, adjusting schedule based on user timezone,
   * setting attendee counts and lists, and checking the user's relationship to the stream
   * (organizer or updatable).</p>
   *
   * @param streamResponse the stream response to enrich
   * @param attendanceDetailMap a map of stream IDs to attendance details for the user
   * @param member a user or member in the system
   */
  protected void processStreamResponse(final StreamResponse streamResponse, final Map<Long, StreamAttendeeSelect> attendanceDetailMap, final Member member) {
    // Update join status, attendance, and speaker info
    updateJoinStatusInResponses(streamResponse, attendanceDetailMap);
    // Adjust schedule to user's timezone
    setOtherScheduleBasedOnUserTimezone(streamResponse, member);
    // Set the total number of attendees attending this stream
    setStreamAttendeesAndTotalAttendeesAttending(streamResponse);
    // Set the first 10 attendees attending the stream (unordered)
    setFirst10AttendeesAttendingInAnyOrderOnStreams(streamResponse);
    // Determine if the user is the organizer of the stream
    determineIfUserIsTheOrganizerOfEntity(streamResponse, member);
    // Determine if the user can update this stream
    setEntityUpdatableByUser(streamResponse, member.getMemberId());
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
    final List<Long> streamIds = extractAndGetEntriesIds(streamResponses);
    // Retrieve the user's attendance records for the provided stream IDs
    final List<StreamAttendeeSelect> attendeeAttendance = streamAttendeeOperationsService.findByMemberAndStreamIds(member, streamIds);
    // Group the attendance details by stream ID and return as a map
    return groupMembershipByEntriesId(attendeeAttendance);
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
      final long totalAttendees = streamAttendeeOperationsService.countByStreamAndRequestToJoinStatusAndAttending(FleenStream.of(streamId), APPROVED, true);
      streamResponse.setTotalAttending(totalAttendees);
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
   * Sets the stream's schedule in the user's timezone if it differs from the stream's original timezone.
   *
   * <p>If the user's timezone is different from the stream's original timezone, this method converts
   * the stream's schedule accordingly and sets it as the "other schedule" in the response. Otherwise,
   * an empty schedule is set.</p>
   *
   * @param streamResponse the stream response containing the original schedule
   * @param member the user whose timezone is used for conversion
   */
  public void setOtherScheduleBasedOnUserTimezone(final StreamResponse streamResponse, final Member member) {
    if (allNonNull(streamResponse, member)) {
      // Get the stream's original timezone
      final String streamTimezone = streamResponse.getSchedule().getTimezone();
      // Get the user's timezone
      final String userTimezone = member.getTimezone();
      // Check if the stream's timezone and user's timezone are different
      if (!streamTimezone.equalsIgnoreCase(userTimezone)) {
        // Convert the stream's schedule to the user's timezone
        final Schedule otherSchedule = createSchedule(streamResponse, userTimezone);
        // Set the converted dates and user's timezone in the stream's other schedule
        streamResponse.setOtherSchedule(otherSchedule);
      } else {
        // If the timezones are the same, set an empty schedule
        streamResponse.setOtherSchedule(Schedule.of());
      }
    }
  }

  /**
   * Creates a schedule for a stream adjusted to the user's timezone.
   *
   * <p>This method checks if the stream and user timezone are valid. If so, it retrieves the
   * stream's original start and end dates, converts them to the user's timezone, and returns
   * the converted schedule with the adjusted dates. If either the stream or user timezone is
   * invalid (null), an empty schedule is returned.</p>
   *
   * @param stream the FleenStreamResponse object representing the stream.
   * @param userTimezone the timezone of the user to which the schedule will be adjusted.
   * @return a Schedule object with the adjusted start and end dates in the user's timezone.
   */
  protected static Schedule createSchedule(final StreamResponse stream, final String userTimezone) {
    if (nonNull(stream) && nonNull(userTimezone)) {
      // Get the stream's original timezone
      final String streamTimezone = stream.getSchedule().getTimezone();

      // Retrieve the start dates from the stream's schedule
      final LocalDateTime startDate = stream.getSchedule().getStartDate();
      // Retrieve the end dates from the stream's schedule
      final LocalDateTime endDate = stream.getSchedule().getEndDate();

      // Convert the stream's start date to the user's timezone
      final LocalDateTime userStartDate = convertToTimezone(startDate, streamTimezone, userTimezone);
      // Convert the stream's end date to the user's timezone
      final LocalDateTime userEndDate = convertToTimezone(endDate, streamTimezone, userTimezone);
      // Return the schedule with the dates in the user's timezone
      return Schedule.of(userStartDate, userEndDate, userTimezone);
    }
    // If the stream or userTimezone is null, return an empty schedule
    return Schedule.of();
  }

}
