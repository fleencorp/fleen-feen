package com.fleencorp.feen.service.impl.chat;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.constant.stream.JoinStatus;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.*;
import com.fleencorp.feen.exception.chat.space.member.ChatSpaceMemberNotFoundException;
import com.fleencorp.feen.exception.member.MemberNotFoundException;
import com.fleencorp.feen.mapper.ChatSpaceMapper;
import com.fleencorp.feen.mapper.ChatSpaceMemberMapper;
import com.fleencorp.feen.mapper.FleenStreamMapper;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.chat.CreateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.DowngradeChatSpaceAdminToMemberDto;
import com.fleencorp.feen.model.dto.chat.UpdateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.UpgradeChatSpaceMemberToAdminDto;
import com.fleencorp.feen.model.dto.chat.member.*;
import com.fleencorp.feen.model.dto.event.CreateChatSpaceEventDto;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceMemberRoleInfo;
import com.fleencorp.feen.model.projection.ChatSpaceMemberSelect;
import com.fleencorp.feen.model.projection.ChatSpaceRequestToJoinPendingSelect;
import com.fleencorp.feen.model.request.calendar.event.CreateCalendarEventRequest;
import com.fleencorp.feen.model.request.chat.space.CreateChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.DeleteChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.UpdateChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.membership.AddChatSpaceMemberRequest;
import com.fleencorp.feen.model.request.chat.space.membership.RemoveChatSpaceMemberRequest;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceMemberSearchRequest;
import com.fleencorp.feen.model.request.search.chat.space.ChatSpaceSearchRequest;
import com.fleencorp.feen.model.response.chat.space.*;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.member.*;
import com.fleencorp.feen.model.response.chat.space.member.base.ChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.event.CreateEventResponse;
import com.fleencorp.feen.model.response.stream.base.FleenStreamResponse;
import com.fleencorp.feen.model.search.broadcast.request.EmptyRequestToJoinSearchResult;
import com.fleencorp.feen.model.search.broadcast.request.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.chat.space.ChatSpaceSearchResult;
import com.fleencorp.feen.model.search.chat.space.EmptyChatSpaceSearchResult;
import com.fleencorp.feen.model.search.chat.space.event.ChatSpaceEventSearchResult;
import com.fleencorp.feen.model.search.chat.space.event.EmptyChatSpaceEventSearchResult;
import com.fleencorp.feen.model.search.chat.space.member.ChatSpaceMemberSearchResult;
import com.fleencorp.feen.model.search.chat.space.member.EmptyChatSpaceMemberSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.chat.ChatSpaceMemberRepository;
import com.fleencorp.feen.repository.chat.ChatSpaceRepository;
import com.fleencorp.feen.repository.chat.UserChatSpaceRepository;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.impl.stream.base.StreamService;
import com.fleencorp.feen.service.impl.stream.update.EventUpdateService;
import com.fleencorp.feen.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus.PENDING;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link ChatSpaceService} interface, providing methods
 * for managing chat spaces and their associated members.
 *
 * <p>This class handles the core logic for creating, updating, and retrieving
 * chat spaces, as well as processing membership requests and notifications.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
public class ChatSpaceServiceImpl implements ChatSpaceService {

  private final String delegatedAuthorityEmail;
  private final MiscService miscService;
  private final NotificationMessageService notificationMessageService;
  private final NotificationService notificationService;
  private final StreamService streamService;
  private final ChatSpaceUpdateService chatSpaceUpdateService;
  private final EventUpdateService eventUpdateService;
  private final ChatSpaceMemberRepository chatSpaceMemberRepository;
  private final ChatSpaceRepository chatSpaceRepository;
  private final FleenStreamRepository fleenStreamRepository;
  private final MemberRepository memberRepository;
  private final UserChatSpaceRepository userChatSpaceRepository;
  private final LocalizedResponse localizedResponse;
  private final FleenStreamMapper streamMapper;
  private final ChatSpaceMapper chatSpaceMapper;
  private final ChatSpaceMemberMapper chatSpaceMemberMapper;

  /**
   * Constructs a {@code ChatSpaceServiceImpl} with the specified dependencies.
   *
   * <p>This constructor initializes the service with all required components for managing
   * chat spaces, including repositories, mappers, and various utility services. It also injects
   * configuration values like the delegated authority email.</p>
   *
   * @param delegatedAuthorityEmail the email address used for delegated authority in Google services.
   * @param miscService handles miscellaneous utility operations.
   * @param notificationMessageService manages notifications sent as messages.
   * @param notificationService processes and sends general notifications.
   * @param streamService provides operations related to streams.
   * @param chatSpaceUpdateService handles updates to chat spaces.
   * @param eventUpdateService manages updates to events associated with chat spaces.
   * @param chatSpaceMemberRepository repository for managing chat space members.
   * @param chatSpaceRepository repository for chat space entities.
   * @param fleenStreamRepository repository for stream-related entities.
   * @param memberRepository repository for member-related data.
   * @param userChatSpaceRepository repository for user-chat space associations.
   * @param localizedResponse provides localized responses for API operations.
   * @param streamMapper maps stream-related entities and responses.
   * @param chatSpaceMapper maps chat space entities to response models.
   * @param chatSpaceMemberMapper maps chat space member entities to response models.
   */
  public ChatSpaceServiceImpl(
      @Value("${google.delegated.authority.email}") final String delegatedAuthorityEmail,
      final MiscService miscService,
      final NotificationMessageService notificationMessageService,
      final NotificationService notificationService,
      final StreamService streamService,
      final ChatSpaceUpdateService chatSpaceUpdateService,
      final EventUpdateService eventUpdateService,
      final ChatSpaceMemberRepository chatSpaceMemberRepository,
      final ChatSpaceRepository chatSpaceRepository,
      final FleenStreamRepository fleenStreamRepository,
      final MemberRepository memberRepository,
      final UserChatSpaceRepository userChatSpaceRepository,
      final LocalizedResponse localizedResponse,
      final FleenStreamMapper streamMapper,
      final ChatSpaceMapper chatSpaceMapper,
      final ChatSpaceMemberMapper chatSpaceMemberMapper) {
    this.delegatedAuthorityEmail = delegatedAuthorityEmail;
    this.miscService = miscService;
    this.notificationMessageService = notificationMessageService;
    this.notificationService = notificationService;
    this.streamService = streamService;
    this.chatSpaceUpdateService = chatSpaceUpdateService;
    this.eventUpdateService = eventUpdateService;
    this.chatSpaceRepository = chatSpaceRepository;
    this.chatSpaceMemberRepository = chatSpaceMemberRepository;
    this.fleenStreamRepository = fleenStreamRepository;
    this.memberRepository = memberRepository;
    this.userChatSpaceRepository = userChatSpaceRepository;
    this.localizedResponse = localizedResponse;
    this.streamMapper = streamMapper;
    this.chatSpaceMapper = chatSpaceMapper;
    this.chatSpaceMemberMapper = chatSpaceMemberMapper;
  }

