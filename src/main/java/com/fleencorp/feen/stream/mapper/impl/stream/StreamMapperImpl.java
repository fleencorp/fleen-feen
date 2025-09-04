package com.fleencorp.feen.stream.mapper.impl.stream;

import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.common.model.info.IsForKidsInfo;
import com.fleencorp.feen.common.model.info.JoinStatusInfo;
import com.fleencorp.feen.common.model.info.ShareCountInfo;
import com.fleencorp.feen.link.model.info.MusicLinkTypeInfo;
import com.fleencorp.feen.link.model.response.base.LinkMusicResponse;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.stream.constant.IsForKids;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.common.MusicLinkType;
import com.fleencorp.feen.stream.constant.core.StreamSource;
import com.fleencorp.feen.stream.constant.core.StreamTimeType;
import com.fleencorp.feen.stream.mapper.common.StreamInfoMapper;
import com.fleencorp.feen.stream.mapper.stream.StreamMapper;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.feen.stream.model.info.attendance.AttendeeCountInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsASpeakerInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsAttendingInfo;
import com.fleencorp.feen.stream.model.info.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.stream.model.info.core.*;
import com.fleencorp.feen.stream.model.info.schedule.ScheduleTimeTypeInfo;
import com.fleencorp.feen.stream.model.other.Organizer;
import com.fleencorp.feen.stream.model.other.Schedule;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public class StreamMapperImpl extends BaseMapper implements StreamMapper {

  private final ToInfoMapper toInfoMapper;
  private final StreamInfoMapper streamInfoMapper;

  public StreamMapperImpl(
      final ToInfoMapper toInfoMapper,
      final StreamInfoMapper streamInfoMapper,
      final MessageSource messageSource) {
    super(messageSource);
    this.toInfoMapper = toInfoMapper;
    this.streamInfoMapper = streamInfoMapper;
  }

  /**
   * Converts a {@link FleenStream} entry to a {@link StreamResponse} object with detailed stream information.
   * The method populates the response with various stream details including visibility, type, status, schedule, and organizer information.
   * The join status is set to "not joined" based on whether the stream is private or public.
   *
   * @param entry the {@link FleenStream} entry to convert
   * @return a {@link StreamResponse} object populated with stream information, or {@code null} if the input entry is {@code null}
   */
  @Override
  public StreamResponse toStreamResponse(final IsAStream entry) {
    if (nonNull(entry)) {

      final StreamResponse response = new StreamResponse();
      response.setId(entry.getStreamId());
      response.setTitle(entry.getTitle());
      response.setDescription(entry.getDescription());
      response.setTags(entry.getTags());
      response.setLocation(entry.getLocation());
      response.setOtherSchedule(Schedule.of());
      response.setSpeakerCount(entry.getTotalSpeakers());

      response.setStreamLink(entry.getMaskedStreamLink());
      response.setStreamLinkUnmasked(entry.getStreamLink());
      response.setStreamLinkNotMasked(entry.getStreamLink());

      response.setIsUpdatable(false);
      response.setAuthorId(entry.getOrganizerId());
      response.setOrganizerId(entry.getOrganizerId());

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());
      response.setSlug(entry.getSlug());

      toInfoMapper.setBookmarkInfo(response, false, entry.getBookmarkCount());
      toInfoMapper.setLikeInfo(response, false, entry.getLikeCount());

      final ShareCountInfo shareCountInfo = toInfoMapper.toShareCountInfo(entry.getShareCount());
      response.setShareCountInfo(shareCountInfo);

      final Schedule schedule = Schedule.of(entry.getScheduledStartDate(), entry.getScheduledEndDate(), entry.getTimezone());
      response.setSchedule(schedule);

      final AttendeeCountInfo attendeeCountInfo = streamInfoMapper.toAttendeeCountInfo(entry.getTotalAttendees());
      response.setAttendeeCountInfo(attendeeCountInfo);

      final StreamStatusInfo streamStatusInfo = streamInfoMapper.toStreamStatusInfo(entry.getStreamStatus());
      response.setStreamStatusInfo(streamStatusInfo);

      final StreamVisibilityInfo visibilityInfo = streamInfoMapper.toStreamVisibilityInfo(entry.getStreamVisibility());
      response.setStreamVisibilityInfo(visibilityInfo);

      final IsDeletedInfo deletedInfo = toInfoMapper.toIsDeletedInfo(entry.getDeleted());
      response.setDeletedInfo(deletedInfo);

      final OtherStreamDetailInfo otherStreamDetailInfo = OtherStreamDetailInfo.of(
        entry.getOtherDetails(),
        entry.getOtherLink(),
        entry.getGroupOrOrganizationName()
      );
      response.setOtherDetailInfo(otherStreamDetailInfo);

      final String musicLink = entry.getMusicLink();
      final MusicLinkType musicLinkType = MusicLinkType.ofType(musicLink);
      final MusicLinkTypeInfo musicLinkTypeInfo = MusicLinkTypeInfo.of(musicLinkType);
      final LinkMusicResponse linkMusicResponse = LinkMusicResponse.of(musicLink, musicLinkTypeInfo);
      response.setMusicLink(linkMusicResponse);

      final StreamTypeInfo streamTypeInfo = streamInfoMapper.toStreamTypeInfo(entry.getStreamType());
      response.setStreamTypeInfo(streamTypeInfo);

      final StreamSource streamSource = entry.getStreamSource();
      final StreamSourceInfo streamSourceInfo = StreamSourceInfo.of(streamSource, translate(streamSource.getMessageCode()));
      response.setStreamSourceInfo(streamSourceInfo);

      final StreamTimeType scheduleTimeType = entry.getStreamSchedule();
      final ScheduleTimeTypeInfo scheduleTimeTypeInfo = ScheduleTimeTypeInfo.of(scheduleTimeType, translate(scheduleTimeType.getMessageCode()));
      response.setScheduleTimeTypeInfo(scheduleTimeTypeInfo);

      final IsForKids forKids = IsForKids.by(entry.isForKids());
      final IsForKidsInfo forKidsInfo = IsForKidsInfo.of(entry.isForKids(), translate(forKids.getMessageCode()));
      response.setForKidsInfo(forKidsInfo);

      final Organizer organizer = Organizer.of(entry.getOrganizerName(), entry.getOrganizerEmail(), entry.getOrganizerPhone());
      response.setOrganizer(organizer);

      final JoinStatus joinStatus = JoinStatus.byStreamStatus(entry.isPrivateOrProtected());
      final JoinStatusInfo joinStatusInfo = JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()), translate(joinStatus.getMessageCode3()));

      final IsAttendingInfo isAttendingInfo = streamInfoMapper.toIsAttendingInfo(false);
      final IsASpeakerInfo isASpeakerInfo = streamInfoMapper.toIsASpeakerInfo(false);
      final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = StreamAttendeeRequestToJoinStatusInfo.of();

      final AttendanceInfo attendanceInfo = AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo, isASpeakerInfo);
      response.setAttendanceInfo(attendanceInfo);

      return response;

    }
    return null;
  }

  /**
   * Converts a {@link FleenStream} entry to a {@link StreamResponse} object with approved join status and request-to-join status.
   * The join status is set to "joined chat space," and the request-to-join status is set to "approved."
   *
   * @param entry the {@link FleenStream} entry to convert
   * @return a {@link StreamResponse} object with approved status information, or {@code null} if the input entry is {@code null}
   */
  @Override
  public StreamResponse toStreamResponseByAdminUpdate(final IsAStream entry) {
    if (nonNull(entry)) {
      final StreamResponse stream = toStreamResponse(entry);

      final IsAttendingInfo isAttendingInfo = streamInfoMapper.toIsAttendingInfo(true);
      final IsASpeakerInfo isASpeakerInfo = streamInfoMapper.toIsASpeakerInfo(true);

      final JoinStatus joinStatus = JoinStatus.joinedChatSpace();
      final JoinStatusInfo joinStatusInfo = JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()), translate(joinStatus.getMessageCode3()));

      final StreamAttendeeRequestToJoinStatus requestToJoinStatus = StreamAttendeeRequestToJoinStatus.approved();
      final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = StreamAttendeeRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode()));

      final AttendanceInfo attendanceInfo = AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo, isASpeakerInfo);

      stream.setAttendanceInfo(attendanceInfo);
      return stream;
    }
    return null;
  }

  /**
   * Converts a {@link FleenStream} entry to a {@link StreamResponse} object, excluding request-to-join and join status information.
   *
   * @param entry the {@link IsAStream} entry to convert
   * @return a {@link StreamResponse} object with no join status or request-to-join status information, or {@code null} if the input entry is {@code null}
   */
  @Override
  public StreamResponse toStreamResponseNoJoinStatus(final IsAStream entry) {
    if (nonNull(entry)) {
      final StreamResponse stream = toStreamResponse(entry);
      stream.setAttendanceInfo(AttendanceInfo.of());

      return stream;
    }
    return null;
  }

  /**
  * Converts a list of IsAStream entities to a list of FleenStreamResponse DTOs.
  *
  * <p>This method takes a list of IsAStream entities and converts each entity
  * to a FleenStreamResponse DTO. Null entries are filtered out from the result.</p>
  *
  * @param entries the list of IsAStream entities to convert
  * @return a list of FleenStreamResponse DTOs, or an empty list if the input is null or empty
  */
  @Override
  public List<StreamResponse> toStreamResponses(final List<IsAStream> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .filter(Objects::nonNull)
          .map(this::toStreamResponse)
          .toList();
    }
    return List.of();
  }

  @Override
  public List<StreamResponse> toStreamResponsesActual(List<FleenStream> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toStreamResponse)
        .toList();
    }
    return List.of();
  }

  /**
   * Updates the stream response with the provided request-to-join status, join status, and attending status.
   *
   * @param stream the stream response object to be updated
   * @param requestToJoinStatus the status of the request to join the stream
   * @param joinStatus the join status to be set in the stream response
   * @param isAttending {@code true} if the user is attending the stream, {@code false} otherwise
   * @param isASpeaker {@code true} if the user is a speaker in the stream, {@code false} otherwise
   */
  @Override
  public void update(final StreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final JoinStatus joinStatus, final boolean isAttending, final boolean isASpeaker) {
    final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = streamInfoMapper.toRequestToJoinStatusInfo(requestToJoinStatus);
    final JoinStatusInfo joinStatusInfo = streamInfoMapper.toJoinStatusInfo(joinStatus);
    final IsAttendingInfo isAttendingInfo = streamInfoMapper.toIsAttendingInfo(isAttending);
    final IsASpeakerInfo isASpeakerInfo = streamInfoMapper.toIsASpeakerInfo(isASpeaker);

    final AttendanceInfo attendanceInfo = AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo, isASpeakerInfo);
    stream.setAttendanceInfo(attendanceInfo);
  }

}
