package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.response.event.base.EventResponse;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;

import static java.util.Objects.nonNull;

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
public class EventMapper {

  /**
  * Converts a FleenStream entity to a FleenStreamResponse.
  *
  * <p>This method takes a FleenStream entity and converts it into a FleenStreamResponse DTO.</p>
  *
  * @param entry the FleenStream entity to convert
  * @return a FleenStreamResponse DTO containing the stream details, or null if the input is null
  */
  public static EventResponse toEventResponse(final FleenStream entry) {
    if (nonNull(entry)) {
      return EventResponse.builder()
          .title(entry.getTitle())
          .description(entry.getDescription())
          .location(entry.getLocation())
          .schedule(FleenStreamResponse.Schedule.of(entry.getScheduledStartDate(), entry.getScheduledEndDate(), entry.getTimezone()))
          .visibility(entry.getStreamVisibility())
          .streamSource(entry.getStreamSource())
          .streamLink(entry.getStreamLink())
          .forKids(entry.getForKids())
          .build();
    }
    return null;
  }
}