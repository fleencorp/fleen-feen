package com.fleencorp.feen.stream.service.impl.event;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.calendar.model.domain.Calendar;
import com.fleencorp.feen.calendar.model.event.AddCalendarEventAttendeesEvent;
import com.fleencorp.feen.calendar.model.request.event.create.CreateCalendarEventRequest;
import com.fleencorp.feen.calendar.model.request.event.create.CreateInstantCalendarEventRequest;
import com.fleencorp.feen.common.event.publisher.StreamEventPublisher;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.common.service.misc.MiscService;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.mapper.StreamMapper;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.dto.event.CreateEventDto;
import com.fleencorp.feen.stream.model.dto.event.CreateEventDto.EventAttendeeOrGuest;
import com.fleencorp.feen.stream.model.dto.event.CreateInstantEventDto;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.request.external.ExternalStreamRequest;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.base.CreateStreamResponse;
import com.fleencorp.feen.stream.model.response.common.event.DataForCreateEventResponse;
import com.fleencorp.feen.stream.service.attendee.StreamAttendeeOperationsService;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.stream.service.core.StreamRequestService;
import com.fleencorp.feen.stream.service.event.EventOperationsService;
import com.fleencorp.feen.stream.service.event.EventService;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.feen.common.validator.impl.TimezoneValidValidator.getAvailableTimezones;
import static com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus.PENDING;
import static com.fleencorp.feen.stream.service.impl.attendee.StreamAttendeeServiceImpl.getAttendeeIds;
import static com.fleencorp.feen.stream.service.impl.attendee.StreamAttendeeServiceImpl.getAttendeesEmailAddresses;

