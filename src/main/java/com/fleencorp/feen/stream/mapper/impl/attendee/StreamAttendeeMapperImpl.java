package com.fleencorp.feen.stream.mapper.impl.attendee;

import com.fleencorp.feen.common.model.info.JoinStatusInfo;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.stream.mapper.attendee.StreamAttendeeMapper;
import com.fleencorp.feen.stream.mapper.common.StreamInfoMapper;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsASpeakerInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsAttendingInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsOrganizerInfo;
import com.fleencorp.feen.stream.model.info.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public class StreamAttendeeMapperImpl extends BaseMapper implements StreamAttendeeMapper {

  private final StreamInfoMapper streamInfoMapper;

  public StreamAttendeeMapperImpl(
      final StreamInfoMapper streamInfoMapper,
      final MessageSource messageSource) {
    super(messageSource);
    this.streamInfoMapper = streamInfoMapper;
  }

  /**
   * Converts a {@link StreamAttendee} entry to a {@link StreamAttendeeResponse}.
   *
   * <p>This method takes a {@code StreamAttendee} object and maps its fields to a new {@code StreamAttendeeResponse} object.
   * If the provided {@code StreamAttendee} is {@code null}, it returns {@code null}.</p>
   *
   * @param entry the {@code StreamAttendee} object to convert
   * @return the populated {@code StreamAttendeeResponse} object, or {@code null} if the input is {@code null}
   */
  protected StreamAttendeeResponse toStreamAttendeeResponse(final StreamAttendee entry) {
    if (nonNull(entry)) {
      final StreamAttendeeResponse response = new StreamAttendeeResponse();

      response.setAttendeeId(entry.getAttendeeId());
      response.setUsername(entry.getUsername());
      response.setFullName(entry.getFullName());
      response.setDisplayPhoto(entry.getProfilePhoto());
      response.setComment(entry.getAttendeeComment());
      response.setOrganizerComment(entry.getOrganizerComment());

      return response;
    }
    return null;
  }

  /**
   * Converts a StreamAttendee entity into a public-facing StreamAttendeeResponse DTO.
   *
   * <p>This method is used to generate a public-facing response with basic attendee details,
   * such as the attendee ID, username, full name, and profile photo. It excludes private
   * or sensitive information, providing only the publicly visible attributes.</p>
   *
   * @param entry the stream attendee entity to convert
   * @return a {@link StreamAttendeeResponse} containing public attendee information, or {@code null} if the entry is null
   */
  protected StreamAttendeeResponse toStreamAttendeeResponsePublic(final StreamAttendee entry) {
    if (nonNull(entry)) {
      final StreamAttendeeResponse response = new StreamAttendeeResponse();

      response.setAttendeeId(entry.getAttendeeId());
      response.setUsername(entry.getUsername());
      response.setFullName(entry.getFullName());
      response.setDisplayPhoto(entry.getProfilePhoto());

      return response;
    }
    return null;
  }

  /**
   * Converts a {@link StreamAttendee} entry to a {@link StreamAttendeeResponse} and populates attendance-related information.
   *
   * <p>This method maps a {@code StreamAttendee} object to a {@code StreamAttendeeResponse}, along with attendance details
   * based on the attendee's request to join status, the stream details, and the attendee's current attending status.
   * It uses the {@code streamMapper} to convert various status-related information into response-friendly formats.
   * If the {@code StreamAttendee} is {@code null}, it returns {@code null}.</p>
   *
   * @param entry the {@code StreamAttendee} object to convert
   * @param streamResponse the {@code FleenStreamResponse} containing the stream details
   * @return the populated {@code StreamAttendeeResponse} with attendance information, or {@code null} if the input {@code StreamAttendee} is {@code null}
   */
  @Override
  public StreamAttendeeResponse toStreamAttendeeResponse(final StreamAttendee entry, final StreamResponse streamResponse) {
    if (nonNull(entry)) {
      // Get the attendance info
      final AttendanceInfo attendanceInfo = getAttendanceInfo(entry, streamResponse);
      final IsOrganizerInfo isOrganizerInfo = streamInfoMapper.toIsOrganizerInfo(entry.isOrganizer());

      final StreamAttendeeResponse response = toStreamAttendeeResponse(entry);
      response.setAttendanceInfo(attendanceInfo);
      response.setIsOrganizerInfo(isOrganizerInfo);

      return response;
    }
    return null;
  }

  /**
   * Converts a StreamAttendee entity into a public-facing StreamAttendeeResponse DTO,
   * incorporating stream-specific attendance and organizer information.
   *
   * <p>This method generates a public-facing response with both basic attendee details
   * (e.g., ID, username, full name, and profile photo) and additional context, such as
   * the attendee's attendance info and organizer status. The attendance and organizer
   * details are determined from both the attendee entry and the associated stream response.</p>
   *
   * @param entry the stream attendee entity to convert
   * @param streamResponse the stream context used to enhance the response with additional details
   * @return a {@link StreamAttendeeResponse} containing public attendee information along with
   *         attendance and organizer info, or {@code null} if the entry is null
   */
  @Override
  public StreamAttendeeResponse toStreamAttendeeResponsePublic(final StreamAttendee entry, final StreamResponse streamResponse) {
    if (nonNull(entry)) {
      // Get the attendance info
      final AttendanceInfo attendanceInfo = getAttendanceInfo(entry, streamResponse);
      final IsOrganizerInfo isOrganizerInfo = streamInfoMapper.toIsOrganizerInfo(entry.isOrganizer());

      final StreamAttendeeResponse response = toStreamAttendeeResponsePublic(entry);
      response.setAttendanceInfo(attendanceInfo);
      response.setIsOrganizerInfo(isOrganizerInfo);

      return response;
    }
    return null;
  }

  /**
   * Converts a list of {@link StreamAttendee} entities to their corresponding
   * public {@link StreamAttendeeResponse} representations for a given stream.
   *
   * <p>Null and empty input lists are safely handled by returning an empty collection.
   * Any null elements within the list are also filtered out during the conversion.</p>
   *
   * @param entries the list of {@link StreamAttendee} entities to convert
   * @param streamResponse the {@link StreamResponse} associated with the attendees
   * @return a collection of {@link StreamAttendeeResponse} representing the public view of each attendee
   */
  @Override
  public Collection<StreamAttendeeResponse> toStreamAttendeeResponsesPublic(final List<StreamAttendee> entries, final StreamResponse streamResponse) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(streamAttendee -> toStreamAttendeeResponsePublic(streamAttendee, streamResponse))
        .toList();
    }

    return List.of();
  }

  /**
   * Retrieves the attendance information for the provided stream attendee.
   *
   * <p>This method converts the attendee's request-to-join status and other details into response-friendly formats
   * and gathers the overall attendance information, including request-to-join status, join status,
   * attendance status, and whether the attendee is marked as a speaker.</p>
   *
   * @param entry the stream attendee whose attendance information is being retrieved
   * @param streamResponse the response details for the stream
   * @return the complete attendance information for the stream attendee
   */
  private AttendanceInfo getAttendanceInfo(final StreamAttendee entry, final StreamResponse streamResponse) {
    if (nonNull(entry) && nonNull(streamResponse) ) {
      final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = streamInfoMapper.toRequestToJoinStatusInfo(entry.getRequestToJoinStatus());
      final JoinStatusInfo joinStatusInfo = streamInfoMapper.toJoinStatus(streamResponse, entry.getRequestToJoinStatus(), entry.isAttending());
      final IsAttendingInfo attendingInfo = streamInfoMapper.toIsAttendingInfo(entry.isAttending());
      final IsASpeakerInfo isASpeakerInfo = streamInfoMapper.toIsASpeakerInfo(entry.isASpeaker());

      return AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, attendingInfo, isASpeakerInfo);
    }

    return AttendanceInfo.of();
  }
}