  /**
   * Finds chat spaces based on the provided search request criteria, which may include
   * date range, title, or default active status.
   *
   * <p>This method checks if all dates are set in the search request to filter chat spaces
   * by the specified date range. If the dates are not set, it checks if a title is provided
   * to filter chat spaces by that title. If neither condition is met, it retrieves all
   * chat spaces that match the default active status.</p>
   *
   * @param searchRequest The search request containing criteria for filtering chat spaces.
   * @return A ChatSpaceSearchResult containing the search results of chat spaces matching the criteria.
   */
  @Override
  public ChatSpaceSearchResult findSpaces(final ChatSpaceSearchRequest searchRequest, final FleenUser user) {
    final Page<ChatSpace> page;

    // Check if all required date parameters are set in the search request
    if (searchRequest.areAllDatesSet()) {
      // Retrieve chat spaces within the specified date range
      page = chatSpaceRepository.findByDateBetween(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(), searchRequest.getDefaultActive(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // Retrieve chat spaces that match the specified title
      page = chatSpaceRepository.findByTitle(searchRequest.getTitle(), searchRequest.getDefaultActive(), searchRequest.getPage());
    } else {
      // Retrieve all chat spaces that match the default active status
      page = chatSpaceRepository.findMany(searchRequest.getDefaultActive(), searchRequest.getPage());
    }

    // Convert the retrieved chat spaces to response objects
    final List<ChatSpaceResponse> views = chatSpaceMapper.toChatSpaceResponses(page.getContent());
    // Determine user join status of spaces
    determineUserJoinStatusFoChatSpace(views, user);
    // Return a search result view with the chat space responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(ChatSpaceSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyChatSpaceSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Finds chat spaces created by the specified user based on the provided search request criteria,
   * which may include date range, title, or a default active status.
   *
   * <p>This method checks if all required date parameters are set in the search request to filter
   * chat spaces by the specified date range. If the dates are not set, it checks if a title is
   * provided to filter chat spaces by that title. If neither condition is met, it retrieves all
   * chat spaces created by the user.</p>
   *
   * @param searchRequest The search request containing criteria for filtering chat spaces.
   * @param user The user whose created chat spaces are being searched.
   * @return A ChatSpaceSearchResult containing the search results of chat spaces created by the user matching the criteria.
   */
  @Override
  public ChatSpaceSearchResult findSpacesCreated(final ChatSpaceSearchRequest searchRequest, final FleenUser user) {
    final Page<ChatSpace> page;

    // Check if all required date parameters are set in the search request
    if (searchRequest.areAllDatesSet()) {
      // Retrieve chat spaces created by the user within the specified date range
      page = userChatSpaceRepository.findByDateBetween(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(), user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // Retrieve chat spaces created by the user that match the specified title
      page = userChatSpaceRepository.findByTitle(searchRequest.getTitle(), user.toMember(), searchRequest.getPage());
    } else {
      // Retrieve all chat spaces created by the user
      page = userChatSpaceRepository.findMany(user.toMember(), searchRequest.getPage());
    }

    // Convert the retrieved chat spaces to response objects
    final List<ChatSpaceResponse> views = chatSpaceMapper.toChatSpaceResponses(page.getContent());
    // Update the total request to join for each chat space
    updateTotalRequestToJoinForChatSpaces(views);
    // Determine user join status of spaces
    determineUserJoinStatusFoChatSpace(views, user);
    // Return a search result view with the chat space responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(ChatSpaceSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyChatSpaceSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Finds chat spaces that the specified user belongs to based on the provided search request criteria,
   * which may include a date range, title, or a default active status.
   *
   * <p>This method first checks if all required date parameters are set in the search request to
   * filter chat spaces by the specified date range. If not, it checks if a title is provided to
   * filter chat spaces by that title. If neither condition is met, it retrieves all chat spaces
   * the user belongs to.</p>
   *
   * @param searchRequest The search request containing criteria for filtering chat spaces.
   * @param user The user whose membership in chat spaces is being queried.
   * @return A ChatSpaceSearchResult containing the search results of chat spaces the user belongs to, matching the criteria.
   */
  @Override
  public ChatSpaceSearchResult findSpacesIBelongTo(final ChatSpaceSearchRequest searchRequest, final FleenUser user) {
    final Page<ChatSpaceMember> page;

    // Check if all required date parameters are set in the search request
    if (searchRequest.areAllDatesSet()) {
      // Retrieve chat spaces the user belongs to within the specified date range
      page = chatSpaceMemberRepository.findSpaceIBelongByDateBetween(searchRequest.getStartDateTime(), searchRequest.getEndDateTime(), user.toMember(), searchRequest.getPage());
    } else if (nonNull(searchRequest.getTitle())) {
      // Retrieve chat spaces the user belongs to that match the specified title
      page = chatSpaceMemberRepository.findSpaceIBelongByTitle(searchRequest.getTitle(), user.toMember(), searchRequest.getPage());
    } else {
      // Retrieve all chat spaces the user belongs to
      page = chatSpaceMemberRepository.findSpaceIBelongMany(user.toMember(), searchRequest.getPage());
    }

    // Convert the retrieved chat spaces from membership to response objects
    final List<ChatSpaceResponse> views = extractUserChatSpaceFromMembershipAndCreateChatResponse(page.getContent());
    // Determine user join status of spaces
    determineUserJoinStatusFoChatSpace(views, user);
    // Return a search result view with the chat space responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(ChatSpaceSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyChatSpaceSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Finds and returns a paginated list of chat space members based on the provided search request.
   *
   * <p>If a member's name is included in the search request, it filters the results by that name;
   * otherwise, it returns all members of the specified chat space.</p>
   *
   * @param chatSpaceId the ID of the chat space
   * @param searchRequest the search criteria for filtering members (includes pagination details)
   * @param user the current user performing the search
   * @return a ChatSpaceMemberSearchResult containing the list of chat space members and pagination info
   */
  @Override
  public ChatSpaceMemberSearchResult findChatSpaceMembers(final Long chatSpaceId, final ChatSpaceMemberSearchRequest searchRequest, final FleenUser user) {

    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    final Page<ChatSpaceMember> page;

    // Check if the search request includes a member's name
    if (nonNull(searchRequest.getMemberName())) {
      // Find members by chat space and member name with pagination
      page = chatSpaceMemberRepository.findByChatSpaceAndMemberName(ChatSpace.of(chatSpaceId), searchRequest.getMemberName(), searchRequest.getPage());
    } else {
      // Find all members by chat space with pagination
      page = chatSpaceMemberRepository.findByChatSpace(ChatSpace.of(chatSpaceId), searchRequest.getPage());
    }

    // Convert the chat space members to response views
    final List<ChatSpaceMemberResponse> views = chatSpaceMemberMapper.toChatSpaceMemberResponses(page.getContent(), chatSpace);
    // Return a search result view with the chat space member responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(ChatSpaceMemberSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyChatSpaceMemberSearchResult.of(toSearchResult(List.of(), page)))
    );
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
    final Page<FleenStream> page = fleenStreamRepository.findByChatSpace(ChatSpace.of(chatSpaceId), searchRequest.getPage());
    // Get the list of event or stream views from the search result
    final List<FleenStreamResponse> views = streamMapper.toFleenStreamResponses(page.getContent());
    // Determine statuses like schedule, join status, schedules and timezones
    streamService.determineDifferentStatusesAndDetailsOfEventOrStreamBasedOnUser(views, user);
    // Set the attendees and total attendee count for each event or stream
    streamService.setStreamAttendeesAndTotalAttendeesAttending(views);
    // Get the first 10 attendees for each event or stream
    streamService.getFirst10AttendingInAnyOrder(views);
    // Return a search result view with the chat space event responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(ChatSpaceEventSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyChatSpaceEventSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Creates a chat space using the provided DTO and user information.
   *
   * <p>This method saves a chat space entity, sends a request to create the chat space,
   * and returns a localized response.</p>
   *
   * @param createChatSpaceDto DTO containing the chat space details.
   * @param user The user creating the chat space.
   * @return A response with details of the created chat space.
   */
  @Override
  @Transactional
  public CreateChatSpaceResponse createChatSpace(final CreateChatSpaceDto createChatSpaceDto, final FleenUser user) {
    // Initialize a new chat space based on the dto details
    ChatSpace chatSpace = createChatSpaceDto.toChatSpace(user.toMember());
    // Save the new chat space to the repository
    chatSpace = chatSpaceRepository.save(chatSpace);
    // Create a request object for creating the chat space
    final CreateChatSpaceRequest createChatSpaceRequest = getCreateChatSpaceRequest(createChatSpaceDto, user);

    // Create and add admin or organizer of space as chat space member
    final ChatSpaceMember chatSpaceMember = ChatSpaceMember.of(chatSpace, user.toMember());
    // Save chat space member to repository
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Increase total members and save chat space
    increaseTotalMembersAndSave(chatSpace);
    // Delegate the creation of the chat space to the update service
    chatSpaceUpdateService.createChatSpace(chatSpace, createChatSpaceRequest);
    // Return a localized response with the chat space details
    return localizedResponse.of(CreateChatSpaceResponse.of(chatSpaceMapper.toChatSpaceResponseApproved(chatSpace)));
  }

  /**
   * Constructs a request to create a new chat space using the provided DTO and user information.
   *
   * @param createChatSpaceDto The DTO containing the necessary details to create a new chat space (title, description, and guidelines).
   * @param user The user who is initiating the chat space creation, used to retrieve the email address.
   * @return A {@link CreateChatSpaceRequest} object containing the chat space creation details.
   * @throws FailedOperationException If any of the provided parameters are null.
   */
  protected static CreateChatSpaceRequest getCreateChatSpaceRequest(final CreateChatSpaceDto createChatSpaceDto, final FleenUser user) {
    // Validate that neither the DTO nor the user is null
    checkIsNullAny(List.of(createChatSpaceDto, user), FailedOperationException::new);

    // Create and return a create chat space request object using the DTO details and user email address
    return CreateChatSpaceRequest.of(
      createChatSpaceDto.getTitle(),
      createChatSpaceDto.getDescription(),
      createChatSpaceDto.getGuidelinesOrRules(),
      user.getEmailAddress()
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
  public CreateEventResponse createChatSpaceEvent(final Long chatSpaceId, final CreateChatSpaceEventDto createChatSpaceEventDto, final FleenUser user) {
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
    stream = fleenStreamRepository.save(stream);
    // Register the organizer of the event as an attendee or guest
    streamService.registerAndApproveOrganizerOfEventOrStreamAsAnAttendee(stream, user);
    // Create the event in Google Calendar and announce it in the chat space
    eventUpdateService.createEventInGoogleCalendarAndAnnounceInSpace(stream, createCalendarEventRequest);
    // Get the stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseApproved(stream);
    // Return a localized response with the created event's details
    return localizedResponse.of(CreateEventResponse.of(stream.getStreamId(), streamResponse));
  }

  /**
   * Updates an existing chat space with new details.
   *
   * <p>This method fetches a chat space by its ID, updates its details based on the provided DTO,
   * and persists the changes to the repository. It also sends an update request to an external
   * chat service to synchronize the changes.</p>
   *
   * @param chatSpaceId The ID of the chat space to be updated.
   * @param updateChatSpaceDto DTO containing the updated chat space details.
   * @param user The user performing the update operation.
   * @return A response containing the updated chat space details.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws ChatSpaceAlreadyDeletedException if the chat space has already been deleted.
   * @throws NotAnAdminOfChatSpaceException if the user is not authorized to disable the chat space.
   */
  @Override
  @Transactional
  public UpdateChatSpaceResponse updateChatSpace(final Long chatSpaceId, final UpdateChatSpaceDto updateChatSpaceDto, final FleenUser user) {
    // Find the chat space by ID or throw an exception if it doesn't exist
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the chat space has already been deleted and that the user is the creator or an admin of the chat space
    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);
    // Update the chat space with the new details or info
    chatSpace.updateDetails(
      updateChatSpaceDto.getTitle(),
      updateChatSpaceDto.getDescription(),
      updateChatSpaceDto.getTags(),
      updateChatSpaceDto.getGuidelinesOrRules(),
      updateChatSpaceDto.getActualVisibility()
    );
    // Save the updated chat space entity to the repository
    chatSpaceRepository.save(chatSpace);
    // Create update chat space request and send to external service
    createAndUpdateChatSpaceInExternalService(updateChatSpaceDto, chatSpace);
    // Return a localized response with the updated chat space details
    return localizedResponse.of(UpdateChatSpaceResponse.of(chatSpaceMapper.toChatSpaceResponseApproved(chatSpace)));
  }

  /**
   * Updates an existing chat space in an external service based on the provided DTO.
   *
   * @param updateChatSpaceDto The DTO containing the updated details of the chat space, such as title, description, and guidelines.
   * @param chatSpace The internal representation of the chat space being updated.
   * @throws FailedOperationException If any of the provided parameters are null.
   */
  protected void createAndUpdateChatSpaceInExternalService(final UpdateChatSpaceDto updateChatSpaceDto, final ChatSpace chatSpace) {
    checkIsNullAny(List.of(updateChatSpaceDto, chatSpace), FailedOperationException::new);

    // Prepare the request to update the external chat space service
    final UpdateChatSpaceRequest updateChatSpaceRequest = UpdateChatSpaceRequest.of(
      chatSpace.getExternalIdOrName(),
      updateChatSpaceDto.getTitle(),
      updateChatSpaceDto.getDescription(),
      updateChatSpaceDto.getGuidelinesOrRules());
    // Call the service to update the external chat space
    chatSpaceUpdateService.updateChatSpace(updateChatSpaceRequest);
  }

  /**
   * Retrieves the details of a chat space by its ID.
   *
   * <p>This method looks up a chat space by its ID and returns a response containing
   * the chat space details. It ensures that the result is localized based on the user's preferences.</p>
   *
   * @param chatSpaceId The ID of the chat space to be retrieved.
   * @param user The user requesting the retrieval operation.
   * @return A response containing the chat space details, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   */
  @Override
  public RetrieveChatSpaceResponse retrieveChatSpace(final Long chatSpaceId, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);

    final ChatSpaceResponse chatSpaceResponse = chatSpaceMapper.toChatSpaceResponse(chatSpace);
    updateUserJoinStatus(chatSpaceResponse, user);

    // Return a localized response containing the chat space details
    return localizedResponse.of(RetrieveChatSpaceResponse.of(chatSpaceResponse));
  }

  public void updateUserJoinStatus(final ChatSpaceResponse chatSpaceResponse, final FleenUser user) {
    if (nonNull(chatSpaceResponse) && nonNull(user)) {
      chatSpaceMemberRepository
        .findByChatSpaceMemberAndChatSpace(ChatSpaceMember.of(user.getId()), ChatSpace.of(chatSpaceResponse.getNumberId()))
        .ifPresent(chatSpaceMember -> {
          final JoinStatus joinStatus = JoinStatus.getJoinStatus(chatSpaceMember.getRequestToJoinStatus(), chatSpaceResponse.getVisibilityInfo().getVisibility());
          chatSpaceMapper.update(chatSpaceResponse, chatSpaceMember.getRequestToJoinStatus(), joinStatus);
      });
    }
  }

  /**
   * Deletes a chat space by its ID.
   *
   * <p>This method retrieves the chat space by its ID, marks it for deletion, and
   * then saves the changes. It returns a localized response confirming the deletion.</p>
   *
   * @param chatSpaceId The ID of the chat space to be deleted.
   * @param user The user requesting the deletion operation.
   * @return A response confirming the deletion of the chat space, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws NotAnAdminOfChatSpaceException if the user is not authorized to disable the chat space.
   */
  @Override
  @Transactional
  public DeleteChatSpaceResponse deleteChatSpace(final Long chatSpaceId, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the chat space has already been deleted and that the user is the creator or an admin of the chat space
    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);
    // Mark the chat space as deleted
    chatSpace.delete();
    // Save the updated chat space status to the repository
    chatSpaceRepository.save(chatSpace);
    // Return a localized response confirming the deletion
    return localizedResponse.of(DeleteChatSpaceResponse.of(chatSpaceId));
  }

  /**
   * Deletes a chat space by an admin using its ID.
   *
   * <p>This method is intended for administrative use. It retrieves the chat space by its ID,
   * marks it for deletion, and saves the changes. Additionally, it sends a request to update
   * the external system by deleting the chat space through the `chatSpaceUpdateService`.</p>
   *
   * @param chatSpaceId The ID of the chat space to be deleted by the admin.
   * @param user The admin user performing the deletion operation.
   * @return A response confirming the deletion of the chat space, localized based on the admin locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   */
  @Override
  @Transactional
  public DeleteChatSpaceResponse deleteChatSpaceByAdmin(final Long chatSpaceId, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Mark the chat space as deleted
    chatSpace.delete();
    // Save the updated chat space status to the repository
    chatSpaceRepository.save(chatSpace);
    // Send a request to delete the chat space from external systems
    chatSpaceUpdateService.deleteChatSpace(DeleteChatSpaceRequest.of(chatSpace.getExternalIdOrName()));
    // Return a localized response confirming the deletion
    return localizedResponse.of(DeleteChatSpaceResponse.of(chatSpaceId));
  }

  /**
   * Enables a chat space by its ID.
   *
   * <p>This method retrieves the chat space using the specified ID, verifies that it has not been
   * deleted, and then enables it. The updated chat space is saved to the repository.</p>
   *
   * @param chatSpaceId The ID of the chat space to be enabled.
   * @param user The user performing the enable operation.
   * @return A response confirming the enabling of the chat space, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws ChatSpaceAlreadyDeletedException if the chat space has already been deleted.
   * @throws NotAnAdminOfChatSpaceException if the user is not authorized to disable the chat space.
   */
  @Override
  @Transactional
  public EnableChatSpaceResponse enableChatSpace(final Long chatSpaceId, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the chat space has already been deleted and that the user is the creator or an admin of the chat space
    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);
    // Enable the chat space
    chatSpace.enable();
    // Save the updated chat space status to the repository
    chatSpaceRepository.save(chatSpace);
    // Return a localized response confirming the enabling of the chat space
    return localizedResponse.of(EnableChatSpaceResponse.of(chatSpaceId));
  }

  /**
   * Enables a chat space by its ID.
   *
   * <p>This method retrieves the chat space using the specified ID, verifies that it has not been
   * deleted, and checks if the user is the creator or an admin of the chat space. If all checks pass,
   * the chat space is enabled. The updated chat space is then saved to the repository.</p>
   *
   * @param chatSpaceId The ID of the chat space to be enabled.
   * @param user The user performing the enable operation.
   * @return A response confirming the enabling of the chat space, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws ChatSpaceAlreadyDeletedException if the chat space has already been deleted.
   * @throws NotAnAdminOfChatSpaceException if the user is not authorized to enable the chat space.
   */
  @Override
  @Transactional
  public DisableChatSpaceResponse disableChatSpace(final Long chatSpaceId, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the chat space has already been deleted and that the user is the creator or an admin of the chat space
    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);
    // Disable the chat space
    chatSpace.disable();
    // Save the updated chat space status to the repository
    chatSpaceRepository.save(chatSpace);
    // Return a localized response confirming the disabling of the chat space
    return localizedResponse.of(DisableChatSpaceResponse.of(chatSpaceId));
  }

  /**
   * Upgrades a chat space member to an admin role within the specified chat space.
   *
   * <p>This method retrieves the chat space by its ID and locates the member to be upgraded
   * based on the provided member ID. If the chat space or member is not found, it throws a
   * {@link ChatSpaceNotFoundException} or {@link ChatSpaceMemberNotFoundException}. If found,
   * the member's role is upgraded to admin, and the updated member information is saved to the
   * repository. The response contains details of the upgrade operation.</p>
   *
   * @param chatSpaceId The ID of the chat space where the member is to be upgraded.
   * @param upgradeChatSpaceMemberToAdminDto The DTO containing the member ID to be upgraded.
   * @param user The user requesting the upgrade operation.
   * @return A response confirming the upgrade of the chat space member to admin, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws ChatSpaceMemberNotFoundException if the chat space member with the specified ID is not found in the chat space.
   */
  @Override
  @Transactional
  public UpgradeChatSpaceMemberToAdminResponse upgradeChatSpaceMemberToAdmin(final Long chatSpaceId, final UpgradeChatSpaceMemberToAdminDto upgradeChatSpaceMemberToAdminDto, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify that the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfSpace(chatSpace, user);

    // Find the chat space member to be upgraded or throw an exception if not found
    final ChatSpaceMember chatSpaceMember = chatSpaceMemberRepository
      .findByChatSpaceMemberAndChatSpace(ChatSpaceMember.of(upgradeChatSpaceMemberToAdminDto.getActualChatSpaceMemberId()), chatSpace)
      .orElseThrow(ChatSpaceMemberNotFoundException.of(upgradeChatSpaceMemberToAdminDto.getActualChatSpaceMemberId()));

    // Upgrade the member's role to admin
    chatSpaceMember.upgradeRole();
    // Save the updated chat space member information to the repository
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Get the c
    final ChatSpaceMemberRoleInfo roleInfo = chatSpaceMemberMapper.toRole(chatSpaceMember);
    // Return a localized response confirming the upgrade
    return localizedResponse.of(UpgradeChatSpaceMemberToAdminResponse.of(chatSpaceId, upgradeChatSpaceMemberToAdminDto.getActualChatSpaceMemberId(), roleInfo));
  }

  /**
   * Downgrades a chat space admin to a member role within the specified chat space.
   *
   * <p>This method retrieves the chat space by its ID and locates the admin to be downgraded
   * based on the provided member ID. If the chat space or admin is not found, it throws a
   * {@link ChatSpaceNotFoundException} or {@link ChatSpaceMemberNotFoundException}. If found,
   * the admin role is downgraded to a member, and the updated member information is saved to the
   * repository. The response contains details of the downgrade operation.</p>
   *
   * @param chatSpaceId The ID of the chat space where the admin is to be downgraded.
   * @param downgradeChatSpaceAdminToMemberDto The DTO containing the admin ID to be downgraded.
   * @param user The user requesting the downgrade operation.
   * @return A response confirming the downgrade of the chat space admin to a member, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID is not found.
   * @throws ChatSpaceMemberNotFoundException if the chat space member with the specified ID is not found in the chat space.
   */
  @Override
  @Transactional
  public DowngradeChatSpaceAdminToMemberResponse downgradeChatSpaceAdminToMember(final Long chatSpaceId, final DowngradeChatSpaceAdminToMemberDto downgradeChatSpaceAdminToMemberDto, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify that the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfSpace(chatSpace, user);

    // Find the chat space admin to be downgraded or throw an exception if not found
    final ChatSpaceMember chatSpaceMember = chatSpaceMemberRepository
      .findByChatSpaceMemberAndChatSpace(ChatSpaceMember.of(downgradeChatSpaceAdminToMemberDto.getActualChatSpaceMemberId()), chatSpace)
      .orElseThrow(ChatSpaceMemberNotFoundException.of(downgradeChatSpaceAdminToMemberDto.getActualChatSpaceMemberId()));
    // Downgrade the admin role to a member
    chatSpaceMember.downgradeRole();
    // Save the updated chat space member information to the repository
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Get chat space member role
    final ChatSpaceMemberRoleInfo roleInfo = chatSpaceMemberMapper.toRole(chatSpaceMember);
    // Return a localized response confirming the downgrade
    return localizedResponse.of(DowngradeChatSpaceAdminToMemberResponse.of(chatSpaceId, downgradeChatSpaceAdminToMemberDto.getActualChatSpaceMemberId(), roleInfo));
  }

  /**
   * Extracts chat spaces from the provided list of chat space members and creates a response
   * representation for each chat space.
   *
   * <p>This method checks if the provided list of chat space members is not null or empty.
   * If valid, it maps each member to its corresponding chat space, then converts each chat space
   * to a {@link ChatSpaceResponse} using the {@link ChatSpaceMapper}. If the list is empty or null,
   * an empty list is returned.</p>
   *
   * @param chatSpaceMembers The list of chat space members from which to extract chat spaces.
   * @return A list of {@link ChatSpaceResponse} objects representing the user's chat spaces.
   */
  protected List<ChatSpaceResponse> extractUserChatSpaceFromMembershipAndCreateChatResponse(final List<ChatSpaceMember> chatSpaceMembers) {
    // Check if the list of chat space members is not null and not empty
    if (nonNull(chatSpaceMembers) && !chatSpaceMembers.isEmpty()) {
      return chatSpaceMembers.stream()
        // Extract the chat space from each chat space member
        .map(ChatSpaceMember::getChatSpace)
        // Convert each chat space to a ChatSpaceResponse
        .map(chatSpaceMapper::toChatSpaceResponse)
        // Collect the responses into a list
        .collect(Collectors.toList());
    }
    // Return an empty list if the input is null or empty
    return List.of();
  }

  /**
   * Allows a user to join a chat space identified by its ID.
   *
   * <p>This method retrieves the chat space using the provided ID and performs validation to
   * ensure that the chat space is active and public. If valid, it either finds an existing
   * member entry for the user in the chat space or creates a new membership record.</p>
   *
   * @param chatSpaceId The ID of the chat space the user wants to join.
   * @param joinChatSpaceDto The DTO containing any additional information required for joining the space.
   * @param user The user attempting to join the chat space.
   * @return A response confirming the user’s successful joining of the chat space, localized based on the user's locale.
   * @throws ChatSpaceNotFoundException if the chat space with the specified ID does not exist.
   * @throws ChatSpaceNotActiveException if the chat space is inactive.
   * @throws CannotJoinPrivateChatSpaceException if the chat space is not public.
   */
  @Override
  @Transactional
  public JoinChatSpaceResponse joinSpace(final Long chatSpaceId, final JoinChatSpaceDto joinChatSpaceDto, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the chat space
    miscService.verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(chatSpace.getMemberId()), user);
    // Verify if the chat space is inactive and throw an exception if it is
    verifyIfChatSpaceInactive(chatSpace);
    // Verify that the chat space is public and throw an exception if it is not
    validatePublicSpace(chatSpace);
    // Find or create a membership entry for the user in the chat space
    findOrCreateChatSpaceMemberAndAddToChatSpace(chatSpace, joinChatSpaceDto, user);
    // Increase total members and save chat space
    increaseTotalMembersAndSave(chatSpace);
    // Return a localized response indicating successful joining
    return localizedResponse.of(JoinChatSpaceResponse.of());
  }

  /**
   * Finds an existing chat space member or creates a new one, adding the user to the chat space.
   *
   * <p>This method checks if the specified user is already a member of the given chat space. If the user
   * exists, it handles the existing member; otherwise, it creates and approves a new chat space member
   * based on the provided details.</p>
   *
   * @param chatSpace The chat space where the member is to be added or found.
   * @param joinChatSpaceDto The data transfer object containing information necessary for joining the chat space.
   * @param user The user attempting to join the chat space.
   */
  protected void findOrCreateChatSpaceMemberAndAddToChatSpace(final ChatSpace chatSpace, final JoinChatSpaceDto joinChatSpaceDto, final FleenUser user) {
    // Find the chat space member or create a new one if none exists
    chatSpaceMemberRepository.findByChatSpaceAndMember(chatSpace, user.toMember())
      .ifPresentOrElse(
        chatSpaceMember -> handleExistingChatSpaceMember(chatSpace, chatSpaceMember, user),
        () -> createAndApproveNewChatSpaceMember(chatSpace, joinChatSpaceDto, user)
      );
  }

  /**
   * Handles the actions required for an existing chat space member based on their membership status.
   *
   * <p>This method checks if the specified chat space member's request to join the chat space has been
   * approved or disapproved. If the request is already approved, an exception is thrown. If the request
   * is disapproved, the method approves the request and sends an invitation to the user.</p>
   *
   * @param chatSpace The chat space that the member is trying to join.
   * @param chatSpaceMember The existing member of the chat space.
   * @param user The user attempting to join the chat space.
   * @throws AlreadyJoinedChatSpaceException if the membership request has already been approved.
   */
  protected void handleExistingChatSpaceMember(final ChatSpace chatSpace, final ChatSpaceMember chatSpaceMember, final FleenUser user) {
    // Check the membership status of the existing chat space member
    if (chatSpaceMember.isRequestToJoinApproved()) {
      throw new AlreadyJoinedChatSpaceException();
    } else if (chatSpaceMember.isRequestToJoinDisapprovedOrPending()) {
      approveChatSpaceMemberJoinRequestAndSendInvitation(user, chatSpaceMember, chatSpace);
    }
  }

  /**
   * Creates and approves a new chat space member and sends an invitation.
   *
   * <p>This method instantiates a new chat space member using the provided chat space and user details,
   * then immediately approves the join request and sends an invitation to the user.</p>
   *
   * @param chatSpace The chat space to which the new member is being added.
   * @param joinChatSpaceDto The DTO containing the user's comment for the join request.
   * @param user The user attempting to join the chat space.
   */
  protected void createAndApproveNewChatSpaceMember(final ChatSpace chatSpace, final JoinChatSpaceDto joinChatSpaceDto, final FleenUser user) {
    // Create a new chat space member with the provided details
    final ChatSpaceMember newChatSpaceMember = ChatSpaceMember.of(chatSpace, user.toMember(), joinChatSpaceDto.getComment());
    // Approve the join request and send an invitation to the user
    approveChatSpaceMemberJoinRequestAndSendInvitation(user, newChatSpaceMember, chatSpace);
  }

  /**
   * Approves a chat space member's join request and sends an invitation to the member.
   *
   * <p>This method updates the join status of the specified chat space member to approved,
   * saves the updated member information to the repository, and then sends an invitation
   * to the user associated with the chat space member.</p>
   *
   * @param user The user whose join request is being approved.
   * @param chatSpaceMember The chat space member whose status is being updated.
   * @param chatSpace The chat space to which the member is being added.
   */
  protected void approveChatSpaceMemberJoinRequestAndSendInvitation(final FleenUser user, final ChatSpaceMember chatSpaceMember, final ChatSpace chatSpace) {
    // Approve the join status for the chat space member
    chatSpaceMember.approveJoinStatus();
    // Save the updated chat space member information to the repository
    chatSpaceMemberRepository.save(chatSpaceMember);
    // Send an invitation to the user to join the chat space
    chatSpaceUpdateService.addMember(chatSpaceMember, AddChatSpaceMemberRequest.of(chatSpace.getExternalIdOrName(), user.getEmailAddress()));
  }

  /**
   * Handles a user's request to join a chat space.
   *
   * <p>This method checks if the specified chat space is active. If the chat space is private,
   * it processes the join request. A localized response is returned upon successful request.</p>
   *
   * @param chatSpaceId The ID of the chat space the user wants to join.
   * @param requestToJoinChatSpaceDto The DTO containing the request details for joining the chat space.
   * @param user The user requesting to join the chat space.
   * @return A localized response confirming the request to join the chat space.
   */
  @Override
  @Transactional
  public RequestToJoinChatSpaceResponse requestToJoinSpace(final Long chatSpaceId, final RequestToJoinChatSpaceDto requestToJoinChatSpaceDto, final FleenUser user) {
    // Find the chat space by its ID or throw an exception if not found
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Create a chat space member to update later
    ChatSpaceMember chatSpaceMember = new ChatSpaceMember();
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the chat space
    miscService.verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(chatSpace.getMemberId()), user);
    // Verify if the chat space is inactive
    verifyIfChatSpaceInactive(chatSpace);
    // If the chat space is private, handle the join request
    if (isSpacePrivate(chatSpace)) {
      chatSpaceMember = handleJoinRequest(chatSpace, requestToJoinChatSpaceDto, user);
    }

    // Create and save notification
    final Notification notification = notificationMessageService.ofReceived(chatSpace, chatSpaceMember, chatSpace.getMember(), user.toMember());
    notificationService.save(notification);
    // Return a localized response confirming the request to join the chat space
    return localizedResponse.of(RequestToJoinChatSpaceResponse.of());
  }

  /**
   * Handles a user's request to join a chat space.
   *
   * <p>This method checks if the user is already a member of the chat space. If the user has an existing
   * join request, it updates the request status based on the provided comment. If the user is not a member,
   * a new chat space member is created with a pending join status.</p>
   *
   * @param chatSpace The chat space for which the join request is being made.
   * @param requestToJoinChatSpaceDto The DTO containing the request details for joining the chat space.
   * @param user The user requesting to join the chat space.
   */
  protected ChatSpaceMember handleJoinRequest(final ChatSpace chatSpace, final RequestToJoinChatSpaceDto requestToJoinChatSpaceDto, final FleenUser user) {
    // Check if the user is already a member of the chat space
    final AtomicReference<ChatSpaceMember> chatSpaceMemberAtomicReference = new AtomicReference<>();
    chatSpaceMemberRepository.findByChatSpaceAndMember(chatSpace, user.toMember())
      .ifPresentOrElse(chatSpaceMember -> {
        // Update the join status based on the existing request
        updateJoinStatusBasedOnExistingRequest(chatSpaceMember, requestToJoinChatSpaceDto.getComment());
        // Save the updated chat space member
        chatSpaceMemberRepository.save(chatSpaceMember);
        chatSpaceMemberAtomicReference.set(chatSpaceMember);
      }, () -> {
        // Create a new chat space member with a pending join status
        final ChatSpaceMember newChatSpaceMember = ChatSpaceMember.of(chatSpace, user.toMember(), requestToJoinChatSpaceDto.getComment());
        newChatSpaceMember.pendingJoinStatus();
        // Save the new chat space member
        chatSpaceMemberRepository.save(newChatSpaceMember);
        chatSpaceMemberAtomicReference.set(newChatSpaceMember);
    });
    return chatSpaceMemberAtomicReference.get();
  }

  /**
   * Updates the join status of an existing chat space member based on their previous join request status.
   *
   * <p>This method checks the current join request status of a chat space member. If the request is approved,
   * an exception is thrown indicating the user is already a member. If the request is disapproved, the member's
   * status is updated to pending with a comment. If the request is still pending, an exception is thrown.</p>
   *
   * @param chatSpaceMember The chat space member whose join request status is being updated.
   * @param comment The comment associated with the join request.
   */
  protected void updateJoinStatusBasedOnExistingRequest(final ChatSpaceMember chatSpaceMember, final String comment) {
    // Check if the join request has been approved
    if (chatSpaceMember.isRequestToJoinApproved()) {
      throw new AlreadyJoinedChatSpaceException();
    }
    // Check if the join request has been disapproved
    else if (chatSpaceMember.isRequestToJoinDisapproved()) {
      chatSpaceMember.pendingJoinStatusWithComment(comment);
    }
    // Check if the join request is still pending
    else if (chatSpaceMember.isRequestToJoinPending()) {
      throw new RequestToJoinChatSpacePendingException();
    }
  }

  /**
   * Finds chat space members with pending requests to join a specific chat space,
   * optionally filtering by member name.
   *
   * <p>This method retrieves the chat space using the provided chat space ID.
   * If a member name is specified in the search request, it fetches members
   * with that name who have a pending join request status. If no member name
   * is provided, it retrieves all members with a pending status for the chat space.</p>
   *
   * @param chatSpaceId The ID of the chat space for which to find members with pending join requests.
   * @param searchRequest The search request containing pagination details and optional member name.
   * @param user The user performing the search, which must have appropriate permissions.
   * @return A RequestToJoinSearchResult containing the search results of chat space members with pending join requests.
   * @throws ChatSpaceNotFoundException if the chat space does not exist.
   */
  @Override
  public RequestToJoinSearchResult findRequestToJoinSpace(final Long chatSpaceId, final ChatSpaceMemberSearchRequest searchRequest, final FleenUser user) {
    // Retrieve the chat space by its ID
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    final Page<ChatSpaceMember> page;

    // Check if a member name is provided in the search request
    if (nonNull(searchRequest.getMemberName())) {
      // Fetch members with the specified name and pending join request status
      page = chatSpaceMemberRepository.findByChatSpaceAndMemberNameRequestToJoinStatus(chatSpace, searchRequest.getMemberName(), PENDING, searchRequest.getPage());
    } else {
      // Fetch all members with a pending join request status
      page = chatSpaceMemberRepository.findByChatSpaceAndRequestToJoinStatus(chatSpace, PENDING, searchRequest.getPage());
    }

    // Convert the chat space members to response objects
    final List<ChatSpaceMemberResponse> views = chatSpaceMemberMapper.toChatSpaceMemberResponses(page.getContent(), chatSpace);
    // Return a search result view with the request to join responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(RequestToJoinSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyRequestToJoinSearchResult.of(toSearchResult(List.of(), page)))
    );
  }

