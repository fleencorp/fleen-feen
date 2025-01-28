package com.fleencorp.feen.service.impl.stream.join;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.mapper.CommonMapper;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.stream.attendance.JoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.NotAttendingStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.ProcessAttendeeRequestToJoinStreamDto;
import com.fleencorp.feen.model.dto.stream.attendance.RequestToJoinStreamDto;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.response.holder.TryToJoinPublicStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.JoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.RequestToJoinStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.notification.NotificationService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.service.stream.join.LiveBroadcastJoinService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.fleencorp.feen.service.impl.stream.base.StreamServiceImpl.verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction;
import static com.fleencorp.feen.service.impl.stream.base.StreamServiceImpl.verifyStreamDetails;

/**
 * Implementation of the {@link LiveBroadcastJoinService} interface that handles the logic for managing attendees joining live broadcasts.
 *
 * <p>This service includes methods for processing requests to join streams, handling attendee statuses,
 * sending notifications, and managing stream attendee records. It integrates with various other services,
 * including stream and attendee services, to handle the lifecycle of attendees joining a live broadcast.</p>
 *
 * @author Yusuf Àlàmù Musa
 * @version 1.0
 */
@Slf4j
@Service
public class LiveBroadcastJoinServiceImpl implements LiveBroadcastJoinService {

  private final StreamAttendeeService attendeeService;
  private final StreamService streamService;
  private final NotificationMessageService notificationMessageService;
  private final NotificationService notificationService;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final CommonMapper commonMapper;
  private final StreamMapper streamMapper;
  private final Localizer localizer;

  /**
   * Constructs a new instance of {@code LiveBroadcastJoinServiceImpl}.
   *
   * <p>This constructor initializes the service with the necessary dependencies to manage live broadcast
   * stream join requests and attendee notifications.</p>
   *
   * @param attendeeService the service for managing stream attendees
   * @param streamService the service for handling stream operations
   * @param notificationMessageService the service for creating notification messages
   * @param notificationService the service for saving notifications
   * @param streamAttendeeRepository the repository for stream attendee records
   * @param localizer the service for generating localized responses
   * @param commonMapper the mapper for common data transformations
   * @param streamMapper the mapper for stream-specific data transformations
   */
  public LiveBroadcastJoinServiceImpl(
      final StreamAttendeeService attendeeService,
      final StreamService streamService,
      final NotificationMessageService notificationMessageService,
      final NotificationService notificationService,
      final StreamAttendeeRepository streamAttendeeRepository,
      final Localizer localizer,
      final CommonMapper commonMapper,
      final StreamMapper streamMapper) {
    this.attendeeService = attendeeService;
    this.streamService = streamService;
    this.notificationMessageService = notificationMessageService;
    this.notificationService = notificationService;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.commonMapper = commonMapper;
    this.streamMapper = streamMapper;
    this.localizer = localizer;
  }

  /**
   * Marks a user as not attending a live broadcast stream and updates the stream attendee record.
   *
   * <p>This method finds the stream by its ID, verifies the user's permissions, and updates the user's
   * attendance status to "not attending." It also decreases the total number of attendees for the stream
   * and saves the updated attendee record. A response is returned indicating the change in attendance status.</p>
   *
   * @param liveBroadcastId the ID of the live broadcast stream
   * @param notAttendingStreamDto the DTO containing stream-related details for the non-attendance action
   * @param user the user who is marking themselves as not attending
   * @return a {@link NotAttendingStreamResponse} containing the updated attendance status and stream type info
   * @throws FleenStreamNotFoundException if the stream with the specified ID is not found
   * @throws FailedOperationException if an error occurs while processing the request
   */
  @Override
  @Transactional
  public NotAttendingStreamResponse notAttendingLiveBroadcast(final Long liveBroadcastId, final NotAttendingStreamDto notAttendingStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(liveBroadcastId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(notAttendingStreamDto.getStreamType());
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the stream
    verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(stream.getMemberId()), user);

    // Find the existing attendee record for the user and stream
    streamAttendeeRepository.findAttendeeByStreamAndUser(stream, user.toMember())
      .ifPresent(streamAttendee -> {
        // If an attendee record exists, update their attendance status to false
        streamAttendee.markAsNotAttending();
        // Decrease the total number of attendees to stream
        streamService.decreaseTotalAttendeesOrGuestsAndSave(stream);
        // Save the updated attendee record
        streamAttendeeRepository.save(streamAttendee);
    });

    // Get a not attending stream response with the details
    final NotAttendingStreamResponse notAttendingStreamResponse = commonMapper.notAttendingStream();
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Set the stream type info
    notAttendingStreamResponse.setStreamTypeInfo(streamTypeInfo);
    // Build and return the response indicating the user is no longer attending
    return localizer.of(notAttendingStreamResponse);
  }