/**
 * Implementation of the EventService interface.
 * This class provides methods for managing events.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class EventServiceImpl implements EventService, StreamRequestService {

  private final String delegatedAuthorityEmail;
  private final EventOperationsService eventOperationsService;
  private final MiscService miscService;
  private final StreamAttendeeOperationsService streamAttendeeOperationsService;
  private final StreamOperationsService streamOperationsService;
  private final StreamMapper streamMapper;
  private final StreamEventPublisher streamEventPublisher;
  private final Localizer localizer;

  public EventServiceImpl(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      @Lazy final EventOperationsService eventOperationsService,
      final MiscService miscService,
      @Lazy final StreamAttendeeOperationsService streamAttendeeOperationsService,
      @Lazy final StreamOperationsService streamOperationsService,
      final Localizer localizer,
      final StreamEventPublisher streamEventPublisher,
      final StreamMapper streamMapper) {
    this.eventOperationsService = eventOperationsService;
    this.miscService = miscService;
    this.streamAttendeeOperationsService = streamAttendeeOperationsService;
    this.streamOperationsService = streamOperationsService;
    this.delegatedAuthorityEmail = delegatedAuthorityEmail;
    this.streamMapper = streamMapper;
    this.streamEventPublisher = streamEventPublisher;
    this.localizer = localizer;
  }

  /**
   * Retrieves the data required for creating an event, including available timezones.
   *
   * <p>This method fetches the set of available timezones and returns them as part of a
   * localized response. This response can be used to populate the event creation form with
   * timezone options.</p>
   *
   * @return a localized response containing a DataForCreateEventResponse with the available timezones.
   */
  @Override
  public DataForCreateEventResponse getDataForCreateEvent() {
    // Get the set of available timezones.
    final Set<String> timezones = getAvailableTimezones();
    // Return the response object containing both the countries and timezones.
    return localizer.of(DataForCreateEventResponse.of(timezones));
  }

  /**
   * Creates a new event for a user, both in the local system and in an external calendar service.
   *
   * <p>This method finds the appropriate calendar for the user's country, creates a new {@link FleenStream}
   * object from the provided DTO, and updates the stream with details such as the user's full name, email address,
   * and phone number. It then registers the user as an attendee, increases the total attendees count, and saves
   * the stream both locally and externally in a calendar service such as Google Calendar.</p>
   *
   * <p>The method returns a localized response indicating the success of the creation operation,
   * including the stream ID, stream type info, and other relevant details about the created event.</p>
   *
   * @param createEventDto the DTO containing the details of the event to be created
   * @param user the user who is organizing the event
   * @return a {@link CreateStreamResponse} containing details about the created event
   * @throws CalendarNotFoundException if no calendar is found for the user's country
   */
  @Override
  @Transactional
  public CreateStreamResponse createEvent(final CreateEventDto createEventDto, final RegisteredUser user) throws CalendarNotFoundException {
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry());
    // Set event organizer as attendee in Google calendar
    final String organizerAliasOrDisplayName = createEventDto.getOrganizerAlias(user.getFullName());
    // Retrieve the organizer details to be added as an attendee
    final EventAttendeeOrGuest attendeeOrGuest = EventAttendeeOrGuest.of(user.getEmailAddress(), organizerAliasOrDisplayName);

    // Create a FleenStream object from the DTO and update its details with the Google Calendar response
    FleenStream stream = createEventDto.toStream(user.toMember());
    stream.update(
      organizerAliasOrDisplayName,
      user.getEmailAddress(),
      user.getPhoneNumber()
    );

    // Save stream and create event in Google Calendar Event Service externally
    stream = streamOperationsService.save(stream);
    // Increase attendees count, save the event
    streamOperationsService.increaseTotalAttendeesOrGuests(stream);
    // Register the organizer of the event as an attendee or guest
    streamOperationsService.registerAndApproveOrganizerOfStreamAsAnAttendee(stream, user);
    // Create and build the request to create an event
    final ExternalStreamRequest createStreamRequest = createAndBuildStreamRequest(calendar, stream, attendeeOrGuest, user.getEmailAddress(), createEventDto);
    // Create and add event in Calendar through external service
    createEventExternally(createStreamRequest);
    // Increment attendee count because of creator or organizer of event
    final StreamResponse streamResponse = streamMapper.toStreamResponseByAdminUpdate(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response of the created event
    return localizer.of(CreateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse));
  }

  /**
   * Builds and returns a {@link ExternalStreamRequest} containing all necessary details for creating a stream,
   * including calendar, stream, attendee or guest information, user email, stream type, and event details.
   *
   * <p>This method consolidates the required data from the provided parameters to create a new instance
   * of {@link ExternalStreamRequest}. It is used to prepare the request for creating a stream and passing
   * the relevant information to external services.</p>
   *
   * @param calendar the calendar associated with the stream
   * @param stream the stream object to be created
   * @param attendeeOrGuest the event attendee or guest to be added to the stream
   * @param userEmailAddress the email address of the user creating the stream
   * @param createEventDto the DTO containing event details such as title and description
   * @return a {@link ExternalStreamRequest} instance with the provided stream details
   */
  protected ExternalStreamRequest createAndBuildStreamRequest(final Calendar calendar, final FleenStream stream, final EventAttendeeOrGuest attendeeOrGuest, final String userEmailAddress, final CreateEventDto createEventDto) {
    return ExternalStreamRequest.ofCreateEvent(
      calendar,
      stream,
      attendeeOrGuest,
      userEmailAddress,
      stream.getStreamType(),
      createEventDto
    );
  }

  /**
   * Creates an event in an external calendar service (e.g., Google Calendar) based on the provided stream request.
   *
   * <p>This method verifies whether the provided {@link ExternalStreamRequest} corresponds to an event.
   * If so, it creates a calendar event request using the details from the request, adds the event
   * organizer as an attendee, and updates the request with additional necessary details, such as the
   * calendar's external ID, the delegated authority email, and the user's email address. Finally,
   * the event is created and added to the external calendar service via the {@link EventOperationsService}.</p>
   *
   * @param createStreamRequest the request object containing details of the event to be created
   */
  protected void createEventExternally(final ExternalStreamRequest createStreamRequest) {
    // Verify if the stream to be updated is an event
    if (createStreamRequest.isAnEvent() && createStreamRequest.isCreateEventRequest()) {
      // Create a Calendar event request
      final CreateCalendarEventRequest createCalendarEventRequest = CreateCalendarEventRequest.by(createStreamRequest.getCreateEventDto());
      // Add event organizer as an attendee
      createCalendarEventRequest.addAttendeeOrGuest(createStreamRequest.getAttendeeOrGuest());
      // Update the event request with necessary details
      createCalendarEventRequest.update(createStreamRequest.calendarExternalId(), delegatedAuthorityEmail, createStreamRequest.userEmailAddress());
      // Create and add event in Calendar through external service
      eventOperationsService.createEventInGoogleCalendar(createStreamRequest.getStream(), createCalendarEventRequest);
    }
  }

  /**
   * Creates an instant event for a user, both in the local system and in an external calendar service.
   *
   * <p>This method finds the appropriate calendar for the user's country, creates a new {@link FleenStream}
   * object from the provided DTO, and updates the stream with details like the user's full name, email address,
   * and phone number. It then registers the user as an attendee, increases the total attendees count,
   * and saves the stream both locally and externally in a calendar service such as Google Calendar.</p>
   *
   * <p>The method returns a localized response indicating the success of the creation operation,
   * including the stream ID, stream type info, and other relevant details about the created event.</p>
   *
   * @param createInstantEventDto the DTO containing the details of the event to be created
   * @param user the user who is organizing the event
   * @return a {@link CreateStreamResponse} containing details about the created event
   * @throws CalendarNotFoundException if no calendar is found for the user's country
   */
  @Override
  @Transactional
  public CreateStreamResponse createInstantEvent(final CreateInstantEventDto createInstantEventDto, final RegisteredUser user) throws CalendarNotFoundException {
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry());
    // Create a Stream object from the DTO and update its details with the Google Calendar response
    FleenStream stream = createInstantEventDto.toFleenStream(user.toMember());
    // Update the details of the stream
    stream.update(
      user.getFullName(),
      user.getEmailAddress(),
      user.getPhoneNumber());

    // Increase attendees count, save the event and and add the event in Google Calendar
    streamOperationsService.increaseTotalAttendeesOrGuests(stream);
    // Register the organizer of the event as an attendee or guest
    streamOperationsService.registerAndApproveOrganizerOfStreamAsAnAttendee(stream, user);
    // Save stream and create event in Google Calendar Event Service externally
    stream = streamOperationsService.save(stream);
    // Create and build the create stream request to be use for external purpose
    final ExternalStreamRequest createInstantStreamRequest = ExternalStreamRequest.ofCreateInstantEvent(calendar, stream, stream.getStreamType(), createInstantEventDto);
    // Create and add event in Calendar through external service
    createInstantEventExternally(createInstantStreamRequest);
    // Get the stream response
    final StreamResponse streamResponse = streamMapper.toStreamResponseByAdminUpdate(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Create the response
    final CreateStreamResponse createStreamResponse = CreateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse);
    // Return a localized response of the created event
    return localizer.of(createStreamResponse);
  }

  /**
   * Creates an instant event in an external calendar service, such as Google Calendar.
   *
   * <p>This method checks if the stream is an event, then creates a request to add an event in an external calendar.
   * It populates the request with the details from the provided {@link ExternalStreamRequest} object,
   * including the event information and calendar details. The event is then created and added to the external calendar
   * service.</p>
   *
   * @param createInstantStreamRequest the request containing the event details and calendar information
   */
  protected void createInstantEventExternally(final ExternalStreamRequest createInstantStreamRequest) {
    if (createInstantStreamRequest.isAnEvent() && createInstantStreamRequest.isCreateInstantEventRequest()) {
      // Create the request for creating an event externally
      final CreateInstantCalendarEventRequest createInstantCalendarEventRequest = CreateInstantCalendarEventRequest.by(createInstantStreamRequest.getCreateInstantEventDto());
      // Update the instant event request with necessary details
      createInstantCalendarEventRequest.update(createInstantStreamRequest.calendarExternalId());
      // Create and add event in Calendar through external service
      eventOperationsService.createInstantEventInGoogleCalendar(createInstantStreamRequest.getStream(), createInstantCalendarEventRequest);
    }
  }

  /**
   * Sends invitations to pending attendees based on the stream's current visibility status.
   *
   * <p>This method checks if the visibility of the stream has changed from private or protected to public.
   * If so, it processes the pending attendees and sends invitations to those who had requested to join when
   * the stream was private or protected. If the provided stream or previous visibility is null, an exception is thrown.</p>
   *
   * @param calendarExternalId the external ID of the calendar associated with the stream
   * @param stream the stream whose visibility is being updated
   * @param previousStreamVisibility the previous visibility status of the stream
   *
   * @throws FailedOperationException if any of the provided values is null or if the operation fails
   */
  @Override
  @Async
  public void sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(final String calendarExternalId, final FleenStream stream, final StreamVisibility previousStreamVisibility)
    throws FailedOperationException {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(stream, previousStreamVisibility), FailedOperationException::new);
    // Determine the updated or current visibility of the stream
    final StreamVisibility currentStreamVisibility = stream.getStreamVisibility();

    // If the stream visibility is PUBLIC, and it was previously PRIVATE or PROTECTED
    if (StreamVisibility.isPublic(currentStreamVisibility) && StreamVisibility.isPrivateOrProtected(previousStreamVisibility)) {
      // Process pending attendees and send invitations
      processPendingAttendees(calendarExternalId, stream);
    }
  }

  /**
   * Processes and approves pending attendees for a given stream and adds them to the calendar event.
   *
   * <p>This method retrieves all pending attendees for the specified stream, approves their requests,
   * and adds them to the calendar event. It first extracts the email addresses and IDs of the pending attendees,
   * approves all their invitation requests, and then adds them to the calendar event using their email addresses.</p>
   *
   * @param calendarExternalId the external ID of the calendar associated with the stream
   * @param stream the stream for which the pending attendees are being processed
   */
  protected void processPendingAttendees(final String calendarExternalId, final FleenStream stream) {
    // Retrieve all pending attendees for the specified stream
    final List<StreamAttendee> pendingAttendees = streamAttendeeOperationsService.findAllByStreamAndRequestToJoinStatus(stream, PENDING);
    // Extract email addresses and IDs of the pending attendees or guests
    final Set<String> attendeesOrGuestsEmailAddresses = getAttendeesEmailAddresses(pendingAttendees);
    // Extract the attendee IDS from pending attendees
    final Set<Long> attendeeIds = getAttendeeIds(pendingAttendees);

    // Approve all pending requests
    streamAttendeeOperationsService.approveAllAttendeeRequestInvitation(APPROVED, new ArrayList<>(attendeeIds));
    // Add attendees to the calendar event
    addNewAttendeesToCalendar(calendarExternalId, stream, attendeesOrGuestsEmailAddresses);
  }

  /**
   * Adds new attendees to the calendar event associated with the stream.
   *
   * <p>This method creates an event object containing the details of the attendees to be added,
   * including the calendar's external ID, the stream's external ID, and the email addresses of the new attendees.
   * It then publishes the event to add the new attendees to the calendar.</p>
   *
   * @param calendarExternalId the external ID of the calendar associated with the stream
   * @param stream the stream to which the new attendees are being added
   * @param attendeesOrGuestsEmailAddresses the email addresses of the attendees or guests to be added
   */
  protected void addNewAttendeesToCalendar(final String calendarExternalId, final FleenStream stream, final Set<String> attendeesOrGuestsEmailAddresses) {
    // Create an event object containing the details of the attendees to be added
    final AddCalendarEventAttendeesEvent addAttendeesEvent = AddCalendarEventAttendeesEvent.of(
      calendarExternalId,
      stream.getExternalId(),
      attendeesOrGuestsEmailAddresses,
      Set.of()
    );
    // Publish the event to add the new attendees to the calendar
    streamEventPublisher.addNewAttendees(addAttendeesEvent);
  }

}