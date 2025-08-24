package com.fleencorp.feen.stream.mapper;

import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.info.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.stream.model.info.core.StreamStatusInfo;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.info.core.StreamVisibilityInfo;
import com.fleencorp.feen.stream.model.response.StreamResponse;

import java.util.List;

public interface StreamMapper {

  StreamResponse toStreamResponse(FleenStream entry);

  StreamResponse toStreamResponseByAdminUpdate(FleenStream entry);

  StreamResponse toStreamResponseNoJoinStatus(FleenStream entry);

  List<StreamResponse> toStreamResponses(List<FleenStream> entries);

  StreamStatusInfo toStreamStatusInfo(StreamStatus streamStatus);

  StreamVisibilityInfo toStreamVisibilityInfo(StreamVisibility streamVisibility);

  StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  StreamTypeInfo toStreamTypeInfo(StreamType streamType);

  void update(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, boolean isAttending, boolean isASpeaker);
}
