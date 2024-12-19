package com.fleencorp.feen.service.impl.chat.space.event;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.service.i18n.LocalizedResponse;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.event.CreateChatSpaceEventDto;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.request.calendar.event.CreateCalendarEventRequest;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.search.chat.space.event.ChatSpaceEventSearchResult;
import com.fleencorp.feen.model.search.chat.space.event.EmptyChatSpaceEventSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.chat.ChatSpaceRepository;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.chat.space.event.ChatSpaceEventService;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.service.stream.update.EventUpdateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;

/**
 * Implementation of the {@link ChatSpaceService} interface, providing methods
 * for managing chat spaces and their associated members.
 *
 * <p>This class handles the core logic for creating, updating, and retrieving
 * chat spaces, as well as processing membership requests and notifications.</p>
 *
 * @author Yusuf Àlàmù Musa
 * @version 1.0
 */
@Service
public class ChatSpaceEventServiceImpl implements ChatSpaceEventService {

  private final String delegatedAuthorityEmail;
  private final MiscService miscService;
  private final StreamAttendeeService streamAttendeeService;
  private final StreamService streamService;
  private final EventUpdateService eventUpdateService;
  private final ChatSpaceRepository chatSpaceRepository;
  private final FleenStreamRepository streamRepository;
  private final LocalizedResponse localizedResponse;
  private final StreamMapper streamMapper;

  /**
   * Constructs a new {@link ChatSpaceEventServiceImpl} with the specified dependencies.
   *
   * @param delegatedAuthorityEmail The email address associated with the delegated authority, injected from the configuration.
   * @param miscService The service that provides miscellaneous operations.
   * @param streamAttendeeService The service for managing stream attendee-related actions.
   * @param streamService The service responsible for stream-related actions.
   * @param eventUpdateService The service for handling event updates.
   * @param chatSpaceRepository The repository for managing chat space entities.
   * @param streamRepository The repository for managing stream entities.
   * @param localizedResponse The response object used for returning localized messages.
   * @param streamMapper The mapper for converting stream-related data between different formats.
   */
  public ChatSpaceEventServiceImpl(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      final MiscService miscService,
      final StreamAttendeeService streamAttendeeService,
      final StreamService streamService,
      final EventUpdateService eventUpdateService,
      final ChatSpaceRepository chatSpaceRepository,
      final FleenStreamRepository streamRepository,
      final LocalizedResponse localizedResponse,
      final StreamMapper streamMapper) {
    this.delegatedAuthorityEmail = delegatedAuthorityEmail;
    this.miscService = miscService;
    this.streamAttendeeService = streamAttendeeService;
    this.streamService = streamService;
    this.eventUpdateService = eventUpdateService;
    this.chatSpaceRepository = chatSpaceRepository;
    this.streamRepository = streamRepository;
    this.localizedResponse = localizedResponse;
    this.streamMapper = streamMapper;
  }

  /**
   * Retrieves a paginated list of events (streams) within a specific chat space.
   *
   * <p>This method searches for streams associated with the provided {@code chatSpaceId}
   * and converts them into {@link FleenStreamResponse} views.</p>
   *
   * @param chatSpaceId the ID of the chat space to find events for.
   * @param searchRequest the search request containing pagination details.
   * @param user the current user performing the search.
   * @return a {@link ChatSpaceEventSearchResult} containing the list of event responses
   *         and pagination metadata.
   */
  @Override
  public ChatSpaceEventSearchResult findChatSpaceEvents(final Long chatSpaceId, final SearchRequest searchRequest, final FleenUser user) {
    // Find events or streams based on the search request
    final Page<FleenStream> page = streamRepository.findByChatSpace(ChatSpace.of(chatSpaceId), searchRequest.getPage());
    // Get the list of event or stream views from the search result
    final List<FleenStreamResponse> views = streamMapper.toFleenStreamResponses(page.getContent());
    // Determine statuses like schedule, join status, schedules and timezones
    streamService.determineDifferentStatusesAndDetailsOfStreamBasedOnUser(views, user);
    // Set the attendees and total attendee count for each event or stream
    streamAttendeeService.setStreamAttendeesAndTotalAttendeesAttending(views);
    // Get the first 10 attendees for each event or stream
    streamAttendeeService.setFirst10AttendeesAttendingInAnyOrderOnStreams(views);
    // Return a search result view with the chat space event responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(ChatSpaceEventSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyChatSpaceEventSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Creates an event within a chat space and announces it.
   *
   * <p>This method finds the associated chat space and calendar, creates a Google Calendar event,
   * updates the FleenStream entity with event details, and announces the event in the chat space.</p>
   *
   * @param chatSpaceId The ID of the chat space where the event will be created.
   * @param createChatSpaceEventDto DTO containing the event details.
   * @param user The user creating the event.
   * @return A response with details of the created event.
   */
  @Override
  @Transactional
  public CreateStreamResponse createChatSpaceEvent(final Long chatSpaceId, final CreateChatSpaceEventDto createChatSpaceEventDto, final FleenUser user) {
    // Find the chat space by its ID
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Find a calendar based on the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry());
    // Create a calendar event request with the event details from the DTO
    final CreateCalendarEventRequest createCalendarEventRequest = CreateCalendarEventRequest.bySuper(createChatSpaceEventDto);
    // Update the calendar event request with additional details (external calendar ID, authority email, user email)
    createCalendarEventRequest.update(calendar.getExternalId(), delegatedAuthorityEmail, user.getEmailAddress());

    // Convert the event DTO into a FleenStream entity
    FleenStream stream = createChatSpaceEventDto.toFleenStream(user.toMember(), chatSpace);
    // Get the organizer's display name or alias
    final String organizerAliasOrDisplayName = createChatSpaceEventDto.getOrganizerAlias(user.getFullName());

    // Update the FleenStream entity with organizer and contact details
    stream.updateDetails(organizerAliasOrDisplayName, user.getEmailAddress(), user.getPhoneNumber());
    // Save the updated FleenStream entity to the repository
    stream = streamRepository.save(stream);
    // Register the organizer of the event as an attendee or guest
    streamService.registerAndApproveOrganizerOfStreamAsAnAttendee(stream, user);
    // Create the event in Google Calendar and announce it in the chat space
    eventUpdateService.createEventInGoogleCalendarAndAnnounceInSpace(stream, createCalendarEventRequest);
    // Get the stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseApproved(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response with the created event's details
    return localizedResponse.of(CreateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse));
  }

  /**
   * Finds a chat space by its ID.
   *
   * <p>This method retrieves a chat space from the repository using the provided ID.
   * If the chat space with the specified ID does not exist, a `ChatSpaceNotFoundException`
   * is thrown.</p>
   *
   * @param chatSpaceId The ID of the chat space to retrieve.
   * @return The chat space associated with the provided ID.
   * @throws ChatSpaceNotFoundException if no chat space with the specified ID is found.
   */
  protected ChatSpace findChatSpace(final Long chatSpaceId) {
    // Attempt to find the chat space by its ID in the repository
    return chatSpaceRepository.findById(chatSpaceId)
      // If not found, throw an exception with the chat space ID
      .orElseThrow(ChatSpaceNotFoundException.of(chatSpaceId));
  }

}
