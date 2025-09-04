package com.fleencorp.feen.stream.mapper.stream;

import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.response.StreamResponse;

import java.util.List;

public interface StreamMapper {

  StreamResponse toStreamResponse(IsAStream entry);

  StreamResponse toStreamResponseByAdminUpdate(IsAStream entry);

  StreamResponse toStreamResponseNoJoinStatus(IsAStream entry);

  List<StreamResponse> toStreamResponses(List<IsAStream> entries);

  List<StreamResponse> toStreamResponsesActual(List<FleenStream> entries);

  void update(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, boolean isAttending, boolean isASpeaker);
}
