package com.fleencorp.feen.mapper.impl.stream.attendee;

import com.fleencorp.feen.mapper.impl.stream.StreamMapperImpl;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.mapper.stream.attendee.StreamAttendeeMapper;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsASpeakerInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

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

  private final MessageSource messageSource;
  private final StreamMapper streamMapper;

  /**
   * Constructs a new {@code StreamAttendeeMapper} with the specified dependencies.
   *
   * @param streamMapper the {@link StreamMapperImpl} used for mapping stream-related entities
   */
  public StreamAttendeeMapperImpl(
      final MessageSource messageSource,
      final StreamMapper streamMapper) {
    this.messageSource = messageSource;
    this.streamMapper = streamMapper;
  }

  /**
   * Translates a message code into the corresponding message based on the current locale.
   * The method uses the {@link MessageSource} to fetch the translated message.
   * It retrieves the locale from the {@link LocaleContextHolder} and looks up the message code in the resource bundle.
   *
   * @param messageCode the code of the message to translate
   * @return the translated message for the given message code, based on the current locale
   */
  private String translate(final String messageCode) {
    final Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(messageCode, null, locale);
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
      response.setUsername(entry.getMemberId());
      response.setFullName(entry.getFullName());
      response.setDisplayPhoto(entry.getProfilePhoto());
      response.setComment(entry.getAttendeeComment());
      response.setOrganizerComment(entry.getOrganizerComment());

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
  public StreamAttendeeResponse toStreamAttendeeResponse(final StreamAttendee entry, final FleenStreamResponse streamResponse) {
    if (nonNull(entry)) {
      // Convert the attendee's request to join status to a response-friendly format
      final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = streamMapper.toRequestToJoinStatus(entry.getRequestToJoinStatus());
      // Determine the join status info based on the stream and attendee details
      final JoinStatusInfo joinStatusInfo = streamMapper.toJoinStatus(streamResponse, entry.getRequestToJoinStatus(), entry.isAttending());
      // Determine the is attending information based on the user's status attendee status
      final IsAttendingInfo attendingInfo = streamMapper.toIsAttendingInfo(entry.isAttending());
      // Determine the is a speaker information based on the user's speaker status
      final IsASpeakerInfo isASpeakerInfo = streamMapper.toIsASpeakerInfo(entry.isASpeaker());

      // Convert to a stream attendee response
      final StreamAttendeeResponse response = toStreamAttendeeResponse(entry);
      // Get the attendance info
      final AttendanceInfo attendanceInfo = AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, attendingInfo, isASpeakerInfo);
      // Add the attendance info on the attendee response
      response.setAttendanceInfo(attendanceInfo);

      return response;
    }
    return null;
  }
}
