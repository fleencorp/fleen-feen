package com.fleencorp.feen.service.impl.stream;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import com.fleencorp.feen.model.dto.stream.AddStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.DeleteStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.response.stream.speaker.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.stream.FleenStreamRepository;
import com.fleencorp.feen.repository.stream.StreamSpeakerRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.stream.StreamSpeakerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsTrue;
import static com.fleencorp.feen.mapper.StreamSpeakerMapper.toStreamSpeakerResponses;
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
public class StreamSpeakerImpl implements StreamSpeakerService {

  private final FleenStreamRepository fleenStreamRepository;
  private final MemberRepository memberRepository;
  private final StreamSpeakerRepository streamSpeakerRepository;
  private final LocalizedResponse localizedResponse;

  /**
   * Constructs an instance of {@code StreamSpeakerImpl}.
   *
   * @param fleenStreamRepository The repository for accessing event or stream data.
   * @param streamSpeakerRepository The repository for managing stream speakers.
   * @param localizedResponse The service for creating localized responses.
   */
  public StreamSpeakerImpl(
      final FleenStreamRepository fleenStreamRepository,
      final MemberRepository memberRepository,
      final StreamSpeakerRepository streamSpeakerRepository,
      final LocalizedResponse localizedResponse) {
    this.fleenStreamRepository = fleenStreamRepository;
    this.memberRepository = memberRepository;
    this.streamSpeakerRepository = streamSpeakerRepository;
    this.localizedResponse = localizedResponse;
  }

  @Override
  public Object findSpeakers(final String nameOrFullNameOrUsernameOrEmailAddress) {
    return null;
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
    checkEventOrStreamExist(eventOrStreamId);
    // Convert the DTO to a set of StreamSpeaker objects linked to the specified event or stream
    final Set<StreamSpeaker> speakers = dto.toStreamSpeakers(FleenStream.of(eventOrStreamId));

    // Validate that all member IDs associated with the speakers exist
    checkIfNonNullMemberIdsExists(speakers);
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
    // Verify that the event or stream exists
    checkEventOrStreamExist(eventOrStreamId);
    // Convert the DTO to a set of StreamSpeaker entities, associating them with the specified event or stream
    final Set<StreamSpeaker> speakers = dto.toStreamSpeakers(FleenStream.of(eventOrStreamId));
    // Ensure all member IDs in the speakers are valid and exist
    checkIfNonNullMemberIdsExists(speakers);
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
    final Set<StreamSpeaker> speakers = dto.toStreamSpeakers();
    streamSpeakerRepository.deleteAll(speakers);

    return localizedResponse.of(DeleteStreamSpeakerResponse.of());
  }

  /**
   * Checks if an event or stream with the specified ID exists.
   * If the event or stream does not exist, throws a {@link FleenStreamNotFoundException}.
   *
   * @param eventOrStreamId The ID of the event or stream to check for existence.
   * @throws FleenStreamNotFoundException if the event or stream is not found.
   */
  protected void checkEventOrStreamExist(final Long eventOrStreamId) {
    fleenStreamRepository.findById(eventOrStreamId)
      .orElseThrow(() -> new FleenStreamNotFoundException(eventOrStreamId));
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
}
