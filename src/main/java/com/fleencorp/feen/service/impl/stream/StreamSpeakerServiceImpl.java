package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.event.publisher.StreamEventPublisher;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.model.domain.calendar.Calendar;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.event.CreateCalendarEventDto.EventAttendeeOrGuest;
import com.fleencorp.feen.model.dto.stream.AddStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.DeleteStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.StreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.event.AddCalendarEventAttendeesEvent;
import com.fleencorp.feen.model.request.search.stream.StreamSpeakerSearchRequest;
import com.fleencorp.feen.model.response.stream.speaker.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.calendar.CalendarRepository;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamAttendeeRepository;
import com.fleencorp.feen.repository.stream.StreamSpeakerRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.common.CountryService;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.stream.StreamSpeakerService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsTrue;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.mapper.StreamSpeakerMapper.toStreamSpeakerResponses;
import static com.fleencorp.feen.mapper.StreamSpeakerMapper.toStreamSpeakerResponsesByMember;
import static com.fleencorp.feen.service.impl.stream.base.StreamService.validateCreatorOfEvent;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link StreamSpeakerService} interface for managing stream speakers.
 *
 * <p>This class provides functionalities to add, update, delete, and retrieve speakers
 * for a given stream or event. It utilizes repositories to interact with stream and
 * speaker data and provides localized responses.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
public class StreamSpeakerServiceImpl implements StreamSpeakerService {

  private final CountryService countryService;
  private final CalendarRepository calendarRepository;
  private final FleenStreamRepository fleenStreamRepository;
  private final MemberRepository memberRepository;
  private final StreamAttendeeRepository streamAttendeeRepository;
  private final StreamSpeakerRepository streamSpeakerRepository;
  private final LocalizedResponse localizedResponse;
  private final StreamEventPublisher streamEventPublisher;

  /**
   * Constructs an instance of {@code StreamSpeakerImpl} with the provided dependencies.
   *
   * @param countryService the service to handle country-related operations
   * @param calendarRepository the repository to manage calendar entities
   * @param fleenStreamRepository the repository to manage stream entities
   * @param memberRepository the repository to manage member entities
   * @param streamAttendeeRepository the repository to manage stream attendee entities
   * @param streamSpeakerRepository the repository to manage stream speaker entities
   * @param localizedResponse the service to handle localized responses
   * @param streamEventPublisher the publisher to handle stream event-related operations
   */
  public StreamSpeakerServiceImpl(
      final CountryService countryService,
      final CalendarRepository calendarRepository,
      final FleenStreamRepository fleenStreamRepository,
      final MemberRepository memberRepository,
      final StreamAttendeeRepository streamAttendeeRepository,
      final StreamSpeakerRepository streamSpeakerRepository,
      final LocalizedResponse localizedResponse,
      final StreamEventPublisher streamEventPublisher) {
    this.countryService = countryService;
    this.calendarRepository = calendarRepository;
    this.fleenStreamRepository = fleenStreamRepository;
    this.memberRepository = memberRepository;
    this.streamAttendeeRepository = streamAttendeeRepository;
    this.streamSpeakerRepository = streamSpeakerRepository;
    this.localizedResponse = localizedResponse;
    this.streamEventPublisher = streamEventPublisher;
  }

  /**
   * Searches for speakers based on the provided search criteria.
   *
   * @param searchRequest the search request containing filtering criteria and pagination information
   * @return a SearchResultView containing the list of speakers matching the search criteria
   */
  @Override
  public SearchResultView findSpeakers(final StreamSpeakerSearchRequest searchRequest) {
    // Extract the name, full name, username, or email address from the search request
    final String nameOrFullNameOrUsernameOrEmailAddress = searchRequest.getUserIdOrName();
    // Retrieve a paginated list of Member entities matching the search criteria
    final Page<Member> page = memberRepository.findAllByEmailAddressOrFirstNameOrLastName(nameOrFullNameOrUsernameOrEmailAddress, searchRequest.getPage());
    // Convert the retrieved Member entities to a list of StreamSpeakerResponse DTOs
    final List<StreamSpeakerResponse> views = toStreamSpeakerResponsesByMember(page.getContent());
    // Return the search results, including the list of speaker responses and pagination details
    return toSearchResult(views, page);
  }

