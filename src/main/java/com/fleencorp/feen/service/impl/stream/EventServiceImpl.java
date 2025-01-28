package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.event.publisher.StreamEventPublisher;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto.EventAttendeeOrGuest;
import com.fleencorp.feen.model.dto.event.CreateInstantCalendarEventDto;
import com.fleencorp.feen.model.dto.stream.base.*;
import com.fleencorp.feen.model.event.AddCalendarEventAttendeesEvent;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.feen.model.info.stream.StreamStatusInfo;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamVisibilityInfo;
import com.fleencorp.feen.model.request.calendar.event.*;
import com.fleencorp.feen.model.request.stream.ExternalStreamRequest;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.base.*;
import com.fleencorp.feen.model.response.stream.common.event.DataForCreateEventResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.stream.EventService;
import com.fleencorp.feen.service.stream.common.StreamRequestService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.service.stream.update.EventUpdateService;
import com.fleencorp.feen.service.stream.update.OtherEventUpdateService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.PENDING;
import static com.fleencorp.feen.service.impl.stream.attendee.StreamAttendeeServiceImpl.getAttendeeIds;
import static com.fleencorp.feen.service.impl.stream.attendee.StreamAttendeeServiceImpl.getAttendeesEmailAddresses;
import static com.fleencorp.feen.service.impl.stream.base.StreamServiceImpl.*;
import static com.fleencorp.feen.validator.impl.TimezoneValidValidator.getAvailableTimezones;

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
  private final MiscService miscService;
  private final StreamService streamService;
  private final EventUpdateService eventUpdateService;
  private final OtherEventUpdateService otherEventUpdateService;
  private final FleenStreamRepository streamRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final StreamMapper streamMapper;
  private final StreamEventPublisher streamEventPublisher;
  private final Localizer localizer;

  /**
   * Constructor for initializing the EventServiceImpl class with required dependencies.
   *
   * <p>This constructor injects the necessary services and components, including the Google
   * delegated authority email, various service and repository classes, and utilities like
   * Localizer and StreamMapper. It sets up the internal state of the EventServiceImpl
   * to manage stream and event operations effectively.</p>
   *
   * @param delegatedAuthorityEmail the email address for the Google delegated authority, injected from the configuration.
   * @param miscService             the service for handling miscellaneous tasks.
   * @param streamService           the service for stream-related operations.
   * @param otherEventUpdateService the service for handling other types of event updates.
   * @param eventUpdateService      the service for updating event details.
   * @param streamRepository        the repository for accessing stream data.
   * @param streamAttendeeRepository the repository for accessing stream attendee data.
   * @param localizer       the service for providing localized responses.
   * @param streamEventPublisher    the publisher for publishing stream events.
   * @param streamMapper            the mapper for converting stream data to different representations.
   */
  public EventServiceImpl(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      final MiscService miscService,
      final StreamService streamService,
      final EventUpdateService eventUpdateService,
      final OtherEventUpdateService otherEventUpdateService,
      final FleenStreamRepository streamRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final Localizer localizer,
      final StreamEventPublisher streamEventPublisher,
      final StreamMapper streamMapper) {
    this.miscService = miscService;
    this.streamService = streamService;
    this.delegatedAuthorityEmail = delegatedAuthorityEmail;
    this.eventUpdateService = eventUpdateService;
    this.otherEventUpdateService = otherEventUpdateService;
    this.streamRepository = streamRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
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
  public CreateStreamResponse createEvent(final CreateCalendarEventDto createEventDto, final FleenUser user) throws CalendarNotFoundException {
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry());
    // Set event organizer as attendee in Google calendar
    final String organizerAliasOrDisplayName = createEventDto.getOrganizerAlias(user.getFullName());
    // Retrieve the organizer details to be added as an attendee
    final EventAttendeeOrGuest attendeeOrGuest = EventAttendeeOrGuest.of(user.getEmailAddress(), organizerAliasOrDisplayName);

    // Create a FleenStream object from the DTO and update its details with the Google Calendar response
    FleenStream stream = createEventDto.toFleenStream(user.toMember());
    stream.update(
      organizerAliasOrDisplayName,
      user.getEmailAddress(),
      user.getPhoneNumber()
    );

    // Increase attendees count, save the event and and add the event in Google Calendar
    stream = streamService.increaseTotalAttendeesOrGuestsAndSaveBecauseOfOrganizer(stream);
    // Register the organizer of the event as an attendee or guest
    streamService.registerAndApproveOrganizerOfStreamAsAnAttendee(stream, user);
    // Create and build the request to create an event
    final ExternalStreamRequest createStreamRequest = createAndBuildStreamRequest(calendar, stream, attendeeOrGuest, user.getEmailAddress(), createEventDto);
    // Create and add event in Calendar through external service
    createEventExternally(createStreamRequest);
    // Increment attendee count because of creator or organizer of event
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseApproved(stream);
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
  protected ExternalStreamRequest createAndBuildStreamRequest(final Calendar calendar, final FleenStream stream, final EventAttendeeOrGuest attendeeOrGuest, final String userEmailAddress, final CreateCalendarEventDto createEventDto) {
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
   * the event is created and added to the external calendar service via the {@link EventUpdateService}.</p>
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
      otherEventUpdateService.createEventInGoogleCalendar(createStreamRequest.getStream(), createCalendarEventRequest);
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
  public CreateStreamResponse createInstantEvent(final CreateInstantCalendarEventDto createInstantEventDto, final FleenUser user) throws CalendarNotFoundException {
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
    stream = streamService.increaseTotalAttendeesOrGuestsAndSaveBecauseOfOrganizer(stream);
    // Register the organizer of the event as an attendee or guest
    streamService.registerAndApproveOrganizerOfStreamAsAnAttendee(stream, user);
    // Save stream and create event in Google Calendar Event Service externally
    stream = streamRepository.save(stream);
    // Create and build the create stream request to be use for external purpose
    final ExternalStreamRequest createInstantStreamRequest = ExternalStreamRequest.ofCreateInstantEvent(calendar, stream, stream.getStreamType(), createInstantEventDto);
    // Create and add event in Calendar through external service
    createInstantEventExternally(createInstantStreamRequest);
    // Get the stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseApproved(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response of the created event
    return localizer.of(CreateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse));
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
      eventUpdateService.createInstantEventInGoogleCalendar(createInstantStreamRequest.getStream(), createInstantCalendarEventRequest);
    }
  }

  /**
   * Updates an event's details and synchronizes the changes externally in a transactional manner.
   *
   * <p>This method retrieves the calendar associated with the user's country and finds the stream (event) by its ID.
   * It validates that the user is the creator of the stream, updates the stream's details using the information from
   * the provided {@link UpdateStreamDto}, and saves the updated stream. It then creates a patch request
   * to synchronize the changes with an external calendar service (e.g., Google Calendar) and updates the event there.</p>
   *
   * <p>The method returns a localized response that includes the updated stream details and stream type information.</p>
   *
   * @param eventId         the ID of the stream to be updated
   * @param updateStreamDto the DTO containing the updated event details (title, description, tags, location)
   * @param user            the user requesting the update
   * @return an {@link UpdateStreamResponse} containing details about the updated stream
   * @throws CalendarNotFoundException       if no calendar is found for the user's country
   * @throws FleenStreamNotFoundException    if the stream with the given ID cannot be found
   * @throws StreamNotCreatedByUserException if the stream was not created by the provided user
   * @throws StreamAlreadyHappenedException  if the stream has already occurred
   * @throws StreamAlreadyCanceledException  if the stream has already been canceled
   * @throws FailedOperationException        if the external operation to patch or update the stream fails
   */
  @Override
  @Transactional
  public UpdateStreamResponse updateEvent(final Long eventId, final UpdateStreamDto updateStreamDto, final FleenUser user)
      throws CalendarNotFoundException, FleenStreamNotFoundException, StreamNotCreatedByUserException,
      StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException  {
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry());
    // Find the stream by its ID
    FleenStream stream = streamService.findStream(eventId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(updateStreamDto.getStreamType());

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
    stream = streamRepository.save(stream);
    // Create and build the patch stream request with necessary payload
    final ExternalStreamRequest patchStreamRequest = createPatchStreamRequest(calendar, stream, updateStreamDto);
    // Patch or update the stream externally
    patchStreamExternally(patchStreamRequest);
    // Get the stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseNoJoinStatus(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response the updated stream
    return localizer.of(UpdateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse));
  }

  /**
   * Patches a stream externally by updating its event details if it is an event.
   *
   * <p>This method checks if the provided {@link ExternalStreamRequest} represents an event.
   * If it is an event, it calls {@link #patchEventInGoogleCalendar(ExternalStreamRequest)} to update the event details
   * in Google Calendar. Otherwise, no external action is performed.</p>
   *
   * @param patchStreamRequest the request containing the details to patch the stream or event
   */
  protected void patchStreamExternally(final ExternalStreamRequest patchStreamRequest) {
    // Verify if the stream to be updated is an event
    if (patchStreamRequest.isAnEvent() && patchStreamRequest.isPatchRequest()) {
      patchEventInGoogleCalendar(patchStreamRequest);
    }
  }

  /**
   * Updates the details of an event in Google Calendar using the provided patch request.
   *
   * <p>This method prepares a request to patch the calendar event with updated details such as the title,
   * description, and location, based on the provided {@link ExternalStreamRequest}. Once the request is prepared,
   * it delegates the operation to the {@code eventUpdateService} to update the event in Google Calendar.</p>
   *
   * @param patchStreamRequest the request containing the details to patch the calendar event
   */
  protected void patchEventInGoogleCalendar(final ExternalStreamRequest patchStreamRequest) {
    // Prepare a request to patch the calendar event with updated details
    final PatchCalendarEventRequest patchCalendarEventRequest = PatchCalendarEventRequest.of(
      patchStreamRequest.calendarExternalId(),
      patchStreamRequest.streamExternalId(),
      patchStreamRequest.getTitle(),
      patchStreamRequest.getDescription(),
      patchStreamRequest.getLocation()
    );

    // Update the event details in the Google Calendar
    eventUpdateService.updateEventInGoogleCalendar(patchStreamRequest.getStream(), patchCalendarEventRequest);
  }

  /**
   * Deletes an event and updates the stream status, performing external deletion operations.
   *
   * <p>This method retrieves the stream by its ID, verifies the stream's details (such as ownership, active status,
   * and ongoing status), and checks if the stream is ongoing. If valid, it marks the stream as deleted, saves the
   * updated stream, and performs the deletion operation externally. Finally, it returns a localized response
   * indicating the result of the deletion process.</p>
   *
   * <p>The method may throw various exceptions if the stream is not found, the user is not the creator,
   * the stream is ongoing, or if the deletion operation fails.</p>
   *
   * @param eventId the ID of the stream to be deleted
   * @param deleteStreamDto the dto containing the deletion details
   * @param user the user requesting the deletion
   * @return a {@link DeleteStreamResponse} containing details about the deleted stream
   * @throws FleenStreamNotFoundException if the stream with the given ID cannot be found
   * @throws CalendarNotFoundException if no calendar is found for the user's country and stream type
   * @throws StreamNotCreatedByUserException if the stream was not created by the provided user
   * @throws CannotCancelOrDeleteOngoingStreamException if the stream is ongoing and cannot be deleted
   * @throws FailedOperationException if the deletion operation fails
   */
  @Override
  @Transactional
  public DeleteStreamResponse deleteEvent(final Long eventId, final DeleteStreamDto deleteStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
        CannotCancelOrDeleteOngoingStreamException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(eventId);
    // Retrieve the stream type
    final StreamType streamType = stream.getStreamType();
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(deleteStreamDto.getStreamType());
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry(), streamType);
    // Validate if the user is the creator of the event
    validateCreatorOfStream(stream, user);
    // Verify if stream is still ongoing
    verifyIfStreamIsOngoing(eventId, stream);
    // Update delete status of event
    stream.delete();
    // Save the stream
    streamRepository.save(stream);

    // Create the request to delete the stream externally
    final ExternalStreamRequest deleteStreamRequest = createDeleteStreamRequest(stream, calendar);
    // Delete the stream externally
    deleteStreamsExternal(deleteStreamRequest);
    // Get the deleted info
    final IsDeletedInfo deletedInfo = streamMapper.toIsDeletedInfo(stream.isDeleted());
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response of the Deleted event
    return localizer.of(DeleteStreamResponse.of(eventId, streamTypeInfo, deletedInfo));
  }

  /**
   * Deletes streams externally based on the provided delete stream request.
   *
   * <p>If the delete stream request represents an event, this method delegates the deletion of the event
   * to the {@link #deleteGoogleCalendarEvent} method. The check is done by evaluating if the request
   * is an event through the {@link ExternalStreamRequest#isAnEvent()} method.</p>
   *
   * @param deleteStreamRequest the request containing the details of the stream to be deleted externally.
   */
  protected void deleteStreamsExternal(final ExternalStreamRequest deleteStreamRequest) {
    if (deleteStreamRequest.isAnEvent() && deleteStreamRequest.isDeleteRequest()) {
      deleteGoogleCalendarEvent(deleteStreamRequest);
    }
  }

  /**
   * Deletes an event from Google Calendar based on the provided delete stream request.
   *
   * <p>This method creates a request to delete the calendar event using the external IDs from the
   * provided {@link ExternalStreamRequest}. It then deletes the event from Google Calendar using
   * the {@link EventUpdateService}.</p>
   *
   * @param deleteStreamRequest the request containing the details needed to delete the event
   *                            from Google Calendar, including the calendar and stream external IDs.
   */
  protected void deleteGoogleCalendarEvent(final ExternalStreamRequest deleteStreamRequest) {
    // Create a request to delete the calendar event
    final DeleteCalendarEventRequest deleteCalendarEventRequest = DeleteCalendarEventRequest.of(
      deleteStreamRequest.calendarExternalId(),
      deleteStreamRequest.streamExternalId()
    );
    // Delete the event in the Google Calendar
    eventUpdateService.deleteEventInGoogleCalendar(deleteCalendarEventRequest);
  }

  /**
   * Cancels an event and updates the stream status, performing external cancellation operations.
   *
   * <p>This method retrieves the stream by its ID, verifies the stream's details (such as ownership and
   * active status), and checks if the stream is ongoing. If valid, it updates the stream's status to "canceled",
   * saves the updated stream, and performs the cancellation externally. Finally, it returns a localized response
   * indicating the cancellation result.</p>
   *
   * <p>The method may throw various exceptions if the stream is not found, the user is not the creator,
   * the stream is already canceled or has already occurred, or if the cancellation operation fails.</p>
   *
   * @param eventId the ID of the stream to be canceled
   * @param cancelStreamDto the dto containing the cancellation details
   * @param user the user requesting the cancellation
   * @return a {@link CancelStreamResponse} containing details about the canceled stream
   * @throws FleenStreamNotFoundException if the stream with the given ID cannot be found
   * @throws CalendarNotFoundException if no calendar is found for the user's country and stream type
   * @throws StreamNotCreatedByUserException if the stream was not created by the provided user
   * @throws StreamAlreadyCanceledException if the stream has already been canceled
   * @throws StreamAlreadyHappenedException if the stream has already occurred
   * @throws CannotCancelOrDeleteOngoingStreamException if the stream is ongoing and cannot be canceled
   * @throws FailedOperationException if the cancellation operation fails
   */
  @Override
  @Transactional
  public CancelStreamResponse cancelEvent(final Long eventId, final CancelStreamDto cancelStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
      StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(eventId);
    // Retrieve the stream type
    final StreamType streamType = stream.getStreamType();
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(cancelStreamDto.getStreamType());
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry(), streamType);

    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Verify if the event or stream is still ongoing
    verifyIfStreamIsOngoing(eventId, stream);
    // Update event status to canceled
    stream.cancel();
    // Save the stream to the repository
    streamRepository.save(stream);
    // Create the cancel stream request
    final ExternalStreamRequest cancelStreamRequest = ExternalStreamRequest.ofCancel(calendar, stream, streamType);
    // Cancel the stream externally
    cancelStreamExternal(cancelStreamRequest);
    // Convert the stream status to info
    final StreamStatusInfo statusInfo = streamMapper.toStreamStatusInfo(stream.getStreamStatus());
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response of the cancellation
    return localizer.of(CancelStreamResponse.of(eventId, statusInfo, streamTypeInfo));
  }

  /**
   * Cancels the stream externally by triggering the appropriate cancellation process.
   *
   * <p>If the cancel stream request represents an event, this method delegates the task of canceling the
   * event in the external Google Calendar service by calling {@link #cancelGoogleCalendarEvent}.</p>
   *
   * @param cancelStreamRequest the request containing the details of the stream to cancel
   */
  protected void cancelStreamExternal(final ExternalStreamRequest cancelStreamRequest) {
    if (cancelStreamRequest.isAnEvent() && cancelStreamRequest.isCancelRequest()) {
      cancelGoogleCalendarEvent(cancelStreamRequest);
    }
  }

  /**
   * Cancels an event in the external Google Calendar service.
   *
   * <p>This method creates a request to cancel the calendar event based on the provided stream and calendar
   * details. It then submits the cancellation request to the external Google Calendar service.</p>
   *
   * @param cancelStreamRequest the request containing details about the calendar event to cancel, including
   *                            the calendar and stream identifiers
   */
  protected void cancelGoogleCalendarEvent(final ExternalStreamRequest cancelStreamRequest) {
    // Create a request to cancel the calendar event and submit request to external Calendar service
    final CancelCalendarEventRequest cancelCalendarEventRequest = CancelCalendarEventRequest.of(
      cancelStreamRequest.calendarExternalId(),
      cancelStreamRequest.streamExternalId()
    );
    // Cancel the stream in the external service
    eventUpdateService.cancelEventInGoogleCalendar(cancelCalendarEventRequest);
  }

  /**
   * Reschedules an event by updating its details and updating the event externally.
   *
   * <p>This method finds the stream by its ID, verifies the stream details, updates the schedule with the
   * new date and time, saves the updated stream, and reschedules the event externally. It then returns a
   * localized response with the updated event details.</p>
   *
   * @param eventId the ID of the stream (event) to reschedule
   * @param rescheduleStreamDto the DTO containing the new schedule details for the event
   * @param user the user requesting the event reschedule
   *
   * @return a localized response containing the updated event details after rescheduling
   *
   * @throws FleenStreamNotFoundException if the stream with the given ID is not found
   * @throws CalendarNotFoundException if the calendar associated with the user's country is not found
   * @throws StreamNotCreatedByUserException if the user is not the creator of the stream
   * @throws StreamAlreadyCanceledException if the stream has already been canceled
   * @throws StreamAlreadyHappenedException if the stream has already occurred
   * @throws FailedOperationException if any operation fails during the reschedule process
   */
  @Transactional
  @Override
  public RescheduleStreamResponse rescheduleEvent(final Long eventId, final RescheduleStreamDto rescheduleStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
      StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(eventId);
    // Retrieve the stream type
    final StreamType streamType = stream.getStreamType();
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(rescheduleStreamDto.getStreamType());
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry(), streamType);
    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Update Stream schedule details and time
    stream.reschedule(
      rescheduleStreamDto.getStartDateTime(),
      rescheduleStreamDto.getEndDateTime(),
      rescheduleStreamDto.getTimezone()
    );

    // Save the stream and event details
    streamRepository.save(stream);
    // Create the reschedule stream request
    final ExternalStreamRequest rescheduleStreamRequest = createRescheduleStreamRequest(calendar, stream, rescheduleStreamDto);
    // Reschedule the stream externally
    rescheduleStreamExternally(rescheduleStreamRequest);
    // Get the stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseNoJoinStatus(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response of the rescheduled event
    return localizer.of(RescheduleStreamResponse.of(eventId, streamResponse, streamTypeInfo));
  }

  /**
   * Reschedules an external calendar event (e.g., in Google Calendar) based on the provided stream's new schedule.
   *
   * <p>This method creates a {@link RescheduleCalendarEventRequest} from the given {@link ExternalStreamRequest}
   * containing the updated schedule details (start date/time, end date/time, and timezone). It then uses
   * {@link #eventUpdateService} to update the event in an external calendar service like Google Calendar.</p>
   *
   * @param rescheduleStreamRequest the request containing the updated details for rescheduling the stream in an external calendar
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
      eventUpdateService.rescheduleEventInGoogleCalendar(rescheduleCalendarEventRequest);
    }
  }

  /**
   * Updates the visibility of a stream (event) and notifies attendees based on the updated visibility.
   *
   * <p>This method finds the calendar associated with the user's country, retrieves the stream, verifies stream
   * details, and updates the event's visibility. It also sends invitations to attendees whose requests are pending
   * due to previous visibility settings. Finally, it returns a localized response with the updated event visibility.</p>
   *
   * @param eventId the ID of the event (stream) to update visibility for
   * @param updateStreamVisibilityDto the DTO containing the new visibility settings for the event
   * @param user the user requesting the visibility update
   *
   * @return a localized response containing the updated event visibility and stream type information
   *
   * @throws FleenStreamNotFoundException if the stream with the given ID is not found
   * @throws CalendarNotFoundException if the calendar associated with the user's country is not found
   * @throws StreamNotCreatedByUserException if the user is not the creator of the stream
   * @throws StreamAlreadyCanceledException if the stream has already been canceled
   * @throws StreamAlreadyHappenedException if the stream has already occurred
   * @throws CannotCancelOrDeleteOngoingStreamException if the stream is ongoing and cannot be updated
   * @throws FailedOperationException if any operation fails during the visibility update process
   */
  @Override
  @Transactional
  public UpdateStreamVisibilityResponse updateEventVisibility(final Long eventId, final UpdateStreamVisibilityDto updateStreamVisibilityDto, final FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
      StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException,
      FailedOperationException {
    // Find the calendar associated with the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry());
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(eventId);
    // Verify if the stream's type is the same as the stream type of the request
    stream.verifyIfStreamTypeNotEqualAndFail(updateStreamVisibilityDto.getStreamType());
    // Retrieve the current or existing status or visibility status of a stream
    final StreamVisibility currentStreamVisibility = stream.getStreamVisibility();
    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Verify if the stream is still ongoing
    verifyIfStreamIsOngoing(eventId, stream);
    // Update the visibility of an event or stream
    updateStreamVisibility(stream, updateStreamVisibilityDto.getActualVisibility());

    // Create request to update stream visibility
    final ExternalStreamRequest updateStreamVisibilityRequest = createUpdateStreamVisibilityRequest(calendar, stream, updateStreamVisibilityDto.getVisibility());
    // Update the stream visibility using an external service
    updateStreamVisibilityExternally(updateStreamVisibilityRequest);

    // Send invitation to attendees that requested to join earlier and whose request is pending because the event or stream was private earlier
    sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(calendar.getExternalId(), stream, currentStreamVisibility);
    // Retrieve the stream visibility information
    final StreamVisibilityInfo streamVisibility = streamMapper.toStreamVisibilityInfo(stream.getStreamVisibility());
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response of the update
    return localizer.of(UpdateStreamVisibilityResponse.of(eventId, streamVisibility, streamTypeInfo));
  }


  /**
   * Updates the visibility of the given stream.
   *
   * <p>This method sets a new visibility status for the provided {@code FleenStream}. Once the visibility is updated,
   * the stream's state is saved in the repository to persist the change.</p>
   *
   * @param stream The stream whose visibility is being updated.
   * @param newVisibility The new visibility status to apply to the stream.
   */
  protected void updateStreamVisibility(final FleenStream stream, final StreamVisibility newVisibility) {
    // Update the visibility of an event or stream
    stream.setStreamVisibility(newVisibility);
    // Save the updated stream in the repository
    streamRepository.save(stream);
  }

  protected void updateStreamVisibilityExternally(final ExternalStreamRequest updateStreamVisibilityRequest) {
    if (updateStreamVisibilityRequest.isAnEvent() && updateStreamVisibilityRequest.isVisibilityUpdateRequest()) {
      // Create a request to update the stream's visibility
      final UpdateCalendarEventVisibilityRequest request = UpdateCalendarEventVisibilityRequest.of(
        updateStreamVisibilityRequest.calendarExternalId(),
        updateStreamVisibilityRequest.streamExternalId(),
        updateStreamVisibilityRequest.getVisibility()
      );

      // Update the event visibility using an external service
      eventUpdateService.updateEventVisibility(request);
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
  protected void sendInvitationToPendingAttendeesBasedOnCurrentStreamStatus(final String calendarExternalId, final FleenStream stream, final StreamVisibility previousStreamVisibility)
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
    final List<StreamAttendee> pendingAttendees = streamAttendeeRepository.findAllByStreamAndRequestToJoinStatus(stream, PENDING);
    // Extract email addresses and IDs of the pending attendees or guests
    final Set<String> attendeesOrGuestsEmailAddresses = getAttendeesEmailAddresses(pendingAttendees);
    // Extract the attendee IDS from pending attendees
    final Set<Long> attendeeIds = getAttendeeIds(pendingAttendees);

    // Approve all pending requests
    streamAttendeeRepository.approveAllAttendeeRequestInvitation(APPROVED, new ArrayList<>(attendeeIds));
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