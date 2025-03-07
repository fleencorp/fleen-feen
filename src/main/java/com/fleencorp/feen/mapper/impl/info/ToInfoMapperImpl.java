package com.fleencorp.feen.mapper.impl.info;

import com.fleencorp.feen.constant.common.JoinStatus;
import com.fleencorp.feen.constant.stream.attendee.IsASpeaker;
import com.fleencorp.feen.constant.stream.attendee.IsAttending;
import com.fleencorp.feen.constant.stream.attendee.IsOrganizer;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.mapper.stream.ToInfoMapper;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsASpeakerInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsOrganizerInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

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
@Component
@Slf4j
public class ToInfoMapperImpl implements ToInfoMapper {

  private final MessageSource messageSource;

  public ToInfoMapperImpl(
      final MessageSource messageSource) {
    this.messageSource = messageSource;
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
   * Converts the given request-to-join status into its corresponding status information.
   *
   * @param requestToJoinStatus the status of the request to join the stream
   * @return the request-to-join status information for the given status
   */
  @Override
  public StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatus(final StreamAttendeeRequestToJoinStatus requestToJoinStatus) {
    return StreamAttendeeRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode()));
  }


  /**
   * Converts the given {@link JoinStatus} into a {@link JoinStatusInfo} object.
   *
   * <p>This method checks if the provided {@link JoinStatus} is non-null and, if so, creates a
   * {@link JoinStatusInfo} instance using the {@link JoinStatus}, along with translations of its
   * associated message codes for localization purposes.</p>
   *
   * <p>The resulting {@link JoinStatusInfo} contains the join status details, including localized
   * messages that can be used to provide feedback to the user based on their join status.</p>
   *
   * @param joinStatus The {@link JoinStatus} to be converted into a {@link JoinStatusInfo} object.
   * @return The {@link JoinStatusInfo} object containing the join status and message codes, or
   *         <code>null</code> if the {@link JoinStatus} is <code>null</code>.
   */
  @Override
  public JoinStatusInfo toJoinStatusInfo(final JoinStatus joinStatus) {
    if (nonNull(joinStatus)) {
      return JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()), translate(joinStatus.getMessageCode3()));
    }
    return null;
  }

  /**
   * Converts the given stream response and request-to-join status into the corresponding join status information.
   *
   * @param stream the stream response to be used for determining the join status
   * @param requestToJoinStatus the status of the request to join the stream
   * @param isAttending {@code true} if the user is attending the stream, {@code false} otherwise
   * @return the join status information for the given stream and request-to-join status
   */
  @Override
  public JoinStatusInfo toJoinStatus(final FleenStreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending) {
    final JoinStatus joinStatus = JoinStatus.getJoinStatus(requestToJoinStatus, stream.getVisibility(), stream.hasHappened(), isAttending);
    return JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()), translate(joinStatus.getMessageCode3()));
  }

  /**
   * Converts the provided stream and attendee details into an {@code AttendanceInfo} object.
   *
   * <p>This method generates an {@code AttendanceInfo} object using the provided stream details,
   * attendee request-to-join status, attendance status, and speaker status. It internally
   * converts each of these components into their respective response-friendly formats:
   * {@code StreamAttendeeRequestToJoinStatusInfo}, {@code JoinStatusInfo}, and {@code IsAttendingInfo}.</p>
   *
   * @param stream the stream details represented by {@code FleenStreamResponse}
   * @param requestToJoinStatus the attendee's request-to-join status
   * @param isAttending boolean flag indicating if the attendee is attending
   * @param isASpeaker boolean flag indicating if the attendee is also a speaker
   * @return an {@code AttendanceInfo} object containing the attendee's request-to-join, join, and attendance info
   */
  @Override
  public AttendanceInfo toAttendanceInfo(final FleenStreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending, final boolean isASpeaker) {
    final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = toRequestToJoinStatusInfo(requestToJoinStatus);
    final JoinStatusInfo joinStatusInfo = toJoinStatus(stream, requestToJoinStatus, isAttending);
    final IsAttendingInfo isAttendingInfo = toIsAttendingInfo(isAttending);
    final IsASpeakerInfo isASpeakerInfo = toIsASpeakerInfo(isASpeaker);

    return AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo, isASpeakerInfo);
  }

  /**
   * Converts the given FleenStreamResponse and StreamAttendeeRequestToJoinStatus
   * to StreamAttendeeRequestToJoinStatusInfo.
   *
   * @param requestToJoinStatus the StreamAttendeeRequestToJoinStatus to be translated.
   * @return the StreamAttendeeRequestToJoinStatusInfo object with translated message
   * if both stream and requestToJoinStatus are non-null, otherwise null.
   */
  @Override
  public StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatusInfo(final StreamAttendeeRequestToJoinStatus requestToJoinStatus) {
    if (nonNull(requestToJoinStatus)) {
      return StreamAttendeeRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode()));
    }
    return null;
  }

  /**
   * Converts the given attendance status into an {@link IsAttendingInfo} object.
   *
   * <p>This method determines the appropriate message code based on the attendance status
   * and translates it to a localized message.</p>
   *
   * @param isAttending a boolean indicating whether the attendee is currently attending
   * @return an {@link IsAttendingInfo} object containing the attendance status and its corresponding localized message
   */
  @Override
  public IsAttendingInfo toIsAttendingInfo(final boolean isAttending) {
    return IsAttendingInfo.of(isAttending, translate(IsAttending.by(isAttending).getMessageCode()));
  }

  /**
   * Converts the given speaker status into an {@code IsASpeakerInfo} object.
   *
   * <p>This method determines whether the provided status indicates the user is a speaker
   * and constructs an {@code IsASpeakerInfo} instance with the appropriate translated message.
   * The message is resolved using the {@code IsASpeaker} enum, which provides the relevant
   * message code for translation based on the speaker status.</p>
   *
   * @param aSpeaker the boolean flag indicating whether the user is a speaker
   * @return an {@code IsASpeakerInfo} object containing the speaker status and a localized message
   */
  @Override
  public IsASpeakerInfo toIsASpeakerInfo(final boolean aSpeaker) {
    final IsASpeaker isASpeaker = IsASpeaker.by(aSpeaker);

    return IsASpeakerInfo.of(aSpeaker, translate(isASpeaker.getMessageCode()), translate(isASpeaker.getMessageCode2()));
  }

  /**
   * Converts a boolean value representing whether an attendee is an organizer into an
   * {@link IsOrganizerInfo} DTO.
   *
   * <p>This method maps the boolean value to an {@link IsOrganizer} enum, then generates
   * an {@link IsOrganizerInfo} object containing localized messages associated with the
   * organizer status. It uses the {@code translate} method to resolve the message codes
   * to localized strings for both the primary and secondary message codes.</p>
   *
   * @param organizer a boolean indicating whether the attendee is an organizer
   * @return an {@link IsOrganizerInfo} object containing the organizer status and
   *         its associated localized messages
   */
  @Override
  public IsOrganizerInfo toIsOrganizerInfo(final boolean organizer) {
    final IsOrganizer isOrganizer = IsOrganizer.by(organizer);

    return IsOrganizerInfo.of(organizer, translate(isOrganizer.getMessageCode()), translate(isOrganizer.getMessageCode2()));
  }

}
