package com.fleencorp.feen.mapper.impl.stream;

import com.fleencorp.feen.constant.stream.*;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.feen.model.info.IsForKidsInfo;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.schedule.ScheduleTimeTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamSourceInfo;
import com.fleencorp.feen.model.info.stream.StreamStatusInfo;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamVisibilityInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.other.Organizer;
import com.fleencorp.feen.model.other.Schedule;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
public class StreamMapperImpl implements StreamMapper {

  private final MessageSource messageSource;

  public StreamMapperImpl(final MessageSource messageSource) {
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
   * Converts a {@link FleenStream} entry to a {@link FleenStreamResponse} object with detailed stream information.
   * The method populates the response with various stream details including visibility, type, status, schedule, and organizer information.
   * The join status is set to "not joined" based on whether the stream is private or public.
   *
   * @param entry the {@link FleenStream} entry to convert
   * @return a {@link FleenStreamResponse} object populated with stream information, or {@code null} if the input entry is {@code null}
   */
  @Override
  public FleenStreamResponse toFleenStreamResponse(final FleenStream entry) {
    if (nonNull(entry)) {
      final JoinStatus joinStatusNotJoinedPrivate = JoinStatus.notJoinedPrivate();
      final JoinStatus joinStatusNotJoinedPublic = JoinStatus.notJoinedPublic();
      final StreamSource streamSource = entry.getStreamSource();
      final StreamTimeType scheduleTimeType = entry.getStreamSchedule();
      final IsForKids forKids = IsForKids.by(entry.isForKids());
      final StreamTypeInfo streamTypeInfo = toStreamTypeInfo(entry.getStreamType());
      final StreamStatusInfo streamStatusInfo = toStreamStatusInfo(entry.getStreamStatus());
      final StreamVisibilityInfo visibilityInfo = toStreamVisibilityInfo(entry.getStreamVisibility());
      final IsDeletedInfo deletedInfo = toIsDeletedInfo(entry.getDeleted());

      final IsAttendingInfo attendingInfo = toIsAttendingInfo(false);
      final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = StreamAttendeeRequestToJoinStatusInfo.of();
      final JoinStatusInfo joinStatusInfo = entry.isPrivateOrProtected()
        ? JoinStatusInfo.of(joinStatusNotJoinedPrivate, translate(joinStatusNotJoinedPrivate.getMessageCode()), translate(joinStatusNotJoinedPrivate.getMessageCode2()))
        : JoinStatusInfo.of(joinStatusNotJoinedPublic, translate(joinStatusNotJoinedPublic.getMessageCode()), translate(joinStatusNotJoinedPrivate.getMessageCode2()));

      return FleenStreamResponse.builder()
          .id(entry.getStreamId())
          .title(entry.getTitle())
          .description(entry.getDescription())
          .tags(entry.getTags())
          .location(entry.getLocation())
          .otherSchedule(Schedule.of())
          .schedule(Schedule.of(entry.getScheduledStartDate(), entry.getScheduledEndDate(), entry.getTimezone()))
          .streamStatusInfo(streamStatusInfo)
          .streamVisibilityInfo(visibilityInfo)
          .deletedInfo(deletedInfo)
          .streamTypeInfo(streamTypeInfo)
          .streamSourceInfo(StreamSourceInfo.of(streamSource, translate(streamSource.getMessageCode())))
          .scheduleTimeTypeInfo(ScheduleTimeTypeInfo.of(scheduleTimeType, translate(scheduleTimeType.getMessageCode())))
          .forKidsIno(IsForKidsInfo.of(entry.isForKids(), translate(forKids.getMessageCode())))
          .organizer(Organizer.of(entry.getOrganizerName(), entry.getOrganizerEmail(), entry.getOrganizerPhone()))
          .streamLink(entry.getMaskedStreamLink())
          .streamLinkUnmasked(entry.getStreamLink())
          .streamLinkNotMasked(entry.getStreamLink())
          .totalAttending(entry.getTotalAttendees())
          .attendanceInfo(AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, attendingInfo))
          .build();
    }
    return null;
  }