  /**
   * Processes a request to join a chat space, either approving or disapproving the request.
   *
   * <p>This method first validates whether the user is the creator or an admin of the chat space.
   * It then retrieves the member associated with the request and updates the chat space member's
   * status based on the provided DTO. If the request status changes from disapproved or pending
   * to approved, the chat space update service is notified.</p>
   *
   * @param chatSpaceId The ID of the chat space for which the join request is being processed.
   * @param processRequestToJoinChatSpaceDto The DTO containing the details of the join request and status.
   * @param user The user processing the request, which must be the creator or an admin of the chat space.
   * @return A response indicating the result of processing the join request.
   * @throws ChatSpaceNotFoundException if the chat space does not exist.
   * @throws MemberNotFoundException if the member does not exist.
   * @throws ChatSpaceMemberNotFoundException if the chat space member does not exist.
   * @throws AlreadyJoinedChatSpaceException if the member is already a part of the chat space.
   */
  @Override
  @Transactional
  public ProcessRequestToJoinChatSpaceResponse processRequestToJoinSpace(final Long chatSpaceId, final ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto, final FleenUser user) {
    // Find the chat space by its ID
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify if the chat space has already been deleted and that the user is the creator or an admin of the chat space
    verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(chatSpace, user);
    // Find the member using the provided member ID from the DTO
    final Member member = findMember(processRequestToJoinChatSpaceDto.getActualMemberId());
    // Find the chat space member related to the chat space and member
    final ChatSpaceMember chatSpaceMember = findChatSpaceMember(chatSpace, member);
    // Set the admin comment for the space member
    chatSpaceMember.setSpaceAdminComment(processRequestToJoinChatSpaceDto.getComment());

    // Store the old request status for comparison
    final ChatSpaceRequestToJoinStatus oldRequestToJoinStatus = chatSpaceMember.getRequestToJoinStatus();
    // Process the join request status based on the DTO
    processJoinRequestStatus(chatSpaceMember, processRequestToJoinChatSpaceDto);
    // Notify the update service if the request status changes to approved
    if (ChatSpaceRequestToJoinStatus.isDisapprovedOrPending(oldRequestToJoinStatus) && processRequestToJoinChatSpaceDto.isApproved()) {
      notifyChatSpaceUpdateService(chatSpaceMember, chatSpace, member);
    }

    // Create and save notification
    final Notification notification = notificationMessageService.ofApprovedOrDisapproved(chatSpace, chatSpaceMember, chatSpace.getMember());
    notificationService.save(notification);
    // Return the localized response indicating the result of the processing
    return localizedResponse.of(ProcessRequestToJoinChatSpaceResponse.of(chatSpaceId, processRequestToJoinChatSpaceDto.getActualMemberId()));
  }

