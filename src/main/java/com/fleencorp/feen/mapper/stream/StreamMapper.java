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
import com.fleencorp.feen.model.response.stream.StreamResponse;

import java.util.List;

public interface StreamMapper {

  StreamResponse toStreamResponse(FleenStream entry);

  StreamResponse toStreamResponseByAdminUpdate(FleenStream entry);

  StreamResponse toStreamResponseNoJoinStatus(FleenStream entry);

  List<StreamResponse> toStreamResponses(List<FleenStream> entries);

  List<StreamResponse> toStreamResponsesNoJoinStatus(List<FleenStream> entries);

  StreamStatusInfo toStreamStatusInfo(StreamStatus streamStatus);

  StreamVisibilityInfo toStreamVisibilityInfo(StreamVisibility streamVisibility);

  StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  StreamTypeInfo toStreamTypeInfo(StreamType streamType);

  void update(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, boolean isAttending, boolean isASpeaker);
}
