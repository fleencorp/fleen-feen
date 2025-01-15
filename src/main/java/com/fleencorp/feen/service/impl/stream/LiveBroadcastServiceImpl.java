package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.stream.base.*;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.feen.model.info.stream.StreamStatusInfo;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamVisibilityInfo;
import com.fleencorp.feen.model.request.stream.ExternalStreamRequest;
import com.fleencorp.feen.model.request.youtube.broadcast.*;
import com.fleencorp.feen.model.response.external.google.youtube.category.YouTubeCategoriesResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.base.*;
import com.fleencorp.feen.model.response.stream.common.live.broadcast.DataForCreateLiveBroadcastResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.oauth2.Oauth2AuthorizationRepository;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.service.external.google.oauth2.GoogleOauth2Service;
import com.fleencorp.feen.service.external.google.youtube.YouTubeChannelService;
import com.fleencorp.feen.service.impl.stream.update.LiveBroadcastUpdateService;
import com.fleencorp.feen.service.stream.LiveBroadcastService;
import com.fleencorp.feen.service.stream.common.StreamRequestService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.fleencorp.base.util.ExceptionUtil.checkIsTrue;
import static com.fleencorp.feen.service.impl.stream.base.StreamServiceImpl.*;
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
public class LiveBroadcastServiceImpl implements LiveBroadcastService, StreamRequestService {