  /**
   * Retrieves the speakers for a specified event or stream.
   *
   * @param eventOrStreamId The ID of the event or stream for which to retrieve speakers.
   * @return A {@link GetStreamSpeakersResponse} containing the details of the speakers.
   */
  @Override
  public GetStreamSpeakersResponse getSpeakers(final Long eventOrStreamId) {
    // Fetch all StreamSpeaker entities associated with the given event or stream ID
    final Set<StreamSpeaker> speakers = streamSpeakerRepository.findAllByFleenStream(FleenStream.of(eventOrStreamId));
    // Convert the retrieved StreamSpeaker entities to a set of StreamSpeakerResponse DTOs
    final Set<StreamSpeakerResponse> speakerResponses = toStreamSpeakerResponses(speakers);
    // Return a localized response containing the list of speaker responses
    return localizedResponse.of(GetStreamSpeakersResponse.of(speakerResponses));
  }

  /**
   * Adds new speakers to a specified event or stream.
   *
   * <p>This method verifies the existence of the event or stream with the given ID before converting
   * the provided {@link AddStreamSpeakerDto} into a set of {@link StreamSpeaker} objects linked to the
   * specified event or stream. It then validates that all member IDs associated with the speakers exist
   * in the repository. If the validation passes, the speakers are saved to the repository.</p>
   *
   * @param eventOrStreamId the ID of the event or stream to add speakers to
   * @param dto the {@link AddStreamSpeakerDto} containing the speaker information to add
   * @param user the {@link FleenUser} performing the add operation
   * @return an {@link AddStreamSpeakerResponse} indicating the outcome of the addition
   * @throws FleenStreamNotFoundException if the event or stream with the given ID does not exist
   * @throws FailedOperationException if the member ID validation fails
   */
  @Override
  @Transactional
  public AddStreamSpeakerResponse addSpeakers(final Long eventOrStreamId, final AddStreamSpeakerDto dto, final FleenUser user) {
    // Check if the event or stream with the given ID exists, throw exception if not
    final FleenStream stream = getAndVerifyStreamDetails(eventOrStreamId, user);
    // Convert the DTO to a set of StreamSpeaker objects linked to the specified event or stream
    final Set<StreamSpeaker> speakers = dto.toStreamSpeakers(stream);

    // Validate that all member IDs associated with the speakers exist
    checkIfNonNullMemberIdsExists(speakers);
    // Check if speakers are not already attendees and send invitations if needed
    checkIfSpeakerIsNotAnAttendeeAndSendInvitation(stream, speakers, dto.getSpeakers());
    // Save all the speakers to the repository
    streamSpeakerRepository.saveAll(speakers);
    return localizedResponse.of(AddStreamSpeakerResponse.of());
  }

  /**
   * Updates the speakers for a specified event or stream.
   *
   * <p>This method first checks if the event or stream with the given ID exists. It then converts
   * the provided {@link UpdateStreamSpeakerDto} into a set of {@link StreamSpeaker} objects linked
   * to the specified event or stream. The method ensures that all member IDs associated with the speakers
   * exist in the repository. If the validation is successful, it saves the updated speakers to the repository.</p>
   *
   * @param eventOrStreamId the ID of the event or stream to update speakers for
   * @param dto the {@link UpdateStreamSpeakerDto} containing the speaker information to update
   * @param user the {@link FleenUser} performing the update operation
   * @return an {@link UpdateStreamSpeakerResponse} indicating the outcome of the update
   * @throws FleenStreamNotFoundException if the event or stream with the given ID does not exist
   * @throws FailedOperationException if the member ID validation fails
   */
  @Override
  @Transactional
  public UpdateStreamSpeakerResponse updateSpeakers(final Long eventOrStreamId, final UpdateStreamSpeakerDto dto, final FleenUser user) {
    // Check if the event or stream with the given ID exists, throw exception if not
    final FleenStream stream = getAndVerifyStreamDetails(eventOrStreamId, user);
    // Convert the DTO to a set of StreamSpeaker entities, associating them with the specified event or stream
    final Set<StreamSpeaker> speakers = dto.toStreamSpeakers(FleenStream.of(eventOrStreamId));
    // Ensure all member IDs in the speakers are valid and exist
    checkIfNonNullMemberIdsExists(speakers);
    // Check if speakers are not already attendees and send invitations if needed
    checkIfSpeakerIsNotAnAttendeeAndSendInvitation(stream, speakers, dto.getSpeakers());
    // Save the updated speakers to the repository
    streamSpeakerRepository.saveAll(speakers);
    // Return a response indicating that the speakers have been successfully updated
    return localizedResponse.of(UpdateStreamSpeakerResponse.of());
  }

