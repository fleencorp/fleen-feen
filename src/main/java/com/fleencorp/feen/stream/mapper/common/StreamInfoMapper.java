package com.fleencorp.feen.stream.mapper.common;

import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.common.model.info.JoinStatusInfo;
import com.fleencorp.feen.poll.model.info.PollIsEndedInfo;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.feen.stream.model.info.attendance.AttendeeCountInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsASpeakerInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsAttendingInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsOrganizerInfo;
import com.fleencorp.feen.stream.model.info.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.stream.model.info.core.StreamStatusInfo;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.info.core.StreamVisibilityInfo;
import com.fleencorp.feen.stream.model.response.StreamResponse;

public interface StreamInfoMapper {

  StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatus(StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  IsOrganizerInfo toIsOrganizerInfo(boolean organizer);

  JoinStatusInfo toJoinStatusInfo(JoinStatus joinStatus);

  JoinStatusInfo toJoinStatus(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, boolean isAttending);

  AttendanceInfo toAttendanceInfo(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, boolean isAttending, boolean isASpeaker);

  StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  IsAttendingInfo toIsAttendingInfo(boolean attending);

  IsASpeakerInfo toIsASpeakerInfo(boolean aSpeaker);

  AttendeeCountInfo toAttendeeCountInfo(Integer attendeeCount);

  StreamVisibilityInfo toStreamVisibilityInfo(StreamVisibility streamVisibility);

  StreamStatusInfo toStreamStatusInfo(StreamStatus streamStatus);

  StreamTypeInfo toStreamTypeInfo(StreamType streamType);
}