  private final GoogleOauth2Service googleOauth2Service;
  private final StreamService streamService;
  private final LiveBroadcastUpdateService liveBroadcastUpdateService;
  private final YouTubeChannelService youTubeChannelService;
  private final Oauth2AuthorizationRepository oauth2AuthorizationRepository;
  private final FleenStreamRepository streamRepository;
  private final StreamMapper streamMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@link LiveBroadcastServiceImpl} with the specified dependencies.
   *
   * <p>This constructor is used to initialize the service, which handles live broadcast operations,
   * including integration with Google OAuth2 for authentication, managing streams, updating broadcasts,
   * and interacting with YouTube channels. The service also interacts with repositories to handle stream
   * data and OAuth2 authorizations, and maps stream data to appropriate response formats.</p>
   *
   * @param googleOauth2Service the service for managing Google OAuth2 authentication
   * @param streamService the service responsible for managing streams
   * @param liveBroadcastUpdateService the service used to update live broadcasts
   * @param youTubeChannelService the service for interacting with YouTube channels
   * @param streamRepository the repository for storing and retrieving stream data
   * @param oauth2AuthorizationRepository the repository for handling OAuth2 authorization data
   * @param localizer the service for generating localized responses
   * @param streamMapper the mapper used to convert stream data to response formats
   */
  public LiveBroadcastServiceImpl(
      final GoogleOauth2Service googleOauth2Service,
      final StreamService streamService,
      @Lazy final LiveBroadcastUpdateService liveBroadcastUpdateService,
      final YouTubeChannelService youTubeChannelService,
      final FleenStreamRepository streamRepository,
      final Oauth2AuthorizationRepository oauth2AuthorizationRepository,
      final Localizer localizer,
      final StreamMapper streamMapper) {
    this.googleOauth2Service = googleOauth2Service;
    this.streamService = streamService;
    this.liveBroadcastUpdateService = liveBroadcastUpdateService;
    this.youTubeChannelService = youTubeChannelService;
    this.oauth2AuthorizationRepository = oauth2AuthorizationRepository;
    this.streamRepository = streamRepository;
    this.streamMapper = streamMapper;
    this.localizer = localizer;
  }

  /**
   * Retrieves the necessary data for creating a live broadcast, including available timezones
   * and YouTube categories.
   *
   * <p>This method fetches the list of available timezones and assigns YouTube categories that can
   * be used during the live broadcast creation process. It then returns this data in the form of a
   * {@link DataForCreateLiveBroadcastResponse} containing the timezones and categories.</p>
   *
   * @return a {@link DataForCreateLiveBroadcastResponse} containing available timezones and YouTube categories
   */
  @Override
  public DataForCreateLiveBroadcastResponse getDataForCreateLiveBroadcast() {
    // Retrieve the available timezones from the system
    final Set<String> timezones = getAvailableTimezones();
    // Retrieve the list of YouTube categories
    final YouTubeCategoriesResponse categoriesResponse = youTubeChannelService.listAssignableCategories();
    // Return the response containing the details to create the live broadcast
    return DataForCreateLiveBroadcastResponse.of(timezones, categoriesResponse.getCategories());
  }

  /**
   * Creates a live broadcast stream on an external platform (e.g., YouTube Live Stream).
   *
   * <p>This method first checks if the user has a valid OAuth2 authorization. If the authorization is
   * valid, it creates a new {@link FleenStream} object based on the provided DTO, updates the stream's
   * details with the user's information, and increments the attendee count. The user is then registered
   * as an attendee for the live broadcast.</p>
   *
   * <p>Next, the method builds a request to create the live broadcast, calls an external service to
   * create the live broadcast on a platform like YouTube, and retrieves the response. Finally, the
   * method returns a localized response containing details about the created live broadcast stream,
   * including the stream ID, stream type info, and other relevant details.</p>
   *
   * @param createLiveBroadcastDto the DTO containing details for creating the live broadcast
   * @param user the user who is organizing the live broadcast
   * @return a {@link CreateStreamResponse} containing the details of the created live broadcast stream
   * @throws Oauth2InvalidAuthorizationException if the OAuth2 authorization is invalid or expired
   */
  @Override
  @Transactional
  public CreateStreamResponse createLiveBroadcast(final CreateLiveBroadcastDto createLiveBroadcastDto, final FleenUser user) throws Oauth2InvalidAuthorizationException {
    // Check if there is a valid OAuth2 authorization for the user
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(createLiveBroadcastDto.getOauth2ServiceType(), user);
    // Create a request object to create the live broadcast on YouTube
    final String organizerAliasOrDisplayName = createLiveBroadcastDto.getOrganizerAlias(user.getFullName());
    // Create a new FleenStream entity based on the DTO and set YouTube response details
    FleenStream stream = createLiveBroadcastDto.toFleenStream(user.toMember());
    // Update the stream details
    stream.updateDetails(
      organizerAliasOrDisplayName,
      user.getEmailAddress(),
      user.getPhoneNumber());

    // Increase attendees count, save the live broadcast and and add the stream in YouTube Live Stream
    stream = streamService.increaseTotalAttendeesOrGuestsAndSaveBecauseOfOrganizer(stream);
    // Register the organizer of the live broadcast as an attendee or guest
    streamService.registerAndApproveOrganizerOfStreamAsAnAttendee(stream, user);
    // Create and build the request to create a live broadcast
    final ExternalStreamRequest createStreamRequest = ExternalStreamRequest.ofCreateLiveBroadcast(stream, stream.getStreamType(), createLiveBroadcastDto, oauth2Authorization);
    // Create and add live broadcast or stream in external service
    createLiveBroadcastExternally(createStreamRequest);
    // Get the stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseApproved(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return the localized response of the created stream
    return localizer.of(CreateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse));
  }

  /**
   * Creates a live broadcast in an external service, such as the YouTube Live Stream API.
   *
   * <p>This method checks if the provided stream request is a live broadcast. If it is, it creates a
   * {@link CreateLiveBroadcastRequest} from the provided DTO, updates it with the necessary access token,
   * and then sends the request to create the live broadcast externally using an external service like
   * the YouTube Live Stream API.</p>
   *
   * @param createStreamRequest the request containing details about the stream and the live broadcast
   *                            to be created, including access token and broadcast information
   */
  protected void createLiveBroadcastExternally(final ExternalStreamRequest createStreamRequest) {
    if (createStreamRequest.isABroadcast() && createStreamRequest.isCreateLiveBroadcastRequest()) {
      // Create a request object for the live broadcast based on the dto
      final CreateLiveBroadcastRequest createLiveBroadcastRequest = CreateLiveBroadcastRequest.by(createStreamRequest.getCreateLiveBroadcastDto());
      // Update access token needed to perform request
      createLiveBroadcastRequest.updateToken(createStreamRequest.accessToken());
      // Create and add live broadcast or stream in external service for example YouTube Live Stream API
      liveBroadcastUpdateService.createLiveBroadcastAndStream(createStreamRequest.getStream(), createLiveBroadcastRequest);
    }
  }

  /**
   * Updates an existing live broadcast stream, both in the local system and in an external service (e.g., YouTube).
   *
   * <p>This method retrieves the live broadcast stream based on the provided stream ID, validates that the user is
   * the creator of the stream, and checks the OAuth2 authorization for the user. The stream's details, such as title,
   * description, tags, and location, are updated accordingly. The updated stream is then saved locally and updated on
   * an external platform using a patch request.</p>
   *
   * <p>The method returns a localized response containing details about the updated stream, including the stream ID,
   * stream type info, and other relevant stream data.</p>
   *
   * @param liveBroadcastId the ID of the live broadcast stream to be updated
   * @param updateStreamDto the DTO containing the new details for the live broadcast
   * @param user            the user who is attempting to update the stream
   * @return a {@link UpdateStreamResponse} containing details of the updated stream
   * @throws Oauth2InvalidAuthorizationException if the user’s OAuth2 authorization is invalid or expired
   */
  @Override
  @Transactional
  public UpdateStreamResponse updateLiveBroadcast(final Long liveBroadcastId, final UpdateStreamDto updateStreamDto, final FleenUser user)
      throws Oauth2InvalidAuthorizationException {
    // Find the stream by its ID
    FleenStream stream = streamService.findStream(liveBroadcastId);
    // Verify if the stream's type is the same as the stream type of the request
    isStreamTypeEqual(stream.getStreamType(), updateStreamDto.getStreamType());
    // Validate if the user is the creator of the live broadcast
    validateCreatorOfStream(stream, user);
    // Check if the OAuth2 authorization exists for the user
    final Oauth2Authorization oauth2Authorization = verifyAndGetUserOauth2Authorization(user);
    // Update the stream entity with new title, description, tags, and location
    stream.update(
      updateStreamDto.getTitle(),
      updateStreamDto.getDescription(),
      updateStreamDto.getTags(),
      updateStreamDto.getLocation()
    );

    // Save the updated stream to the repository
    stream = streamRepository.save(stream);
    // Create and build patch request
    final ExternalStreamRequest patchStreamRequest = createPatchStreamRequest(oauth2Authorization, stream, updateStreamDto);
    // Patch or update the stream externally
    patchStreamExternally(patchStreamRequest);
    // Get the stream response
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponse(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response of the updated stream
    return localizer.of(UpdateStreamResponse.of(stream.getStreamId(), streamTypeInfo, streamResponse));
  }

  /**
   * Patches or updates an existing live broadcast stream on an external platform (e.g., YouTube Live Stream).
   *
   * <p>This method checks if the provided request is related to a broadcast and, if so, creates a request
   * to update the live broadcast's details using the provided access token, title, description, and stream
   * external ID. It then calls an external service to apply the updates to the live broadcast stream on a
   * platform like YouTube.</p>
   *
   * @param patchStreamRequest the request containing the details to update the live broadcast
   */
  protected void patchStreamExternally(final ExternalStreamRequest patchStreamRequest) {
    if (patchStreamRequest.isABroadcast() && patchStreamRequest.isPatchRequest()) {
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
   * Reschedules a live broadcast stream by updating its schedule details and external service.
   *
   * <p>This method retrieves a {@link FleenStream} entity based on the provided stream ID, validates if the user
   * is the creator of the stream, and updates the stream's schedule with the new start and end times, as well as
   * the timezone. It then saves the updated stream to the repository and sends a request to an external service
   * (e.g., YouTube) to reschedule the live broadcast. The method returns a localized response containing the updated
   * stream details.</p>
   *
   * @param liveBroadcastId the ID of the live broadcast stream to be rescheduled
   * @param rescheduleStreamDto the DTO containing the new schedule details (start time, end time, timezone)
   * @param user the {@link FleenUser} making the request, used for authorization and validation
   * @return a {@link RescheduleStreamResponse} containing the updated stream details
   * @throws FleenStreamNotFoundException if the stream with the given ID is not found
   * @throws Oauth2InvalidAuthorizationException if the OAuth2 authorization is invalid or expired
   */
  @Override
  @Transactional
  public RescheduleStreamResponse rescheduleLiveBroadcast(final Long liveBroadcastId, final RescheduleStreamDto rescheduleStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, Oauth2InvalidAuthorizationException {
    // Retrieve the FleenStream entity from the repository based on the stream ID
    final FleenStream stream = streamService.findStream(liveBroadcastId);
    // Verify if the stream's type is the same as the stream type of the request
    isStreamTypeEqual(stream.getStreamType(), rescheduleStreamDto.getStreamType());
    // Validate if the user is the creator of the live broadcast
    validateCreatorOfStream(stream, user);
    // Retrieve the Oauth2 Authorization associated with the user
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.youTube(), user);
    // Update the schedule of the FleenStream entity with new start and end times and timezone
    stream.updateSchedule(
      rescheduleStreamDto.getStartDateTime(),
      rescheduleStreamDto.getEndDateTime(),
      rescheduleStreamDto.getTimezone()
    );

    // Save the rescheduled stream to the repository
    streamRepository.save(stream);
    // Create the reschedule stream request
    final ExternalStreamRequest rescheduleStreamRequest = createRescheduleStreamRequest(oauth2Authorization, stream, rescheduleStreamDto);
    // Reschedule the stream using an external service
    rescheduleStreamExternally(rescheduleStreamRequest);
    // Convert the stream to the equivalent stream response whether live broadcast or live broadcast or live stream
    final FleenStreamResponse streamResponse = streamMapper.toFleenStreamResponseNoJoinStatus(stream);
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return the localized response of the Reschedule
    return localizer.of(RescheduleStreamResponse.of(liveBroadcastId, streamResponse, streamTypeInfo));
  }

  /**
   * Reschedules a live broadcast (e.g., on YouTube) based on the provided updated schedule details.
   *
   * <p>This method creates a {@link RescheduleLiveBroadcastRequest} using the provided details from the
   * {@link ExternalStreamRequest} (including the start and end date/times). It then uses
   * {@link #liveBroadcastUpdateService} to perform the rescheduling operation in the external service,
   * such as the YouTube Live Stream API.</p>
   *
   * @param rescheduleStreamRequest the request containing updated details for rescheduling the stream's live broadcast
   */
  protected void rescheduleStreamExternally(final ExternalStreamRequest rescheduleStreamRequest) {
    if (rescheduleStreamRequest.isABroadcast() && rescheduleStreamRequest.isRescheduleRequest()) {
      // Create a request object to reschedule the live broadcast on the external service
      final RescheduleLiveBroadcastRequest rescheduleLiveBroadcastRequest = RescheduleLiveBroadcastRequest.of(
        rescheduleStreamRequest.accessToken(),
        rescheduleStreamRequest.getStartDateTime(),
        rescheduleStreamRequest.getEndDateTime(),
        null,
        rescheduleStreamRequest.streamExternalId()
      );

      // Reschedule the live broadcast using an external service
      liveBroadcastUpdateService.rescheduleLiveBroadcastAndStream(rescheduleStreamRequest.getStream(), rescheduleLiveBroadcastRequest);
    }
  }

  /**
   * Deletes a live broadcast stream, updates its status, and performs the deletion operation externally.
   *
   * <p>This method retrieves the stream by its ID and validates the stream's details, including ownership, active status,
   * and whether the stream is ongoing. If the stream is valid and not ongoing, it marks the stream as deleted, saves the
   * updated stream, and performs the external deletion operation. Finally, it returns a localized response containing the
   * result of the deletion process.</p>
   *
   * <p>The method may throw several exceptions. A {@link FleenStreamNotFoundException} is thrown if the stream with the
   * given ID does not exist. An {@link Oauth2InvalidAuthorizationException} is thrown if the OAuth2 authorization is invalid
   * or expired. If the stream was not created by the provided user, a {@link StreamNotCreatedByUserException} is thrown.
   * If the stream has already occurred, a {@link StreamAlreadyHappenedException} will be thrown. If the stream has already
   * been canceled, a {@link StreamAlreadyCanceledException} will be thrown. A {@link CannotCancelOrDeleteOngoingStreamException}
   * is thrown if the stream is ongoing and cannot be deleted. If the deletion operation fails for any reason, a
   * {@link FailedOperationException} will be thrown.</p>
   *
   * @param liveBroadcastId the ID of the live broadcast stream to be deleted
   * @param deleteStreamDto the dto containing the deletion details
   * @param user the {@link FleenUser} requesting the deletion
   * @return a {@link DeleteStreamResponse} containing details of the deleted stream
   * @throws FleenStreamNotFoundException if the stream with the given ID does not exist
   * @throws Oauth2InvalidAuthorizationException if the OAuth2 authorization is invalid or expired
   * @throws StreamNotCreatedByUserException if the stream was not created by the provided user
   * @throws CannotCancelOrDeleteOngoingStreamException if the stream is ongoing and cannot be deleted
   * @throws FailedOperationException if the deletion operation fails
   */
  @Override
  @Transactional
  public DeleteStreamResponse deleteLiveBroadcast(final Long liveBroadcastId, final DeleteStreamDto deleteStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, Oauth2InvalidAuthorizationException, StreamNotCreatedByUserException,
        StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(liveBroadcastId);
    // Verify if the stream's type is the same as the stream type of the request
    isStreamTypeEqual(stream.getStreamType(), deleteStreamDto.getStreamType());
    // Retrieve the Oauth2 Authorization associated with the user
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.youTube(), user);
    // Validate if the user is the creator of the live broadcast
    validateCreatorOfStream(stream, user);
    // Verify if stream is still ongoing
    verifyIfStreamIsOngoing(liveBroadcastId, stream);
    // Update delete status of live broadcast
    stream.delete();
    // Save the stream
    streamRepository.save(stream);

    // Create a request to delete the live broadcast
    final ExternalStreamRequest deleteStreamRequest = createDeleteStreamRequest(stream, oauth2Authorization);
    // Reschedule the live broadcast using an external service
    deleteStreamExternally(deleteStreamRequest);
    // Get the deleted info
    final IsDeletedInfo deletedInfo = streamMapper.toIsDeletedInfo(stream.isDeleted());
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response of the deleted stream
    return localizer.of(DeleteStreamResponse.of(liveBroadcastId, streamTypeInfo, deletedInfo));
  }

  /**
   * Cancels a live broadcast and updates the stream status, performing external cancellation operations.
   *
   * <p>This method retrieves the stream by its ID, verifies the stream's details (such as ownership and
   * active status), and checks if the stream is ongoing. If valid, it updates the stream's status to "canceled",
   * saves the updated stream, and performs the cancellation externally. Finally, it returns a localized response
   * indicating the cancellation result.</p>
   *
   * <p>The method may throw various exceptions if the stream is not found, the user is not the creator,
   * the stream is already canceled or has already occurred, or if the cancellation operation fails.</p>
   *
   * @param broadcastId the ID of the stream to be canceled
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
  public CancelStreamResponse cancelLiveBroadcast(final Long broadcastId, final CancelStreamDto cancelStreamDto, final FleenUser user)
      throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
        StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException, FailedOperationException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(broadcastId);
    // Verify if the stream's type is the same as the stream type of the request
    isStreamTypeEqual(stream.getStreamType(), cancelStreamDto.getStreamType());
    // Verify stream details like the owner, stream date and active status of the stream
    verifyStreamDetails(stream, user);
    // Verify if the stream is still ongoing
    verifyIfStreamIsOngoing(broadcastId, stream);
    // Update stream status to canceled
    stream.cancel();
    // Save the stream to the repository
    streamRepository.save(stream);
    // Convert the stream status to info
    final StreamStatusInfo statusInfo = streamMapper.toStreamStatusInfo(stream.getStreamStatus());
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response of the cancellation
    return localizer.of(CancelStreamResponse.of(broadcastId, statusInfo, streamTypeInfo));
  }


  /**
   * Deletes a live broadcast stream externally, such as from YouTube.
   *
   * <p>This method checks if the stream is a broadcast and, if so, creates a request to delete the associated live
   * broadcast on an external service (e.g., YouTube). It then calls the external service to perform the deletion.</p>
   *
   * @param deleteStreamRequest the {@link ExternalStreamRequest} containing the details of the stream to be deleted,
   *                             including the external stream ID and OAuth2 access token
   */
  protected void deleteStreamExternally(final ExternalStreamRequest deleteStreamRequest) {
    if (deleteStreamRequest.isABroadcast() && deleteStreamRequest.isDeleteRequest()) {
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
   * Updates the visibility of a stream and synchronizes the changes with an external service.
   *
   * <p>This method finds a stream by its ID, validates the user's OAuth2 authorization, verifies
   * the stream details (ownership, stream date, and status), checks if the stream is ongoing,
   * updates the stream visibility both locally and on an external service, and returns the
   * response with the updated details.</p>
   *
   * @param liveBroadcastId The ID of the stream to be updated.
   * @param updateStreamVisibilityDto DTO containing the new visibility status for the stream.
   * @param user The user making the request to update the stream's visibility.
   * @return An {@link UpdateStreamVisibilityResponse} containing the stream ID and updated stream details.
   * @throws CannotCancelOrDeleteOngoingStreamException If the stream is still ongoing and cannot be updated.
   */
  @Override
  @Transactional
  public UpdateStreamVisibilityResponse updateLiveBroadcastVisibility(final Long liveBroadcastId, final UpdateStreamVisibilityDto updateStreamVisibilityDto, final FleenUser user)
      throws FleenStreamNotFoundException, Oauth2InvalidAuthorizationException {
    // Find the stream by its ID
    final FleenStream stream = streamService.findStream(liveBroadcastId);
    // Verify if the stream's type is the same as the stream type of the request
    isStreamTypeEqual(stream.getStreamType(), updateStreamVisibilityDto.getStreamType());
    // Retrieve the Oauth2 Authorization associated with the user
    final Oauth2Authorization oauth2Authorization = validateAccessTokenExpiryTimeOrRefreshToken(Oauth2ServiceType.youTube(), user);
    // Verify stream details like the owner, stream date and active status of the stream
    verifyStreamDetails(stream, user);
    // Verify if stream is still ongoing
    verifyIfStreamIsOngoing(liveBroadcastId, stream);
    // Update the visibility of the stream
    stream.setStreamVisibility(updateStreamVisibilityDto.getActualVisibility());
    // Save the stream
    streamRepository.save(stream);

    // Create request to update stream visibility
    final ExternalStreamRequest updateStreamVisibilityRequest = createUpdateStreamVisibilityRequest(oauth2Authorization, stream, updateStreamVisibilityDto.getVisibility());
    // Update the stream visibility using an external service
    updateStreamVisibilityExternally(updateStreamVisibilityRequest);

    // Retrieve the stream visibility information
    final StreamVisibilityInfo streamVisibility = streamMapper.toStreamVisibilityInfo(stream.getStreamVisibility());
    // Retrieve the stream type info
    final StreamTypeInfo streamTypeInfo = streamMapper.toStreamTypeInfo(stream.getStreamType());
    // Return a localized response of the updated stream with its visibility
    return localizer.of(UpdateStreamVisibilityResponse.of(liveBroadcastId, streamVisibility, streamTypeInfo));
  }

  protected void updateStreamVisibilityExternally(final ExternalStreamRequest updateStreamVisibilityRequest) {
    if (updateStreamVisibilityRequest.isABroadcast() && updateStreamVisibilityRequest.isVisibilityUpdateRequest()) {
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

}