  /**
   * Processes the join request status for a chat space member based on the provided join status.
   *
   * <p>This method evaluates the current status of a chat space member's join request. If the request is
   * pending or disapproved, it updates the status based on the actual join status provided in the
   * {@code processRequestToJoinChatSpaceDto}. If approved, the join status is approved with an optional comment;
   * if disapproved, the request status is updated accordingly. The updated member information is then saved to the repository.</p>
   *
   * @param chatSpaceMember The chat space member whose join request status is being processed.
   * @param processRequestToJoinChatSpaceDto The DTO containing the actual join status and optional comment.
   */
  protected void processJoinRequestStatus(final ChatSpaceMember chatSpaceMember, final ProcessRequestToJoinChatSpaceDto processRequestToJoinChatSpaceDto) {
    // Check if the join request is disapproved or pending
    if (chatSpaceMember.isRequestToJoinDisapprovedOrPending()) {
      // Approve the join request if the actual status is approved
      if (ChatSpaceRequestToJoinStatus.isApproved(processRequestToJoinChatSpaceDto.getActualJoinStatus())) {
        // Disapprove the join request if the actual status is disapproved
        chatSpaceMember.approveJoinStatusWithComment(processRequestToJoinChatSpaceDto.getComment());
      } else if (ChatSpaceRequestToJoinStatus.isDisapproved(processRequestToJoinChatSpaceDto.getActualJoinStatus())) {
        chatSpaceMember.disapprovedRequestToJoin();
      }
      // Save the updated chat space member to the repository
      chatSpaceMemberRepository.save(chatSpaceMember);
    }
  }

