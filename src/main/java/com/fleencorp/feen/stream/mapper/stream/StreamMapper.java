package com.fleencorp.feen.stream.mapper.stream;

import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.response.StreamResponse;

import java.util.List;

public interface StreamMapper {

  StreamResponse toStreamResponse(FleenStream entry);

  StreamResponse toStreamResponseByAdminUpdate(FleenStream entry);

  StreamResponse toStreamResponseNoJoinStatus(FleenStream entry);

  List<StreamResponse> toStreamResponses(List<FleenStream> entries);

  void update(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, boolean isAttending, boolean isASpeaker);
}
