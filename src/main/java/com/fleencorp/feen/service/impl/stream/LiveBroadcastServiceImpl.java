package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.CannotCancelOrDeleteOngoingStreamException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.RescheduleLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.UpdateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.stream.ProcessAttendeeRequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.RequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.UpdateEventOrStreamVisibilityDto;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.request.search.youtube.LiveBroadcastSearchRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.*;
import com.fleencorp.feen.model.response.broadcast.*;
import com.fleencorp.feen.model.response.external.google.youtube.category.YouTubeCategoriesResponse;
import com.fleencorp.feen.model.response.stream.EventOrStreamAttendeesResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.PageAndFleenStreamResponse;
import com.fleencorp.feen.model.response.stream.StreamAttendeeResponse;
import com.fleencorp.feen.model.search.broadcast.EmptyLiveBroadcastSearchResult;
import com.fleencorp.feen.model.search.broadcast.LiveBroadcastSearchResult;
import com.fleencorp.feen.model.search.broadcast.request.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.oauth2.Oauth2AuthorizationRepository;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.repository.stream.UserFleenStreamRepository;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.impl.external.google.oauth2.GoogleOauth2Service;
import com.fleencorp.feen.service.impl.external.google.youtube.YouTubeChannelService;
import com.fleencorp.feen.service.impl.external.google.youtube.YouTubeLiveBroadcastService;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.impl.stream.base.StreamService;
import com.fleencorp.feen.service.impl.stream.update.LiveBroadcastUpdateService;
import com.fleencorp.feen.service.notification.NotificationService;
import com.fleencorp.feen.service.stream.EventService;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.fleencorp.base.util.ExceptionUtil.checkIsTrue;
import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus.APPROVED;
import static com.fleencorp.feen.mapper.FleenStreamMapper.toFleenStreamResponse;
import static com.fleencorp.feen.validator.impl.TimezoneValidValidator.getAvailableTimezones;