  /**
   * Adds a member to a chat space.
   *
   * <p>This method retrieves the chat space based on the provided {@code chatSpaceId}, validates the user's
   * permissions to add members, and finds or creates a chat space member object. The member is then
   * updated with an optional admin comment, and the chat space update service is notified of the change.</p>
   *
   * @param chatSpaceId The ID of the chat space.
   * @param addChatSpaceMemberDto The data transfer object containing member details.
   * @param user The user performing the action.
   * @return A response indicating the result of the operation.
   */
  @Override
  @Transactional
  public AddChatSpaceMemberResponse addMember(final Long chatSpaceId, final AddChatSpaceMemberDto addChatSpaceMemberDto, final FleenUser user) {
    // Find the chat space by its ID
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Validate if the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfSpace(chatSpace, user);
    // Find the member to be added using the provided member ID
    final Member member = findMember(addChatSpaceMemberDto.getActualMemberId());
    // Find or create the chat space member object
    final ChatSpaceMember chatSpaceMember = findOrCreateChatMember(chatSpace, member);
    // Approve chat space member join status since the request is been made by the admin
    approveChatMemberJoinStatusAndSaveIfNew(chatSpaceMember);
    // Set an admin comment for the chat space member
    chatSpaceMember.setSpaceAdminComment(addChatSpaceMemberDto.getComment());
    // Notify the chat space update service about the change
    notifyChatSpaceUpdateService(chatSpaceMember, chatSpace, member);
    // Return a localized response indicating success
    return localizedResponse.of(AddChatSpaceMemberResponse.of(chatSpaceId, chatSpaceMember.getChatSpaceMemberId()));
  }

