package com.fleencorp.feen.service.impl.stream.speaker;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.event.publisher.StreamEventPublisher;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.exception.stream.speaker.OrganizerOfStreamCannotBeRemovedAsSpeakerException;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.mapper.impl.speaker.StreamSpeakerMapperImpl;
import com.fleencorp.feen.mapper.stream.speaker.StreamSpeakerMapper;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.event.CreateEventDto.EventAttendeeOrGuest;
import com.fleencorp.feen.model.dto.stream.base.RemoveStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.MarkAsStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.event.AddCalendarEventAttendeesEvent;
import com.fleencorp.feen.model.info.stream.attendee.IsASpeakerInfo;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeInfoSelect;
import com.fleencorp.feen.model.request.search.stream.StreamSpeakerSearchRequest;
import com.fleencorp.feen.model.response.stream.speaker.MarkAsStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.RemoveStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.StreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.UpdateStreamSpeakerResponse;
import com.fleencorp.feen.model.search.stream.speaker.StreamSpeakerSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.speaker.StreamSpeakerRepository;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.stream.StreamOperationsService;
import com.fleencorp.feen.service.stream.attendee.StreamAttendeeOperationsService;
import com.fleencorp.feen.service.stream.speaker.StreamSpeakerService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.ExceptionUtil.checkIsTrue;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus.*;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link StreamSpeakerService} interface for managing stream speakers.
 *
 * <p>This class provides functionalities to add, update, delete, and retrieve speakers
 * for a given stream. It utilizes repositories to interact with stream and
 * speaker data and provides localized responses.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class StreamSpeakerServiceImpl implements StreamSpeakerService {

  private final MiscService miscService;
  private final StreamOperationsService streamOperationsService;
  private final StreamAttendeeOperationsService streamAttendeeOperationsService;
  private final StreamSpeakerRepository streamSpeakerRepository;
  private final StreamEventPublisher streamEventPublisher;
  private final StreamSpeakerMapper streamSpeakerMapper;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code StreamSpeakerServiceImpl}, which manages operations related to
   * stream speakers including assignment, removal, and retrieval of speaker data.
   *
   * @param miscService utility service for miscellaneous helper operations
   * @param streamOperationsService the service responsible for core stream lifecycle operations
   * @param streamAttendeeOperationsService service managing attendees within streams
   * @param streamSpeakerRepository the repository for accessing and managing stream speaker data
   * @param streamEventPublisher event publisher for emitting stream-related events
   * @param streamSpeakerMapper mapper used to convert between stream speaker entities and DTOs
   * @param unifiedMapper general-purpose mapper for consistent DTO transformation
   * @param localizer utility for resolving localized messages and responses
   */
  public StreamSpeakerServiceImpl(
      final MiscService miscService,
      final StreamOperationsService streamOperationsService,
      final StreamAttendeeOperationsService streamAttendeeOperationsService,
      final StreamSpeakerRepository streamSpeakerRepository,
      final StreamEventPublisher streamEventPublisher,
      final StreamSpeakerMapperImpl streamSpeakerMapper,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.miscService = miscService;
    this.streamOperationsService = streamOperationsService;
    this.streamAttendeeOperationsService = streamAttendeeOperationsService;
    this.streamSpeakerRepository = streamSpeakerRepository;
    this.streamEventPublisher = streamEventPublisher;
    this.streamSpeakerMapper = streamSpeakerMapper;
    this.unifiedMapper = unifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Searches for speakers based on the provided search criteria.
   *
   * @param searchRequest the search request containing filtering criteria and pagination information
   * @param user the authenticated user who might be the owner of a stream
   * @return a StreamSpeakerSearchResult containing the list of speakers matching the search criteria
   */
  @Override
  public StreamSpeakerSearchResult findSpeakers(final Long streamId, final StreamSpeakerSearchRequest searchRequest, final FleenUser user) {
    // Retrieve the stream with the given ID
    final FleenStream stream = streamOperationsService.findStream(streamId);
    // Validate if the user is the creator of the stream
    stream.checkIsOrganizer(user.getId());
    // Extract the name, full name, username, or email address from the search request
    final String fullNameOrUsername = searchRequest.getUserIdOrName();
    // Retrieve a paginated list of Member entities matching the search criteria
    final Page<StreamAttendeeInfoSelect> page = streamAttendeeOperationsService.findPotentialAttendeeSpeakersByStreamAndFullNameOrUsername(streamId, user.getId(), fullNameOrUsername, searchRequest.getPage());
    // Convert the retrieved Member entities to a list of StreamSpeakerResponse DTOs
    final List<StreamSpeakerResponse> speakerResponses = streamSpeakerMapper.toStreamSpeakerResponsesByProjection(page.getContent());
    // Create the search result
    final SearchResult searchResult = toSearchResult(speakerResponses, page);
    // Create the search result
    final StreamSpeakerSearchResult streamSpeakerSearchResult = StreamSpeakerSearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
    return localizer.of(streamSpeakerSearchResult);
  }

  /**
   * Searches for stream speakers based on the provided stream ID and search request.
   *
   * <p>This method fetches all {@link StreamSpeaker} entities associated with the specified stream ID,
   * paginates the results according to the given {@code StreamSpeakerSearchRequest}, and converts
   * them into a list of {@code StreamSpeakerResponse} DTOs. The result is returned as a localized
   * {@code StreamSpeakerSearchResult}. If no speakers are found, an empty result is returned.</p>
   *
   * @param streamId the ID of the stream for which speakers are being searched
   * @param searchRequest the search request object containing pagination details
   * @param user the authenticated user who might be the owner of a stream
   * @return a {@code StreamSpeakerSearchResult} containing a list of speakers, or an empty result if no speakers are found
   */
  @Override
  public StreamSpeakerSearchResult findStreamSpeakers(final Long streamId, final StreamSpeakerSearchRequest searchRequest, final FleenUser user) {
    // Set default number of speakers to retrieve
    searchRequest.setDefaultPageSize();
    // Retrieve the stream with the given ID
    final FleenStream stream = streamOperationsService.findStream(streamId);
    // Validate if the user is the creator of the stream
    stream.checkIsOrganizer(user.getId());
    // Fetch all StreamSpeaker entities associated with the given stream ID
    final Page<StreamSpeaker> page = streamSpeakerRepository.findAllByStream(FleenStream.of(streamId), searchRequest.getPage());
    // Filter out the organizer using a separate method
    final List<StreamSpeaker> filteredSpeakers = filterOutOrganizer(page.getContent(), stream.getOrganizerId());
    // Convert the retrieved StreamSpeaker entities to a set of StreamSpeakerResponse DTOs
    final List<StreamSpeakerResponse> speakerResponses = streamSpeakerMapper.toStreamSpeakerResponses(filteredSpeakers);
    // Create the search result
    final SearchResult searchResult = toSearchResult(speakerResponses, page);
    // Create the search result
    final StreamSpeakerSearchResult streamSpeakerSearchResult = StreamSpeakerSearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
    return localizer.of(streamSpeakerSearchResult);
  }

  /**
   * Marks the specified attendees as speakers for the given stream.
   *
   * <p>
   * This method retrieves the stream by its ID and validates that the provided user is the organizer of the stream.
   * It then converts the provided {@code MarkAsStreamSpeakerDto} into a set of {@code StreamSpeaker} objects and
   * performs validation to ensure that all attendee IDs linked to the speakers exist. Additionally, it checks if any
   * of the speakers are not attendees and sends invitations if necessary. Finally, it saves the speakers to the repository
   * and returns a localized response.
   * </p>
   *
   * @param streamId the ID of the stream where the speakers will be added
   * @param dto the {@code MarkAsStreamSpeakerDto} containing the speakers' information
   * @param user the {@code FleenUser} who must be the organizer of the stream
   * @return a localized {@code MarkAsStreamSpeakerResponse} indicating the success of the operation
   * @throws StreamNotFoundException if the stream with the specified ID cannot be found
   * @throws StreamNotCreatedByUserException if the user is not the organizer of the stream
   * @throws FailedOperationException if the attendee IDs associated with the speakers are invalid
   */
  @Override
  @Transactional
  public MarkAsStreamSpeakerResponse markAsSpeaker(final Long streamId, final MarkAsStreamSpeakerDto dto, final FleenUser user)
      throws StreamNotFoundException, StreamNotCreatedByUserException, FailedOperationException {
    // Retrieve the stream with the given ID
    final FleenStream stream = streamOperationsService.findStream(streamId);
    // Validate if the user is the creator of the stream
    stream.checkIsOrganizer(user.getId());
    // Convert the DTO to a set of StreamSpeaker objects linked to the specified stream
    final Set<StreamSpeaker> speakers = dto.toStreamSpeakers(stream);

    // Validate that all attendee IDs associated with the speakers exist
    checkIfNonNullAttendeeIdsExists(speakers);
    // Process the speakers by checking for existing entries in the database
    final Set<StreamSpeaker> updatedSpeakers = new HashSet<>();
    // Check if a user is already a speaker and update their details or info
    checkIfSpeakerExistsAndUpdateInfoOrAddNewSpeaker(speakers, stream, updatedSpeakers);

    // Set the member id for each speaker
    setMemberIdsForSpeakers(updatedSpeakers);
    // Save all the speakers to the repository
    streamSpeakerRepository.saveAll(updatedSpeakers);
    // Mark the speakers as attendees
    markAttendeesAsSpeaker(updatedSpeakers);

    // Check if speakers are not already attendees and send invitations if needed
    checkIfSpeakerIsNotAnAttendeeAndSendInvitation(stream, speakers);
    // Create the is a speaker information
    final IsASpeakerInfo isASpeakerInfo = unifiedMapper.toIsASpeakerInfo(true);
    // Create the response
    final MarkAsStreamSpeakerResponse markResponse = MarkAsStreamSpeakerResponse.of(isASpeakerInfo);
    // Create and return the response
    return localizer.of(markResponse);
  }

  /**
   * Updates the speakers for a specified stream.
   *
   * <p>This method first checks if the stream with the given ID exists. It then converts
   * the provided {@link UpdateStreamSpeakerDto} into a set of {@link StreamSpeaker} objects linked
   * to the specified stream. The method ensures that all attendee IDs associated with the speakers
   * exist in the repository. If the validation is successful, it saves the updated speakers to the repository.</p>
   *
   * @param streamId the ID of the stream to update speakers for
   * @param dto the {@link UpdateStreamSpeakerDto} containing the speaker information to update
   * @param user the {@link FleenUser} performing the update operation
   * @return an {@link UpdateStreamSpeakerResponse} indicating the outcome of the update
   * @throws StreamNotFoundException if the stream with the given ID does not exist
   * @throws StreamNotCreatedByUserException if the user is not the organizer of the stream
   * @throws FailedOperationException if the attendee ID validation fails
   */
  @Override
  @Transactional
  public UpdateStreamSpeakerResponse updateSpeakers(final Long streamId, final UpdateStreamSpeakerDto dto, final FleenUser user)
      throws StreamNotFoundException, StreamNotCreatedByUserException, FailedOperationException {
    // Retrieve the stream with the given ID
    final FleenStream stream = streamOperationsService.findStream(streamId);
    // Validate if the user is the organizer of the stream
    stream.checkIsOrganizer(user.getId());
    // Convert the DTOs to a set of StreamSpeakers, associating them with the specified stream
    final Set<StreamSpeaker> newSpeakers = dto.toStreamSpeakers(FleenStream.of(streamId));
    // Ensure all attendee IDs in the speakers are valid and exist
    checkIfNonNullAttendeeIdsExists(newSpeakers);
    // Process the speakers by checking for existing entries in the database
    final Set<StreamSpeaker> updatedSpeakers = new HashSet<>();
    // Check if a user is already a speaker and update their details or info
    checkIfSpeakerExistsAndUpdateInfoOrAddNewSpeaker(newSpeakers, stream, updatedSpeakers);
    // Check if speakers are not already attendees and send invitations if needed
    checkIfSpeakerIsNotAnAttendeeAndSendInvitation(stream, updatedSpeakers);

    // Set the member id for each speaker
    setMemberIdsForSpeakers(updatedSpeakers);
    // Check if the list or set of speakers is not empty
    if (!updatedSpeakers.isEmpty()) {
      // Save the updated speakers to the repository
      streamSpeakerRepository.saveAll(updatedSpeakers);
    }
    // Mark the speakers as attendees
    markAttendeesAsSpeaker(updatedSpeakers);

    // Convert the retrieved Member entities to a list of StreamSpeakerResponse DTOs
    final List<StreamSpeakerResponse> views = streamSpeakerMapper.toStreamSpeakerResponses(new ArrayList<>(newSpeakers));
    // Create the response
    final UpdateStreamSpeakerResponse updateStreamSpeakerResponse = UpdateStreamSpeakerResponse.of(views);
    // Return a response indicating that the speakers have been successfully updated
    return localizer.of(updateStreamSpeakerResponse);
  }

  /**
   * Checks if each speaker in the provided set of new speakers already exists and updates their information or adds them as a new speaker.
   *
   * <p>This method iterates through the {@code newSpeakers} set, checking whether each speaker has a speaker ID. If a speaker has
   * an ID, their existing information is updated using the {@code updateExistingSpeaker} method. If a speaker does not have an ID,
   * they are added as a new speaker using the {@code addNewSpeaker} method. All updated or newly added speakers are added to the
   * {@code updatedSpeakers} set.</p>
   *
   * @param newSpeakers    the set of new speakers to be checked and processed
   * @param stream         the stream to which the speakers are associated
   * @param updatedSpeakers the set of speakers that have been updated or newly added
   */
  protected void checkIfSpeakerExistsAndUpdateInfoOrAddNewSpeaker(final Set<StreamSpeaker> newSpeakers, final FleenStream stream, final Set<StreamSpeaker> updatedSpeakers) {
    newSpeakers.forEach(newSpeaker -> {
      if (newSpeaker.hasSpeakerId()) {
        updateExistingSpeaker(newSpeaker, stream, updatedSpeakers);
      } else {
        addNewSpeaker(newSpeaker, stream, updatedSpeakers);
      }
    });
  }

  /**
   * Updates an existing speaker for the given stream and adds them to the set of updated speakers.
   *
   * <p>This method looks for an existing speaker in the repository based on the provided speaker id and the associated {@code stream}.
   * If the speaker is found, their details (full name, title, and description) are updated
   * with the values from the {@code newSpeaker}, and the updated speaker is added to the {@code updatedSpeakers} set.</p>
   *
   * <p>If a speaker with the ID is not found, a new speaker is created and is added to the {@code updatedSpeakers} set</p>
   *
   * @param newSpeaker      the speaker with updated details to be applied to the existing speaker
   * @param stream          the stream to which the speaker belongs
   * @param updatedSpeakers the set of updated speakers, which will include the updated speaker if found
   */
  private void updateExistingSpeaker(final StreamSpeaker newSpeaker, final FleenStream stream, final Set<StreamSpeaker> updatedSpeakers) {
    final Optional<StreamSpeaker> existingStreamSpeaker = streamSpeakerRepository.findBySpeakerIdAndStream(newSpeaker, stream);

    if (existingStreamSpeaker.isPresent()) {
      final StreamSpeaker speaker = existingStreamSpeaker.get();
      speaker.update(newSpeaker.getFullName(), newSpeaker.getTitle(), newSpeaker.getDescription());
      addToUpdatedSpeakers(speaker, updatedSpeakers);
    } else {
      addNewSpeaker(newSpeaker, stream, updatedSpeakers);
    }
  }

  /**
   * Associates a new speaker with the stream and adds them to the set of updated speakers.
   *
   * <p>This method sets the given {@code newSpeaker} to the specified {@code stream}, and then adds the speaker
   * to the {@code updatedSpeakers} set.</p>
   *
   * @param newSpeaker      the speaker to be associated with the stream and added to the updated speakers set
   * @param stream          the stream to which the new speaker will be assigned
   * @param updatedSpeakers the set of updated speakers, which will include the new speaker
   */
  private void addNewSpeaker(final StreamSpeaker newSpeaker, final FleenStream stream, final Set<StreamSpeaker> updatedSpeakers) {
    newSpeaker.setStream(stream);
    addToUpdatedSpeakers(newSpeaker, updatedSpeakers);
  }

  /**
   * Adds a speaker to the set of updated speakers if the set is not null.
   *
   * <p>This method checks if the {@code updatedSpeakers} set is non-null.
   * If so, the given {@code speaker} is added to the set.</p>
   *
   * @param speaker         the speaker to be added to the updated speakers set
   * @param updatedSpeakers the set of updated speakers, which will include the given speaker if it's non-null
   */
  private void addToUpdatedSpeakers(final StreamSpeaker speaker, final Set<StreamSpeaker> updatedSpeakers) {
    if (nonNull(updatedSpeakers)) {
      updatedSpeakers.add(speaker);
    }
  }

  /**
   * Removed the specified speakers from a given stream.
   *
   * @param streamId The ID of the stream from which speakers are to be deleted.
   * @param dto A {@link RemoveStreamSpeakerDto} containing the details of the speakers to be deleted.
   * @param user The user performing the delete operation.
   * @return A {@link RemoveStreamSpeakerResponse} indicating the result of the delete operation.
   * @throws StreamNotFoundException if the stream with the given ID does not exist
   * @throws OrganizerOfStreamCannotBeRemovedAsSpeakerException if the organizer is found in the list of speakers
   */
  @Override
  @Transactional
  public RemoveStreamSpeakerResponse removeSpeakers(final Long streamId, final RemoveStreamSpeakerDto dto, final FleenUser user)
      throws StreamNotFoundException, OrganizerOfStreamCannotBeRemovedAsSpeakerException {
    // Retrieve the stream with the given ID
    final FleenStream stream = streamOperationsService.findStream(streamId);
    // Validate if the user is the creator of the stream
    stream.checkIsOrganizer(user.getId());
    // Get all stream speakers ids from dto
    final Set<Long> speakersIds = dto.toSpeakerIds();
    // Fetch the managed speakers from the database
    final Set<StreamSpeaker> speakers = streamSpeakerRepository.findAllByIds(speakersIds);

    // Verify organizer is not part of the speakers to be removed
    checkOrganizerOfStreamCannotBeRemovedAsSpeaker(stream, stream.getOrganizer(), speakers);

    // Check if the list or set of speakers is not empty
    if (!speakers.isEmpty()) {
      // Fetch the associated attendees and mark them as non-speakers
      markAttendeesAsNonSpeakers(speakers);
      // Delete all speakers requested
      streamSpeakerRepository.deleteAll(speakers);
    }

    return localizer.of(RemoveStreamSpeakerResponse.of());
  }

  /**
   * Validates that all non-null attendee IDs in the given set of speakers exist in the repository.
   *
   * <p>This method checks if the provided set of {@code speakers} is non-null and not empty.
   * It extracts the attendee IDs from each speaker and verifies if all of them exist in the
   * repository. If the count of found attendee does not match the number of speakers, an exception
   * is thrown.</p>
   *
   * @param speakers the set of {@link StreamSpeaker} objects to be checked
   * @throws FailedOperationException if the count of found members does not match the number of speakers
   */
  protected void checkIfNonNullAttendeeIdsExists(final Set<StreamSpeaker> speakers) {
    // Check if the speakers set is non-null and not empty
    if (nonNull(speakers) && !speakers.isEmpty()) {
      // Extract attendee IDs from the speakers and collect them into a set
      final Set<Long> attendeeIds = speakers.stream()
        .filter(Objects::nonNull)
        .map(StreamSpeaker::getAttendeeId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

      if (!attendeeIds.isEmpty()) {
        // Count the number of attendee found in the repository matching the attendee IDs
        final long totalAttendeesFound = streamAttendeeOperationsService.countByIds(attendeeIds);
        // Validate that the number of found attendee matches the number of unique attendee IDs
        checkIsTrue(totalAttendeesFound != attendeeIds.size(), FailedOperationException::new);
      }
    }
  }

  /**
   * Checks if any of the speakers are not already attendees and sends invitations if necessary.
   * Processes speakers without attendee IDs, pending or disapproved attendees, and new guests.
   *
   * @param stream the stream to which speakers are being added
   * @param speakers the set of current stream speakers
   */
  private void checkIfSpeakerIsNotAnAttendeeAndSendInvitation(final FleenStream stream, final Set<StreamSpeaker> speakers) {
    // Initialize a list to hold attendees or guests that require invitations
    final Set<EventAttendeeOrGuest> guests = new HashSet<>();
    // Get id associated with stream
    final Long streamId = stream.getStreamId();
    // Get the attendee IDs of the current speakers
    final Set<Long> speakerAttendeeIds = getSpeakerAttendeeIds(speakers);

    // Find attendees associated with the stream by matching speaker attendee IDs
    final Set<StreamAttendee> allAttendees = findAttendees(streamId, speakerAttendeeIds);
    // Extract disapproved or pending attendee from all the attendees
    final Set<StreamAttendee> disapprovedOrPendingAttendees = getDisapprovedOrPendingAttendees(allAttendees);
    // Get the IDs of attendees who are either pending or disapproved
    final Set<Long> disapprovedOrPendingAttendeeIds = getDisapprovedOrPendingAttendeeIds(disapprovedOrPendingAttendees);

    // Process attendees who are pending or disapproved
    processPendingOrDisapprovedAttendeesAndAddToGuestsList(disapprovedOrPendingAttendeeIds, disapprovedOrPendingAttendees, speakers, guests);
    // Save all attendees
    streamAttendeeOperationsService.saveAll(allAttendees);
    // Send invitations to the new attendees or guests
    sendInvitationToNewAttendeesOrGuests(stream, stream.getMember(), guests);
  }

  /**
   * Extracts the attendee IDs from the given set of speakers.
   *
   * @param speakers the set of speakers
   * @return a set of attendee IDs
   */
  private Set<Long> getSpeakerAttendeeIds(final Set<StreamSpeaker> speakers) {
    // Map each speaker to its attendee ID and collect them into a set
    return speakers.stream()
      .filter(Objects::nonNull)
      .map(StreamSpeaker::getAttendeeId)
      .filter(Objects::nonNull)
      .collect(Collectors.toSet());
  }

  /**
   * Finds attendees for the given stream ID who have specific statuses.
   *
   * @param streamId   the ID of the stream
   * @param speakerAttendeeIds  the set of attendee IDs to search for
   * @return a set of attendees with approved, disapproved or pending statuses
   */
  private Set<StreamAttendee> findAttendees(final Long streamId, final Set<Long> speakerAttendeeIds) {
    // Retrieve attendees with PENDING or DISAPPROVED statuses for the given stream ID
    return streamAttendeeOperationsService.findAttendeesByIdsAndStreamIdAndStatuses(
      new ArrayList<>(speakerAttendeeIds),
      streamId,
      List.of(APPROVED, DISAPPROVED, PENDING)
    );
  }

  /**
   * Retrieves the set of attendees who are either in a DISAPPROVED or PENDING status
   * from the provided set of {@link StreamAttendee} objects.
   *
   * @param streamAttendees the set of {@link StreamAttendee} objects to filter.
   * @return a set of {@link StreamAttendee} objects where the status is either DISAPPROVED or PENDING.
   */
  private static Set<StreamAttendee> getDisapprovedOrPendingAttendees(final Set<StreamAttendee> streamAttendees) {
    // Retrieve attendees with DISAPPROVED or PENDING statuses for the given stream ID
    return streamAttendees.stream()
      .filter(Objects::nonNull)
      .filter(StreamAttendee::isRequestToJoinDisapprovedOrPending)
      .collect(Collectors.toSet());
  }

  /**
   * Retrieves the IDs of attendees with disapproved or pending statuses.
   *
   * @param attendees the set of attendees to filter
   * @return a set of attendee attendee IDs with disapproved or pending statuses
   */
  private static Set<Long> getDisapprovedOrPendingAttendeeIds(final Set<StreamAttendee> attendees) {
    // Filter attendees with DISAPPROVED or PENDING status and collect their attendee IDs
    return attendees.stream()
      .filter(Objects::nonNull)
      .filter(StreamAttendee::isRequestToJoinDisapprovedOrPending)
      .map(StreamAttendee::getAttendeeId)
      .filter(Objects::nonNull)
      .collect(Collectors.toSet());
  }

  /**
   * Processes pending or disapproved attendees and adds them to the guests list.
   *
   * <p>This method iterates over the provided {@code pendingOrDisapprovedAttendeeIds}, finds the corresponding attendee and speaker
   * for each ID, and if both are found, processes them by updating the speaker's full name, approving the attendee's attendance,
   * and adding them to the {@code guests} list as an attendee or guest.</p>
   *
   * @param pendingOrDisapprovedAttendeeIds the set of attendee IDs that are either pending or disapproved
   * @param pendingOrDisapprovedAttendees   the set of attendees that are either pending or disapproved
   * @param speakers                        the set of speakers associated with the attendees
   * @param guests                          the set of guests to which the processed attendees and speakers will be added
   */
  private static void processPendingOrDisapprovedAttendeesAndAddToGuestsList(
    final Set<Long> pendingOrDisapprovedAttendeeIds,
    final Set<StreamAttendee> pendingOrDisapprovedAttendees,
    final Set<StreamSpeaker> speakers,
    final Set<EventAttendeeOrGuest> guests) {

    pendingOrDisapprovedAttendeeIds.stream()
      .filter(Objects::nonNull)
      .forEach(attendeeId -> {
        final StreamAttendee attendee = findAttendeeById(attendeeId, pendingOrDisapprovedAttendees);
        final StreamSpeaker speaker = findSpeakerById(attendeeId, speakers);

        if (nonNull(attendee) && nonNull(speaker)) {
          processAttendeeAndSpeaker(attendee, speaker, guests);
        }
    });
  }

  /**
   * Finds an attendee by their attendee ID from a set of attendees.
   *
   * <p>This method searches the provided {@code attendees} set for an attendee whose attendee ID matches the given {@code attendeeId}.
   * If a matching attendee is found, they are returned. If no match is found, {@code null} is returned.</p>
   *
   * @param attendeeId the ID of the attendee to be found
   * @param attendees  the set of attendees to search through
   * @return the attendee whose attendee ID matches the given {@code attendeeId}, or {@code null} if no match is found
   */
  private static StreamAttendee findAttendeeById(final Long attendeeId, final Set<StreamAttendee> attendees) {
    return attendees.stream()
      .filter(Objects::nonNull)
      .filter(attendee -> attendee.getAttendeeId().equals(attendeeId))
      .findFirst()
      .orElse(null);
  }

  /**
   * Finds a speaker by their attendee ID from a set of speakers.
   *
   * <p>This method searches the provided {@code speakers} set for a speaker whose attendee ID matches the given {@code attendeeId}.
   * If a matching speaker is found, they are returned. If no match is found, {@code null} is returned.</p>
   *
   * @param attendeeId the ID of the attendee whose corresponding speaker is to be found
   * @param speakers   the set of speakers to search through
   * @return the speaker whose attendee ID matches the given {@code attendeeId}, or {@code null} if no match is found
   */
  private static StreamSpeaker findSpeakerById(final Long attendeeId, final Set<StreamSpeaker> speakers) {
    return speakers.stream()
      .filter(Objects::nonNull)
      .filter(speaker -> speaker.getAttendeeId().equals(attendeeId))
      .findFirst()
      .orElse(null);
  }

  /**
   * Processes a stream attendee and a speaker, updating the speaker's full name and approving the attendee's attendance.
   *
   * <p>This method updates the {@code speaker}'s full name based on the attendee's full name and sets it. It then approves
   * the {@code attendee}'s attendance and adds them to the set of {@code guests} as an attendee or guest.</p>
   *
   * @param attendee the attendee whose attendance is being processed and approved
   * @param speaker  the speaker associated with the attendee, whose full name will be updated
   * @param guests   the set of guests or attendees to which the processed attendee will be added
   */
  private static void processAttendeeAndSpeaker(final StreamAttendee attendee, final StreamSpeaker speaker, final Set<EventAttendeeOrGuest> guests) {
    final String fullName = speaker.getName(attendee.getFullName());
    speaker.setFullName(fullName);
    attendee.approveUserAttendance();

    final EventAttendeeOrGuest eventAttendeeOrGuest = EventAttendeeOrGuest.of(attendee.getEmailAddress(), speaker.getFullName(), false);
    guests.add(eventAttendeeOrGuest);
  }

  /**
   * Sends an invitation to new attendees or guests for a given stream if it is an event.
   *
   * <p>This method checks if the provided stream is an event. If it is, it finds the calendar
   * associated with the organizer of the stream, creates an event to add the guests
   * to the calendar, and publishes the event to add the new attendees.</p>
   *
   * @param stream the stream for which the invitations are to be sent
   * @param organizerOfStream the organizer of the stream
   * @param guests a set of attendees or guests that require invitations
   */
  private void sendInvitationToNewAttendeesOrGuests(final FleenStream stream, final Member organizerOfStream, final Set<EventAttendeeOrGuest> guests) {
    if (nonNull(stream) && stream.isAnEvent() && nonNull(guests) && !guests.isEmpty()) {
      // Find the calendar based on the stream organizer's country
      final Calendar calendar = miscService.findCalendar(organizerOfStream.getCountry());

      // Create an event to add attendees to the calendar
      final AddCalendarEventAttendeesEvent addCalendarEventAttendeesEvent = AddCalendarEventAttendeesEvent.of(
        calendar.getExternalId(),
        stream.getExternalId(),
        Set.of(),
        guests
      );

      // Publish the event to add new attendees
      streamEventPublisher.addNewAttendees(addCalendarEventAttendeesEvent);
    }
  }

  /**
   * Marks attendees as speakers.
   *
   * <p>This method takes a set of {@code StreamSpeaker} entities, retrieves their associated attendee IDs,
   * and marks all the corresponding attendees as speakers by updating the database.</p>
   *
   * @param speakers the set of {@code StreamSpeaker} entities representing attendees to be marked as speakers
   */
  private void markAttendeesAsSpeaker(final Set<StreamSpeaker> speakers) {
    if (nonNull(speakers) && !speakers.isEmpty()) {
      // Get the attendee IDs of the current speakers
      final Set<Long> speakerAttendeeIds = getSpeakerAttendeeIds(speakers);

      // Mark all the attendees as speakers
      streamAttendeeOperationsService.markAllAttendeesAsSpeaker(new ArrayList<>(speakerAttendeeIds));
    }
  }

  /**
   * Validates that the organizer of the stream cannot be removed as a speaker.
   *
   * <p>This method checks if the organizer of the stream is included in the list of speakers to be removed.
   * If the organizer is included, an {@code OrganizerOfStreamCannotBeRemovedAsSpeakerException} is thrown.
   * It first validates that none of the input parameters are null and that the set of speakers is not empty.
   * If any of these conditions fail, a {@code FailedOperationException} is thrown.</p>
   *
   * @param stream the stream associated with the speakers
   * @param organizerOfStream the member who is the organizer of the stream
   * @param speakers the set of {@code StreamSpeaker} entities representing speakers
   * @throws FailedOperationException if any of the parameters are null or the set of speakers is empty
   * @throws OrganizerOfStreamCannotBeRemovedAsSpeakerException if the organizer is found in the list of speakers
   */
  private void checkOrganizerOfStreamCannotBeRemovedAsSpeaker(final FleenStream stream, final Member organizerOfStream, final Set<StreamSpeaker> speakers) {
    // Check if any of the input parameters (stream, organizerOfStream, speakers) are null
    checkIsNullAny(List.of(stream, organizerOfStream, speakers), FailedOperationException::new);

    // Throw exception if the set of speakers is empty
    if (speakers.isEmpty()) {
      throw new FailedOperationException();
    }

    // Find the stream attendee who is the organizer of the stream
    final StreamAttendee streamAttendee = streamAttendeeOperationsService.findOrganizerByStream(stream, organizerOfStream)
      .orElseThrow(FailedOperationException::new);

    // Get the attendee ID of the stream organizer
    final Long streamOrganizerAttendeeId = streamAttendee.getAttendeeId();
    // Get the set of attendee IDs of the speakers
    final Set<Long> speakerAttendeeIds = getSpeakerAttendeeIds(speakers);

    // Check if the organizer is in the list of speaker attendee IDs and throw exception if so
    if (speakerAttendeeIds.contains(streamOrganizerAttendeeId)) {
      throw new OrganizerOfStreamCannotBeRemovedAsSpeakerException();
    }
  }

  /**
   * Sets the {@code memberId} for speakers who do not yet have a member assigned.
   *
   * <p>This method filters out speakers that don't have an assigned member. It then retrieves
   * the corresponding attendees based on the filtered attendee IDs and creates a mapping from
   * {@code attendeeId} to {@link StreamAttendee}. The speakers are updated with the
   * corresponding {@link Member} from the attendees, and the updated speakers are saved
   * back to the repository.</p>
   *
   * @param speakers a set of {@link StreamSpeaker} entities whose member information needs
   *                 to be set for speakers without a member
   */
  public void setMemberIdsForSpeakers(final Set<StreamSpeaker> speakers) {
    // Filter speakers without memberId
    final Set<Long> attendeeIdsWithoutMember = speakers.stream()
      .filter(Objects::nonNull)
      .filter(StreamSpeaker::hasNoMember)
      .map(StreamSpeaker::getAttendeeId)
      .collect(Collectors.toSet());

    if (attendeeIdsWithoutMember.isEmpty()) {
      return;
    }

    // Fetch all attendees whose IDs match the filtered set
    final List<StreamAttendee> attendees = streamAttendeeOperationsService.findAllByAttendeeIds(attendeeIdsWithoutMember);

    // Create a map from attendeeId to StreamAttendee for faster lookup
    final Map<Long, StreamAttendee> attendeeMap = attendees.stream()
      .collect(Collectors.toMap(StreamAttendee::getAttendeeId, attendee -> attendee));

    // Map attendees to their corresponding speakers and set the memberId
    for (final StreamSpeaker speaker : speakers) {
      if (speaker.hasNoMember()) {
        final StreamAttendee attendee = attendeeMap.get(speaker.getAttendeeId());
        if (nonNull(attendee)) {
          speaker.setMember(attendee.getMember());
        }
      }
    }
  }

  /**
   * Filters out the organizer from the list of stream speakers.
   *
   * <p>This method removes the speaker who is also the organizer of the stream.
   * The organizer is identified by comparing the organizer's ID with the
   * {@code memberId} of each {@link StreamSpeaker}.</p>
   *
   * @param speakers    the list of {@link StreamSpeaker} entities to be filtered
   * @param organizerId the ID of the organizer used for filtering
   * @return a list of {@link StreamSpeaker} entities excluding the organizer
   */
  private List<StreamSpeaker> filterOutOrganizer(final List<StreamSpeaker> speakers, final Long organizerId) {
    // Filter out the organizer by comparing the FleenUser ID and StreamSpeaker memberId
    return speakers.stream()
      .filter(speaker -> speaker.isNotOrganizer(organizerId))
      .toList();
  }

  /**
   * Marks all attendees associated with the given set of speakers as non-speakers.
   *
   * <p>This method iterates through the provided set of {@code StreamSpeaker} entities,
   * retrieves the associated {@code StreamAttendee}, and marks each attendee as a
   * non-speaker. The attendees marked as non-speakers are collected in a list and
   * then persisted to the database in a batch operation.</p>
   *
   * @param speakers the set of {@code StreamSpeaker} entities whose associated attendees
   *                 are to be marked as non-speakers
   */
  private void markAttendeesAsNonSpeakers(final Set<StreamSpeaker> speakers) {
    final List<StreamAttendee> attendeesToBeNonSpeakers = new ArrayList<>();

    for (final StreamSpeaker speaker : speakers) {
      // Fetch the associated attendee (already managed by JPA now)
      final StreamAttendee attendee = speaker.getAttendee();

      if (attendee != null) {
        // Mark attendee as non-speaker
        attendee.markAsNonSpeaker();
        // Add the attendee to list of attendees removed as non-speakers
        attendeesToBeNonSpeakers.add(attendee);
      }
    }

    // Save the updated attendees
    streamAttendeeOperationsService.saveAll(attendeesToBeNonSpeakers);
  }


}
