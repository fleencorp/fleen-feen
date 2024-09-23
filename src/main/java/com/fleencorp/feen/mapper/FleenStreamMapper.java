package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;

import java.util.List;
import java.util.Objects;

import static com.fleencorp.feen.model.response.stream.FleenStreamResponse.Organizer;
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
          .timezone(entry.getTimezone())
          .scheduledStartDate(entry.getScheduledStartDate())
          .scheduledEndDate(entry.getScheduledEndDate())
          .visibility(entry.getStreamVisibility())
          .streamSource(entry.getStreamSource())
          .streamLink(entry.getStreamLink())
          .forKids(entry.getForKids())
          .status(entry.getStreamStatus())
          .organizer(Organizer.of(entry.getOrganizerName(), entry.getOrganizerEmail(), entry.getOrganizerPhone()))
          .build();
    }
    return null;
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
  public static List<FleenStreamResponse> toFleenStreams(final List<FleenStream> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .filter(Objects::nonNull)
          .map(FleenStreamMapper::toFleenStreamResponse)
          .collect(toList());
    }
    return List.of();
  }

}
