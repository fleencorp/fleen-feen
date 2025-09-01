package com.fleencorp.feen.stream.mapper.impl;

import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.common.model.info.JoinStatusInfo;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.review.model.info.ReviewCountInfo;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.mapper.StreamUnifiedMapper;
import com.fleencorp.feen.stream.mapper.attendee.StreamAttendeeMapper;
import com.fleencorp.feen.stream.mapper.common.StreamCommonMapper;
import com.fleencorp.feen.stream.mapper.common.StreamInfoMapper;
import com.fleencorp.feen.stream.mapper.speaker.StreamSpeakerMapper;
import com.fleencorp.feen.stream.mapper.stream.StreamMapper;
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
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class StreamUnifiedMapperImpl implements StreamUnifiedMapper {

  private final StreamAttendeeMapper streamAttendeeMapper;
  private final StreamCommonMapper streamCommonMapper;
  private final StreamInfoMapper streamInfoMapper;
  private final StreamMapper streamMapper;
  private final StreamSpeakerMapper streamSpeakerMapper;
  private final ToInfoMapper toInfoMapper;

  public StreamUnifiedMapperImpl(
      final StreamAttendeeMapper streamAttendeeMapper,
      final StreamCommonMapper streamCommonMapper,
      final StreamInfoMapper streamInfoMapper,
      final StreamMapper streamMapper,
      final StreamSpeakerMapper streamSpeakerMapper,
      final ToInfoMapper toInfoMapper) {
    this.streamAttendeeMapper = streamAttendeeMapper;
    this.streamInfoMapper = streamInfoMapper;
    this.streamMapper = streamMapper;
    this.streamCommonMapper = streamCommonMapper;
    this.streamSpeakerMapper = streamSpeakerMapper;
    this.toInfoMapper = toInfoMapper;
  }

  @Override
  public StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatus(final StreamAttendeeRequestToJoinStatus requestToJoinStatus) {
    return streamInfoMapper.toRequestToJoinStatus(requestToJoinStatus);
  }

  @Override
  public StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatus requestToJoinStatus) {
    return streamInfoMapper.toRequestToJoinStatusInfo(requestToJoinStatus);
  }

  @Override
  public IsOrganizerInfo toIsOrganizerInfo(final boolean organizer) {
    return streamInfoMapper.toIsOrganizerInfo(organizer);
  }

  @Override
  public JoinStatusInfo toJoinStatusInfo(final JoinStatus joinStatus) {
    return streamInfoMapper.toJoinStatusInfo(joinStatus);
  }

  @Override
  public JoinStatusInfo toJoinStatus(final StreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending) {
    return streamInfoMapper.toJoinStatus(stream, requestToJoinStatus, isAttending);
  }

  @Override
  public AttendanceInfo toAttendanceInfo(final StreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending, final boolean isASpeaker) {
    return streamInfoMapper.toAttendanceInfo(stream, requestToJoinStatus, isAttending, isASpeaker);
  }

  @Override
  public IsAttendingInfo toIsAttendingInfo(final boolean isAttending) {
    return streamInfoMapper.toIsAttendingInfo(isAttending);
  }

  @Override
  public IsASpeakerInfo toIsASpeakerInfo(final boolean aSpeaker) {
    return streamInfoMapper.toIsASpeakerInfo(aSpeaker);
  }

  @Override
  public AttendeeCountInfo toAttendeeCountInfo(final Integer attendeeCount) {
    return streamInfoMapper.toAttendeeCountInfo(attendeeCount);
  }

  @Override
  public StreamAttendeeResponse toStreamAttendeeResponse(final StreamAttendee entry, final StreamResponse streamResponse) {
    return streamAttendeeMapper.toStreamAttendeeResponse(entry, streamResponse);
  }

  @Override
  public Collection<StreamAttendeeResponse> toStreamAttendeeResponsesPublic(final List<StreamAttendee> entries, final StreamResponse streamResponse) {
    return streamAttendeeMapper.toStreamAttendeeResponsesPublic(entries, streamResponse);
  }

  @Override
  public StreamResponse toStreamResponse(final FleenStream entry) {
    return streamMapper.toStreamResponse(entry);
  }

  @Override
  public StreamResponse toStreamResponseNoJoinStatus(final FleenStream entry) {
    return streamMapper.toStreamResponseNoJoinStatus(entry);
  }

  @Override
  public List<StreamResponse> toStreamResponses(final List<FleenStream> entries) {
    return streamMapper.toStreamResponses(entries);
  }

  @Override
  public StreamStatusInfo toStreamStatusInfo(final StreamStatus streamStatus) {
    return streamInfoMapper.toStreamStatusInfo(streamStatus);
  }

  @Override
  public StreamVisibilityInfo toStreamVisibilityInfo(final StreamVisibility streamVisibility) {
    return streamInfoMapper.toStreamVisibilityInfo(streamVisibility);
  }

  @Override
  public StreamTypeInfo toStreamTypeInfo(final StreamType streamType) {
    return streamInfoMapper.toStreamTypeInfo(streamType);
  }

  @Override
  public void update(final StreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final JoinStatus joinStatus, final boolean isAttending, final boolean isASpeaker) {
    streamMapper.update(stream, requestToJoinStatus, joinStatus, isAttending, isASpeaker);
  }

  @Override
  public NotAttendingStreamResponse notAttendingStream() {
    return streamCommonMapper.notAttendingStream();
  }

  @Override
  public List<StreamSpeakerResponse> toStreamSpeakerResponses(List<StreamSpeaker> entries) {
    return streamSpeakerMapper.toStreamSpeakerResponses(entries);
  }

  @Override
  public List<StreamSpeakerResponse> toStreamSpeakerResponsesByProjection(List<StreamAttendeeInfoSelect> entries) {
    return streamSpeakerMapper.toStreamSpeakerResponsesByProjection(entries);
  }

  @Override
  public ReviewCountInfo toReviewCountInfo(final Integer reviewCount) {
    return toInfoMapper.toReviewCountInfo(reviewCount);
  }

  @Override
  public StreamResponse toStreamResponseByAdminUpdate(FleenStream entry) {
    return streamMapper.toStreamResponseByAdminUpdate(entry);
  }

  @Override
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(StreamResponse stream, StreamAttendee attendee) {
    return streamCommonMapper.processAttendeeRequestToJoinStream(stream, attendee);
  }

}
