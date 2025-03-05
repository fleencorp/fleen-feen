package com.fleencorp.feen.service.impl.stream.speaker;

import com.fleencorp.feen.event.publisher.StreamEventPublisher;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.mapper.impl.speaker.StreamSpeakerMapperImpl;
import com.fleencorp.feen.mapper.stream.speaker.StreamSpeakerMapper;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto.EventAttendeeOrGuest;
import com.fleencorp.feen.model.dto.stream.base.DeleteStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.MarkAsStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.event.AddCalendarEventAttendeesEvent;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeInfoSelect;
import com.fleencorp.feen.model.request.search.stream.StreamSpeakerSearchRequest;
import com.fleencorp.feen.model.response.stream.speaker.DeleteStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.MarkAsStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.StreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.UpdateStreamSpeakerResponse;
import com.fleencorp.feen.model.search.stream.speaker.EmptyStreamSpeakerSearchResult;
import com.fleencorp.feen.model.search.stream.speaker.StreamSpeakerSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.repository.stream.StreamSpeakerRepository;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.feen.service.stream.speaker.StreamSpeakerService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsTrue;
import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
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
  private final StreamService streamService;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final StreamSpeakerRepository streamSpeakerRepository;
  private final StreamEventPublisher streamEventPublisher;
  private final StreamSpeakerMapper streamSpeakerMapper;
  private final Localizer localizer;

  /**
   * Constructs an instance of {@code StreamSpeakerImpl} with the provided dependencies.
   *
   * @param miscService the {@link MiscService} used for handling miscellaneous tasks
   * @param streamAttendeeRepository the repository to manage stream attendee entities
   * @param streamSpeakerRepository the repository to manage stream speaker entities
   * @param streamEventPublisher the publisher to handle stream event-related operations
   * @param streamSpeakerMapper the mapper service for mapping stream speaker domain entity to responses
   * @param localizer the service to handle localized responses
   */
  public StreamSpeakerServiceImpl(
      final MiscService miscService,
      final StreamService streamService,
      final StreamAttendeeRepository streamAttendeeRepository,
      final StreamSpeakerRepository streamSpeakerRepository,
      final StreamEventPublisher streamEventPublisher,
      final StreamSpeakerMapperImpl streamSpeakerMapper,
      final Localizer localizer) {
    this.miscService = miscService;
    this.streamService = streamService;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.streamSpeakerRepository = streamSpeakerRepository;
    this.streamEventPublisher = streamEventPublisher;
    this.streamSpeakerMapper = streamSpeakerMapper;
    this.localizer = localizer;
  }

  /**
   * Searches for speakers based on the provided search criteria.
   *
   * @param searchRequest the search request containing filtering criteria and pagination information
   * @return a StreamSpeakerSearchResult containing the list of speakers matching the search criteria
   */
  @Override
  public StreamSpeakerSearchResult findSpeakers(final Long streamId, final StreamSpeakerSearchRequest searchRequest) {
    // Extract the name, full name, username, or email address from the search request
    final String nameOrFullNameOrUsernameOrEmailAddress = searchRequest.getUserIdOrName();
    // Retrieve a paginated list of Member entities matching the search criteria
    final Page<StreamAttendeeInfoSelect> page = streamAttendeeRepository.findAttendeeByStreamAndEmailAddressOrFirstNameOrLastName(streamId, nameOrFullNameOrUsernameOrEmailAddress, searchRequest.getPage());
    // Convert the retrieved Member entities to a list of StreamSpeakerResponse DTOs
    final List<StreamSpeakerResponse> views = streamSpeakerMapper.toStreamSpeakerResponsesByProjection(page.getContent());
    // Return a search result view with the speaker responses and pagination details
    return handleSearchResult(
      page,
      localizer.of(StreamSpeakerSearchResult.of(toSearchResult(views, page))),
      localizer.of(EmptyStreamSpeakerSearchResult.of(toSearchResult(List.of(), page)))
    );
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
   * @return a {@code StreamSpeakerSearchResult} containing a list of speakers, or an empty result if no speakers are found
   */
  @Override
  public StreamSpeakerSearchResult findStreamSpeakers(final Long streamId, StreamSpeakerSearchRequest searchRequest) {
    // Set default number of speakers to retrieve
    searchRequest.setDefaultPageSize();
    // Fetch all StreamSpeaker entities associated with the given stream ID
    final Page<StreamSpeaker> page = streamSpeakerRepository.findAllByStream(FleenStream.of(streamId), searchRequest.getPage());
    // Convert the retrieved StreamSpeaker entities to a set of StreamSpeakerResponse DTOs
    final List<StreamSpeakerResponse> views = streamSpeakerMapper.toStreamSpeakerResponses(page.getContent());
    // Return a localized response containing the list of speaker responses
    return handleSearchResult(
      page,
      localizer.of(StreamSpeakerSearchResult.of(toSearchResult(views, page))),
      localizer.of(EmptyStreamSpeakerSearchResult.of(toSearchResult(List.of(), page)))
    );
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
   * @throws FleenStreamNotFoundException if the stream with the specified ID cannot be found
   * @throws com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException if the user is not the organizer of the stream
   * @throws FailedOperationException if the attendee IDs associated with the speakers are invalid
   */
  @Override
  @Transactional
  public MarkAsStreamSpeakerResponse markAsSpeaker(final Long streamId, final MarkAsStreamSpeakerDto dto, final FleenUser user) {
    // Retrieve the stream with the given ID
    final FleenStream stream = streamService.findStream(streamId);
    // Validate if the user is the creator of the stream
    stream.checkIsOrganizer(user.getId());
    // Convert the DTO to a set of StreamSpeaker objects linked to the specified stream
    final Set<StreamSpeaker> speakers = dto.toStreamSpeakers(stream);

    // Validate that all attendee IDs associated with the speakers exist
    checkIfNonNullAttendeeIdsExists(speakers);
    // Check if speakers are not already attendees and send invitations if needed
    checkIfSpeakerIsNotAnAttendeeAndSendInvitation(stream, speakers);
    // Save all the speakers to the repository
    streamSpeakerRepository.saveAll(speakers);
    // Create and return the response
    return localizer.of(MarkAsStreamSpeakerResponse.of());
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
   * @throws FleenStreamNotFoundException if the stream with the given ID does not exist
   * @throws FailedOperationException if the attendee ID validation fails
   */
  @Override
  @Transactional
  public UpdateStreamSpeakerResponse updateSpeakers(final Long streamId, final UpdateStreamSpeakerDto dto, final FleenUser user) {
    // Retrieve the stream with the given ID
    final FleenStream stream = streamService.findStream(streamId);
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

    // Check if the list or set of speakers is not empty
    if (!updatedSpeakers.isEmpty()) {
      // Save the updated speakers to the repository
      streamSpeakerRepository.saveAll(updatedSpeakers);
    }

    // Check if speakers are not already attendees and send invitations if needed
    checkIfSpeakerIsNotAnAttendeeAndSendInvitation(stream, updatedSpeakers);
    // Return a response indicating that the speakers have been successfully updated
    return localizer.of(UpdateStreamSpeakerResponse.of());
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
   * @param newSpeaker      the speaker with updated details to be applied to the existing speaker
   * @param stream          the stream to which the speaker belongs
   * @param updatedSpeakers the set of updated speakers, which will include the updated speaker if found
   */
  private void updateExistingSpeaker(final StreamSpeaker newSpeaker, final FleenStream stream, final Set<StreamSpeaker> updatedSpeakers) {
    streamSpeakerRepository.findBySpeakerIdAndStream(newSpeaker, stream)
      .ifPresent(existingSpeaker -> {
        existingSpeaker.update(newSpeaker.getFullName(), newSpeaker.getTitle(), newSpeaker.getDescription());
        addToUpdatedSpeakers(existingSpeaker, updatedSpeakers);
    });
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
   * Deletes the specified speakers from a given stream.
   *
   * @param streamId The ID of the stream from which speakers are to be deleted.
   * @param dto A {@link DeleteStreamSpeakerDto} containing the details of the speakers to be deleted.
   * @param user The user performing the delete operation.
   * @return A {@link DeleteStreamSpeakerResponse} indicating the result of the delete operation.
   */
  @Override
  @Transactional
  public DeleteStreamSpeakerResponse deleteSpeakers(final Long streamId, final DeleteStreamSpeakerDto dto, final FleenUser user) {
    // Retrieve the stream with the given ID
    final FleenStream stream = streamService.findStream(streamId);
    // Validate if the user is the creator of the stream
    stream.checkIsOrganizer(user.getId());
    // Get all stream speakers from dto
    final Set<StreamSpeaker> speakers = dto.toStreamSpeakers();

    // Check if the list or set of speakers is not empty
    if (!speakers.isEmpty()) {
    // Delete all speakers requested
      streamSpeakerRepository.deleteAll(speakers);
    }

    return localizer.of(DeleteStreamSpeakerResponse.of());
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
        final long totalAttendeesFound = streamAttendeeRepository.countByIds(attendeeIds);
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
    final Set<Long> speakerWithAttendeeIds = getSpeakerAttendeeIds(speakers);

    // Find attendees associated with the stream by matching speaker attendee IDs
    final Set<StreamAttendee> allAttendees = findAttendees(streamId, speakerWithAttendeeIds);
    // Extract disapproved or pending attendee from all the attendees
    final Set<StreamAttendee> disapprovedOrPendingAttendees = getDisapprovedOrPendingAttendees(allAttendees);
    // Get the IDs of attendees who are either pending or disapproved
    final Set<Long> disapprovedOrPendingAttendeeIds = getDisapprovedOrPendingAttendeeIds(disapprovedOrPendingAttendees);

    // Process attendees who are pending or disapproved
    processPendingOrDisapprovedAttendeesAndAddToGuestsList(disapprovedOrPendingAttendeeIds, disapprovedOrPendingAttendees, speakers, guests);
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
    return streamAttendeeRepository.findAttendeesByIdsAndStreamIdAndStatuses(
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

    EventAttendeeOrGuest eventAttendeeOrGuest = EventAttendeeOrGuest.of(attendee.getEmailAddress(), speaker.getFullName(), false);
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
}