  /**
   * Removes a member from a chat space based on the provided member ID.
   *
   * <p>This method first retrieves the chat space using the provided {@code chatSpaceId}. It then verifies that
   * the user invoking the removal is either the creator or an admin of the chat space. After confirming the user's 
   * permissions, it locates the chat space member to be removed using the member ID from the 
   * {@code removeChatSpaceMemberDto}. Finally, it deletes the member from the repository and updates the chat space 
   * to reflect the change.</p>
   *
   * @param chatSpaceId The ID of the chat space from which the member is to be removed.
   * @param removeChatSpaceMemberDto The DTO containing the member ID of the member to be removed.
   * @param user The user attempting to remove the member.
   * @return A response object indicating the result of the removal operation.
   * @throws NotAnAdminOfChatSpaceException If the user is not the creator or an admin of the chat space.
   * @throws ChatSpaceMemberNotFoundException If the specified chat space member does not exist.
   */
  @Override
  @Transactional
  public RemoveChatSpaceMemberResponse removeMember(final Long chatSpaceId, final RemoveChatSpaceMemberDto removeChatSpaceMemberDto, final FleenUser user) {
    // Retrieve the chat space using the provided chatSpaceId
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Verify that the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfSpace(chatSpace, user);
    // Remove the user from the chat space
    final ChatSpaceMember chatSpaceMember = leaveChatSpaceOrRemoveChatSpaceMember(chatSpace, removeChatSpaceMemberDto.getActualMemberId());
    // Return a localized response indicating the member removal was successful
    return localizedResponse.of(RemoveChatSpaceMemberResponse.of(chatSpaceId, chatSpaceMember.getChatSpaceMemberId()));
  }

  /**
   * Allows a user to leave a chat space by removing them from the specified {@link ChatSpace},
   * and returns a localized response indicating the successful removal.
   *
   * <p>This method retrieves the chat space based on the provided {@code chatSpaceId}, removes the user from the chat space,
   * and returns a {@link LeaveChatSpaceResponse} to confirm the member has left the chat space.</p>
   *
   * @param chatSpaceId the ID of the {@link ChatSpace} the user wishes to leave
   * @param user        the {@link FleenUser} who is leaving the chat space
   * @return a localized {@link LeaveChatSpaceResponse} indicating successful removal from the chat space
   */
  @Override
  @Transactional
  public LeaveChatSpaceResponse leaveChatSpace(final Long chatSpaceId, final FleenUser user) {
    // Retrieve the chat space using the provided chatSpaceId
    final ChatSpace chatSpace = findChatSpace(chatSpaceId);
    // Remove the user from the chat space
    leaveChatSpaceOrRemoveChatSpaceMember(chatSpace, user.getId());
    // Return a localized response indicating the member removal was successful
    return localizedResponse.of(LeaveChatSpaceResponse.of());
  }

  /**
   * Removes a member from the specified chat space or allows the member to leave.
   *
   * <p>This method handles the removal of a member from a chat space based on their member ID.
   * It first finds the member within the chat space, deletes the member entry from the repository,
   * updates the total number of members in the chat space, and notifies the chat space update service
   * of the member's removal.</p>
   *
   * @param chatSpace the chat space from which the member is being removed
   * @param memberId the ID of the member to be removed
   * @return the deleted {@link ChatSpaceMember}
   */
  protected ChatSpaceMember leaveChatSpaceOrRemoveChatSpaceMember(final ChatSpace chatSpace, final Long memberId) {
    // Locate the chat space member to be removed using the member ID from the DTO
    final ChatSpaceMember chatSpaceMember = findChatSpaceMember(chatSpace, Member.of(memberId));
    // Remove the member from the chat space repository
    chatSpaceMemberRepository.delete(chatSpaceMember);
    // Decrease total members and save chat space
    decreaseTotalMembersAndSave(chatSpace);

    // Notify the chat space update service about the removal
    chatSpaceUpdateService.removeMember(RemoveChatSpaceMemberRequest.of(chatSpace.getExternalIdOrName(), chatSpaceMember.getExternalIdOrName()));
    // Return deleted chat space member details
    return chatSpaceMember;
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

  /**
   * Finds a member by their unique identifier.
   *
   * <p>This method retrieves a member from the repository using the provided member ID. If no member is found
   * with the specified ID, a {@link MemberNotFoundException} is thrown.</p>
   *
   * @param memberId The unique identifier of the member to be retrieved.
   * @return The {@link Member} associated with the given member ID.
   * @throws MemberNotFoundException if no member is found with the specified ID.
   */
  protected Member findMember(final Long memberId) {
    // Retrieve the member by ID and throw an exception if not found
    return memberRepository.findById(memberId)
      .orElseThrow(MemberNotFoundException.of(memberId));
  }

  /**
   * Finds a chat space member based on the provided chat space and member.
   *
   * <p>This method retrieves a {@link ChatSpaceMember} from the repository using the specified chat space and member.
   * If no chat space member is found for the given chat space and member, a {@link ChatSpaceMemberNotFoundException}
   * is thrown.</p>
   *
   * @param chatSpace The chat space in which to find the member.
   * @param member The member whose association with the chat space is to be retrieved.
   * @return The {@link ChatSpaceMember} associated with the specified chat space and member.
   * @throws ChatSpaceMemberNotFoundException if no chat space member is found for the specified chat space and member.
   */
  protected ChatSpaceMember findChatSpaceMember(final ChatSpace chatSpace, final Member member) {
    // Retrieve the chat space member and throw an exception if not found
    return chatSpaceMemberRepository.findByChatSpaceAndMember(chatSpace, member)
      .orElseThrow(ChatSpaceMemberNotFoundException.of(member.getMemberId()));
  }


  /**
   * Notifies the chat space update service about the addition of a member to a chat space.
   *
   * <p>This method creates an {@link AddChatSpaceMemberRequest} using the external ID or name of the chat space
   * and the email address of the member being added. It then calls the chat space update service to process
   * the addition of the member.</p>
   *
   * @param chatSpaceMember The {@link ChatSpaceMember} being added to the chat space.
   * @param chatSpace The {@link ChatSpace} to which the member is being added.
   * @param member The {@link Member} that is being added to the chat space.
   */
  protected void notifyChatSpaceUpdateService(final ChatSpaceMember chatSpaceMember, final ChatSpace chatSpace, final Member member) {
    // Create a request to add the member to the chat space
    final AddChatSpaceMemberRequest addChatSpaceMemberRequest = AddChatSpaceMemberRequest.of(chatSpace.getExternalIdOrName(), member.getEmailAddress());
    // Notify the chat space update service of the new member addition
    chatSpaceUpdateService.addMember(chatSpaceMember, addChatSpaceMemberRequest);
  }

  /**
   * Validates that the provided user is either the creator or an admin of the specified chat space.
   *
   * <p>This method checks if the user is the creator of the chat space by comparing their IDs.
   * If the user is not the creator, it further checks if the user is an admin of the chat space.
   * If the user is neither the creator nor an admin, a {@link NotAnAdminOfChatSpaceException} is thrown.</p>
   *
   * @param chatSpace The chat space to validate against.
   * @param user The user whose permissions are being validated.
   * @throws FailedOperationException if any of the provided values is null.
   * @throws NotAnAdminOfChatSpaceException if the user is neither the creator nor an admin of the chat space.
   */
  protected void verifyCreatorOrAdminOfSpace(final ChatSpace chatSpace, final FleenUser user) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(chatSpace, user), FailedOperationException::new);

