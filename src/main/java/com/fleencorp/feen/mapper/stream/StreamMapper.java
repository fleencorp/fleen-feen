package com.fleencorp.feen.mapper.stream;

import com.fleencorp.feen.constant.common.JoinStatus;
import com.fleencorp.feen.constant.stream.StreamStatus;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.info.stream.StreamStatusInfo;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamVisibilityInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.response.stream.FleenStreamResponse;

import java.util.List;

public interface StreamMapper {

  FleenStreamResponse toStreamResponse(FleenStream entry);

  FleenStreamResponse toStreamResponseByAdminUpdate(FleenStream entry);

  FleenStreamResponse toFleenStreamResponseNoJoinStatus(FleenStream entry);

  List<FleenStreamResponse> toStreamResponses(List<FleenStream> entries);

  List<FleenStreamResponse> toFleenStreamResponsesNoJoinStatus(List<FleenStream> entries);

  StreamStatusInfo toStreamStatusInfo(StreamStatus streamStatus);

  StreamVisibilityInfo toStreamVisibilityInfo(StreamVisibility streamVisibility);

  StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  StreamTypeInfo toStreamTypeInfo(StreamType streamType);

  void update(FleenStreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, boolean isAttending, boolean isASpeaker);
}
