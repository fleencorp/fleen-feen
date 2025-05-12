package com.fleencorp.feen.service.impl.stream.common;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.CannotCancelOrDeleteOngoingStreamException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.stream.base.*;
import com.fleencorp.feen.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.feen.model.info.stream.StreamStatusInfo;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamVisibilityInfo;
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.request.stream.ExternalStreamRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.DeleteLiveBroadcastRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.RescheduleLiveBroadcastRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.UpdateLiveBroadcastRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.UpdateLiveBroadcastVisibilityRequest;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.base.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.impl.stream.update.LiveBroadcastUpdateService;
import com.fleencorp.feen.service.stream.StreamOperationsService;
import com.fleencorp.feen.service.stream.common.CommonStreamService;
import com.fleencorp.feen.service.stream.common.StreamRequestService;
import com.fleencorp.feen.service.stream.event.EventOperationsService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fleencorp.feen.service.impl.stream.common.StreamServiceImpl.validateCreatorOfStream;
import static com.fleencorp.feen.service.impl.stream.common.StreamServiceImpl.verifyStreamDetails;

@Service
public class CommonStreamServiceImpl implements CommonStreamService, StreamRequestService {

  private final EventOperationsService eventOperationsService;
  private final LiveBroadcastUpdateService liveBroadcastUpdateService;
  private final StreamOperationsService streamOperationsService;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code CommonStreamServiceImpl}, which manages core stream functionality such as broadcasting and event coordination.
   *
   * @param eventOperationsService the service responsible for handling event-related operations tied to streams
   * @param liveBroadcastUpdateService the service for updating live broadcast details and states
   * @param streamOperationsService the service that provides low-level operations on stream entities
   * @param unifiedMapper the utility for mapping domain models to DTOs and vice versa
   * @param localizer the component used for resolving localized messages based on locale
   */
  public CommonStreamServiceImpl(
      final EventOperationsService eventOperationsService,
      final LiveBroadcastUpdateService liveBroadcastUpdateService,
      final StreamOperationsService streamOperationsService,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.eventOperationsService = eventOperationsService;
    this.liveBroadcastUpdateService = liveBroadcastUpdateService;
    this.streamOperationsService = streamOperationsService;
    this.unifiedMapper = unifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Deletes a stream based on its ID and type. The method first verifies the stream's existence and
   * ensures that the user is the creator of the stream. It also checks if the stream is ongoing before proceeding
   * with the deletion process. Depending on the stream type, the method retrieves the necessary external
   * resources such as calendar or OAuth2 authorization details to handle external deletions for events or live broadcasts.
   *
   * <p>For streams identified as events, it retrieves the associated calendar from an external source and handles the
   * external deletion. For live broadcasts, it retrieves and validates the OAuth2 authorization token and handles the
   * deletion of the broadcast via the relevant service.</p>
   *
   * <p>The method then marks the stream as deleted, saves the updated stream entity, and creates an external request to
   * delete the stream (event or live broadcast). Once the external deletion is handled, it returns a localized response
   * containing information about the deletion and stream type.</p>
   *
   * @param streamId          the ID of the stream to be deleted
   * @param deleteStreamDto    the request object containing information about the stream type for deletion
   * @param user               the user requesting the deletion, who must be the creator of the stream
   * @return DeleteStreamResponse  a response object indicating the result of the deletion, including the stream type
   *                              and whether the stream was successfully deleted
   *
   * @throws StreamNotFoundException              if the stream with the given ID is not found
   * @throws CalendarNotFoundException                 if the calendar associated with the event is not found
   * @throws StreamNotCreatedByUserException           if the user is not the creator of the stream
   * @throws CannotCancelOrDeleteOngoingStreamException if the stream is ongoing and cannot be deleted
   * @throws FailedOperationException                  if an error occurs during the deletion process
   */
  @Override
  @Transactional
  public DeleteStreamResponse deleteStream(final Long streamId, final DeleteStreamDto deleteStreamDto, final FleenUser user)
      throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
        CannotCancelOrDeleteOngoingStreamException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamOperationsService.findStream(streamId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.checkStreamTypeNotEqual(deleteStreamDto.getStreamType());

    // Get stream other details
    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user);
    // Retrieve the calendar from the stream details
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    // Retrieve the oauth2Authorization from the stream details
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    // Validate if the user is the creator of the event
    validateCreatorOfStream(stream, user);
    // Verify if stream is still ongoing
    stream.checkNotOngoingForCancelOrDeleteOrUpdate();
    // Update delete status of event
    stream.delete();
    // Save the stream
    streamOperationsService.save(stream);

    // Create the request to delete the stream externally
    final ExternalStreamRequest deleteStreamRequest = createDeleteStreamRequest(calendar, oauth2Authorization, stream);
    // Delete the stream externally
    deleteStreamExternally(deleteStreamRequest);
    // Get the deleted info
    final IsDeletedInfo deletedInfo = unifiedMapper.toIsDeletedInfo(stream.isDeleted());
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = unifiedMapper.toStreamTypeInfo(stream.getStreamType());
    // Create the response
    final DeleteStreamResponse deleteStreamResponse = DeleteStreamResponse.of(streamId, streamTypeInfo, deletedInfo);
    // Return a localized response of the Deleted event
    return localizer.of(deleteStreamResponse);
  }

  /**
   * Handles the external deletion of a stream, whether it is an event or a live broadcast.
   * Based on the type of the stream (event or broadcast), this method creates the appropriate request to either
   * delete a calendar event (such as in Google Calendar) or delete a live broadcast.
   *
   * <p>If the stream is identified as an event and the request indicates a deletion, a `DeleteCalendarEventRequest`
   * is created with the external calendar ID and stream ID. This request is then passed to the `eventUpdateService`
   * to delete the event from Google Calendar.</p>
   *
   * <p>If the stream is identified as a broadcast and the request is for deletion, a `DeleteLiveBroadcastRequest`
   * is created with the external stream ID and access token. This request is then sent to `liveBroadcastUpdateService`
   * to delete the live broadcast using the specified external service.</p>
   *
   * @param deleteStreamRequest  the request containing information about the stream to be deleted, including
   *                             stream type (event or broadcast), external IDs, and necessary access tokens for
   *                             performing the external deletion
   */
  protected void deleteStreamExternally(final ExternalStreamRequest deleteStreamRequest) {
    if (deleteStreamRequest.isAnEvent() && deleteStreamRequest.isDeleteRequest()) {
      // Create a request to delete the calendar event
      final DeleteCalendarEventRequest deleteCalendarEventRequest = DeleteCalendarEventRequest.of(
        deleteStreamRequest.calendarExternalId(),
        deleteStreamRequest.streamExternalId()
      );
      // Delete the event in the Google Calendar
      eventOperationsService.deleteEventInGoogleCalendar(deleteCalendarEventRequest);

    } else if (deleteStreamRequest.isABroadcast() && deleteStreamRequest.isDeleteRequest()) {
      // Create a request to delete the live broadcast
      final DeleteLiveBroadcastRequest deleteLiveBroadcastRequest = DeleteLiveBroadcastRequest.of(
        deleteStreamRequest.streamExternalId(),
        deleteStreamRequest.accessToken()
      );

      // Reschedule the live broadcast using an external service
      liveBroadcastUpdateService.deleteLiveBroadcast(deleteLiveBroadcastRequest);
    }
  }


  /**
   * Cancels an existing stream based on the provided stream ID and cancellation details. This method ensures that
   * the stream is properly validated, checking various stream details, and updates its status to "canceled" if
   * all conditions are met. It also handles any necessary external operations, such as canceling the event in an
   * external calendar service or stopping a live broadcast if the stream is a live event.
   *
   * <p>The method begins by retrieving the stream by its ID and verifying that the stream's type matches the type
   * specified in the cancellation request. It validates details such as stream ownership, the event date, and
   * whether the stream has already occurred or is still active. If the stream is ongoing, it prevents cancellation.</p>
   *
   * <p>Once validated, the stream's status is updated to "canceled" and the change is persisted in the repository.
   * If the stream is an event, the associated calendar is retrieved, and the external calendar event cancellation
   * is handled. In the case of live broadcasts, OAuth2 authorization is retrieved, and the live stream is deleted
   * through an external service.</p>
   *
   * <p>The method creates the necessary external cancellation request and processes any interactions with external
   * services to cancel the stream. Finally, it returns a localized response containing details about the canceled
   * stream, including its ID, status, and type.</p>
   *
   * @param streamId          the unique identifier of the stream to be canceled
   * @param cancelStreamDto    the DTO containing details about the stream cancellation request
   * @param user              the user requesting the cancellation; must be the creator of the stream
   * @return                  a localized response object containing details about the canceled stream
   * @throws StreamNotFoundException          if the stream with the provided ID is not found
   * @throws CalendarNotFoundException             if the associated calendar for an event stream is not found
   * @throws StreamNotCreatedByUserException       if the user is not the creator of the stream
   * @throws StreamAlreadyHappenedException        if the stream has already taken place
   * @throws StreamAlreadyCanceledException        if the stream has already been canceled
   * @throws CannotCancelOrDeleteOngoingStreamException  if the stream is currently ongoing and cannot be canceled
   * @throws FailedOperationException              if any external cancellation operation fails
   */
  @Transactional
  @Override
  public CancelStreamResponse cancelStream(final Long streamId, final CancelStreamDto cancelStreamDto, final FleenUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
      StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException,
      FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamOperationsService.findStream(streamId);
    // Retrieve the stream type
    final StreamType streamType = stream.getStreamType();
    // Verify if the stream's type is the same as the stream type of the request
    stream.checkStreamTypeNotEqual(cancelStreamDto.getStreamType());
    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Verify if the event or stream is still ongoing
    stream.checkNotOngoingForCancelOrDeleteOrUpdate();
    // Update event status to canceled
    stream.cancel();

    // Get stream other details
    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user);
    // Retrieve the calendar from the stream details
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    // Retrieve the oauth2Authorization from the stream details
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    // Save the stream to the repository
    streamOperationsService.save(stream);
    // Convert the stream status to info
    final StreamStatusInfo statusInfo = unifiedMapper.toStreamStatusInfo(stream.getStreamStatus());
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = unifiedMapper.toStreamTypeInfo(stream.getStreamType());
    // Create the cancel stream request
    final ExternalStreamRequest cancelStreamRequest = ExternalStreamRequest.ofCancel(calendar, oauth2Authorization, stream, streamType);
    // Check other details and if necessary, cancel stream externally
    cancelStreamExternally(cancelStreamRequest);
    // Create a response
    final CancelStreamResponse cancelStreamResponse = CancelStreamResponse.of(streamId, statusInfo, streamTypeInfo);
    // Return a localized response of the cancellation
    return localizer.of(cancelStreamResponse);
  }

  /**
   * Cancels a stream externally based on the provided request. This method specifically handles the cancellation
   * of events that are associated with external calendar services. If the stream is identified as an event
   * and the request is for cancellation, it creates and submits a request to cancel the event in an external
   * calendar service (e.g., Google Calendar).
   *
   * <p>The method checks the type of stream in the request and ensures that the stream is an event
   * and the request is indeed a cancellation request. It then proceeds to create a `CancelCalendarEventRequest`
   * using the external calendar and stream identifiers. This request is then processed by the
   * external service to handle the event cancellation.</p>
   *
   * @param cancelStreamRequest  the request object containing the necessary information to cancel the stream
   *                             externally, including stream and calendar identifiers
   */
  protected void cancelStreamExternally(final ExternalStreamRequest cancelStreamRequest) {
    if (cancelStreamRequest.isAnEvent() && cancelStreamRequest.isCancelRequest()) {
      // Create a request to cancel the calendar event and submit request to external Calendar service
      final CancelCalendarEventRequest cancelCalendarEventRequest = CancelCalendarEventRequest.of(
        cancelStreamRequest.calendarExternalId(),
        cancelStreamRequest.streamExternalId()
      );

      // Cancel the stream in the external service
      eventOperationsService.cancelEventInGoogleCalendar(cancelCalendarEventRequest);
    }
  }

  /**
   * Reschedules an existing stream based on the provided stream ID and rescheduling details. This method updates
   * the stream's schedule by verifying its current status and other details such as the owner and event status.
   * It also handles any necessary external operations, such as updating the stream in an external calendar service
   * or rescheduling a live broadcast if the stream is a live event.
   *
   * <p>The method begins by retrieving the stream by its ID and verifying that the stream's type matches the type
   * specified in the rescheduling request. It finds the user's associated calendar and verifies that the user is
   * the creator of the stream. The method then checks if the stream has already taken place or if it has been
   * canceled.</p>
   *
   * <p>Once validated, the stream's schedule details are updated, including the new start and end time, as well as
   * the timezone. The updated stream is persisted in the repository. If the stream is an event, the associated
   * calendar is retrieved and updated accordingly. For live streams, OAuth2 authorization is retrieved and the
   * live stream is rescheduled through an external service.</p>
   *
   * <p>The method creates the necessary external rescheduling request and processes any interactions with external
   * services to reschedule the stream. Finally, it returns a localized response containing details about the
   * rescheduled stream, including its ID, schedule, and type.</p>
   *
   * @param streamId              the unique identifier of the stream to be rescheduled
   * @param rescheduleStreamDto    the DTO containing the new schedule details for the stream
   * @param user                  the user requesting the rescheduling; must be the creator of the stream
   * @return                      a localized response object containing details about the rescheduled stream
   * @throws StreamNotFoundException          if the stream with the provided ID is not found
   * @throws CalendarNotFoundException             if the associated calendar for an event stream is not found
   * @throws StreamNotCreatedByUserException       if the user is not the creator of the stream
   * @throws StreamAlreadyHappenedException        if the stream has already taken place and cannot be rescheduled
   * @throws StreamAlreadyCanceledException        if the stream has already been canceled
   * @throws FailedOperationException              if any external rescheduling operation fails
   */
  @Override
  @Transactional
  public RescheduleStreamResponse rescheduleStream(final Long streamId, final RescheduleStreamDto rescheduleStreamDto, final FleenUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamOperationsService.findStream(streamId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.checkStreamTypeNotEqual(rescheduleStreamDto.getStreamType());

    // Get stream other details
    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user);
    // Retrieve the calendar from the stream details
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    // Retrieve the oauth2Authorization from the stream details
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Update Stream schedule details and time
    stream.reschedule(
      rescheduleStreamDto.getStartDateTime(),
      rescheduleStreamDto.getEndDateTime(),
      rescheduleStreamDto.getTimezone()
    );

    // Save the stream and event details
    streamOperationsService.save(stream);
    // Create the reschedule stream request
    final ExternalStreamRequest rescheduleStreamRequest = createRescheduleStreamRequest(calendar, oauth2Authorization, stream, rescheduleStreamDto);
    // Reschedule the stream externally
    rescheduleStreamExternally(rescheduleStreamRequest);
    // Get the stream response
    final StreamResponse streamResponse = unifiedMapper.toStreamResponseNoJoinStatus(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = unifiedMapper.toStreamTypeInfo(stream.getStreamType());
    // Create the response
    final RescheduleStreamResponse rescheduleStreamResponse = RescheduleStreamResponse.of(streamId, streamResponse, streamTypeInfo);
    // Return a localized response of the rescheduled stream
    return localizer.of(rescheduleStreamResponse);
  }

  /**
   * Handles the external rescheduling of a stream, either as a calendar event or a live broadcast.
   * If the stream is an event, the method prepares a request to reschedule the calendar event with
   * new schedule details such as start time, end time, and timezone. It then updates the schedule
   * in the external calendar service, such as Google Calendar.
   *
   * <p>If the stream is a live broadcast, the method prepares a request to reschedule the live broadcast
   * with updated schedule details. It interacts with the external service to reschedule the live
   * broadcast using the provided OAuth2 access token.</p>
   *
   * <p>The method checks if the reschedule request pertains to an event or broadcast before executing
   * the corresponding external service call.</p>
   *
   * @param rescheduleStreamRequest the request object containing rescheduling details, such as start
   *                                time, end time, and timezone, along with external stream IDs and
   *                                authorization tokens if necessary.
   */
  protected void rescheduleStreamExternally(final ExternalStreamRequest rescheduleStreamRequest) {
    if (rescheduleStreamRequest.isAnEvent() && rescheduleStreamRequest.isRescheduleRequest()) {
      // Prepare a request to reschedule the calendar event with the new schedule details
      final RescheduleCalendarEventRequest rescheduleCalendarEventRequest = RescheduleCalendarEventRequest.of(
        rescheduleStreamRequest.calendarExternalId(),
        rescheduleStreamRequest.streamExternalId(),
        rescheduleStreamRequest.getStartDateTime(),
        rescheduleStreamRequest.getEndDateTime(),
        rescheduleStreamRequest.getTimezone()
      );
      // Update event schedule details in the Google Calendar service
      eventOperationsService.rescheduleEventInGoogleCalendar(rescheduleCalendarEventRequest);

    } else if (rescheduleStreamRequest.isABroadcast() && rescheduleStreamRequest.isRescheduleRequest()) {
      // Create a request object to reschedule the live broadcast on the external service
      final RescheduleLiveBroadcastRequest rescheduleLiveBroadcastRequest = RescheduleLiveBroadcastRequest.of(
        rescheduleStreamRequest.accessToken(),
        rescheduleStreamRequest.getStartDateTime(),
        rescheduleStreamRequest.getEndDateTime(),
        rescheduleStreamRequest.getTimezone(),
        rescheduleStreamRequest.streamExternalId()
      );

      // Reschedule the live broadcast using an external service
      liveBroadcastUpdateService.rescheduleLiveBroadcastAndStream(rescheduleStreamRequest.getStream(), rescheduleLiveBroadcastRequest);
    }
  }

  /**
   * Updates the stream details such as the title, description, tags, and location both internally and externally.
   *
   * <p>The method starts by finding the stream by its ID and validating the stream type against the update request. It ensures
   * that the user is the creator of the stream and performs updates on the FleenStream entity before saving it in the repository.</p>
   *
   * <p>If the stream is a live broadcast, OAuth2 authorization is handled to ensure the user has the necessary permissions.
   * The method then prepares an external update request and applies the changes to external services, such as Google Calendar
   * or a live broadcasting platform.</p>
   *
   * <p>Finally, it maps the updated stream details to a response object and returns a localized response for the updated stream.</p>
   *
   * @param streamId           the ID of the stream to be updated
   * @param updateStreamDto    the DTO containing updated stream information such as title, description, tags, and location
   * @param user               the user attempting to update the stream, which must be the stream's creator
   * @return                   a localized response containing the updated stream details
   * @throws CalendarNotFoundException if the calendar associated with the stream is not found
   * @throws Oauth2InvalidAuthorizationException if OAuth2 authorization for the live stream is invalid
   * @throws StreamNotFoundException if the stream to be updated is not found
   * @throws StreamNotCreatedByUserException if the user attempting to update the stream is not its creator
   * @throws StreamAlreadyHappenedException if the stream has already occurred and cannot be updated
   * @throws StreamAlreadyCanceledException if the stream has been canceled and cannot be updated
   * @throws FailedOperationException if the update operation fails
   */
  @Override
  @Transactional
  public UpdateStreamResponse updateStream(final Long streamId, final UpdateStreamDto updateStreamDto, final FleenUser user)
    throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
      StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException,
      FailedOperationException {
    // Find the stream by its ID
    FleenStream stream = streamOperationsService.findStream(streamId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.checkStreamTypeNotEqual(updateStreamDto.getStreamType());
    // Validate if the user is the creator of the event
    verifyStreamDetails(stream, user);
    // Update the FleenStream object with the response from Google Calendar
    stream.update(
      updateStreamDto.getTitle(),
      updateStreamDto.getDescription(),
      updateStreamDto.getTags(),
      updateStreamDto.getLocation()
    );

    // Save the updated stream to the repository
    stream = streamOperationsService.save(stream);

    // Get stream other details
    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user);
    // Retrieve the calendar from the stream details
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    // Retrieve the oauth2Authorization from the stream details
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    // Create and build the patch stream request with necessary payload
    final ExternalStreamRequest patchStreamRequest = createPatchStreamRequest(calendar, oauth2Authorization, stream, updateStreamDto);
    // Patch or update the stream externally
    patchStreamExternally(patchStreamRequest);
    // Get the stream response
    final StreamResponse streamResponse = unifiedMapper.toStreamResponseNoJoinStatus(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = unifiedMapper.toStreamTypeInfo(stream.getStreamType());
    // Create the response
    final UpdateStreamResponse updateStreamResponse = UpdateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse);
    // Return a localized response the updated stream
    return localizer.of(updateStreamResponse);
  }

  /**
   * Updates the additional details of a stream, including other links and the organization name.
   *
   * <p>This method finds the stream by its ID, checks that the stream type matches the type in the update request, and verifies that the user attempting the update is the creator of the stream.</p>
   *
   * <p>It then updates the stream's other details and saves the changes to the repository. The updated stream is mapped to a response object, and a localized response is returned.</p>
   *
   * @param streamId The ID of the stream to be updated.
   * @param updateStreamOtherDetailDto The DTO containing the new details to apply to the stream.
   * @param user The user requesting the update.
   * @return A localized response containing the updated stream information.
   * @throws StreamNotFoundException If no stream exists for the given ID.
   * @throws CalendarNotFoundException If the corresponding calendar could not be found.
   * @throws Oauth2InvalidAuthorizationException If the user's OAuth2 authorization is invalid.
   * @throws StreamNotCreatedByUserException If the user is not the creator of the stream.
   * @throws StreamAlreadyHappenedException If the stream has already occurred.
   * @throws StreamAlreadyCanceledException If the stream has already been canceled.
   * @throws FailedOperationException If any step in the update process fails.
   */
  @Override
  public UpdateStreamResponse updateStreamOtherDetails(final Long streamId, final UpdateStreamOtherDetailDto updateStreamOtherDetailDto, final FleenUser user) throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException, StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException {
    // Find the stream by its ID
    FleenStream stream = streamOperationsService.findStream(streamId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.checkStreamTypeNotEqual(updateStreamOtherDetailDto.getStreamType());
    // Validate if the user is the creator of the event
    verifyStreamDetails(stream, user);
    // Update the FleenStream object with the response from Google Calendar
    stream.updateOtherDetail(
      updateStreamOtherDetailDto.getOtherDetails(),
      updateStreamOtherDetailDto.getOtherLink(),
      updateStreamOtherDetailDto.getGroupOrOrganizationName()
    );

    // Save the updated stream to the repository
    stream = streamOperationsService.save(stream);
    // Get the stream response
    final StreamResponse streamResponse = unifiedMapper.toStreamResponseNoJoinStatus(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = unifiedMapper.toStreamTypeInfo(stream.getStreamType());
    // Create the response
    final UpdateStreamResponse updateStreamResponse = UpdateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse);
    // Return a localized response the updated stream
    return localizer.of(updateStreamResponse);
  }

  /**
   * Handles updating or patching stream details externally, either as a calendar event or a live broadcast.
   * If the stream is an event, it prepares a request to update the calendar event details, such as the title,
   * description, and location, and interacts with the external calendar service, like Google Calendar.
   *
   * <p>If the stream is a live broadcast, the method creates an update request containing the necessary details
   * and uses the provided OAuth2 access token to update the live broadcast through the external service.</p>
   *
   * <p>The method determines whether the stream is an event or a broadcast, and whether it is a patch request,
   * before executing the corresponding external service update.</p>
   *
   * @param patchStreamRequest the request object containing updated stream details, such as the title,
   *                           description, and location, along with external stream IDs and authorization tokens if necessary.
   */
  protected void patchStreamExternally(final ExternalStreamRequest patchStreamRequest) {
    // Verify if the stream to be updated is an event
    if (patchStreamRequest.isAnEvent() && patchStreamRequest.isPatchRequest()) {
      // Prepare a request to patch the calendar event with updated details
      final PatchCalendarEventRequest patchCalendarEventRequest = PatchCalendarEventRequest.of(
        patchStreamRequest.calendarExternalId(),
        patchStreamRequest.streamExternalId(),
        patchStreamRequest.getTitle(),
        patchStreamRequest.getDescription(),
        patchStreamRequest.getLocation()
      );

      // Update the event details in the Google Calendar
      eventOperationsService.updateEventInGoogleCalendar(patchStreamRequest.getStream(), patchCalendarEventRequest);

    } else if (patchStreamRequest.isABroadcast() && patchStreamRequest.isPatchRequest()) {
      // Create an update request using the access token and update details
      final UpdateLiveBroadcastRequest updateLiveBroadcastRequest = UpdateLiveBroadcastRequest.of(
        patchStreamRequest.accessToken(),
        patchStreamRequest.getTitle(),
        patchStreamRequest.getDescription(),
        patchStreamRequest.streamExternalId()
      );

      // Update the live broadcast using an external service
      liveBroadcastUpdateService.updateLiveBroadcastAndStream(patchStreamRequest.getStream(), updateLiveBroadcastRequest);
    }
  }

  /**
   * Updates the visibility of an existing stream, either an event or a live broadcast, based on the provided stream ID
   * and visibility details. This method ensures that the stream's visibility is updated both in the internal system
   * and through any necessary external services such as Google Calendar or YouTube for live broadcasts.
   *
   * <p>The method starts by retrieving the stream using its ID, followed by verifying that the type of the stream
   * matches the type provided in the visibility update request. It also checks if the user requesting the update
   * is the creator of the stream and verifies that the stream is not already completed or canceled.</p>
   *
   * <p>Next, the method validates that the stream is not currently ongoing before updating its visibility status.
   * The updated stream is saved to the repository, ensuring the change is persisted internally. Depending on the
   * type of stream (event or live broadcast), the method retrieves OAuth2 authorization if needed and handles
   * the necessary external API calls to update the stream visibility with the external calendar or broadcast service.</p>
   *
   * <p>After updating the visibility externally, the method returns a localized response containing the updated
   * stream visibility information, the stream type, and its ID. It ensures all interactions with external services
   * are handled correctly, and if any issues arise during the update process, appropriate exceptions are thrown.</p>
   *
   * @param streamId                   the unique identifier of the stream (event or broadcast) to update visibility for
   * @param updateStreamVisibilityDto  the DTO containing the new visibility status for the stream
   * @param user                      the user making the update request; must be the creator of the stream
   * @return                          a localized response object containing details about the updated stream visibility
   * @throws StreamNotFoundException              if the stream with the specified ID cannot be found
   * @throws CalendarNotFoundException                 if a calendar associated with the user's country is not found
   * @throws Oauth2InvalidAuthorizationException       if OAuth2 authorization is invalid or has expired
   * @throws StreamNotCreatedByUserException           if the user is not the creator of the stream
   * @throws StreamAlreadyHappenedException            if the stream has already taken place and cannot be updated
   * @throws StreamAlreadyCanceledException            if the stream has been canceled and cannot be updated
   * @throws CannotCancelOrDeleteOngoingStreamException if the stream is currently ongoing and cannot be canceled or updated
   * @throws FailedOperationException                  if the update operation fails for any reason, either internally or externally
   */
  @Override
  @Transactional
  public UpdateStreamVisibilityResponse updateStreamVisibility(final Long streamId, final UpdateStreamVisibilityDto updateStreamVisibilityDto, final FleenUser user)
    throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
      StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException,
      CannotCancelOrDeleteOngoingStreamException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamOperationsService.findStream(streamId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.checkStreamTypeNotEqual(updateStreamVisibilityDto.getStreamType());
    // Retrieve the current or existing status or visibility status of a stream
    final StreamVisibility currentStreamVisibility = stream.getStreamVisibility();
    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Verify if the stream is still ongoing
    stream.checkNotOngoingForUpdate();
    // Update the visibility of an event or stream
    stream.setStreamVisibility(updateStreamVisibilityDto.getActualVisibility());
    // Save the updated stream in the repository
    streamOperationsService.save(stream);

    // Get stream other details
    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user);
    // Retrieve the calendar from the stream details
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    // Retrieve the oauth2Authorization from the stream details
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    // Create request to update stream visibility
    final ExternalStreamRequest updateStreamVisibilityRequest = createUpdateStreamVisibilityRequest(calendar, oauth2Authorization, stream, updateStreamVisibilityDto.getVisibility());
    // Update the stream visibility using an external service
    updateStreamVisibilityExternally(updateStreamVisibilityRequest, currentStreamVisibility);

    // Retrieve the stream visibility information
    final StreamVisibilityInfo streamVisibility = unifiedMapper.toStreamVisibilityInfo(stream.getStreamVisibility());
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = unifiedMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response of the update
    return localizer.of(UpdateStreamVisibilityResponse.of(streamId, streamVisibility, streamTypeInfo));
  }

  /**
   * Updates the stream's visibility both internally and externally based on the type of stream (event or broadcast).
   *
   * <p>The method checks if the stream is an event or a broadcast and prepares the necessary requests to update the stream's
   * visibility in external services. For events, it sends pending invitations to attendees if the visibility change affects
   * their status.</p>
   *
   * <p>If the stream is an event, it updates the visibility in Google Calendar. If it's a broadcast, it updates the stream
   * visibility on the external live broadcasting platform using the provided access token.</p>
   *
   * @param updateStreamVisibilityRequest  the request containing the necessary details for updating the stream visibility
   * @param previousStreamVisibility       the previous visibility status of the stream to determine attendee updates
   */
  protected void updateStreamVisibilityExternally(final ExternalStreamRequest updateStreamVisibilityRequest, final StreamVisibility previousStreamVisibility) {
    if (updateStreamVisibilityRequest.isAnEvent() && updateStreamVisibilityRequest.isVisibilityUpdateRequest()) {
      // Create a request to update the stream's visibility
      final UpdateCalendarEventVisibilityRequest request = UpdateCalendarEventVisibilityRequest.of(
        updateStreamVisibilityRequest.calendarExternalId(),
        updateStreamVisibilityRequest.streamExternalId(),
        updateStreamVisibilityRequest.getVisibility()
      );

      // Update the event visibility using an external service
      eventOperationsService.updateEventVisibility(request);

      eventOperationsService.sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(
        updateStreamVisibilityRequest.calendarExternalId(),
        updateStreamVisibilityRequest.getStream(),
        previousStreamVisibility
      );

    } else if (updateStreamVisibilityRequest.isABroadcast() && updateStreamVisibilityRequest.isVisibilityUpdateRequest()) {
      // Create a request to update the service's visibility
      final UpdateLiveBroadcastVisibilityRequest updateLiveBroadcastVisibilityRequest = UpdateLiveBroadcastVisibilityRequest.of(
        updateStreamVisibilityRequest.accessToken(),
        updateStreamVisibilityRequest.streamExternalId(),
        updateStreamVisibilityRequest.getVisibility()
      );

      // Update the stream visibility using an external service
      liveBroadcastUpdateService.updateStreamVisibility(updateLiveBroadcastVisibilityRequest);
    }
  }

}
