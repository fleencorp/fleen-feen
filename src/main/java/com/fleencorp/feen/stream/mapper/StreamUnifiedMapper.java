package com.fleencorp.feen.stream.mapper;

import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.review.model.info.ReviewCountInfo;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamSpeaker;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.feen.stream.model.info.attendance.AttendeeCountInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsASpeakerInfo;
import com.fleencorp.feen.stream.model.info.core.StreamStatusInfo;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.info.core.StreamVisibilityInfo;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.stream.model.response.speaker.StreamSpeakerResponse;

import java.util.Collection;
import java.util.List;

public interface StreamUnifiedMapper {

  AttendanceInfo toAttendanceInfo(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, boolean isAttending, boolean isASpeaker);

  IsASpeakerInfo toIsASpeakerInfo(boolean aSpeaker);

  AttendeeCountInfo toAttendeeCountInfo(Integer attendeeCount);

  StreamAttendeeResponse toStreamAttendeeResponse(IsAttendee entry, StreamResponse streamResponse);

  Collection<StreamAttendeeResponse> toStreamAttendeeResponsesPublic(List<IsAttendee> entries, StreamResponse streamResponse);

  StreamResponse toStreamResponse(IsAStream entry);

  StreamResponse toStreamResponseNoJoinStatus(IsAStream entry);

  List<StreamResponse> toStreamResponses(List<IsAStream> entries);

  List<StreamResponse> toStreamResponsesActual(List<FleenStream> entries);

  StreamStatusInfo toStreamStatusInfo(StreamStatus streamStatus);

  StreamVisibilityInfo toStreamVisibilityInfo(StreamVisibility streamVisibility);

  StreamTypeInfo toStreamTypeInfo(StreamType streamType);

  void update(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, boolean isAttending, boolean isASpeaker);

  NotAttendingStreamResponse notAttendingStream();

  List<StreamSpeakerResponse> toStreamSpeakerResponses(List<StreamSpeaker> entries);

  List<StreamSpeakerResponse> toStreamSpeakerResponsesByProjection(List<IsAttendee> entries);

  ReviewCountInfo toReviewCountInfo(Integer reviewCount);

  StreamResponse toStreamResponseByAdminUpdate(IsAStream entry);

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(StreamResponse stream, IsAttendee attendee);
}