  /**
   * Deletes the specified speakers from a given stream or event.
   *
   * @param eventOrStreamId The ID of the event or stream from which speakers are to be deleted.
   * @param dto A {@link DeleteStreamSpeakerDto} containing the details of the speakers to be deleted.
   * @param user The user performing the delete operation.
   * @return A {@link DeleteStreamSpeakerResponse} indicating the result of the delete operation.
   */
  @Override
  @Transactional
  public DeleteStreamSpeakerResponse deleteSpeakers(final Long eventOrStreamId, final DeleteStreamSpeakerDto dto, final FleenUser user) {
    // Check if the event or stream with the given ID exists, throw exception if not
    getAndVerifyStreamDetails(eventOrStreamId, user);
    // Get all stream speakers from dto
    final Set<StreamSpeaker> speakers = dto.toStreamSpeakers();
    // Delete all speakers requested
    streamSpeakerRepository.deleteAll(speakers);

    return localizedResponse.of(DeleteStreamSpeakerResponse.of());
  }

  /**
   * Validates that all non-null member IDs in the given set of speakers exist in the repository.
   *
   * <p>This method checks if the provided set of {@code speakers} is non-null and not empty.
   * It extracts the member IDs from each speaker and verifies if all of them exist in the
   * repository. If the count of found members does not match the number of speakers, an exception
   * is thrown.</p>
   *
   * @param speakers the set of {@link StreamSpeaker} objects to be checked
   * @throws FailedOperationException if the count of found members does not match the number of speakers
   */
  protected void checkIfNonNullMemberIdsExists(final Set<StreamSpeaker> speakers) {
    // Check if the speakers set is non-null and not empty
    if (nonNull(speakers) && !speakers.isEmpty()) {
      // Extract member IDs from the speakers and collect them into a set
      final Set<Long> memberIds = speakers.stream()
        .map(StreamSpeaker::getMemberId)
        .collect(Collectors.toSet());
      // Count the number of members found in the repository matching the member IDs
      final long totalMembersFound = memberRepository.countByIds(memberIds);

      // Validate that the number of found members matches the number of unique member IDs
      checkIsTrue(totalMembersFound != speakers.size(), FailedOperationException::new);
    }
  }

