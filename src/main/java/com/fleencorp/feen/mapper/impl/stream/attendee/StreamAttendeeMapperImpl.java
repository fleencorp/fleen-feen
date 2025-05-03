package com.fleencorp.feen.mapper.impl.stream.attendee;

import com.fleencorp.feen.mapper.impl.stream.StreamMapperImpl;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.mapper.stream.ToInfoMapper;
import com.fleencorp.feen.mapper.stream.attendee.StreamAttendeeMapper;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsASpeakerInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsOrganizerInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.response.stream.StreamResponse;
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
  private final ToInfoMapper toInfoMapper;

  /**
   * Constructs a new {@code StreamAttendeeMapper} with the specified dependencies.
   *
   * @param streamMapper the {@link StreamMapperImpl} used for mapping stream-related entities
   */
  public StreamAttendeeMapperImpl(
      final StreamMapper streamMapper,
      final ToInfoMapper toInfoMapper) {
    this.streamMapper = streamMapper;
    this.toInfoMapper = toInfoMapper;
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
      // Determine the is organizer information based on the user's speaker status
      final IsOrganizerInfo isOrganizerInfo = toInfoMapper.toIsOrganizerInfo(entry.isOrganizer());

      // Convert to a stream attendee response
      final StreamAttendeeResponse response = toStreamAttendeeResponse(entry);
      // Add the attendance info on the attendee response
      response.setAttendanceInfo(attendanceInfo);
      // Add the is organizer info
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
      // Determine the is organizer information based on the user's speaker status
      final IsOrganizerInfo isOrganizerInfo = toInfoMapper.toIsOrganizerInfo(entry.isOrganizer());

      // Convert to a stream attendee response
      final StreamAttendeeResponse response = toStreamAttendeeResponsePublic(entry);
      // Add the attendance info on the attendee response
      response.setAttendanceInfo(attendanceInfo);
      // Add the is organizer info
      response.setIsOrganizerInfo(isOrganizerInfo);

      return response;
    }
    return null;
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
    // Convert the attendee's request to join status to a response-friendly format
    final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = toInfoMapper.toRequestToJoinStatus(entry.getRequestToJoinStatus());
    // Determine the join status info based on the stream and attendee details
    final JoinStatusInfo joinStatusInfo = toInfoMapper.toJoinStatus(streamResponse, entry.getRequestToJoinStatus(), entry.isAttending());
    // Determine the is attending information based on the user's status attendee status
    final IsAttendingInfo attendingInfo = toInfoMapper.toIsAttendingInfo(entry.isAttending());
    // Determine the is a speaker information based on the user's speaker status
    final IsASpeakerInfo isASpeakerInfo = toInfoMapper.toIsASpeakerInfo(entry.isASpeaker());


    // Return the attendance info
    return AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, attendingInfo, isASpeakerInfo);
  }
}
