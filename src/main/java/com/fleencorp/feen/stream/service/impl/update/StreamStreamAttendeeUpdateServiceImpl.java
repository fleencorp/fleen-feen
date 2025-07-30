package com.fleencorp.feen.stream.service.impl.update;

import com.fleencorp.feen.calendar.model.request.event.create.AddNewEventAttendeeRequest;
import com.fleencorp.feen.stream.service.event.EventOperationsService;
import com.fleencorp.feen.stream.service.update.StreamAttendeeUpdateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the AttendeeUpdateService interface for stream attendees.
 *
 * <p>This class provides the functionality for updating attendee information
 * within the context of a stream. It implements the methods defined in the
 * AttendeeUpdateService interface to manage and update attendee details related
 * to streams.</p>
 *
 */
@Service
public class StreamStreamAttendeeUpdateServiceImpl implements StreamAttendeeUpdateService {

  private final EventOperationsService eventOperationsService;

  public StreamStreamAttendeeUpdateServiceImpl(final EventOperationsService eventOperationsService) {
    this.eventOperationsService = eventOperationsService;
   }

  /**
   * Creates a new stream attendee request and sends an invitation to the specified attendee.
   *
   * <p>This method constructs an {@code AddNewEventAttendeeRequest} using the provided
   * calendar ID, stream ID, attendee email address, and an optional comment.
   * The newly created request is then sent to the {@code eventUpdateService} to add
   * the attendee to the calendar event.</p>
   *
   * @param calendarExternalId The external ID of the calendar to which the stream belongs.
   * @param streamExternalId The external ID of the stream.
   * @param attendeeEmailAddress The email address of the attendee to invite.
   * @param comment An optional comment regarding the attendee invitation.
   */
  @Override
  @Transactional
  public void createNewEventAttendeeRequestAndSendInvitation(final String calendarExternalId, final String streamExternalId, final String attendeeEmailAddress, final String comment) {
    final AddNewEventAttendeeRequest addNewEventAttendeeRequest = AddNewEventAttendeeRequest.withComment(
      calendarExternalId,
      streamExternalId,
      attendeeEmailAddress,
      comment
    );

    // Send an invitation to the user in the Calendar & Event API
    eventOperationsService.addNewAttendeeToCalendarEvent(addNewEventAttendeeRequest);
  }
}