    // Check if the user is the creator of the space
    if (Objects.equals(chatSpace.getMemberId(), user.getId())) {
      return;
    }
    // Check if the user is an admin in the space
    if (checkIfUserIsAnAdminInSpace(chatSpace, user)) {
      return;
    }
    // If neither, throw exception
    throw new NotAnAdminOfChatSpaceException();
  }

  /**
   * Verifies if the specified chat space has already been marked as deleted.
   *
   * <p>This method checks if the provided chat space is not null and if it has been marked
   * as deleted. If the chat space is found to be deleted, an exception is thrown to indicate
   * that the chat space cannot be operated on.</p>
   *
   * @param chatSpace The chat space to verify.
   * @throws ChatSpaceAlreadyDeletedException if the chat space has already been deleted.
   */
  protected void verifyIfChatSpaceAlreadyDeleted(final ChatSpace chatSpace) {
    // Check if the chat space is not null and if it is marked as deleted
    if (nonNull(chatSpace) && chatSpace.isDeleted()) {
      // Throw an exception if the chat space is already deleted
      throw new ChatSpaceAlreadyDeletedException();
    }
  }

  /**
   * Validates that the chat space has not been deleted and that the user is either the creator or an admin of the chat space.
   *
   * <p>This method first checks if the provided {@code chatSpace} has been deleted. If the chat space is not
   * deleted, it then verifies whether the provided {@code user} is the creator or an admin of the chat space.</p>
   *
   * @param chatSpace The chat space to validate.
   * @param user The user whose permissions are being checked.
   * @throws ChatSpaceAlreadyDeletedException If the chat space has been deleted.
   * @throws NotAnAdminOfChatSpaceException If the user is not the creator or an admin of the chat space.
   */
  protected void verifyIfChatSpaceAlreadyDeletedAndCreatorOrAdminOfSpace(final ChatSpace chatSpace, final FleenUser user) {
    // Verify if the chat space has already been deleted
    verifyIfChatSpaceAlreadyDeleted(chatSpace);
    // Verify that the user is the creator or an admin of the chat space
    verifyCreatorOrAdminOfSpace(chatSpace, user);
  }

  /**
   * Verifies if the specified chat space is inactive.
   *
   * <p>This method checks if the provided chat space is not null and if it is marked
   * as inactive. If the chat space is found to be inactive, an exception is thrown to indicate
   * that the chat space cannot be operated on.</p>
   *
   * @param chatSpace The chat space to verify.
   * @throws ChatSpaceNotActiveException if the chat space is inactive.
   */
  protected void verifyIfChatSpaceInactive(final ChatSpace chatSpace) {
    // Check if the chat space is not null and if it is marked as inactive
    if (nonNull(chatSpace) && chatSpace.isInactive()) {
      // Throw an exception if the chat space is disabled or inactive
      throw new ChatSpaceNotActiveException();
    }
  }

  /**
   * Checks if the specified user is an admin in the provided chat space.
   *
   * <p>This method retrieves all members of the chat space with the admin role and extracts their IDs.
   * It then checks if the given user is among those members. If the user is found in the list of admin
   * member IDs, the method returns true; otherwise, it returns false.</p>
   *
   * @param chatSpace The chat space to check the user's admin status against.
   * @param user The user whose admin status is being checked.
   * @return {@code true} if the user is an admin in the chat space; {@code false} otherwise.
   */
  protected boolean checkIfUserIsAnAdminInSpace(final ChatSpace chatSpace, final FleenUser user) {
    // Retrieve all members of the chat space with the admin role
    final Set<ChatSpaceMember> chatSpaceMembers = chatSpaceMemberRepository.findByChatSpaceAndRole(chatSpace, ChatSpaceMemberRole.ADMIN);
    // Extract the IDs of the admin members
    final Set<Long> spaceMemberIds = extractMemberIds(chatSpaceMembers);
    // Check if the user is among the admin members
    return isSpaceMemberAnAdmin(spaceMemberIds, user);
  }

  /**
   * Extracts the member IDs from a set of chat space members.
   *
   * <p>This method checks if the provided set of chat space members is not null and not empty.
   * If valid, it streams the members and maps them to their corresponding member IDs, collecting
   * the results into a set. If the input set is null or empty, an empty set is returned.</p>
   *
   * @param chatSpaceMembers The set of chat space members from which to extract member IDs.
   * @return A set of member IDs extracted from the provided chat space members, or an empty set if the input is null or empty.
   */
  protected Set<Long> extractMemberIds(final Set<ChatSpaceMember> chatSpaceMembers) {
    // Check if the chat space members set is not null and not empty
    if (nonNull(chatSpaceMembers) && !chatSpaceMembers.isEmpty()) {
      // Stream the members and collect their IDs into a set
      return chatSpaceMembers.stream()
        .map(ChatSpaceMember::getMemberId)
        .collect(Collectors.toSet());
    }
    // Return an empty set if the input is null or empty
    return Set.of();
  }

  /**
   * Checks if the specified user is an admin among the provided space member IDs.
   *
   * <p>This method verifies that the set of space member IDs and the user are not null or empty.
   * If valid, it checks if the user’s ID is present in the set of space member IDs, indicating
   * that the user is an admin. If the set is null, empty, or the user is null, it returns false.</p>
   *
   * @param spaceMemberIds The set of space member IDs to check against.
   * @param user The user whose admin status is being checked.
   * @return True if the user is an admin (their ID is in the set of member IDs); false otherwise.
   */
  protected boolean isSpaceMemberAnAdmin(final Set<Long> spaceMemberIds, final FleenUser user) {
    // Check if the space member IDs set and user are not null or empty
    if (nonNull(spaceMemberIds) && !spaceMemberIds.isEmpty() && nonNull(user)) {
      // Check if the user's ID is present in the set of space member IDs
      return spaceMemberIds.contains(user.getId());
    }
    // Return false if the input set is null, empty, or the user is null
    return false;
  }

  /**
   * Checks if the specified chat space is private.
   *
   * <p>This method verifies that the chat space object is not null and checks its
   * privacy status. It returns true if the chat space is marked as private; otherwise,
   * it returns false.</p>
   *
   * @param chatSpace The chat space to check for privacy.
   * @return True if the chat space is private; false if it is public or the chat space is null.
   */
  public boolean isSpacePrivate(final ChatSpace chatSpace) {
    // Check if the chat space is not null and if it is marked as private
    return nonNull(chatSpace) && chatSpace.isPrivate();
  }

  /**
   * Validates whether the specified chat space is public.
   *
   * <p>This method checks if the provided chat space is private. If the chat space is private,
   * it throws an exception, preventing users from joining.</p>
   *
   * @param chatSpace The chat space to validate for public access.
   * @throws CannotJoinPrivateChatSpaceException if the chat space is private and cannot be joined.
   */
  protected void validatePublicSpace(final ChatSpace chatSpace) {
    // Check if the chat space is private and throw an exception if it is
    if (isSpacePrivate(chatSpace)) {
      throw new CannotJoinPrivateChatSpaceException();
    }
  }

  /**
   * Finds an existing chat space member or creates a new one if it doesn't exist.
   *
   * <p>This method attempts to find a member within a given chat space. If the member does
   * not already exist in the space, a new {@code ChatSpaceMember} is created and saved
   * to the repository.</p>
   *
   * @param chatSpace the chat space in which to find or create the member.
   * @param member the member to find or create in the chat space.
   * @return the existing or newly created {@code ChatSpaceMember}.
   */
  protected ChatSpaceMember findOrCreateChatMember(final ChatSpace chatSpace, final Member member) {
    // Attempt to find the chat space member by chat space and member or create one if none can be found
    return chatSpaceMemberRepository.findByChatSpaceAndMember(chatSpace, member)
      .orElse(ChatSpaceMember.of(chatSpace, member, null));
  }

  /**
   * Approves the join status of the given {@link ChatSpaceMember} and saves it to the repository if it is newly created.
   *
   * <p>This method first checks if the {@code chatSpaceMember} is non-null, then approves the member's join status by
   * invoking {@link ChatSpaceMember#approveJoinStatus()}. If the member does not yet have a {@code chatSpaceMemberId},
   * indicating it is a new entity, it will be saved to the repository.</p>
   *
   * @param chatSpaceMember the {@link ChatSpaceMember} whose join status is to be approved
   *                        and saved if it is newly created
   */
  protected void approveChatMemberJoinStatusAndSaveIfNew(final ChatSpaceMember chatSpaceMember) {
    // Verify the chat space member is not empty
    if (nonNull(chatSpaceMember)) {
      chatSpaceMember.approveJoinStatus();
      // If the chat space member is newly created, save it to the repository
      if (isNull(chatSpaceMember.getChatSpaceMemberId())) {
        chatSpaceMemberRepository.save(chatSpaceMember);
      }
    }
  }

  /**
   * Increases the total number of members in the specified chat space and saves the updated chat space entity.
   *
   * @param chatSpace the chat space where the total number of members should be increased
   */
  protected void increaseTotalMembersAndSave(final ChatSpace chatSpace) {
    // Increase total members in chat space
    chatSpace.increaseTotalMembers();
    // Save chat space to repository
    chatSpaceRepository.save(chatSpace);
  }

  /**
   * Decreases the total number of members in the specified chat space and saves the updated chat space entity.
   *
   * @param chatSpace the chat space where the total number of members should be decreased
   */
  protected void decreaseTotalMembersAndSave(final ChatSpace chatSpace) {
    // Decrease total members in chat space
    chatSpace.decreaseTotalMembers();
    // Save chat space to repository
    chatSpaceRepository.save(chatSpace);
  }

  /**
   * Determines the user's join status for each chat space in the provided list of {@link ChatSpaceResponse} objects.
   *
   * <p>If both the user and the responses are valid, the method retrieves the chat space IDs from the responses,
   * fetches the user's attendance or membership information from the {@link ChatSpaceMemberRepository},
   * and then updates the join status of each response based on the user's membership status.</p>
   *
   * <p>This ensures that each {@link ChatSpaceResponse} reflects the user's current membership or attendance status
   * for the corresponding chat spaces.</p>
   *
   * @param responses the list of {@link ChatSpaceResponse} objects to update with join status
   * @param user the {@link FleenUser} whose membership status will be checked and applied to the responses
   */
  protected void determineUserJoinStatusFoChatSpace(final List<ChatSpaceResponse> responses, final FleenUser user) {
    // Check if both user and responses are valid before proceeding
    if (isUserAndResponsesValid(responses, user)) {
      // Extract chat space IDs from the responses
      final List<Long> eventIds = extractAndGetChatSpaceIds(responses);
      // Retrieve the user's membership or attendance status for the chat spaces
      final List<ChatSpaceMemberSelect> userMemberships = chatSpaceMemberRepository.findByMemberAndEventOrStreamIds(user.toMember(), eventIds);
      // Group the user's membership statuses by chat space ID
      final Map<Long, ChatSpaceMemberSelect> membershipMap = groupMemberStatusByChatSpaceId(userMemberships);
      // Update the join status of the responses based on the membership status map
      updateJoinStatusInResponses(responses, membershipMap);
    }
  }

  /**
   * Checks if both the user and the collection of {@link ChatSpaceResponse} objects are valid.
   *
   * <p>This method ensures that the {@link FleenUser} object is not null, the user has been converted
   * to a valid {@link Member}, and the provided collection of responses is not null.</p>
   *
   * <p>If any of these checks fail, the method returns false, otherwise, it returns true.</p>
   *
   * @param responses the collection of {@link ChatSpaceResponse} objects to validate
   * @param user the {@link FleenUser} to validate
   * @return true if both the user and responses are valid; false otherwise
   */
  protected boolean isUserAndResponsesValid(final Collection<ChatSpaceResponse> responses, final FleenUser user) {
    // Ensure that the user, user's member, and responses are all non-null
    return nonNull(user) && nonNull(user.toMember()) && nonNull(responses);
  }

  /**
   * Extracts and returns the list of chat space IDs from the provided list of {@link ChatSpaceResponse}.
   *
   * <p>This method processes a list of {@link ChatSpaceResponse} objects, filtering out null entries
   * and mapping each response to its corresponding chat space ID, which is then collected into a list.</p>
   *
   * <p>If the input list is null, an empty list is returned.</p>
   *
   * @param responses the list of {@link ChatSpaceResponse} objects to extract chat space IDs from
   * @return a list of chat space IDs or an empty list if the input is null
   */
  protected static List<Long> extractAndGetChatSpaceIds(final List<ChatSpaceResponse> responses) {
    if (nonNull(responses)) {
      return responses.stream()
        // Filter out null responses
        .filter(Objects::nonNull)
        // Map each response to its chat space ID
        .map(ChatSpaceResponse::getNumberId)
        // Collect the IDs into a list
        .toList();
    }
    // Return an empty list if the input is null
    return List.of();
  }

  /**
   * Groups membership statuses by chat space ID from the provided list of {@link ChatSpaceMemberSelect}.
   *
   * <p>This method processes a list of {@link ChatSpaceMemberSelect} objects, filtering out null entries
   * and collecting the results into a map. The keys of the map are chat space IDs, and the values are
   * the corresponding membership statuses.</p>
   *
   * <p>If the input list is null or empty, an empty map is returned.</p>
   *
   * @param userMembership the list of user memberships to be processed
   * @return a map of chat space IDs to membership statuses
   */
  protected static Map<Long, ChatSpaceMemberSelect> groupMemberStatusByChatSpaceId(final List<ChatSpaceMemberSelect> userMembership) {
    if (nonNull(userMembership) && !userMembership.isEmpty()) {
      return userMembership.stream()
        // Filter out any null entries in the list
        .filter(Objects::nonNull)
        // Collect results into a map with chat space ID as key and membership status as value
        .collect(Collectors.toMap(ChatSpaceMemberSelect::getChatSpaceId, Function.identity()));
    }
    // Return an empty map if the input list is null or empty
    return Map.of();
  }

  /**
   * Updates the join status of each {@link ChatSpaceResponse} in the provided list based on the
   * corresponding membership status from the provided map.
   *
   * <p>If a response's ID exists in the membership status map, its join status is updated accordingly.</p>
   *
   * <p>This method iterates over the responses, checks the presence of each response's ID in the
   * membership status map, and sets the join status for responses with a matching entry.</p>
   *
   * @param responses the list of {@link ChatSpaceResponse} to be updated
   * @param membershipStatusMap a map of membership statuses keyed by the chat space ID
   */
  protected void updateJoinStatusInResponses(final List<ChatSpaceResponse> responses, final Map<Long, ChatSpaceMemberSelect> membershipStatusMap) {
    if (nonNull(responses) && nonNull(membershipStatusMap)) {
      responses.stream()
        .filter(Objects::nonNull)
        .forEach(chatSpace -> {
          // Retrieve the membership status for the current chat space response
          final Optional<ChatSpaceMemberSelect> existingMembership = Optional.ofNullable(membershipStatusMap.get(chatSpace.getNumberId()));
          // If a membership status exists, set the join status on the response
          existingMembership.ifPresent(membership -> chatSpaceMapper.update(chatSpace, membership.getRequestToJoinStatus(), membership.getJoinStatus()));
      });
    }
  }

  /**
   * Updates the total number of pending requests to join for each chat space in the given collection of ChatSpaceResponse objects.
   *
   * <p>This method retrieves the pending join request counts for each chat space by querying the repository,
   * maps the counts to the respective chat spaces, and then updates the {@code totalRequestToJoin} field in each {@link ChatSpaceResponse}.
   * If no pending requests are found for a chat space, the total is set to 0.</p>
   *
   * @param views a collection of {@link ChatSpaceResponse} objects representing the chat spaces
   */
  private void updateTotalRequestToJoinForChatSpaces(final Collection<ChatSpaceResponse> views) {
    if (nonNull(views) && (!views.isEmpty())) {
      // Get a list of chatSpaceIds to retrieve join request counts
      final List<Long> chatSpaceIds = views.stream()
        .filter(Objects::nonNull)
        .map(ChatSpaceResponse::getNumberId)
        .toList();

      // Fetch the pending join request counts for the chat spaces
      final List<ChatSpaceRequestToJoinPendingSelect> pendingRequests = chatSpaceMemberRepository
        .countPendingJoinRequestsForChatSpaces(chatSpaceIds, PENDING);

      // Map the counts back to the ChatSpaceResponse objects
      final Map<Long, Long> pendingRequestsMap = pendingRequests.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(ChatSpaceRequestToJoinPendingSelect::getChatSpaceId, ChatSpaceRequestToJoinPendingSelect::getRequestToJoinTotal));

      // Set the total requests to join in each ChatSpaceResponse
      views.stream()
        .filter(Objects::nonNull)
        .forEach(view -> view.setTotalRequestToJoin(pendingRequestsMap.getOrDefault(view.getNumberId(), 0L)));
    }
  }

}
