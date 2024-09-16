package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.response.stream.EventOrStreamAttendeeResponse;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

/**
* StreamAttendeeMapper is responsible for mapping StreamAttendee entities to EventAttendeeResponse DTOs.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
public class StreamAttendeeMapper {

  /**
  * Converts a StreamAttendee entity to an EventAttendeeResponse DTO.
  *
  * @param entry the StreamAttendee entity to convert
  * @return the corresponding EventAttendeeResponse DTO, or null if the entry is null
  */
  public static EventOrStreamAttendeeResponse toEventAttendeeResponse(final StreamAttendee entry) {
    if (nonNull(entry)) {
      return EventOrStreamAttendeeResponse.builder()
          .id(entry.getMember().getMemberId())
          .name(entry.getMember().getFullName())
          .displayPhoto(entry.getMember().getProfilePhotoUrl())
          .comment(entry.getAttendeeComment())
          .organizerComment(entry.getOrganizerComment())
          .build();
    }
    return null;
  }

  /**
  * Converts a list of StreamAttendee entities to a list of EventAttendeeResponse DTOs.
  *
  * @param entries the list of StreamAttendee entities to convert
  * @return a list of corresponding EventAttendeeResponse DTOs, or an empty list if the entries are null or empty
  */
  public static List<EventOrStreamAttendeeResponse> toEventAttendeeResponses(final List<StreamAttendee> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .map(StreamAttendeeMapper::toEventAttendeeResponse)
          .filter(Objects::nonNull)
          .collect(toList());
    }
    return emptyList();
  }
}
