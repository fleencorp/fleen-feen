package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import com.fleencorp.feen.model.response.stream.speaker.StreamSpeakerResponse;

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
public class StreamSpeakerMapper {

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
      return StreamSpeakerResponse.builder()
        .memberId(entry.getMemberId())
        .title(entry.getTitle())
        .description(entry.getDescription())
        .fullName(entry.getFullName())
        .build();
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
}
