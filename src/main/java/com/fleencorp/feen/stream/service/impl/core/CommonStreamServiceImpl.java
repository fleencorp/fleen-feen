package com.fleencorp.feen.stream.service.impl.core;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.oauth2.model.domain.Oauth2Authorization;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.exception.core.*;
import com.fleencorp.feen.stream.mapper.StreamUnifiedMapper;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.dto.core.*;
import com.fleencorp.feen.stream.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.stream.model.info.core.StreamStatusInfo;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.info.core.StreamVisibilityInfo;
import com.fleencorp.feen.stream.model.request.external.ExternalStreamRequest;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.base.*;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.stream.service.core.CommonStreamService;
import com.fleencorp.feen.stream.service.core.ExternalStreamRequestService;
import com.fleencorp.feen.stream.service.core.StreamRequestService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fleencorp.feen.stream.service.impl.core.StreamServiceImpl.verifyStreamDetails;

@Service
public class CommonStreamServiceImpl implements CommonStreamService, StreamRequestService {

  private final ExternalStreamRequestService externalStreamRequestService;
  private final StreamOperationsService streamOperationsService;
  private final UnifiedMapper unifiedMapper;
  private final StreamUnifiedMapper streamUnifiedMapper;
  private final Localizer localizer;

  public CommonStreamServiceImpl(
      final ExternalStreamRequestService externalStreamRequestService,
      final StreamOperationsService streamOperationsService,
      final UnifiedMapper unifiedMapper,
      final StreamUnifiedMapper streamUnifiedMapper,
      final Localizer localizer) {
    this.externalStreamRequestService = externalStreamRequestService;
    this.streamOperationsService = streamOperationsService;
    this.unifiedMapper = unifiedMapper;
    this.streamUnifiedMapper = streamUnifiedMapper;
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
  public DeleteStreamResponse deleteStream(final Long streamId, final DeleteStreamDto deleteStreamDto, final RegisteredUser user)
      throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
        CannotCancelOrDeleteOngoingStreamException, FailedOperationException {
    final FleenStream stream = streamOperationsService.findStream(streamId);
    stream.checkStreamTypeNotEqual(deleteStreamDto.getStreamType());

    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user.toMember());
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    stream.checkIsOrganizer(user.getId());
    stream.checkNotOngoingForCancelOrDeleteOrUpdate();
    stream.delete();
    streamOperationsService.save(stream);

    final ExternalStreamRequest deleteStreamRequest = createDeleteStreamRequest(calendar, oauth2Authorization, stream);
    externalStreamRequestService.deleteStreamExternally(deleteStreamRequest);

    final IsDeletedInfo deletedInfo = unifiedMapper.toIsDeletedInfo(stream.isDeleted());
    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(stream.getStreamType());

    final DeleteStreamResponse deleteStreamResponse = DeleteStreamResponse.of(streamId, streamTypeInfo, deletedInfo);
    return localizer.of(deleteStreamResponse);
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
  public CancelStreamResponse cancelStream(final Long streamId, final CancelStreamDto cancelStreamDto, final RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
      StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException,
      FailedOperationException {
    final FleenStream stream = streamOperationsService.findStream(streamId);
    final StreamType streamType = stream.getStreamType();

    stream.checkStreamTypeNotEqual(cancelStreamDto.getStreamType());
    verifyStreamDetails(stream, user);
    stream.checkNotOngoingForCancelOrDeleteOrUpdate();
    stream.cancel();

    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user.toMember());
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    streamOperationsService.save(stream);

    final StreamStatusInfo statusInfo = streamUnifiedMapper.toStreamStatusInfo(stream.getStreamStatus());
    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(stream.getStreamType());

    final ExternalStreamRequest cancelStreamRequest = ExternalStreamRequest.ofCancel(calendar, oauth2Authorization, stream, streamType);
    externalStreamRequestService.cancelStreamExternally(cancelStreamRequest);

    final CancelStreamResponse cancelStreamResponse = CancelStreamResponse.of(streamId, statusInfo, streamTypeInfo);
    return localizer.of(cancelStreamResponse);
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
  public RescheduleStreamResponse rescheduleStream(final Long streamId, final RescheduleStreamDto rescheduleStreamDto, final RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
      StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException {
    final FleenStream stream = streamOperationsService.findStream(streamId);

    stream.checkStreamTypeNotEqual(rescheduleStreamDto.getStreamType());

    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user.toMember());
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    verifyStreamDetails(stream, user);
    stream.reschedule(
      rescheduleStreamDto.getStartDateTime(),
      rescheduleStreamDto.getEndDateTime(),
      rescheduleStreamDto.getTimezone()
    );

    streamOperationsService.save(stream);
    final ExternalStreamRequest rescheduleStreamRequest = createRescheduleStreamRequest(calendar, oauth2Authorization, stream, rescheduleStreamDto);
    externalStreamRequestService.rescheduleStreamExternally(rescheduleStreamRequest);

    final StreamResponse streamResponse = streamUnifiedMapper.toStreamResponseNoJoinStatus(stream);
    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(stream.getStreamType());
    final RescheduleStreamResponse rescheduleStreamResponse = RescheduleStreamResponse.of(streamId, streamResponse, streamTypeInfo);

    return localizer.of(rescheduleStreamResponse);
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
  public UpdateStreamResponse updateStream(final Long streamId, final UpdateStreamDto updateStreamDto, final RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
      StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException,
      FailedOperationException {
    FleenStream stream = streamOperationsService.findStream(streamId);

    stream.checkStreamTypeNotEqual(updateStreamDto.getStreamType());
    verifyStreamDetails(stream, user);

    stream.update(
      updateStreamDto.getTitle(),
      updateStreamDto.getDescription(),
      updateStreamDto.getTags(),
      updateStreamDto.getLocation()
    );

    stream = streamOperationsService.save(stream);

    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user.toMember());
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    final ExternalStreamRequest patchStreamRequest = createPatchStreamRequest(calendar, oauth2Authorization, stream, updateStreamDto);
    externalStreamRequestService.patchStreamExternally(patchStreamRequest);

    final StreamResponse streamResponse = streamUnifiedMapper.toStreamResponseNoJoinStatus(stream);
    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(stream.getStreamType());
    final UpdateStreamResponse updateStreamResponse = UpdateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse);

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
  public UpdateStreamResponse updateStreamOtherDetails(final Long streamId, final UpdateStreamOtherDetailDto updateStreamOtherDetailDto, final RegisteredUser user) throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException, StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException {
    FleenStream stream = streamOperationsService.findStream(streamId);

    stream.checkStreamTypeNotEqual(updateStreamOtherDetailDto.getStreamType());
    verifyStreamDetails(stream, user);

    stream.updateOtherDetail(
      updateStreamOtherDetailDto.getOtherDetails(),
      updateStreamOtherDetailDto.getOtherLink(),
      updateStreamOtherDetailDto.getGroupOrOrganizationName()
    );

    stream = streamOperationsService.save(stream);

    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(stream.getStreamType());
    final StreamResponse streamResponse = streamUnifiedMapper.toStreamResponseNoJoinStatus(stream);
    final UpdateStreamResponse updateStreamResponse = UpdateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse);

    return localizer.of(updateStreamResponse);
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
  public UpdateStreamVisibilityResponse updateStreamVisibility(final Long streamId, final UpdateStreamVisibilityDto updateStreamVisibilityDto, final RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
      StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException,
      CannotCancelOrDeleteOngoingStreamException, FailedOperationException {
    final FleenStream stream = streamOperationsService.findStream(streamId);

    stream.checkStreamTypeNotEqual(updateStreamVisibilityDto.getStreamType());
    final StreamVisibility currentStreamVisibility = stream.getStreamVisibility();

    verifyStreamDetails(stream, user);
    stream.checkNotOngoingForUpdate();
    stream.setStreamVisibility(updateStreamVisibilityDto.getActualVisibility());

    streamOperationsService.save(stream);

    final StreamOtherDetailsHolder streamOtherDetailsHolder = streamOperationsService.retrieveStreamOtherDetailsHolder(stream, user.toMember());
    final Calendar calendar = streamOtherDetailsHolder.calendar();
    final Oauth2Authorization oauth2Authorization = streamOtherDetailsHolder.oauth2Authorization();

    final ExternalStreamRequest updateStreamVisibilityRequest = createUpdateStreamVisibilityRequest(calendar, oauth2Authorization, stream, updateStreamVisibilityDto.getVisibility());
    externalStreamRequestService.updateStreamVisibilityExternally(updateStreamVisibilityRequest, currentStreamVisibility);

    final StreamVisibilityInfo streamVisibility = streamUnifiedMapper.toStreamVisibilityInfo(stream.getStreamVisibility());
    final StreamTypeInfo streamTypeInfo = streamUnifiedMapper.toStreamTypeInfo(stream.getStreamType());
    final UpdateStreamVisibilityResponse updateStreamVisibilityResponse = UpdateStreamVisibilityResponse.of(stream.getStreamId(), streamVisibility, streamTypeInfo);

    return localizer.of(updateStreamVisibilityResponse);
  }

}
