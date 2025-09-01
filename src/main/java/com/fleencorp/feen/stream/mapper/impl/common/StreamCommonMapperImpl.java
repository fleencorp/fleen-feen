package com.fleencorp.feen.stream.mapper.impl.common;

import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.common.model.info.JoinStatusInfo;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.mapper.common.StreamCommonMapper;
import com.fleencorp.feen.stream.mapper.common.StreamInfoMapper;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsAttendingInfo;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class StreamCommonMapperImpl extends BaseMapper implements StreamCommonMapper {

  private final StreamInfoMapper streamInfoMapper;

  public StreamCommonMapperImpl(
      final StreamInfoMapper streamInfoMapper,
      final MessageSource messageSource) {
    super(messageSource);
    this.streamInfoMapper = streamInfoMapper;
  }

  /**
   * Generates a response for a stream where the user is not attending.
   *
   * <p>This method creates a {@link NotAttendingStreamResponse} object, sets its
   * attendance status to "not attending", and populates the join status
   * information using the provided {@link JoinStatus}.</p>
   *
   * @return A {@link NotAttendingStreamResponse} indicating that the user
   *         is not attending the stream.
   */
  @Override
  public NotAttendingStreamResponse notAttendingStream() {
    final JoinStatus joinStatus = JoinStatus.notAttendingStream();
    final JoinStatusInfo joinStatusInfo = streamInfoMapper.toJoinStatusInfo(joinStatus);

    final IsAttendingInfo isAttendingInfo = streamInfoMapper.toIsAttendingInfo(false);
    final NotAttendingStreamResponse notAttendingStreamResponse = NotAttendingStreamResponse.of();

    notAttendingStreamResponse.setAttendingInfo(isAttendingInfo);
    notAttendingStreamResponse.setJoinStatusInfo(joinStatusInfo);

    return notAttendingStreamResponse;
  }

  /**
   * Processes an attendee's request to join an event stream.
   *
   * <p>This method handles the logic for processing the request of an attendee
   * who is seeking to join a stream. It checks if the attendee already exists
   * and retrieves their current request status. A response is generated
   * containing the stream information and the current request status.</p>
   *
   * If the attendee does not exist, the method returns null.
   *
   * @param stream The {@link StreamResponse} containing the stream details.
   * @param attendee the {@link StreamAttendee} of the stream
   *
   * @return A {@link ProcessAttendeeRequestToJoinStreamResponse} populated with stream details
   *         and the request to join status if the attendee exists, or null if no attendee is found.
   */
  @Override
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(final StreamResponse stream, final StreamAttendee attendee) {
    if (nonNull(stream) && nonNull(attendee)) {
      final StreamAttendeeRequestToJoinStatus requestToJoinStatus = attendee.getRequestToJoinStatus();
      final StreamTypeInfo streamTypeInfo = streamInfoMapper.toStreamTypeInfo(stream.getStreamType());

      final AttendanceInfo attendanceInfo = streamInfoMapper.toAttendanceInfo(
        stream,
        requestToJoinStatus,
        attendee.isAttending(),
        attendee.isASpeaker()
      );

      return ProcessAttendeeRequestToJoinStreamResponse.of(
        stream.getNumberId(),
        attendanceInfo,
        streamTypeInfo,
        stream.getAttendeeCountInfo()
      );
    }

    return null;
  }

}