  /**
   * Converts a {@link FleenStream} entry to a {@link FleenStreamResponse} object with approved join status and request-to-join status.
   * The join status is set to "joined chat space," and the request-to-join status is set to "approved."
   *
   * @param entry the {@link FleenStream} entry to convert
   * @return a {@link FleenStreamResponse} object with approved status information, or {@code null} if the input entry is {@code null}
   */
  @Override
  public FleenStreamResponse toFleenStreamResponseApproved(final FleenStream entry) {
    if (nonNull(entry)) {
      final FleenStreamResponse stream = toFleenStreamResponse(entry);
      final JoinStatus joinStatus = JoinStatus.joinedChatSpace();
      final StreamAttendeeRequestToJoinStatus requestToJoinStatus = StreamAttendeeRequestToJoinStatus.approved();

      final IsAttendingInfo isAttendingInfo = toIsAttendingInfo(true);
      final JoinStatusInfo joinStatusInfo = JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()));
      final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = StreamAttendeeRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode()));

      stream.setAttendanceInfo(AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo));
      return stream;
    }
    return null;
  }

  /**
   * Converts a {@link FleenStream} entry to a {@link FleenStreamResponse} object, excluding request-to-join and join status information.
   *
   * @param entry the {@link FleenStream} entry to convert
   * @return a {@link FleenStreamResponse} object with no join status or request-to-join status information, or {@code null} if the input entry is {@code null}
   */
  @Override
  public FleenStreamResponse toFleenStreamResponseNoJoinStatus(final FleenStream entry) {
    if (nonNull(entry)) {
      final FleenStreamResponse stream = toFleenStreamResponse(entry);
      stream.setAttendanceInfo(AttendanceInfo.of());
      return stream;
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
  @Override
  public List<FleenStreamResponse> toFleenStreamResponses(final List<FleenStream> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .filter(Objects::nonNull)
          .map(this::toFleenStreamResponse)
          .toList();
    }
    return List.of();
  }

  /**
   * Converts a list of {@link FleenStream} entries to a list of {@link FleenStreamResponse} objects, excluding join status.
   *
   * @param entries the list of {@link FleenStream} entries to convert
   * @return a list of {@link FleenStreamResponse} objects corresponding to the given entries, or an empty list if the input is {@code null} or empty
   */
  @Override
  public List<FleenStreamResponse> toFleenStreamResponsesNoJoinStatus(final List<FleenStream> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toFleenStreamResponseNoJoinStatus)
        .toList();
    }
    return List.of();
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
    return JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()));
  }

  /**
   * Converts the given stream to its corresponding stream status information.
   *
   * @param streamStatus the status of the stream
   * @return the stream status information, or {@code null} if the stream is {@code null}
   */
  @Override
  public StreamStatusInfo toStreamStatusInfo(final StreamStatus streamStatus) {
    if (nonNull(streamStatus)) {
      return StreamStatusInfo.of(streamStatus, translate(streamStatus.getMessageCode()), translate(streamStatus.getMessageCode2()), translate(streamStatus.getMessageCode3()));
    }
    return null;
  }

  @Override
  public AttendanceInfo toAttendanceInfo(final FleenStreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending) {
    final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = toRequestToJoinStatusInfo(requestToJoinStatus);
    final JoinStatusInfo joinStatusInfo = toJoinStatus(stream, requestToJoinStatus, isAttending);
    final IsAttendingInfo isAttendingInfo = toIsAttendingInfo(isAttending);

    return AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo);
  }

  /**
   * Converts a {@link FleenStream} to a {@link StreamVisibilityInfo}.
   *
   * <p>This method checks if the provided {@link FleenStream} is not null. If it is not, it retrieves
   * the {@link StreamVisibility} associated with the stream and constructs a {@link StreamVisibilityInfo}
   * by translating the message code from the {@link StreamVisibility}. If the stream is null, it returns null.</p>
   *
   * @param streamVisibility the visibility of the stream to be converted into a {@link StreamVisibilityInfo}
   * @return a {@link StreamVisibilityInfo} containing the stream's visibility and translated message, or null if the stream is null
   */
  @Override
  public StreamVisibilityInfo toStreamVisibilityInfo(final StreamVisibility streamVisibility) {
    if (nonNull(streamVisibility)) {
      return StreamVisibilityInfo.of(streamVisibility, translate(streamVisibility.getMessageCode()));
    }
    return null;
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
   * Converts the given FleenStreamResponse and JoinStatus to JoinStatusInfo.
   *
   * @param joinStatus the JoinStatus to be translated.
   * @return the JoinStatusInfo object with translated messages if both stream and joinStatus are non-null, otherwise null.
   */
  public JoinStatusInfo toJoinStatusInfo(final JoinStatus joinStatus) {
    if (nonNull(joinStatus)) {
      return JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()));
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
   * Converts the given attendance status into an {@link IsAttendingInfo} object.
   *
   * <p>This method determines the appropriate message code based on the attendance status
   * and translates it to a localized message.</p>
   *
   * @param deleted a boolean indicating whether the attendee is currently attending
   * @return an {@link IsAttendingInfo} object containing the attendance status and its corresponding localized message
   */
  @Override
  public IsDeletedInfo toIsDeletedInfo(final boolean deleted) {
    final IsDeleted isDeleted = IsDeleted.by(deleted);
    return IsDeletedInfo.of(deleted, translate(isDeleted.getMessageCode()), translate(isDeleted.getMessageCode2()));
  }

  /**
   * Converts the given attendance status into an {@link IsAttendingInfo} object.
   *
   * <p>This method determines the appropriate message code based on the attendance status
   * and translates it to a localized message.</p>
   *
   * @return an {@link IsAttendingInfo} object containing the attendance status and its corresponding localized message
   */
  @Override
  public StreamTypeInfo toStreamTypeInfo(final StreamType streamType) {
    return StreamTypeInfo.of(streamType, translate(streamType.getMessageCode()));
  }

  /**
   * Updates the stream response with the provided request-to-join status, join status, and attending status.
   *
   * @param stream the stream response object to be updated
   * @param requestToJoinStatus the status of the request to join the stream
   * @param joinStatus the join status to be set in the stream response
   * @param isAttending {@code true} if the user is attending the stream, {@code false} otherwise
   */
  @Override
  public void update(final FleenStreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final JoinStatus joinStatus, final boolean isAttending) {
    final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = toRequestToJoinStatusInfo(requestToJoinStatus);
    final JoinStatusInfo joinStatusInfo = toJoinStatusInfo(joinStatus);
    final IsAttendingInfo isAttendingInfo = toIsAttendingInfo(isAttending);

    stream.setAttendanceInfo(AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo));
  }

}
