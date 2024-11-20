package com.fleencorp.feen.mapper;

import com.fleencorp.feen.constant.stream.*;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.info.IsForKidsInfo;
import com.fleencorp.feen.model.info.schedule.ScheduleTimeTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamSourceInfo;
import com.fleencorp.feen.model.info.stream.StreamStatusInfo;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamVisibilityInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.other.Organizer;
import com.fleencorp.feen.model.other.Schedule;
import com.fleencorp.feen.model.response.stream.base.FleenStreamResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
@Component
public class FleenStreamMapper {

  private final MessageSource messageSource;;

  public FleenStreamMapper(final MessageSource messageSource) {
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
  public String translate(final String messageCode) {
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
  public FleenStreamResponse toFleenStreamResponse(final FleenStream entry) {
    if (nonNull(entry)) {
      final JoinStatus joinStatusNotJoinedPrivate = JoinStatus.notJoinedPrivate();
      final JoinStatus joinStatusNotJoinedPublic = JoinStatus.notJoinedPublic();
      final StreamVisibility visibility = entry.getStreamVisibility();
      final StreamType streamType = entry.getStreamType();
      final StreamSource streamSource = entry.getStreamSource();
      final StreamStatus streamStatus = entry.getStreamStatus();
      final StreamTimeType scheduleTimeType = entry.getStreamSchedule();
      final IsForKids isForKids = IsForKids.by(entry.isForKids());

      return FleenStreamResponse.builder()
          .id(entry.getStreamId())
          .title(entry.getTitle())
          .description(entry.getDescription())
          .tags(entry.getTags())
          .location(entry.getLocation())
          .otherSchedule(Schedule.of())
          .schedule(Schedule.of(entry.getScheduledStartDate(), entry.getScheduledEndDate(), entry.getTimezone()))
          .streamVisibilityInfo(StreamVisibilityInfo.of(visibility, translate(visibility.getMessageCode())))
          .streamTypeInfo(StreamTypeInfo.of(streamType, translate(streamType.getMessageCode())))
          .streamSourceInfo(StreamSourceInfo.of(streamSource, translate(streamSource.getMessageCode())))
          .streamStatusInfo(StreamStatusInfo.of(streamStatus, translate(streamStatus.getMessageCode())))
          .scheduleTimeTypeInfo(ScheduleTimeTypeInfo.of(scheduleTimeType, translate(scheduleTimeType.getMessageCode())))
          .forKidsIno(IsForKidsInfo.of(entry.isForKids(), translate(isForKids.getMessageCode())))
          .organizer(Organizer.of(entry.getOrganizerName(), entry.getOrganizerEmail(), entry.getOrganizerPhone()))
          .requestToJoinStatusInfo(StreamAttendeeRequestToJoinStatusInfo.of())
          .joinStatusInfo(entry.isPrivate()
            ? JoinStatusInfo.of(joinStatusNotJoinedPrivate, translate(joinStatusNotJoinedPrivate.getMessageCode()), translate(joinStatusNotJoinedPrivate.getMessageCode2()))
            : JoinStatusInfo.of(joinStatusNotJoinedPublic, translate(joinStatusNotJoinedPublic.getMessageCode()), translate(joinStatusNotJoinedPrivate.getMessageCode2())))
          .isAttendingInfo(toIsAttendingInfo(false))
          .streamLink(entry.getMaskedStreamLink())
          .streamLinkUnmasked(entry.getStreamLink())
          .streamLinkNotMasked(entry.getStreamLink())
          .totalAttending(entry.getTotalAttendees())
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
  public FleenStreamResponse toFleenStreamResponseApproved(final FleenStream entry) {
    if (nonNull(entry)) {
      final FleenStreamResponse streamResponse = toFleenStreamResponse(entry);
      final JoinStatus joinStatus = JoinStatus.joinedChatSpace();
      final StreamAttendeeRequestToJoinStatus requestToJoinStatus = StreamAttendeeRequestToJoinStatus.approved();

      streamResponse.setIsAttendingInfo(toIsAttendingInfo(true));
      streamResponse.setJoinStatusInfo(JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2())));
      streamResponse.setRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode())));
      return streamResponse;
    }
    return null;
  }

  /**
   * Converts a {@link FleenStream} entry to a {@link FleenStreamResponse} object, excluding request-to-join and join status information.
   *
   * @param entry the {@link FleenStream} entry to convert
   * @return a {@link FleenStreamResponse} object with no join status or request-to-join status information, or {@code null} if the input entry is {@code null}
   */
  public FleenStreamResponse toFleenStreamResponseNoJoinStatus(final FleenStream entry) {
    if (nonNull(entry)) {
      final FleenStreamResponse streamResponse = toFleenStreamResponse(entry);
      streamResponse.setRequestToJoinStatusInfo(null);
      streamResponse.setJoinStatusInfo(null);
      return streamResponse;
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
  public List<FleenStreamResponse> toFleenStreamResponses(final List<FleenStream> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .filter(Objects::nonNull)
          .map(this::toFleenStreamResponse)
          .collect(toList());
    }
    return List.of();
  }

  /**
   * Converts a list of {@link FleenStream} entries to a list of {@link FleenStreamResponse} objects, excluding join status.
   *
   * @param entries the list of {@link FleenStream} entries to convert
   * @return a list of {@link FleenStreamResponse} objects corresponding to the given entries, or an empty list if the input is {@code null} or empty
   */
  public List<FleenStreamResponse> toFleenStreamResponsesNoJoinStatus(final List<FleenStream> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toFleenStreamResponseNoJoinStatus)
        .collect(toList());
    }
    return List.of();
  }

  /**
   * Converts the given request-to-join status into its corresponding status information.
   *
   * @param requestToJoinStatus the status of the request to join the stream
   * @return the request-to-join status information for the given status
   */
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
  public JoinStatusInfo toJoinStatus(final FleenStreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending) {
    final JoinStatus joinStatus = JoinStatus.getJoinStatus(requestToJoinStatus, stream.getVisibility(), stream.hasHappened(), isAttending);
    return JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()));
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
  public IsAttendingInfo toIsAttendingInfo(final boolean isAttending) {
    return IsAttendingInfo.of(isAttending, translate(IsAttending.by(isAttending).getMessageCode()));
  }

  /**
   * Converts the given stream to its corresponding stream status information.
   *
   * @param stream the stream to convert
   * @return the stream status information, or {@code null} if the stream is {@code null}
   */
  public StreamStatusInfo toStreamStatus(final FleenStream stream) {
    if (nonNull(stream)) {
      final StreamStatus streamStatus = stream.getStreamStatus();
      return StreamStatusInfo.of(streamStatus, translate(streamStatus.getMessageCode()));
    }
    return null;
  }

  /**
   * Updates a {@link FleenStreamResponse} representation of the given {@link FleenStream} with the
   * specified request-to-join status and attendance status.
   *
   * <p>This method creates a {@link FleenStreamResponse} from the given {@code stream}, then updates
   * it with the provided {@code requestToJoinStatus} and {@code isAttending} values.</p>
   *
   * @param stream the {@link FleenStream} entity to be converted and updated
   * @param requestToJoinStatus the request-to-join status to apply to the response
   * @param isAttending the attendance status to apply to the response
   * @return the updated {@link FleenStreamResponse}
   */
  public FleenStreamResponse update(final FleenStream stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending) {
    final FleenStreamResponse streamResponse = toFleenStreamResponse(stream);
    return update(streamResponse, requestToJoinStatus, isAttending);
  }

  /**
   * Updates the stream response with the provided request-to-join status and attending status, and computes the join status.
   *
   * @param stream the stream response object to be updated
   * @param requestToJoinStatus the status of the request to join the stream
   * @param isAttending {@code true} if the user is attending the stream, {@code false} otherwise
   * @return the updated stream response with the new statuses
   */
  public FleenStreamResponse update(final FleenStreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending) {
    final JoinStatus joinStatus = JoinStatus.getJoinStatus(requestToJoinStatus, stream.getVisibility(), stream.hasHappened(), isAttending);
    update(stream, requestToJoinStatus, joinStatus, isAttending);
    return stream;
  }

  /**
   * Updates the stream response with the provided request-to-join status, join status, and attending status.
   *
   * @param stream the stream response object to be updated
   * @param requestToJoinStatus the status of the request to join the stream
   * @param joinStatus the join status to be set in the stream response
   * @param isAttending {@code true} if the user is attending the stream, {@code false} otherwise
   */
  public void update(final FleenStreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final JoinStatus joinStatus, final boolean isAttending) {
    updateRequestToJoinStatus(stream, requestToJoinStatus);
    updateJoinStatus(stream, joinStatus);
    updateAttendingStatus(stream, isAttending);
  }

  /**
   * Updates the request-to-join status information in the given stream response.
   *
   * @param stream the stream response object to be updated
   * @param requestToJoinStatus the status of the request to join the stream
   */
  private void updateRequestToJoinStatus(final FleenStreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus) {
    if (nonNull(stream) && nonNull(requestToJoinStatus)) {
      stream.setRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode())));
    }
  }

  /**
   * Updates the join status information in the given stream response.
   *
   * @param stream the response object containing stream information to be updated
   * @param joinStatus the join status to be set in the stream response
   */
  private void updateJoinStatus(final FleenStreamResponse stream, final JoinStatus joinStatus) {
    if (nonNull(stream) && nonNull(joinStatus)) {
      stream.setJoinStatusInfo(JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2())));
    }
  }

  /**
   * Updates the attending status information in the given stream response.
   *
   * @param stream the response object containing stream information to be updated
   * @param isAttending {@code true} if the user is attending the stream, {@code false} otherwise
   */
  private void updateAttendingStatus(final FleenStreamResponse stream, final boolean isAttending) {
    if (nonNull(stream)) {
      stream.setIsAttendingInfo(IsAttendingInfo.of(isAttending, translate(IsAttending.by(isAttending).getMessageCode())));
    }
  }

}
