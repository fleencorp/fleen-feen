package com.fleencorp.feen.stream.mapper.impl.common;

import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.common.model.info.JoinStatusInfo;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.stream.constant.attendee.AttendeeCount;
import com.fleencorp.feen.stream.constant.attendee.IsAttending;
import com.fleencorp.feen.stream.constant.attendee.IsOrganizer;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.constant.speaker.IsASpeaker;
import com.fleencorp.feen.stream.constant.speaker.SpeakerCount;
import com.fleencorp.feen.stream.mapper.common.StreamInfoMapper;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.feen.stream.model.info.attendance.AttendeeCountInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsASpeakerInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsAttendingInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsOrganizerInfo;
import com.fleencorp.feen.stream.model.info.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.stream.model.info.core.StreamStatusInfo;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.info.core.StreamVisibilityInfo;
import com.fleencorp.feen.stream.model.info.speaker.SpeakerCountInfo;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class StreamInfoMapperImpl extends BaseMapper implements StreamInfoMapper {

  public StreamInfoMapperImpl(final MessageSource messageSource) {
    super(messageSource);
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
      return JoinStatusInfo.of(
        joinStatus,
        translate(joinStatus.getMessageCode()),
        translate(joinStatus.getMessageCode2()),
        translate(joinStatus.getMessageCode3()));
    }

    return JoinStatusInfo.of();
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
  public JoinStatusInfo toJoinStatus(final StreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending) {
    if (nonNull(requestToJoinStatus)) {
      final JoinStatus joinStatus = JoinStatus.getJoinStatus(
        requestToJoinStatus,
        stream.getVisibility(),
        stream.hasHappened(),
        isAttending);

      return JoinStatusInfo.of(
        joinStatus,
        translate(joinStatus.getMessageCode()),
        translate(joinStatus.getMessageCode2()),
        translate(joinStatus.getMessageCode3())
      );
    }

    return JoinStatusInfo.of();
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
  public AttendanceInfo toAttendanceInfo(final StreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending, final boolean isASpeaker) {
    if (nonNull(stream) && nonNull(requestToJoinStatus)) {
      final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = toRequestToJoinStatusInfo(requestToJoinStatus);
      final JoinStatusInfo joinStatusInfo = toJoinStatus(stream, requestToJoinStatus, isAttending);
      final IsAttendingInfo isAttendingInfo = toIsAttendingInfo(isAttending);
      final IsASpeakerInfo isASpeakerInfo = toIsASpeakerInfo(isASpeaker);

      return AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo, isASpeakerInfo);
    }

    return AttendanceInfo.of();
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

    return StreamAttendeeRequestToJoinStatusInfo.of();
  }


  /**
   * Converts the given attendance status into an {@link IsAttendingInfo} object.
   *
   * <p>This method determines the appropriate message code based on the attendance status
   * and translates it to a localized message.</p>
   *
   * @param attending a boolean indicating whether the attendee is currently attending
   * @return an {@link IsAttendingInfo} object containing the attendance status and its corresponding localized message
   */
  @Override
  public IsAttendingInfo toIsAttendingInfo(final boolean attending) {
    IsAttending isAttending = IsAttending.by(attending);

    return IsAttendingInfo.of(
      attending,
      translate(isAttending.getMessageCode())
    );
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

    return IsASpeakerInfo.of(
      aSpeaker,
      translate(isASpeaker.getMessageCode()),
      translate(isASpeaker.getMessageCode2())
    );
  }

  /**
   * Converts the given attendee count into an {@link AttendeeCountInfo} containing both
   * the numeric count and its localized message representations.
   *
   * <p>The method creates a {@link AttendeeCount} instance representing the total
   * attendee count, then resolves multiple localized messages using the provided count
   * and the message codes from the {@link AttendeeCount}. Finally, it constructs and
   * returns an {@link AttendeeCountInfo} with this information.</p>
   *
   * @param attendeeCount the total number of attendees
   * @return an {@link AttendeeCountInfo} containing the numeric count and its localized messages
   */
  @Override
  public AttendeeCountInfo toAttendeeCountInfo(final Integer attendeeCount) {
    final AttendeeCount totalAttendeeCount = AttendeeCount.totalAttendee();

    return AttendeeCountInfo.of(attendeeCount,
      translate(totalAttendeeCount.getMessageCode(), attendeeCount),
      translate(totalAttendeeCount.getMessageCode2(), attendeeCount),
      translate(totalAttendeeCount.getMessageCode3(), attendeeCount)
    );
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

    return StreamVisibilityInfo.of();
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
      return StreamStatusInfo.of(
        streamStatus,
        translate(streamStatus.getMessageCode()),
        translate(streamStatus.getMessageCode2()),
        translate(streamStatus.getMessageCode3())
      );
    }

    return StreamStatusInfo.of();
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
    if (nonNull(streamType)) {
      return StreamTypeInfo.of(
        streamType,
        translate(streamType.getMessageCode())
      );
    }

    return StreamTypeInfo.of();
  }

  @Override
  public SpeakerCountInfo toSpeakerCountInfo(final Integer speakerCount) {
    final SpeakerCount totalSpeakerCount = SpeakerCount.totalSpeakerCount();

    return SpeakerCountInfo.of(speakerCount,
      translate(totalSpeakerCount.getMessageCode(), speakerCount)
    );
  }

}
