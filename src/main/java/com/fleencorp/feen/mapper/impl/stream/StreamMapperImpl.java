package com.fleencorp.feen.mapper.impl.stream;

import com.fleencorp.feen.constant.common.JoinStatus;
import com.fleencorp.feen.constant.common.MusicLinkType;
import com.fleencorp.feen.constant.stream.*;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.feen.model.info.IsForKidsInfo;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.like.UserLikeInfo;
import com.fleencorp.feen.model.info.link.MusicLinkTypeInfo;
import com.fleencorp.feen.model.info.schedule.ScheduleTimeTypeInfo;
import com.fleencorp.feen.model.info.stream.*;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsASpeakerInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.other.Organizer;
import com.fleencorp.feen.model.other.Schedule;
import com.fleencorp.feen.model.response.link.base.MusicLinkResponse;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
* Mapper class for converting FleenStream entities to various DTOs.
*
* <p>This class provides static methods to map FleenStream entities to their
* corresponding Data Transfer Objects (DTOs). It includes methods to convert
* single entities as well as lists of entities.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Component
@Slf4j
public class StreamMapperImpl extends BaseMapper implements StreamMapper {

  private final ToInfoMapper toInfoMapper;

  public StreamMapperImpl(
      final ToInfoMapper toInfoMapper,
      final MessageSource messageSource) {
    super(messageSource);
    this.toInfoMapper = toInfoMapper;
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
  public StreamResponse toStreamResponse(final FleenStream entry) {
    if (nonNull(entry)) {

      final StreamResponse response = new StreamResponse();
      response.setId(entry.getStreamId());
      response.setTitle(entry.getTitle());
      response.setDescription(entry.getDescription());
      response.setTags(entry.getTags());
      response.setLocation(entry.getLocation());
      response.setOtherSchedule(Schedule.of());
      response.setTotalLikeCount(entry.getLikeCount());

      response.setStreamLink(entry.getMaskedStreamLink());
      response.setStreamLinkUnmasked(entry.getStreamLink());
      response.setStreamLinkNotMasked(entry.getStreamLink());
      response.setTotalAttending(entry.getTotalAttendees());
      response.setOrganizerId(entry.getOrganizerId());

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      final Schedule schedule = Schedule.of(entry.getScheduledStartDate(), entry.getScheduledEndDate(), entry.getTimezone());
      response.setSchedule(schedule);

      final StreamStatusInfo streamStatusInfo = toStreamStatusInfo(entry.getStreamStatus());
      response.setStreamStatusInfo(streamStatusInfo);

      final StreamVisibilityInfo visibilityInfo = toStreamVisibilityInfo(entry.getStreamVisibility());
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
      final MusicLinkResponse musicLinkResponse = MusicLinkResponse.of(musicLink, musicLinkTypeInfo);
      response.setMusicLink(musicLinkResponse);

      final StreamTypeInfo streamTypeInfo = toStreamTypeInfo(entry.getStreamType());
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

      final IsAttendingInfo isAttendingInfo = toInfoMapper.toIsAttendingInfo(false);
      final IsASpeakerInfo isASpeakerInfo = toInfoMapper.toIsASpeakerInfo(false);
      final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = StreamAttendeeRequestToJoinStatusInfo.of();

      final AttendanceInfo attendanceInfo = AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo, isASpeakerInfo);
      response.setAttendanceInfo(attendanceInfo);

      final UserLikeInfo userLikeInfo = UserLikeInfo.of();
      response.setUserLikeInfo(userLikeInfo);

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
  public StreamResponse toStreamResponseByAdminUpdate(final FleenStream entry) {
    if (nonNull(entry)) {
      final StreamResponse stream = toStreamResponse(entry);

      final IsAttendingInfo isAttendingInfo = toInfoMapper.toIsAttendingInfo(true);
      final IsASpeakerInfo isASpeakerInfo = toInfoMapper.toIsASpeakerInfo(true);

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
   * @param entry the {@link FleenStream} entry to convert
   * @return a {@link StreamResponse} object with no join status or request-to-join status information, or {@code null} if the input entry is {@code null}
   */
  @Override
  public StreamResponse toStreamResponseNoJoinStatus(final FleenStream entry) {
    if (nonNull(entry)) {
      final StreamResponse stream = toStreamResponse(entry);
      stream.setAttendanceInfo(AttendanceInfo.of());

      return stream;
    }
    return null;
  }

  /**
  * Converts a list of FleenStream entities to a list of FleenStreamResponse DTOs.
  *
  * <p>This method takes a list of FleenStream entities and converts each entity
  * to a FleenStreamResponse DTO. Null entries are filtered out from the result.</p>
  *
  * @param entries the list of FleenStream entities to convert
  * @return a list of FleenStreamResponse DTOs, or an empty list if the input is null or empty
  */
  @Override
  public List<StreamResponse> toStreamResponses(final List<FleenStream> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
          .filter(Objects::nonNull)
          .map(this::toStreamResponse)
          .toList();
    }
    return List.of();
  }

  /**
   * Converts a list of {@link FleenStream} entries to a list of {@link StreamResponse} objects, excluding join status.
   *
   * @param entries the list of {@link FleenStream} entries to convert
   * @return a list of {@link StreamResponse} objects corresponding to the given entries, or an empty list if the input is {@code null} or empty
   */
  @Override
  public List<StreamResponse> toStreamResponsesNoJoinStatus(final List<FleenStream> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toStreamResponseNoJoinStatus)
        .toList();
    }
    return List.of();
  }

  /**
   * Converts the given stream to its corresponding stream status information.
   *
   * @param streamStatus the status of the stream
   * @return the stream status information, or {@code null} if the stream is {@code null}
   */
  @Override
  public StreamStatusInfo toStreamStatusInfo(final StreamStatus streamStatus) {
    if (nonNull(streamStatus)) {
      return StreamStatusInfo.of(streamStatus, translate(streamStatus.getMessageCode()), translate(streamStatus.getMessageCode2()), translate(streamStatus.getMessageCode3()));
    }
    return null;
  }

  /**
   * Converts a {@link FleenStream} to a {@link StreamVisibilityInfo}.
   *
   * <p>This method checks if the provided {@link FleenStream} is not null. If it is not, it retrieves
   * the {@link StreamVisibility} associated with the stream and constructs a {@link StreamVisibilityInfo}
   * by translating the message code from the {@link StreamVisibility}. If the stream is null, it returns null.</p>
   *
   * @param streamVisibility the visibility of the stream to be converted into a {@link StreamVisibilityInfo}
   * @return a {@link StreamVisibilityInfo} containing the stream's visibility and translated message, or null if the stream is null
   */
  @Override
  public StreamVisibilityInfo toStreamVisibilityInfo(final StreamVisibility streamVisibility) {
    if (nonNull(streamVisibility)) {
      return StreamVisibilityInfo.of(streamVisibility, translate(streamVisibility.getMessageCode()));
    }
    return null;
  }

  /**
   * Converts the given FleenStreamResponse and StreamAttendeeRequestToJoinStatus
   * to StreamAttendeeRequestToJoinStatusInfo.
   *
   * @param requestToJoinStatus the StreamAttendeeRequestToJoinStatus to be translated.
   * @return the StreamAttendeeRequestToJoinStatusInfo object with translated message
   * if both stream and requestToJoinStatus are non-null, otherwise null.
   */
  @Override
  public StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatusInfo(final StreamAttendeeRequestToJoinStatus requestToJoinStatus) {
    if (nonNull(requestToJoinStatus)) {
      return StreamAttendeeRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode()));
    }
    return null;
  }


  /**
   * Converts the given attendance status into an {@link IsAttendingInfo} object.
   *
   * <p>This method determines the appropriate message code based on the attendance status
   * and translates it to a localized message.</p>
   *
   * @return an {@link IsAttendingInfo} object containing the attendance status and its corresponding localized message
   */
  @Override
  public StreamTypeInfo toStreamTypeInfo(final StreamType streamType) {
    return StreamTypeInfo.of(streamType, translate(streamType.getMessageCode()));
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
    final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = toRequestToJoinStatusInfo(requestToJoinStatus);
    final JoinStatusInfo joinStatusInfo = toInfoMapper.toJoinStatusInfo(joinStatus);
    final IsAttendingInfo isAttendingInfo = toInfoMapper.toIsAttendingInfo(isAttending);
    final IsASpeakerInfo isASpeakerInfo = toInfoMapper.toIsASpeakerInfo(isASpeaker);

    final AttendanceInfo attendanceInfo = AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo, isASpeakerInfo);
    stream.setAttendanceInfo(attendanceInfo);
  }

}
