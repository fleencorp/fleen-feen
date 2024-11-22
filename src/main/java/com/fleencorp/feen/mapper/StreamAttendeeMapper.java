package com.fleencorp.feen.mapper;

import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.response.stream.EventOrStreamAttendeeResponse;
import com.fleencorp.feen.model.response.stream.base.FleenStreamResponse;
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
public class StreamAttendeeMapper {

  private final MessageSource messageSource;
  private final FleenStreamMapper streamMapper;

  /**
   * Constructs a new {@code StreamAttendeeMapper} with the specified dependencies.
   *
   * @param messageSource the {@link MessageSource} used for translating message codes
   * @param streamMapper the {@link FleenStreamMapper} used for mapping stream-related entities
   */
  public StreamAttendeeMapper(
      final MessageSource messageSource,
      final FleenStreamMapper streamMapper) {
    this.messageSource = messageSource;
    this.streamMapper = streamMapper;
  }

  /**
   * Translates the given message code into a localized message based on the current locale.
   *
   * @param messageCode the code representing the message to be localized
   * @return the localized message corresponding to the provided message code
   */
  public String translate(final String messageCode) {
    final Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(messageCode, null, locale);
  }

  /**
  * Converts a StreamAttendee entity to an EventOrStreamAttendeeResponse DTO.
  *
  * @param entry the StreamAttendee entity to convert
  * @return the corresponding EventOrStreamAttendeeResponse DTO, or null if the entry is null
  */
  public EventOrStreamAttendeeResponse toEventOrStreamAttendeeResponse(final StreamAttendee entry) {
    if (nonNull(entry)) {
      final StreamAttendeeRequestToJoinStatus requestToJoinStatus = entry.getRequestToJoinStatus();

      return EventOrStreamAttendeeResponse.builder()
          .id(entry.getMemberId())
          .name(entry.getFullName())
          .displayPhoto(entry.getMember().getProfilePhotoUrl())
          .comment(entry.getAttendeeComment())
          .organizerComment(entry.getOrganizerComment())
          .requestToJoinStatusInfo(StreamAttendeeRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode())))
          .joinStatusInfo(JoinStatusInfo.of())
          .build();
    }
    return null;
  }

  /**
   * Converts the given {@link StreamAttendee} and associated {@link FleenStreamResponse} into an
   * {@link EventOrStreamAttendeeResponse}.
   *
   * <p>The method maps the attendee's request-to-join status, join status, and attendance information
   * to the response object, using the provided stream response as context.</p>
   *
   * @param entry the {@link StreamAttendee} entity containing attendee details
   * @param streamResponse the {@link FleenStreamResponse} providing contextual stream information
   * @return the constructed {@link EventOrStreamAttendeeResponse}, or {@code null} if the given entry is {@code null}
   */
  public EventOrStreamAttendeeResponse toEventOrStreamAttendeeResponse(final StreamAttendee entry, final FleenStreamResponse streamResponse) {
    if (nonNull(entry)) {
      final StreamAttendeeRequestToJoinStatus requestToJoinStatus = entry.getRequestToJoinStatus();
      final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = streamMapper.toRequestToJoinStatus(requestToJoinStatus);
      final JoinStatusInfo joinStatusInfo = streamMapper.toJoinStatus(streamResponse, requestToJoinStatus, entry.isAttending());
      final IsAttendingInfo isAttendingInfo = streamMapper.toIsAttendingInfo(entry.isAttending());

      final EventOrStreamAttendeeResponse response = toEventOrStreamAttendeeResponse(entry);
      response.setRequestToJoinStatusInfo(requestToJoinStatusInfo);
      response.setJoinStatusInfo(joinStatusInfo);
      response.setIsAttendingInfo(isAttendingInfo);
      return response;
    }

    return null;
  }
}