/**
 * Implementation of LiveBroadcastService that handles creating live broadcasts and searching for them.
 *
 * <p>This class interacts with YouTubeLiveBroadcastService to create live broadcasts,
 * FleenStreamRepository to store and retrieve live broadcast data, and GoogleOauth2AuthorizationRepository
 * to handle OAuth2 authorization.</p>
 *
 * <p>It is responsible for creating live broadcasts using details from CreateLiveBroadcastDto,
 * verifying OAuth2 authorization, and saving the broadcast data. Additionally, it supports
 * finding live broadcasts based on search criteria such as date range, title, or general pagination.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class LiveBroadcastServiceImpl extends StreamService implements LiveBroadcastService {

  private final EventService eventService;
  private final GoogleOauth2Service googleOauth2Service;
  private final LiveBroadcastUpdateService liveBroadcastUpdateService;
  private final NotificationMessageService notificationMessageService;
  private final NotificationService notificationService;
  private final YouTubeChannelService youTubeChannelService;
  private final FleenStreamRepository fleenStreamRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final Oauth2AuthorizationRepository oauth2AuthorizationRepository;
  private final UserFleenStreamRepository userFleenStreamRepository;
  private final LocalizedResponse localizedResponse;

  /**
   * Constructs a new instance of LiveBroadcastServiceImpl.
   *
   * <p>This constructor initializes the necessary services and repositories for handling live broadcasts,
   * including the YouTubeLiveBroadcastService for creating broadcasts on YouTube, the FleenStreamRepository
   * for managing broadcast data, and the GoogleOauth2AuthorizationRepository for OAuth2 authorization.</p>
   *
   * @param eventService the service for interacting and managing events
   * @param googleOauth2Service the service for interacting with Google Oauth2 service
   * @param miscService the service for managing miscellaneous actions
   * @param notificationMessageService the service that manages notification messages for events and attendees
   * @param notificationService the service responsible for managing notifications in the system
   * @param youTubeChannelService the service for managing YouTube channels and categories
   * @param fleenStreamRepository the repository to manage FleenStream data
   * @param streamAttendeeRepository the repository for managing event or stream attendees
   * @param oauth2AuthorizationRepository the repository to handle OAuth2 authorization
   * @param userFleenStreamRepository  the repository for FleenStream operations related to a user profile
   * @param localizedResponse the service for creating localized response message
   */
  public LiveBroadcastServiceImpl(
      final EventService eventService,
      final GoogleOauth2Service googleOauth2Service,
      @Lazy final LiveBroadcastUpdateService liveBroadcastUpdateService,
      final MiscService miscService,
      final NotificationMessageService notificationMessageService,
      final NotificationService notificationService,
      final YouTubeChannelService youTubeChannelService,
      final FleenStreamRepository fleenStreamRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final Oauth2AuthorizationRepository oauth2AuthorizationRepository,
      final UserFleenStreamRepository userFleenStreamRepository,
      final LocalizedResponse localizedResponse) {
    super(miscService, fleenStreamRepository, streamAttendeeRepository, localizedResponse);
    this.eventService = eventService;
    this.googleOauth2Service = googleOauth2Service;
    this.notificationMessageService = notificationMessageService;
    this.notificationService = notificationService;
    this.liveBroadcastUpdateService = liveBroadcastUpdateService;
    this.youTubeChannelService = youTubeChannelService;
    this.fleenStreamRepository = fleenStreamRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.oauth2AuthorizationRepository = oauth2AuthorizationRepository;
    this.userFleenStreamRepository = userFleenStreamRepository;
    this.localizedResponse = localizedResponse;
  }

  /**
   * Retrieves the data required for creating a stream, including available timezones and YouTube categories.
   *
   * @return a {@link DataForCreateStreamResponse} containing the set of available timezones and
   *         the list of YouTube categories
   */
  @Override
  public DataForCreateStreamResponse getDataForCreateStream() {
    final Set<String> timezones = getAvailableTimezones();
    final YouTubeCategoriesResponse categoriesResponse = youTubeChannelService.listAssignableCategories();
    return DataForCreateStreamResponse.of(timezones, categoriesResponse.getCategories());
  }

  /**
   * Finds live broadcasts based on the search criteria provided in the LiveBroadcastSearchRequest.
   *
   * <p>This method searches for live broadcasts in the repository by checking if start and end dates
   * are provided in the search request. If both dates are provided, it searches for broadcasts within
   * the specified date range. If only a title is provided, it searches for broadcasts by title. If no
   * specific criteria are provided, it retrieves a default set of broadcasts.</p>
   *
   * <p>The method then converts the retrieved broadcasts into a list of FleenStreamResponse objects
   * and returns a SearchResultView containing the search results.</p>
   *
   * @param searchRequest the request object containing search criteria
   * @return a LiveBroadcastSearchResult containing the search results
   */
  @Override
  public LiveBroadcastSearchResult findLiveBroadcasts(final LiveBroadcastSearchRequest searchRequest, final FleenUser user) {
    final PageAndFleenStreamResponse pageAndResponse = findEventsOrStreams(searchRequest);
    final List<FleenStreamResponse> views = pageAndResponse.getResponses();
    // Determine statuses like schedule, join status, schedules and timezones
    determineDifferentStatusesAndDetailsOfEventOrStreamBasedOnUser(views, user);
    // Set the attendees and total attendee count for each event or stream
    setStreamAttendeesAndTotalAttendeesAttending(views);
    // Get the first 10 attendees for each event or stream
    getFirst10AttendingInAnyOrder(views);
    // Return a search result view with the live broadcast responses and pagination details
    return handleSearchResult(
      pageAndResponse.getPage(),
      localizedResponse.of(LiveBroadcastSearchResult.of(toSearchResult(views, pageAndResponse.getPage()))),
      localizedResponse.of(EmptyLiveBroadcastSearchResult.of(toSearchResult(List.of(), pageAndResponse.getPage())))
    );
  }

  /**
   * Retrieves details about a specific stream, including its attendees and their statuses.
   *
   * @param streamId the ID of the stream to retrieve
   * @return a {@link RetrieveStreamResponse} containing details of the stream, its attendees, and the total count of approved attendees
   */
  @Override
  public RetrieveStreamResponse retrieveStream(final Long streamId) {
    // Find the stream by its ID
    final FleenStream stream = findStream(streamId);
    // Get all event or stream attendees
    final Set<StreamAttendee> streamAttendeesGoingToStream = getAttendeesGoingToStream(stream);
    // Convert the attendees to response objects
    final Set<StreamAttendeeResponse> streamAttendees = toStreamAttendeeResponses(streamAttendeesGoingToStream);
    // Count total attendees whose request to join event is approved and are attending the event because they are interested
    final long totalAttendees = streamAttendeeRepository.countByFleenStreamAndRequestToJoinStatusAndIsAttending(stream, APPROVED, true);
    return localizedResponse.of(RetrieveStreamResponse.of(streamId, toFleenStreamResponse(stream), streamAttendees, totalAttendees));
  }

  /**
   * Creates a live broadcast on YouTube using the provided details and associates it with a new FleenStream entity.
   *
   * <p>This method first checks if there is a valid OAuth2 authorization for the user. It constructs a
   * {@link CreateLiveBroadcastRequest} using the details from {@link CreateLiveBroadcastDto} and sets the access token
   * for the HTTP request. The live broadcast is created using {@link YouTubeLiveBroadcastService#createBroadcast(CreateLiveBroadcastRequest)}.</p>
   *
   * <p>Upon successful creation of the YouTube live broadcast, a new {@link FleenStream} entity is instantiated using
   * {@link CreateLiveBroadcastDto#toFleenStream()}. The live stream link and broadcast ID from the YouTube response are
   * set to the stream entity. Finally, the stream entity is saved to the repository, and a {@link CreateStreamResponse}
   * is returned with the newly created stream ID and its corresponding {@link FleenStreamResponse}.</p>
   *
   * @param createLiveBroadcastDto The DTO containing details for creating the live broadcast.
   * @param user                   The authenticated user initiating the broadcast creation.
   * @return A {@link CreateStreamResponse} object containing the details of the created stream.
   * @throws Oauth2InvalidAuthorizationException If the OAuth2 authorization for the user is invalid or missing.
   */
  @Override
  @Transactional
  public CreateStreamResponse createLiveBroadcast(final CreateLiveBroadcastDto createLiveBroadcastDto, final FleenUser user) {
    // Check if there is a valid OAuth2 authorization for the user
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(createLiveBroadcastDto.getOauth2ServiceType(), user);
    // Create a request object to create the live broadcast on YouTube
    final String organizerAliasOrDisplayName = createLiveBroadcastDto.getOrganizerAlias(user.getFullName());
    final CreateLiveBroadcastRequest createLiveBroadcastRequest = CreateLiveBroadcastRequest.by(createLiveBroadcastDto);
    // Update access token needed to perform request
    createLiveBroadcastRequest.updateToken(oauth2Authorization.getAccessToken());
    // Create a new FleenStream entity based on the DTO and set YouTube response details
    FleenStream stream = createLiveBroadcastDto.toFleenStream(user.toMember());
    stream.updateDetails(
      organizerAliasOrDisplayName,
      user.getEmailAddress(),
      user.getPhoneNumber());

    // Increase attendees count, save the event and and add the stream in YouTube Live Stream
    stream = increaseTotalAttendeesOrGuestsAndSave(stream);
    // Register the organizer of the event as an attendee or guest
    registerAndApproveOrganizerOfEventAsAnAttendee(stream, user);
    // Create and add live broadcast or stream in external service for example YouTube Live Stream API
    liveBroadcastUpdateService.createLiveBroadcastAndStream(stream, createLiveBroadcastRequest);

    return localizedResponse.of(CreateStreamResponse.of(stream.getStreamId(), incrementAttendeeBecauseOfOrganizerAndGetResponse(stream)));
  }

  /**
   * Updates a live broadcast associated with the specified stream ID using the provided details.
   *
   * <p>This method retrieves the {@link FleenStream} entity from the repository based on the given stream ID.
   * It verifies the OAuth2 authorization for the user and constructs an {@link UpdateLiveBroadcastRequest}
   * using the access token and update details from {@link UpdateLiveBroadcastDto}. The live broadcast is then
   * updated using {@link YouTubeLiveBroadcastService#updateLiveBroadcast(UpdateLiveBroadcastRequest)}.</p>
   *
   * <p>If the stream and authorization are valid, the method updates the stream's title, description, tags, and
   * location. It saves the updated stream back to the repository and returns an {@link UpdateStreamResponse} with
   * the updated stream ID and its corresponding {@link FleenStreamResponse}.</p>
   *
   * <p>If the stream ID does not exist in the repository or the OAuth2 authorization is invalid, appropriate
   * exceptions are thrown.</p>
   *
   * @param streamId               The ID of the stream to update.
   * @param updateLiveBroadcastDto The DTO containing updated details for the live broadcast.
   * @param user                   The authenticated user initiating the update.
   * @return An {@link UpdateStreamResponse} object representing the updated stream response.
   * @throws FleenStreamNotFoundException     If the specified stream ID does not exist in the repository.
   * @throws Oauth2InvalidAuthorizationException If the OAuth2 authorization for the user is invalid or missing.
   */
  @Override
  @Transactional
  public UpdateStreamResponse updateLiveBroadcast(final Long streamId, final UpdateLiveBroadcastDto updateLiveBroadcastDto, final FleenUser user) {
    // Retrieve the FleenStream entity from the repository based on the stream ID
    final FleenStream stream = findStream(streamId);
    // Validate if the user is the creator of the event
    validateCreatorOfEvent(stream, user);
    // Check if the OAuth2 authorization exists for the user
    final Oauth2Authorization oauth2Authorization = verifyAndGetUserOauth2Authorization(user);
    // Create an update request using the access token and update details
    final UpdateLiveBroadcastRequest updateLiveBroadcastRequest = UpdateLiveBroadcastRequest.of(
      oauth2Authorization.getAccessToken(),
      updateLiveBroadcastDto.getTitle(),
      updateLiveBroadcastDto.getDescription(),
      stream.getExternalId()
    );

    // Update the stream entity with new title, description, tags, and location
    stream.update(
      updateLiveBroadcastDto.getTitle(),
      updateLiveBroadcastDto.getDescription(),
      updateLiveBroadcastDto.getTags(),
      updateLiveBroadcastDto.getLocation()
    );
    // Save the updated stream to the repository
    fleenStreamRepository.save(stream);

    // Update the live broadcast using YouTubeLiveBroadcastService
    liveBroadcastUpdateService.updateLiveBroadcastAndStream(stream, updateLiveBroadcastRequest);
    return localizedResponse.of(UpdateStreamResponse.of(streamId, toFleenStreamResponse(stream)));
  }

  /**
   * Reschedules a live broadcast associated with the specified stream ID using the provided details.
   *
   * <p>This method retrieves the {@link FleenStream} entity from the repository based on the stream ID. It validates
   * the OAuth2 authorization for the user and constructs a {@link RescheduleLiveBroadcastRequest} using the provided
   * reschedule details and access token. The live broadcast is then rescheduled using
   * {@link YouTubeLiveBroadcastService#rescheduleLiveBroadcast(RescheduleLiveBroadcastRequest)}.</p>
   *
   * <p>After successfully rescheduling the YouTube live broadcast, the method updates the schedule of the corresponding
   * {@link FleenStream} entity with the new start and end times and timezone. The updated stream entity is saved back
   * to the repository, and a {@link RescheduleStreamResponse} is returned with the rescheduled stream ID and its
   * corresponding {@link FleenStreamResponse}.</p>
   *
   * @param streamId                  The ID of the FleenStream to reschedule.
   * @param rescheduleLiveBroadcastDto The DTO containing details for rescheduling the live broadcast.
   * @param user                      The authenticated user initiating the broadcast rescheduling.
   * @return A {@link RescheduleStreamResponse} object containing the details of the rescheduled stream.
   * @throws FleenStreamNotFoundException    If the FleenStream with the specified ID is not found.
   * @throws Oauth2InvalidAuthorizationException If the OAuth2 authorization for the user is invalid or missing.
   */
  @Override
  @Transactional
  public RescheduleStreamResponse rescheduleLiveBroadcast(final Long streamId, final RescheduleLiveBroadcastDto rescheduleLiveBroadcastDto, final FleenUser user) {
    // Retrieve the FleenStream entity from the repository based on the stream ID
    final FleenStream stream = findStream(streamId);
    // Validate if the user is the creator of the event
    validateCreatorOfEvent(stream, user);
    // Retrieve the Oauth2 Authorization associated with the user
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(rescheduleLiveBroadcastDto.getOauth2ServiceType(), user);
    // Create a request object to reschedule the live broadcast on YouTube
    final RescheduleLiveBroadcastRequest rescheduleLiveBroadcastRequest = RescheduleLiveBroadcastRequest.of(
      oauth2Authorization.getAccessToken(),
      rescheduleLiveBroadcastDto.getActualStartDateTime(),
      rescheduleLiveBroadcastDto.getActualEndDateTime(), null,
      stream.getExternalId()
    );

    // Update the schedule of the FleenStream entity with new start and end times and timezone
    stream.updateSchedule(
      rescheduleLiveBroadcastDto.getActualStartDateTime(),
      rescheduleLiveBroadcastDto.getActualEndDateTime(),
      rescheduleLiveBroadcastDto.getTimezone()
    );
    // Save the rescheduled stream to the repository
    fleenStreamRepository.save(stream);

    // Reschedule the live broadcast using Live Stream API
    liveBroadcastUpdateService.rescheduleLiveBroadcastAndStream(stream, rescheduleLiveBroadcastRequest);
    return localizedResponse.of(RescheduleStreamResponse.of(streamId, toFleenStreamResponse(stream)));
  }

  /**
   * Updates the attendance status of a user for a specific event to indicate they are not attending.
   *
   * @param eventId the ID of the event
   * @param user the user who is indicating they are not attending
   * @return a {@link NotAttendingStreamResponse} indicating the outcome of the operation
   */
  @Override
  @Transactional
  public NotAttendingStreamResponse notAttendingStream(final Long eventId, final FleenUser user) {
    // Find the stream by its ID
    final FleenStream stream = findStream(eventId);
    // Verify if the user is the owner and fail the operation because the owner is automatically a member of the chat space
    verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(Member.of(stream.getMemberId()), user);

    // Find the existing attendee record for the user and event
    streamAttendeeRepository.findByFleenStreamAndMember(stream, user.toMember())
      .ifPresent(streamAttendee -> {
        // If an attendee record exists, update their attendance status to false
        streamAttendee.setIsNotAttending();
        // Decrease the total number of attendees to stream
        decreaseTotalAttendeesOrGuestsAndSave(stream);
        // Save the updated attendee record
        streamAttendeeRepository.save(streamAttendee);
      });
    // Build and return the response indicating the user is no longer attending
    return localizedResponse.of(NotAttendingStreamResponse.of());
  }

  /**
   * Facilitates a user's action to join a specific live stream.
   *
   * <p>This method enables a user to join a live stream identified by the given stream ID. It delegates
   * the join operation to the {@link StreamService#verifyDetailsAndTryToJoinEventOrStream(Long, FleenUser)} method, which
   * processes the join request based on the business logic defined within the event service. The operation
   * involves updating the user's participation status in the event or stream.</p>
   *
   * <p>The result of this operation is encapsulated in a {@link JoinStreamResponse} object, which provides
   * a localized response indicating the success or failure of the join action.</p>
   *
   * @param streamId the unique identifier of the stream that the user intends to join.
   * @param user the user who is attempting to join the stream. This object contains the user's details,
   *             including identity and permissions necessary for the join action.
   * @return a localized {@link JoinStreamResponse} object that conveys the outcome of the join attempt.
   */
  @Override
  @Transactional
  public JoinStreamResponse joinStream(final Long streamId, final FleenUser user) {
    // Verify the event or stream details and attempt to join the event or stream
    verifyDetailsAndTryToJoinEventOrStream(streamId, user);

    return localizedResponse.of(JoinStreamResponse.of(streamId));
  }

  /**
   * Handles a user's request to join a specified live stream.
   *
   * <p>This method facilitates a user's request to participate in a live stream identified by its ID. It
   * delegates the processing of the join request to the {@link EventService} method.
   * This delegation ensures that the request is processed according to the predefined business rules
   * and logic within the event service.</p>
   *
   * <p>The method returns a localized response indicating the outcome of the join request, encapsulated
   * within a {@link RequestToJoinStreamResponse} object.</p>
   *
   * @param streamId the unique identifier of the stream the user wishes to join.
   * @param requestToJoinEventOrStreamDto a data transfer object containing details about the user's join
   *                                      request, including any specific instructions or preferences.
   * @param user the user who is making the request to join the stream. This object contains the user's
   *             identity and relevant permission details.
   * @return a localized {@link RequestToJoinStreamResponse} object that provides feedback on the result of
   *         the join request.
   */
  @Override
  @Transactional
  public RequestToJoinStreamResponse requestToJoinStream(final Long streamId, final RequestToJoinEventOrStreamDto requestToJoinEventOrStreamDto, final FleenUser user) {
    eventService.requestToJoinEvent(streamId, requestToJoinEventOrStreamDto, user);

    return localizedResponse.of(RequestToJoinStreamResponse.of(streamId));
  }

  /**
   * Processes an attendee's request to join an stream.
   *
   * <p>This method validates if the user is the creator of the stream and if the stream is still active.
   * It then processes the attendee's request to join the stream, updating their status and adding them
   * as an attendee if the request is approved.</p>
   *
   * @param streamId                              the ID of the stream the attendee wants to join
   * @param processAttendeeRequestToJoinEventOrStreamDto the DTO containing the details of the attendee's request
   * @param user                                 the user who is processing the request
   * @return {@link ProcessAttendeeRequestToJoinStreamResponse} a response containing the result of processing the request
   * @throws FleenStreamNotFoundException if the event or stream cannot be found or does not exist
   */
  @Override
  @Transactional
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(final Long streamId, final ProcessAttendeeRequestToJoinEventOrStreamDto processAttendeeRequestToJoinEventOrStreamDto, final FleenUser user) {
    // Retrieve the stream using the provided stream ID
    final FleenStream stream = findStream(streamId);
    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);

    // Check if the user is already an attendee of the stream and process accordingly
    checkIfUserIsAlreadyAnAttendee(stream, Long.parseLong(processAttendeeRequestToJoinEventOrStreamDto.getAttendeeUserId()))
      .ifPresentOrElse(
        streamAttendee -> processAttendeeRequestToJoin(stream, streamAttendee, processAttendeeRequestToJoinEventOrStreamDto),
        () -> {}
      );

    // Return a localized response with the processed stream details
    return localizedResponse.of(ProcessAttendeeRequestToJoinStreamResponse.of(streamId, toFleenStreamResponse(stream)));
  }

  /**
   * Processes a pending request from a StreamAttendee to join an event or stream.
   *
   * <p>This method checks if the attendee's request status is pending. If it is,
   * the method updates the request status and any comments from the organizer.
   * If the request is approved, the attendee is then saved to the repository.</p>
   *
   * @param stream the event or stream the attendee is attempting to join
   * @param streamAttendee The StreamAttendee whose request is being processed.
   * @param processAttendeeRequestToJoinEventOrStreamDto The DTO containing the updated request status and comments.
   */
  protected void processAttendeeRequestToJoin(final FleenStream stream, final StreamAttendee streamAttendee, final ProcessAttendeeRequestToJoinEventOrStreamDto processAttendeeRequestToJoinEventOrStreamDto) {
    // Process the request if the attendee's status is pending
    if (streamAttendee.isRequestToJoinPending()) {
      // Update the attendee's request status and any organizer comments
      updateAttendeeRequestStatus(streamAttendee, processAttendeeRequestToJoinEventOrStreamDto);
      // If the attendee's request is approved, save the attendee
      if (processAttendeeRequestToJoinEventOrStreamDto.isApproved()) {
        streamAttendeeRepository.save(streamAttendee);
      }
    }

    // Create and save notification
    final Notification notification = notificationMessageService.ofApprovedOrDisapproved(streamAttendee.getFleenStream(), streamAttendee, stream.getMember());
    notificationService.save(notification);
  }

  /**
   * Deletes a stream based on the provided stream ID and user details.
   * The method validates the user's access and authorization, checks the event status,
   * updates the delete status of the stream, and deletes the associated live broadcast.
   *
   * @param streamId The ID of the stream to be deleted.
   * @param user The user requesting the deletion, who must be the creator of the event.
   * @return A localized response indicating the stream has been deleted.
   */
  @Override
  @Transactional
  public DeletedStreamResponse deleteStream(final Long streamId, final FleenUser user) {
    // Find the stream by its ID
    final FleenStream stream = findStream(streamId);
    // Retrieve the Oauth2 Authorization associated with the user
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.youTube(), user);
    // Validate if the user is the creator of the event
    validateCreatorOfEvent(stream, user);
    // Verify if stream is still ongoing
    verifyIfStreamIsOngoing(streamId, stream);
    // Update delete status of event
    stream.delete();
    fleenStreamRepository.save(stream);

    // Create a request to delete the calendar event
    final DeleteLiveBroadcastRequest deleteLiveBroadcastRequest = DeleteLiveBroadcastRequest.of(
      stream.getExternalId(),
      oauth2Authorization.getAccessToken()
    );
    // Reschedule the live broadcast using Live Stream API
    liveBroadcastUpdateService.deleteLiveBroadcast(deleteLiveBroadcastRequest);
    return localizedResponse.of(DeletedStreamResponse.of(streamId));
  }

  /**
   * Retrieves requests from attendees who want to join a specific stream.
   * This method delegates the request retrieval process to the event service,
   * utilizing the existing method to fetch attendee requests associated with events.
   *
   * @param streamId      The ID of the stream for which attendee requests are being retrieved.
   * @param searchRequest The search request object containing filters and sorting criteria for attendee requests.
   * @param user          The user making the request, typically the owner or an organizer of the stream.
   * @return              A RequestToJoinSearchResult containing the results of attendee requests to join the stream.
   */
  @Override
  public RequestToJoinSearchResult getAttendeeRequestsToJoinStream(final Long streamId, final StreamAttendeeSearchRequest searchRequest, final FleenUser user) {
    // Delegate the operation to the eventService to leverage existing event-attendee request functionality
    return eventService.getEventAttendeeRequestsToJoinEvent(streamId, searchRequest, user);
  }

  /**
   * Updates the visibility of a stream and synchronizes the changes with an external service.
   *
   * <p>This method finds a stream by its ID, validates the user's OAuth2 authorization, verifies
   * the stream details (ownership, event date, and status), checks if the stream is ongoing,
   * updates the stream visibility both locally and on an external service, and returns the
   * response with the updated details.</p>
   *
   * @param streamId The ID of the stream to be updated.
   * @param updateEventOrStreamVisibilityDto DTO containing the new visibility status for the stream.
   * @param user The user making the request to update the stream's visibility.
   * @return An {@link UpdateStreamVisibilityResponse} containing the stream ID and updated stream details.
   * @throws CannotCancelOrDeleteOngoingStreamException If the stream is still ongoing and cannot be updated.
   */
  @Override
  @Transactional
  public UpdateStreamVisibilityResponse updateStreamVisibility(final Long streamId, final UpdateEventOrStreamVisibilityDto updateEventOrStreamVisibilityDto, final FleenUser user) {
    // Find the stream by its ID
    final FleenStream stream = findStream(streamId);
    // Retrieve the Oauth2 Authorization associated with the user
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.youTube(), user);
    // Verify stream details like the owner, event date and active status of the event
    verifyStreamDetails(stream, user);
    // Verify if stream is still ongoing
    verifyIfStreamIsOngoing(streamId, stream);
    // Update the visibility of an event or stream
    stream.setStreamVisibility(updateEventOrStreamVisibilityDto.getActualVisibility());
    fleenStreamRepository.save(stream);

    // Create a request to update the event's visibility
    final UpdateLiveBroadcastVisibilityRequest updateCalendarEventVisibilityRequest = UpdateLiveBroadcastVisibilityRequest.of(
      oauth2Authorization.getAccessToken(),
      stream.getExternalId(),
      updateEventOrStreamVisibilityDto.getVisibility()
    );

    // Update the event visibility on the Google Calendar Service
    liveBroadcastUpdateService.updateStreamVisibility(updateCalendarEventVisibilityRequest);
    return localizedResponse.of(UpdateStreamVisibilityResponse.of(streamId, toFleenStreamResponse(stream)));
  }

  /**
   * Finds the attendees for a specified stream based on the provided search criteria.
   *
   * @param streamId The ID of the stream for which attendees are to be found.
   * @param searchRequest The search criteria for filtering stream attendees.
   * @return A {@code StreamAttendeeSearchResult} containing the details of the stream attendees that match the search criteria.
   */
  @Override
  public StreamAttendeeSearchResult findStreamAttendees(final Long streamId, final StreamAttendeeSearchRequest searchRequest) {
    return findEventOrStreamAttendees(streamId, searchRequest);
  }

  /**
   * Retrieves the attendees of a stream for a given event ID and user.
   *
   * @param streamId The ID of the event or stream for which to retrieve attendees.
   * @param searchRequest the search parameters and request use to filter the attendees to return
   * @param user The user requesting the attendee information.
   * @return An {@code EventOrStreamAttendeesResponse} containing the details of the stream attendees.
   */
  @Override
  public EventOrStreamAttendeesResponse getStreamAttendees(final Long streamId, final StreamAttendeeSearchRequest searchRequest, final FleenUser user) {
    return getEventOrStreamAttendees(streamId, searchRequest, searchRequest.youtubeLive());
  }

  /**
   * Counts the total number of events created by a user.
   *
   * <p>This method retrieves the total number of events created by a given user.
   * It uses the userFleenStreamRepository to count the total events and returns the count in a response object.</p>
   *
   * @param user the user whose events are to be counted
   * @return a response object containing the total number of events created by the user
   */
  @Override
  public TotalStreamsCreatedByUserResponse countTotalStreamsByUser(final FleenUser user) {
    final Long totalCount = userFleenStreamRepository.countTotalEventsByUser(user.toMember());
    return TotalStreamsCreatedByUserResponse.of(totalCount);
  }

  /**
   * Counts the total number of events attended by a user.
   *
   * <p>This method retrieves the total number of events attended by a given user.
   * It uses the userFleenStreamRepository to count the total events attended and returns the count in a response object.</p>
   *
   * @param user the user whose attended events are to be counted
   * @return a response object containing the total number of events attended by the user
   */
  @Override
  public TotalStreamsAttendedByUserResponse countTotalStreamsAttended(final FleenUser user) {
    final Long totalCount = userFleenStreamRepository.countTotalEventsAttended(user.toMember());
    return TotalStreamsAttendedByUserResponse.of(totalCount);
  }

  /**
   * Validates the expiry time of the access token for the specified OAuth2 service type and user,
   * or refreshes the token if necessary.
   *
   * @param oauth2ServiceType the type of OAuth2 service (e.g., Google, Facebook) to validate or refresh the token for
   * @param user the user whose access token is being validated or refreshed
   * @return an {@link Oauth2Authorization} object containing updated authorization details
   */
  public Oauth2Authorization validateAccessTokenExpiryTimeOrRefreshToken(final Oauth2ServiceType oauth2ServiceType, final FleenUser user) {
    return googleOauth2Service.validateAccessTokenExpiryTimeOrRefreshToken(oauth2ServiceType, user);
  }

  /**
   * Verifies and retrieves the OAuth2 authorization details for the specified FleenUser.
   *
   * <p>This method delegates to {@link #verifyAndGetUserOauth2Authorization(Member)}
   * using the Member representation of the provided FleenUser.</p>
   *
   * @param user The FleenUser whose OAuth2 authorization needs to be verified and retrieved.
   * @return The {@link Oauth2Authorization} entity associated with the specified FleenUser.
   * @throws Oauth2InvalidAuthorizationException If no valid OAuth2 authorization is found for the FleenUser.
   */
  public Oauth2Authorization verifyAndGetUserOauth2Authorization(final FleenUser user) {
    // Delegate to verifyAndGetUserOauth2Authorization(Member) method
    return verifyAndGetUserOauth2Authorization(user.toMember());
  }

  /**
   * Verifies and retrieves the OAuth2 authorization details for the specified member.
   *
   * <p>This method retrieves the {@link Oauth2Authorization} entity associated with the provided member
   * from the repository. If no authorization is found, an {@link Oauth2InvalidAuthorizationException} is thrown.
   * Otherwise, it returns the retrieved {@link Oauth2Authorization}.</p>
   *
   * @param member The member whose OAuth2 authorization needs to be verified and retrieved.
   * @return The {@link Oauth2Authorization} entity associated with the specified member.
   * @throws Oauth2InvalidAuthorizationException If no valid OAuth2 authorization is found for the member.
   */
  public Oauth2Authorization verifyAndGetUserOauth2Authorization(final Member member) {
    // Retrieve the OAuth2 authorization entity associated with the member
    final Optional<Oauth2Authorization> existingGoogleOauth2Authorization = oauth2AuthorizationRepository.findByMemberAndServiceType(member, Oauth2ServiceType.youTube());
    // Throw exception if no authorization is found
    checkIsTrue(existingGoogleOauth2Authorization.isEmpty(), Oauth2InvalidAuthorizationException.of(Oauth2ServiceType.youTube()));
    // Return the retrieved OAuth2 authorization
    return existingGoogleOauth2Authorization.get();
  }

  /**
   * Verifies if the specified stream is currently ongoing.
   *
   * <p>This method checks the ongoing status of the provided FleenStream instance.
   * If the stream is ongoing, an exception is thrown indicating that the stream
   * cannot be canceled or deleted.</p>
   *
   * @param streamId The ID of the stream being verified.
   * @param stream The FleenStream instance to check for its ongoing status.
   * @throws CannotCancelOrDeleteOngoingStreamException if the stream is ongoing.
   */
  protected void verifyIfStreamIsOngoing(final Long streamId, final FleenStream stream) {
    // Verify if the event or stream is still ongoing
    checkIsTrue(stream.isOngoing(), CannotCancelOrDeleteOngoingStreamException.of(streamId));
  }

}