  /**
   * Checks if any of the speakers are not already attendees and sends invitations if necessary.
   * Processes speakers without member IDs, pending or disapproved attendees, and new guests.
   *
   * @param stream the stream event to which speakers are being added
   * @param speakers the set of current stream speakers
   * @param speakersDto the set of stream speaker DTOs containing speaker information
   */
  protected void checkIfSpeakerIsNotAnAttendeeAndSendInvitation(final FleenStream stream, final Set<StreamSpeaker> speakers, final Set<StreamSpeakerDto> speakersDto) {
    // Initialize a list to hold attendees or guests that require invitations
    final Set<EventAttendeeOrGuest> guests = new HashSet<>();
    final Long eventOrStreamId = stream.getFleenStreamId();

    // Get the member IDs of the current speakers
    final Set<Long> speakerWithMemberIds = getSpeakerMemberIds(speakers);
    // Filter out speakers that do not have member IDs
    final Set<StreamSpeakerDto> speakersWithNoMemberIds = filterSpeakersWithoutIds(speakersDto);
    // Find attendees associated with the event/stream by matching speaker member IDs
    final Set<StreamAttendee> pendingOrDisapprovedAttendees = findAttendees(eventOrStreamId, speakerWithMemberIds);

    // Get the IDs of attendees who are either pending or disapproved
    final Set<Long> pendingOrDisapprovedAttendeeMemberIds = getPendingOrDisapprovedAttendeeIds(pendingOrDisapprovedAttendees);
    // Identify speakers who are not attendees
    final Set<Long> notYetAttendeeMemberIds = getNonAttendeeMemberIds(speakerWithMemberIds, pendingOrDisapprovedAttendees);

    // Process attendees who are pending or disapproved
    processPendingOrDisapprovedAttendeesAndAddToGuestsList(pendingOrDisapprovedAttendeeMemberIds, pendingOrDisapprovedAttendees, speakers, guests);
    // Add non-attendee members to the guest list for invitation
    addNotYetAttendeeMembersToGuestsList(notYetAttendeeMemberIds, guests);
    // Add speakers without IDs to the guest list for invitation
    addSpeakersWithNoMemberIdsToGuestList(speakersWithNoMemberIds, guests);

    // Add non-attendee members as stream attendees or guests
    addNotYetAttendeeMembersAsStreamAttendeesOrGuests(notYetAttendeeMemberIds, stream);

    // Send invitations to the new attendees or guests
    sendInvitationToNewAttendeesOrGuests(stream, stream.getMember(), guests);
  }

  /**
   * Extracts the member IDs from the given set of speakers.
   *
   * @param speakers the set of speakers
   * @return a set of member IDs
   */
  private Set<Long> getSpeakerMemberIds(final Set<StreamSpeaker> speakers) {
    // Map each speaker to its member ID and collect them into a set
    return speakers.stream()
      .map(StreamSpeaker::getMemberId)
      .collect(Collectors.toSet());
  }

  /**
   * Filters out speakers that do not have a member ID.
   *
   * @param streamSpeakersDto the set of stream speaker DTOs
   * @return a set of speaker DTOs without member IDs
   */
  private Set<StreamSpeakerDto> filterSpeakersWithoutIds(final Set<StreamSpeakerDto> streamSpeakersDto) {
    // Filter speakers whose member ID is null and collect them into a set
    return streamSpeakersDto.stream()
      .filter(speaker -> isNull(speaker.getMemberId()))
      .collect(Collectors.toSet());
  }

  /**
   * Finds attendees for the given event or stream ID who have specific statuses.
   *
   * @param eventOrStreamId   the ID of the event or stream
   * @param speakerMemberIds  the set of member IDs to search for
   * @return a set of attendees with pending or disapproved statuses
   */
  private Set<StreamAttendee> findAttendees(final Long eventOrStreamId, final Set<Long> speakerMemberIds) {
    // Retrieve attendees with PENDING or DISAPPROVED statuses for the given event or stream ID
    return streamAttendeeRepository.findAttendeesByEventOrStreamIdAndMemberIdsAndStatuses(
      eventOrStreamId,
      new ArrayList<>(speakerMemberIds),
      List.of(StreamAttendeeRequestToJoinStatus.PENDING, StreamAttendeeRequestToJoinStatus.DISAPPROVED)
    );
  }

  /**
   * Retrieves the IDs of attendees with pending or disapproved statuses.
   *
   * @param attendees the set of attendees to filter
   * @return a set of attendee member IDs with pending or disapproved statuses
   */
  private Set<Long> getPendingOrDisapprovedAttendeeIds(final Set<StreamAttendee> attendees) {
    // Filter attendees with PENDING or DISAPPROVED status and collect their member IDs
    return attendees.stream()
      .filter(attendee -> attendee.isDisapproved() || attendee.isPending())
      .map(StreamAttendee::getMemberId)
      .collect(Collectors.toSet());
  }

