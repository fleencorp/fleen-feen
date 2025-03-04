package com.fleencorp.feen.mapper.stream.speaker;

import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeInfoSelect;
import com.fleencorp.feen.model.response.stream.speaker.StreamSpeakerResponse;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Utility class for mapping {@link StreamSpeaker} entities to {@link StreamSpeakerResponse} DTOs.
 *
 * <p>This class provides static methods to facilitate the conversion of {@code StreamSpeaker} entities into
 * their corresponding {@code StreamSpeakerResponse} representations. It is designed to be used as a helper
 * for transforming data between different layers of the application.</p>
 *
 * <p>All methods in this class are static and intended to be used in a stateless manner.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public final class StreamSpeakerMapper {

  private StreamSpeakerMapper() {}

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
  public static StreamSpeakerResponse toStreamSpeakerResponse(final StreamSpeaker entry) {
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

  public static StreamSpeakerResponse toStreamSpeakerResponse(final StreamAttendeeInfoSelect entry) {
    if (nonNull(entry)) {
      final StreamSpeakerResponse speakerResponse = new StreamSpeakerResponse();
      speakerResponse.setAttendeeId(entry.getAttendeeId());
      speakerResponse.setFullName(entry.getFullName());

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
  public static Set<StreamSpeakerResponse> toStreamSpeakerResponses(final Set<StreamSpeaker> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(StreamSpeakerMapper::toStreamSpeakerResponse)
        .collect(Collectors.toSet());
    }
    return Set.of();
  }

  /**
   * Converts a list of members to a list of {@link StreamSpeakerResponse} objects.
   *
   * <p>This method processes a list of {@link Member} objects, filtering out any null entries,
   * and maps each non-null {@link Member} to a {@link StreamSpeakerResponse} using the
   * {@link StreamSpeakerMapper#toStreamSpeakerResponse(StreamAttendeeInfoSelect)} (Member)} method.</p>
   *
   * @param entries the list of {@link Member} objects to be converted
   * @return a list of {@link StreamSpeakerResponse} objects, or an empty list if the input is null
   */
  public static List<StreamSpeakerResponse> toStreamSpeakerResponses(final List<StreamAttendeeInfoSelect> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(StreamSpeakerMapper::toStreamSpeakerResponse)
        .toList();
    }
    return List.of();
  }
}
