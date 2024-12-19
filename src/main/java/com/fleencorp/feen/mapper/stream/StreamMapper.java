package com.fleencorp.feen.mapper.stream;

import com.fleencorp.feen.constant.stream.*;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.stream.StreamStatusInfo;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamVisibilityInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;

import java.util.List;

public interface StreamMapper {

  FleenStreamResponse toFleenStreamResponse(FleenStream entry);

  FleenStreamResponse toFleenStreamResponseApproved(FleenStream entry);

  FleenStreamResponse toFleenStreamResponseNoJoinStatus(FleenStream entry);

  List<FleenStreamResponse> toFleenStreamResponses(List<FleenStream> entries);

  List<FleenStreamResponse> toFleenStreamResponsesNoJoinStatus(List<FleenStream> entries);

  StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatus(StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  JoinStatusInfo toJoinStatus(FleenStreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, boolean isAttending);

  StreamStatusInfo toStreamStatusInfo(StreamStatus streamStatus);

  AttendanceInfo toAttendanceInfo(FleenStreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, boolean isAttending);

  StreamVisibilityInfo toStreamVisibilityInfo(StreamVisibility streamVisibility);

  StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  IsAttendingInfo toIsAttendingInfo(boolean isAttending);

  IsDeletedInfo toIsDeletedInfo(boolean deleted);

  StreamTypeInfo toStreamTypeInfo(StreamType streamType);

  void update(FleenStreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, boolean isAttending);
}