  /**
   * Retrieves speaker member IDs that are not attendees.
   *
   * @param speakerMemberIds the set of speaker member IDs
   * @param attendees the set of current attendees
   * @return a set of member IDs that are not attendees
   */
  private Set<Long> getNonAttendeeMemberIds(final Set<Long> speakerMemberIds, final Set<StreamAttendee> attendees) {
    // Collect all attendee member IDs into a set
    final Set<Long> allAttendeeIds = attendees.stream()
      .map(StreamAttendee::getMemberId)
      .collect(Collectors.toSet());
    // Filter speaker IDs to include only those that are not in the attendee list
    return speakerMemberIds.stream()
      .filter(speakerMemberId -> !allAttendeeIds.contains(speakerMemberId))
      .collect(Collectors.toSet());
  }

  /**
   * Processes pending or disapproved attendees and adds them to the guest list.
   * Matches attendees with corresponding speakers, approves attendance, and sets the speaker's full name.
   * Finally, creates EventAttendeeOrGuest objects and adds them to the guest list.
   *
   * @param pendingOrDisapprovedAttendeeMemberIds set of member IDs of attendees who are pending or disapproved.
   * @param pendingOrDisapprovedAttendees set of attendees with pending or disapproved status.
   * @param speakers set of speakers associated with the event.
   * @param guests the list of guests to be invited, which will be updated with processed attendees.
   */
  private void processPendingOrDisapprovedAttendeesAndAddToGuestsList(final Set<Long> pendingOrDisapprovedAttendeeMemberIds, final Set<StreamAttendee> pendingOrDisapprovedAttendees, final Set<StreamSpeaker> speakers, final Set<EventAttendeeOrGuest> guests) {
    // Iterate through each attendee ID
    pendingOrDisapprovedAttendeeMemberIds.forEach(memberId -> {
      // Find the attendee with the matching member ID
      pendingOrDisapprovedAttendees.stream()
        .filter(attendee -> attendee.getMemberId().equals(memberId))
        .findFirst()
        .ifPresent(attendee -> {
          // Find the corresponding speaker with the same member ID
          speakers.stream()
            .filter(speaker -> speaker.getMemberId().equals(memberId))
            .findFirst()
            .ifPresent(speaker -> {
              // Process the attendee and speaker, setting full name and approving attendance
              String fullName = speaker.getName(attendee.getFullName());
              speaker.setFullName(fullName);
              attendee.approveUserAttendance();

              // Add the attendee to the guest list
              guests.add(EventAttendeeOrGuest.of(attendee.getEmailAddress(), speaker.getFullName()));
            });
        });
    });
  }

  /**
   * Adds non-attendee members to the guest list.
   *
   * @param memberIds the set of member IDs not currently attendees
   * @param guests the list to which new guests will be added
   */
  private void addNotYetAttendeeMembersToGuestsList(final Set<Long> memberIds, final Set<EventAttendeeOrGuest> guests) {
    // Retrieve all members by their IDs
    final Set<Member> members = memberRepository.findAllByIds(memberIds);
    // Add each member to the guest list
    members.forEach(member -> guests.add(EventAttendeeOrGuest.of(member.getEmailAddress(), member.getFullName())));
  }

  /**
   * Adds speakers without IDs to the guest list.
   *
   * @param speakers the set of speakers without member IDs
   * @param guests the list to which new guests will be added
   */
  private void addSpeakersWithNoMemberIdsToGuestList(final Set<StreamSpeakerDto> speakers, final Set<EventAttendeeOrGuest> guests) {
    // Add each speaker to the guest list
    speakers.forEach(speaker -> guests.add(EventAttendeeOrGuest.of(speaker.getEmailAddress(), speaker.getFullName())));
  }

  private Set<EventAttendeeOrGuest> getSpeakersWithNoIds(final Set<StreamSpeakerDto> speakers, final Set<EventAttendeeOrGuest> guests) {
    // Add each speaker to the guest list
    return speakers.stream()
      .map(speaker -> EventAttendeeOrGuest.of(speaker.getEmailAddress(), speaker.getFullName()))
      .collect(Collectors.toSet());
  }

