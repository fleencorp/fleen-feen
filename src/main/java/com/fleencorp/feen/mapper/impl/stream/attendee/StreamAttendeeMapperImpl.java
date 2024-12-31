package com.fleencorp.feen.mapper.impl.stream.attendee;

import com.fleencorp.feen.mapper.impl.stream.StreamMapperImpl;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.mapper.stream.attendee.StreamAttendeeMapper;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

/**
 * A mapper class responsible for converting {@link StreamAttendee} and related entities
 * into their corresponding response objects or DTOs.
 * It also handles translation of message codes for localization.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class StreamAttendeeMapperImpl implements StreamAttendeeMapper {

  private final StreamMapper streamMapper;

  /**
   * Constructs a new {@code StreamAttendeeMapper} with the specified dependencies.
   *
   * @param streamMapper the {@link StreamMapperImpl} used for mapping stream-related entities
   */
  public StreamAttendeeMapperImpl(
      final StreamMapper streamMapper) {
    this.streamMapper = streamMapper;
  }

  protected StreamAttendeeResponse toStreamAttendeeResponse(final StreamAttendee entry) {
    if (nonNull(entry)) {
      return StreamAttendeeResponse.builder()
        .attendeeId(entry.getStreamAttendeeId())
        .attendeeMemberId(entry.getMemberId())
        .fullName(entry.getFullName())
        .displayPhoto(entry.getProfilePhoto())
        .comment(entry.getAttendeeComment())
        .organizerComment(entry.getOrganizerComment())
        .build();
    }
    return null;
  }

  @Override
  public StreamAttendeeResponse toStreamAttendeeResponse(final StreamAttendee entry, final FleenStreamResponse streamResponse) {
    if (nonNull(entry)) {
      // Convert the attendee's request to join status to a response-friendly format
      final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = streamMapper.toRequestToJoinStatus(entry.getRequestToJoinStatus());
      // Determine the join status info based on the stream and attendee details
      final JoinStatusInfo joinStatusInfo = streamMapper.toJoinStatus(streamResponse, entry.getRequestToJoinStatus(), entry.isAttending());
      // Determine the is attending information based on the user's status attendee status
      final IsAttendingInfo attendingInfo = streamMapper.toIsAttendingInfo(entry.isAttending());

      final StreamAttendeeResponse response = toStreamAttendeeResponse(entry);
      // Add the attendance info on the attendee response
      response.setAttendanceInfo(AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, attendingInfo));

      return response;
    }

    return null;
  }
}