  /**
   * Allows a user to join a live broadcast stream and returns the stream details and attendance status.
   *
   * <p>This method verifies the user's details and attempts to join the live broadcast stream. It retrieves
   * the stream, attendance info, and stream type information, and returns a localized response indicating the
   * user's status in the stream.</p>
   *
   * @param liveBroadcastId the ID of the live broadcast stream the user wants to join
   * @param joinStreamDto the data transfer object containing the comment and other join details
   * @param user the user attempting to join the live broadcast stream
   * @return a {@link JoinStreamResponse} containing the details of the stream and the user's attendance status
   * @throws FleenStreamNotFoundException if the live broadcast stream is not found
   * @throws StreamAlreadyCanceledException if the stream has already been canceled
   * @throws StreamAlreadyHappenedException if the stream has already taken place
   * @throws CannotJointStreamWithoutApprovalException if the user cannot join the stream without approval
   * @throws AlreadyRequestedToJoinStreamException if the user has already requested to join the stream
   * @throws AlreadyApprovedRequestToJoinException if the user has already had their join request approved
   */
  @Override
  @Transactional
  public JoinStreamResponse joinLiveBroadcast(final Long liveBroadcastId, final JoinStreamDto joinStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
        CannotJointStreamWithoutApprovalException, AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException {
    // Verify the user details and attempt to join the live broadcast
    final TryToJoinPublicStreamResponse tryToJoinResponse = streamService.tryToJoinPublicStream(liveBroadcastId, joinStreamDto.getComment(), user);
    // Extract the stream
    final FleenStream stream = tryToJoinResponse.stream();
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(joinStreamDto.getStreamType());
    // Extract the attendance info
    final AttendanceInfo attendanceInfo = tryToJoinResponse.attendanceInfo();
    // Get stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return localized response of the join stream including status
    return localizer.of(JoinStreamResponse.of(liveBroadcastId, attendanceInfo, streamTypeInfo));
  }

  /**
   * Allows a user to request to join a live broadcast stream and returns the response to the request.
   *
   * <p>This method verifies the user's details and attempts to join the live broadcast stream by sending
   * a request. The request is processed and a response is returned indicating the status of the request.</p>
   *
   * @param streamId the ID of the live broadcast stream the user wants to join
   * @param requestToJoinStreamDto the data transfer object containing the details of the request
   * @param user the user requesting to join the live broadcast stream
   * @return a {@link RequestToJoinStreamResponse} containing the details of the request status
   * @throws FleenStreamNotFoundException if the live broadcast stream is not found
   * @throws StreamAlreadyCanceledException if the stream has already been canceled
   * @throws StreamAlreadyHappenedException if the stream has already taken place
   * @throws AlreadyRequestedToJoinStreamException if the user has already requested to join the stream
   * @throws AlreadyApprovedRequestToJoinException if the user has already had their join request approved
   */
  @Override
  @Transactional
  public RequestToJoinStreamResponse requestToJoinLiveBroadcast(final Long streamId, final RequestToJoinStreamDto requestToJoinStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, StreamAlreadyCanceledException, StreamAlreadyHappenedException,
        AlreadyRequestedToJoinStreamException, AlreadyApprovedRequestToJoinException {
    // Verify the user details and attempt to join the stream and return the response
    return streamService.requestToJoinStream(streamId, requestToJoinStreamDto, user);
  }

  /**
   * Processes an attendee's request to join a live broadcast stream and returns the response.
   *
   * <p>This method retrieves the stream using the provided stream ID, verifies the stream's details such as
   * its owner, date, and active status, and then checks if the user is already an attendee of the stream.
   * If the user is found as an attendee, their request to join the stream is processed and a response is returned.</p>
   *
   * @param liveBroadcastId the ID of the live broadcast stream to process the attendee's request
   * @param processAttendeeRequestToJoinStreamDto the data transfer object containing the attendee's request details
   * @param user the user who is processing the request to join the live broadcast stream
   * @return a {@link ProcessAttendeeRequestToJoinStreamResponse} containing the details of the processed request
   * @throws FleenStreamNotFoundException if the live broadcast stream is not found
   * @throws StreamNotCreatedByUserException if the stream was not created by the user
   * @throws StreamAlreadyHappenedException if the stream has already taken place
   * @throws StreamAlreadyCanceledException if the stream has already been canceled
   * @throws FailedOperationException if the operation to process the request fails
   */
  @Override
  @Transactional
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinLiveBroadcast(final Long liveBroadcastId, final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, StreamNotCreatedByUserException, StreamAlreadyHappenedException,
        StreamAlreadyCanceledException, FailedOperationException {
    // Retrieve the stream using the provided stream ID
    final FleenStream stream = streamService.findStream(liveBroadcastId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(processAttendeeRequestToJoinStreamDto.getStreamType());
    // Verify stream details like the owner, stream date and active status of the stream
    verifyStreamDetails(stream, user);

    // Check if the user is already an attendee of the stream and process accordingly
    final Optional<StreamAttendee> existingAttendee = attendeeService.findAttendee(stream, Long.parseLong(processAttendeeRequestToJoinStreamDto.getAttendeeUserId()));
    // If the attendee exists and is found, process their request to join request
    existingAttendee.ifPresent(streamAttendee -> processAttendeeRequestToJoin(stream, streamAttendee, processAttendeeRequestToJoinStreamDto));
    // Get a processed attendee request to join stream response
    final ProcessAttendeeRequestToJoinStreamResponse processedRequestToJoin = commonMapper.processAttendeeRequestToJoinStream(streamMapper.toFleenStreamResponse(stream), existingAttendee);
    // Return a localized response with the processed stream details
    return localizer.of(processedRequestToJoin);
  }

  /**
   * Processes the attendee's request to join a stream and handles approval or disapproval.
   *
   * <p>If the attendee's request to join the stream is still pending, the method updates the request status
   * based on the provided details. If the request is approved, the attendee's information is saved. A notification
   * regarding the request's approval or disapproval is created and saved as well.</p>
   *
   * @param stream the stream to which the attendee is requesting to join
   * @param streamAttendee the attendee whose request is being processed
   * @param processAttendeeRequestToJoinStreamDto the data transfer object containing the request details, including approval status and comments
   */
  protected void processAttendeeRequestToJoin(final FleenStream stream, final StreamAttendee streamAttendee, final ProcessAttendeeRequestToJoinStreamDto processAttendeeRequestToJoinStreamDto) {
    // Process the request if the attendee's status is pending
    if (streamAttendee.isRequestToJoinPending()) {
      // Update the attendee's request status and any organizer comments
      streamService.updateAttendeeRequestStatus(streamAttendee, processAttendeeRequestToJoinStreamDto);
      // If the attendee's request is approved, save the attendee
      if (processAttendeeRequestToJoinStreamDto.isApproved()) {
        streamAttendeeRepository.save(streamAttendee);
      }
    }

    // Create the notification
    final Notification notification = notificationMessageService.ofApprovedOrDisapproved(streamAttendee.getStream(), streamAttendee, stream.getMember());
    // Save the notification
    notificationService.save(notification);
  }

}