  /**
   * Sends an invitation to new attendees or guests for a given stream if it is an event.
   *
   * <p>This method checks if the provided stream is an event. If it is, it finds the calendar
   * associated with the owner of the event or stream, creates an event to add the guests
   * to the calendar, and publishes the event to add the new attendees.</p>
   *
   * @param stream the stream or event for which the invitations are to be sent
   * @param ownerOfEventOrStream the owner of the event or stream
   * @param guests a set of attendees or guests that require invitations
   */
  private void sendInvitationToNewAttendeesOrGuests(final FleenStream stream, final Member ownerOfEventOrStream, final Set<EventAttendeeOrGuest> guests) {
    if (nonNull(stream) && stream.isAnEvent()) {
      // Find the calendar based on the event or stream owner's country
      final Calendar calendar = findCalendar(ownerOfEventOrStream.getCountry());

      // Create an event to add attendees to the calendar
      final AddCalendarEventAttendeesEvent addCalendarEventAttendeesEvent = AddCalendarEventAttendeesEvent
        .of(calendar.getExternalId(),
          stream.getExternalId(),
          Set.of(),
          guests);

      // Publish the event to add new attendees
      streamEventPublisher.addNewAttendees(addCalendarEventAttendeesEvent);
    }
  }

  /**
   * Adds non-attendee members as stream attendees for the specified stream.
   *
   * <p>This method iterates through the provided member IDs and creates a new
   * {@link StreamAttendee} for each member that is not currently an attendee.
   * Each new attendee is linked to the specified stream and is assigned an
   * appropriate request-to-join status.</p>
   *
   * @param memberIds the set of member IDs to be added as attendees
   * @param stream the stream to which the attendees will be added
   */
  private void addNotYetAttendeeMembersAsStreamAttendeesOrGuests(final Set<Long> memberIds, final FleenStream stream) {
    final Set<StreamAttendee> attendees = memberIds.stream()
        .map(memberId -> {
          // Create a new StreamAttendee for each non-attendee member
          final StreamAttendee newAttendee = StreamAttendee.of(Member.of(memberId), stream);
          newAttendee.approveUserAttendance(); // or appropriate status
          return newAttendee;
        })
        .collect(Collectors.toSet());

    // Save the new StreamAttendee if necessary
    streamAttendeeRepository.saveAll(attendees);
  }

  /**
   * Checks if an event or stream with the specified ID exists.
   * If the event or stream does not exist, throws a {@link FleenStreamNotFoundException}.
   *
   * @param eventOrStreamId The ID of the event or stream to check for existence.
   * @throws FleenStreamNotFoundException if the event or stream is not found.
   */
  protected FleenStream checkEventOrStreamExist(final Long eventOrStreamId) {
    return fleenStreamRepository.findById(eventOrStreamId)
      .orElseThrow(() -> new FleenStreamNotFoundException(eventOrStreamId));
  }

  /**
   * Finds the calendar associated with the specified country title.
   *
   * @param countryTitle the title of the country for which the calendar is to be found
   * @return the calendar associated with the given country title
   * @throws CalendarNotFoundException if no country code or calendar is found for the specified country title
   */
  protected Calendar findCalendar(final String countryTitle) {
    final String countryCode = countryService.getCountryCodeByTitle(countryTitle)
      .orElseThrow(() -> new CalendarNotFoundException(countryTitle));

    return calendarRepository.findDistinctByCodeIgnoreCase(countryCode)
      .orElseThrow(() -> new CalendarNotFoundException(countryCode));
  }

  /**
   * Retrieves and verifies the stream details for the given event or stream ID.
   *
   * <p>This method checks if the event or stream exists by the provided ID and validates
   * whether the specified user is the creator of the event. If the event or stream
   * does not exist or the user is not the creator, it throws appropriate exceptions.
   *
   * @param eventOrStreamId the ID of the event or stream to be retrieved and verified
   * @param user the user to be validated as the creator of the event or stream
   * @return the {@link FleenStream} object containing the event or stream details
   * @throws FleenStreamNotFoundException if the event or stream does not exist
   */
  protected FleenStream getAndVerifyStreamDetails(final Long eventOrStreamId, final FleenUser user) {
    // Check if the event or stream with the given ID exists, throw exception if not
    final FleenStream stream = checkEventOrStreamExist(eventOrStreamId);
    // Validate if the user is the creator of the event
    validateCreatorOfEvent(stream, user);

    return stream;
  }


}
