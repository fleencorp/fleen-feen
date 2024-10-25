package com.fleencorp.feen.mapper;

import com.fleencorp.feen.constant.security.mask.MaskedStreamLinkUri;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.other.Organizer;
import com.fleencorp.feen.model.other.Schedule;
import com.fleencorp.feen.model.response.stream.base.FleenStreamResponse;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

/**
* Mapper class for converting FleenStream entities to various DTOs.
*
* <p>This class provides static methods to map FleenStream entities to their
* corresponding Data Transfer Objects (DTOs). It includes methods to convert
* single entities as well as lists of entities.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public class FleenStreamMapper {

    private FleenStreamMapper() {}

  /**
  * Converts a FleenStream entity to a FleenStreamResponse.
  *
  * <p>This method takes a FleenStream entity and converts it into a FleenStreamResponse DTO.</p>
  *
  * @param entry the FleenStream entity to convert
  * @return a FleenStreamResponse DTO containing the stream details, or null if the input is null
  */
  public static FleenStreamResponse toFleenStreamResponse(final FleenStream entry) {
    if (nonNull(entry)) {
      return FleenStreamResponse.builder()
          .id(entry.getStreamId())
          .title(entry.getTitle())
          .description(entry.getDescription())
          .location(entry.getLocation())
          .schedule(Schedule.of(entry.getScheduledStartDate(), entry.getScheduledEndDate(), entry.getTimezone()))
          .visibility(entry.getStreamVisibility())
          .streamType(entry.getStreamType())
          .streamSource(entry.getStreamSource())
          .streamLink(nonNull(entry.getStreamLink()) ? MaskedStreamLinkUri.of(entry.getStreamLink(), entry.getStreamSource()) : null)
          .streamLinkUnmasked(entry.getStreamLink())
          .streamLinkNotMasked(entry.getStreamLink())
          .forKids(entry.getForKids())
          .status(entry.getStreamStatus())
          .organizer(Organizer.of(entry.getOrganizerName(), entry.getOrganizerEmail(), entry.getOrganizerPhone()))
          .build();
    }
    return null;
  }

  /**
   * Converts the given {@link FleenStream} instance to its corresponding
   * {@link FleenStreamResponse}. This method serves as an alias for {@code toFleenStreamResponse}.
   *
   * @param entry the {@link FleenStream} instance to be converted.
   * @return the corresponding {@link FleenStreamResponse} for the provided {@link FleenStream}.
   */
  public static FleenStreamResponse toEventResponse(final FleenStream entry) {
    return toFleenStreamResponse(entry);
  }


  /**
  * Converts a list of FleenStream entities to a list of FleenStreamResponse DTOs.
  *
  * <p>This method takes a list of FleenStream entities and converts each entity
  * to a FleenStreamResponse DTO. Null entries are filtered out from the result.</p>
  *
  * @param entries the list of FleenStream entities to convert
  * @return a list of FleenStreamResponse DTOs, or an empty list if the input is null or empty
  */
  public static List<FleenStreamResponse> toFleenStreamResponses(final List<FleenStream> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .filter(Objects::nonNull)
          .map(FleenStreamMapper::toFleenStreamResponse)
          .collect(toList());
    }
    return List.of();
  }

}
