package com.fleencorp.feen.stream.mapper.impl.speaker;

import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.mapper.speaker.StreamSpeakerMapper;
import com.fleencorp.feen.stream.model.domain.StreamSpeaker;
import com.fleencorp.feen.stream.model.response.speaker.StreamSpeakerResponse;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.nonNull;

@Component
public final class StreamSpeakerMapperImpl implements StreamSpeakerMapper {

  private StreamSpeakerMapperImpl() {}

  /**
   * Converts a {@link StreamSpeaker} entity to a {@link StreamSpeakerResponse} DTO.
   *
   * <p>This method maps the properties of the provided {@code StreamSpeaker} entity to a new
   * {@code StreamSpeakerResponse} object. If the provided entity is {@code null}, the method
   * returns {@code null}.</p>
   *
   * @param entry the {@link StreamSpeaker} entity to be converted; may be {@code null}
   * @return a {@link StreamSpeakerResponse} DTO representing the {@code StreamSpeaker} entity,
   *         or {@code null} if the provided entity is {@code null}
   */
  private static StreamSpeakerResponse toStreamSpeakerResponse(final StreamSpeaker entry) {
    if (nonNull(entry)) {
      final StreamSpeakerResponse speakerResponse = new StreamSpeakerResponse();
      speakerResponse.setSpeakerId(entry.getSpeakerId());
      speakerResponse.setAttendeeId(entry.getAttendeeId());
      speakerResponse.setFullName(entry.getFullName());
      speakerResponse.setTitle(entry.getTitle());
      speakerResponse.setDescription(entry.getDescription());

      return speakerResponse;
    }
    return null;
  }

  /**
   * Converts a {@link IsAttendee} entry into a {@link StreamSpeakerResponse}.
   *
   * <p>
   * This method takes a non-null {@code IsAttendee} entry and maps its relevant fields
   * (attendee ID and full name) to a new {@code StreamSpeakerResponse}. If the entry is {@code null},
   * the method returns {@code null}.
   * </p>
   *
   * @param entry the {@code IsAttendee} object containing attendee information
   * @return a {@code StreamSpeakerResponse} populated with the attendee's ID and full name, or {@code null} if the entry is {@code null}
   */
  private static StreamSpeakerResponse toStreamSpeakerResponse(final IsAttendee entry) {
    if (nonNull(entry)) {
      final StreamSpeakerResponse speakerResponse = new StreamSpeakerResponse();
      speakerResponse.setAttendeeId(entry.getAttendeeId());
      speakerResponse.setFullName(entry.getFullName());
      speakerResponse.setUsername(entry.getUsername());

      return speakerResponse;
    }
    return null;
  }

  /**
   * Converts a {@link Set} of {@link StreamSpeaker} entities to a {@link Set} of {@link StreamSpeakerResponse} DTOs.
   *
   * <p>This method maps each non-{@code null} {@code StreamSpeaker} entity in the provided set to a
   * {@code StreamSpeakerResponse} object. If the provided set is {@code null}, the method returns an empty set.</p>
   *
   * @param entries the {@link Set} of {@link StreamSpeaker} entities to be converted; may be {@code null}
   * @return a {@link Set} of {@link StreamSpeakerResponse} DTOs representing the {@code StreamSpeaker} entities,
   *         or an empty {@link Set} if the provided set is {@code null}
   */
  @Override
  public List<StreamSpeakerResponse> toStreamSpeakerResponses(final List<StreamSpeaker> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(StreamSpeakerMapperImpl::toStreamSpeakerResponse)
        .toList();
    }
    return List.of();
  }

  /**
   * Converts a list of members to a list of {@link StreamSpeakerResponse} objects.
   *
   * <p>This method processes a list of {@link Member} objects, filtering out any null entries,
   * and maps each non-null {@link Member} to a {@link StreamSpeakerResponse} using the
   * {@link StreamSpeakerMapperImpl#toStreamSpeakerResponse(IsAttendee)} (Member)} method.</p>
   *
   * @param entries the list of {@link Member} objects to be converted
   * @return a list of {@link StreamSpeakerResponse} objects, or an empty list if the input is null
   */
  @Override
  public List<StreamSpeakerResponse> toStreamSpeakerResponsesByProjection(final List<IsAttendee> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(StreamSpeakerMapperImpl::toStreamSpeakerResponse)
        .toList();
    }
    return List.of();
  }
}
