package com.fleencorp.feen.service.impl.chat.space.event;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.event.CreateChatSpaceEventDto;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.request.calendar.event.CreateCalendarEventRequest;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.search.chat.space.event.ChatSpaceEventSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.chat.space.ChatSpaceRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.chat.space.event.ChatSpaceEventService;
import com.fleencorp.feen.service.chat.space.member.ChatSpaceMemberService;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.stream.StreamOperationsService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.service.stream.event.EventOperationsService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
  private final ChatSpaceMemberService chatSpaceMemberService;
  private final EventOperationsService eventOperationsService;
  private final MiscService miscService;
  private final StreamOperationsService streamOperationsService;
  private final StreamService streamService;
  private final ChatSpaceRepository chatSpaceRepository;
  private final Localizer localizer;
  private final StreamMapper streamMapper;

  /**
   * Constructs a new {@code ChatSpaceEventServiceImpl} with all required dependencies.
   *
   * @param delegatedAuthorityEmail the email address used for delegated Google API access
   * @param chatSpaceMemberService the service for handling chat space member-related operations
   * @param eventOperationsService the service for managing event-related operations
   * @param miscService the service for miscellaneous utilities or operations
   * @param streamOperationsService the service for handling stream-specific operations
   * @param streamService the core service for stream management
   * @param chatSpaceRepository the repository interface for accessing chat space data
   * @param localizer the utility for resolving localized messages
   * @param streamMapper the mapper used to convert between stream entities and DTOs
   */
  public ChatSpaceEventServiceImpl(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      final ChatSpaceMemberService chatSpaceMemberService,
      final EventOperationsService eventOperationsService,
      final MiscService miscService,
      final StreamOperationsService streamOperationsService,
      final StreamService streamService,
      final ChatSpaceRepository chatSpaceRepository,
      final Localizer localizer,
      final StreamMapper streamMapper) {
    this.delegatedAuthorityEmail = delegatedAuthorityEmail;
    this.chatSpaceMemberService = chatSpaceMemberService;
    this.miscService = miscService;
    this.streamOperationsService = streamOperationsService;
    this.streamService = streamService;
    this.eventOperationsService = eventOperationsService;
    this.chatSpaceRepository = chatSpaceRepository;
    this.localizer = localizer;
    this.streamMapper = streamMapper;
  }

  /**
   * Retrieves a paginated list of events (streams) within a specific chat space.
   *
   * <p>This method searches for streams associated with the provided {@code chatSpaceId}
   * and converts them into {@link StreamResponse} views.</p>
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
    final Page<FleenStream> page = streamOperationsService.findByChatSpaceId(chatSpaceId, searchRequest.getPage());
    // Get the list of event or stream views from the search result
    final List<StreamResponse> streamResponses = streamMapper.toStreamResponses(page.getContent());
    // Process other details of the streams
    streamOperationsService.processOtherStreamDetails(streamResponses, user);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(StreamType.EVENT);
    // Create the search result
    final ChatSpaceEventSearchResult searchResult = ChatSpaceEventSearchResult.of(toSearchResult(streamResponses, page), streamTypeInfo);
    // Return a search result view with the chat space event responses and pagination details
    return localizer.of(searchResult);
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
   * @throws ChatSpaceNotFoundException if the chat space is not found
   * @throws CalendarNotFoundException if the calendar cannot be found
   * @throws FailedOperationException if there is an invalid input
   */
  @Override
  @Transactional
  public CreateStreamResponse createChatSpaceEvent(final Long chatSpaceId, final CreateChatSpaceEventDto createChatSpaceEventDto, final FleenUser user)
      throws ChatSpaceNotFoundException, CalendarNotFoundException, FailedOperationException {
    // Find the chat space by its ID
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Check if user is a member of
    chatSpaceMemberService.findByChatSpaceAndMember(chatSpace, user.toMember());
    // Find a calendar based on the user's country
    final Calendar calendar = miscService.findCalendar(user.getCountry());
    // Create a calendar event request with the event details from the DTO
    final CreateCalendarEventRequest createCalendarEventRequest = CreateCalendarEventRequest.bySuper(createChatSpaceEventDto);
    // Update the calendar event request with additional details (external calendar ID, authority email, user email)
    createCalendarEventRequest.update(calendar.getExternalId(), delegatedAuthorityEmail, user.getEmailAddress(), chatSpace.getMetadata());

    // Convert the event DTO into a FleenStream entity
    FleenStream stream = createChatSpaceEventDto.toStream(user.toMember(), chatSpace);
    // Get the organizer's display name or alias
    final String organizerAliasOrDisplayName = createChatSpaceEventDto.getOrganizerAlias(user.getFullName());

    // Update the FleenStream entity with organizer and contact details
    stream.update(organizerAliasOrDisplayName, user.getEmailAddress(), user.getPhoneNumber());
    // Save the updated FleenStream entity to the repository
    stream = streamOperationsService.save(stream);
    // Increase attendees count, save the event
    streamService.increaseTotalAttendeesOrGuests(stream);
    // Register the organizer of the event as an attendee or guest
    streamService.registerAndApproveOrganizerOfStreamAsAnAttendee(stream, user);
    // Create the event in Google Calendar and announce it in the chat space
    createEventExternally(stream, createCalendarEventRequest);
    // Get the stream response
    final StreamResponse streamResponse = streamMapper.toStreamResponseByAdminUpdate(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Create the response
    final CreateStreamResponse createStreamResponse = CreateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse);
    // Return a localized response with the created event's details
    return localizer.of(createStreamResponse);
  }

  /**
   * Creates a calendar event for the given stream using an external service.
   *
   * <p>This method delegates the operation to the {@code eventOperationsService}, which handles
   * the creation of the event in Google Calendar and announces it in the chat space.
   *
   * @param stream the stream for which the calendar event is being created
   * @param createCalendarEventRequest the request payload containing event details
   */
  private void createEventExternally(final FleenStream stream, final CreateCalendarEventRequest createCalendarEventRequest) {
    eventOperationsService.createEventInGoogleCalendarAndAnnounceInSpace(stream, createCalendarEventRequest);
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
