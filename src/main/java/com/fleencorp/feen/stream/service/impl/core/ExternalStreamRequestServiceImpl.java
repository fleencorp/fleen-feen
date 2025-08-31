package com.fleencorp.feen.stream.service.impl.core;

import com.fleencorp.feen.calendar.model.request.event.update.*;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.model.request.external.ExternalStreamRequest;
import com.fleencorp.feen.stream.model.request.external.broadcast.DeleteLiveBroadcastRequest;
import com.fleencorp.feen.stream.model.request.external.broadcast.RescheduleLiveBroadcastRequest;
import com.fleencorp.feen.stream.model.request.external.broadcast.UpdateLiveBroadcastRequest;
import com.fleencorp.feen.stream.model.request.external.broadcast.UpdateLiveBroadcastVisibilityRequest;
import com.fleencorp.feen.stream.service.core.ExternalStreamRequestService;
import com.fleencorp.feen.stream.service.event.EventOperationsService;
import com.fleencorp.feen.stream.service.impl.update.LiveBroadcastUpdateService;
import org.springframework.stereotype.Service;

@Service
public class ExternalStreamRequestServiceImpl implements ExternalStreamRequestService {


  private final EventOperationsService eventOperationsService;
  private final LiveBroadcastUpdateService liveBroadcastUpdateService;

  public ExternalStreamRequestServiceImpl(
    final EventOperationsService eventOperationsService,
    final LiveBroadcastUpdateService liveBroadcastUpdateService) {
    this.eventOperationsService = eventOperationsService;
    this.liveBroadcastUpdateService = liveBroadcastUpdateService;
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
  @Override
  public void deleteStreamExternally(final ExternalStreamRequest deleteStreamRequest) {
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
  @Override
  public void cancelStreamExternally(final ExternalStreamRequest cancelStreamRequest) {
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
  @Override
  public void rescheduleStreamExternally(final ExternalStreamRequest rescheduleStreamRequest) {
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
  @Override
  public void patchStreamExternally(final ExternalStreamRequest patchStreamRequest) {
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
  @Override
  public void updateStreamVisibilityExternally(final ExternalStreamRequest updateStreamVisibilityRequest, final StreamVisibility previousStreamVisibility) {
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
