package com.fleencorp.feen.stream.mapper;

import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.common.model.info.JoinStatusInfo;
import com.fleencorp.feen.review.model.info.ReviewCountInfo;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import com.fleencorp.feen.stream.model.domain.StreamSpeaker;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.feen.stream.model.info.attendance.AttendeeCountInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsASpeakerInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsAttendingInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsOrganizerInfo;
import com.fleencorp.feen.stream.model.info.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.stream.model.info.core.StreamStatusInfo;
import com.fleencorp.feen.stream.model.info.core.StreamTypeInfo;
import com.fleencorp.feen.stream.model.info.core.StreamVisibilityInfo;
import com.fleencorp.feen.stream.model.projection.StreamAttendeeInfoSelect;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.stream.model.response.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.stream.model.response.attendee.StreamAttendeeResponse;
import com.fleencorp.feen.stream.model.response.speaker.StreamSpeakerResponse;

import java.util.Collection;
import java.util.List;

public interface StreamUnifiedMapper {

  StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatus(StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  IsOrganizerInfo toIsOrganizerInfo(boolean organizer);

  JoinStatusInfo toJoinStatusInfo(JoinStatus joinStatus);

  JoinStatusInfo toJoinStatus(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, boolean isAttending);

  AttendanceInfo toAttendanceInfo(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, boolean isAttending, boolean isASpeaker);

  IsAttendingInfo toIsAttendingInfo(boolean isAttending);

  IsASpeakerInfo toIsASpeakerInfo(boolean aSpeaker);

  AttendeeCountInfo toAttendeeCountInfo(Integer attendeeCount);

  StreamAttendeeResponse toStreamAttendeeResponse(StreamAttendee entry, StreamResponse streamResponse);

  Collection<StreamAttendeeResponse> toStreamAttendeeResponsesPublic(List<StreamAttendee> entries, StreamResponse streamResponse);

  StreamResponse toStreamResponse(FleenStream entry);

  StreamResponse toStreamResponseNoJoinStatus(FleenStream entry);

  List<StreamResponse> toStreamResponses(List<FleenStream> entries);

  StreamStatusInfo toStreamStatusInfo(StreamStatus streamStatus);

  StreamVisibilityInfo toStreamVisibilityInfo(StreamVisibility streamVisibility);

  StreamTypeInfo toStreamTypeInfo(StreamType streamType);

  void update(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, boolean isAttending, boolean isASpeaker);

  NotAttendingStreamResponse notAttendingStream();

  List<StreamSpeakerResponse> toStreamSpeakerResponses(List<StreamSpeaker> entries);

  List<StreamSpeakerResponse> toStreamSpeakerResponsesByProjection(List<StreamAttendeeInfoSelect> entries);

  ReviewCountInfo toReviewCountInfo(Integer reviewCount);

  StreamResponse toStreamResponseByAdminUpdate(FleenStream entry);

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(StreamResponse stream, StreamAttendee attendee);
}
