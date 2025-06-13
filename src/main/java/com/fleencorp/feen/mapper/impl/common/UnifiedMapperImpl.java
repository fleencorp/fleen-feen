package com.fleencorp.feen.mapper.impl.common;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.constant.common.JoinStatus;
import com.fleencorp.feen.constant.social.ShareContactRequestStatus;
import com.fleencorp.feen.constant.stream.StreamStatus;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.follower.model.info.IsFollowedInfo;
import com.fleencorp.feen.follower.model.info.IsFollowingInfo;
import com.fleencorp.feen.mapper.CommonMapper;
import com.fleencorp.feen.mapper.chat.ChatSpaceMapper;
import com.fleencorp.feen.mapper.chat.member.ChatSpaceMemberMapper;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.mapper.stream.attendee.StreamAttendeeMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceStatusInfo;
import com.fleencorp.feen.model.info.chat.space.membership.ChatSpaceMembershipInfo;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.model.info.share.contact.request.ShareContactRequestStatusInfo;
import com.fleencorp.feen.model.info.stream.StreamStatusInfo;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.info.stream.StreamVisibilityInfo;
import com.fleencorp.feen.model.info.stream.attendance.AttendanceInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsASpeakerInfo;
import com.fleencorp.feen.model.info.user.profile.*;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.member.base.ChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.NotAttendingStreamResponse;
import com.fleencorp.feen.model.response.stream.attendance.ProcessAttendeeRequestToJoinStreamResponse;
import com.fleencorp.feen.model.response.stream.attendee.StreamAttendeeResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class UnifiedMapperImpl implements UnifiedMapper {

  private final ChatSpaceMapper chatSpaceMapper;
  private final ChatSpaceMemberMapper chatSpaceMemberMapper;
  private final CommonMapper commonMapper;
  private final ToInfoMapper toInfoMapper;
  private final StreamAttendeeMapper streamAttendeeMapper;
  private final StreamMapper streamMapper;

  public UnifiedMapperImpl(
      final ChatSpaceMapper chatSpaceMapper,
      final ChatSpaceMemberMapper chatSpaceMemberMapper,
      final CommonMapper commonMapper,
      final ToInfoMapper toInfoMapper,
      final StreamAttendeeMapper streamAttendeeMapper,
      final StreamMapper streamMapper) {
    this.chatSpaceMapper = chatSpaceMapper;
    this.chatSpaceMemberMapper = chatSpaceMemberMapper;
    this.commonMapper = commonMapper;
    this.toInfoMapper = toInfoMapper;
    this.streamAttendeeMapper = streamAttendeeMapper;
    this.streamMapper = streamMapper;
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
    return streamMapper.toStreamStatusInfo(streamStatus);
  }

  @Override
  public StreamVisibilityInfo toStreamVisibilityInfo(final StreamVisibility streamVisibility) {
    return streamMapper.toStreamVisibilityInfo(streamVisibility);
  }

  @Override
  public StreamTypeInfo toStreamTypeInfo(final StreamType streamType) {
    return streamMapper.toStreamTypeInfo(streamType);
  }

  @Override
  public void update(final StreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final JoinStatus joinStatus, final boolean isAttending, final boolean isASpeaker) {
    streamMapper.update(stream, requestToJoinStatus, joinStatus, isAttending, isASpeaker);
  }

  @Override
  public ChatSpaceResponse toChatSpaceResponse(final ChatSpace entry) {
    return chatSpaceMapper.toChatSpaceResponse(entry);
  }

  @Override
  public ChatSpaceResponse toChatSpaceResponseByAdminUpdate(final ChatSpace entry) {
    return chatSpaceMapper.toChatSpaceResponseByAdminUpdate(entry);
  }

  @Override
  public List<ChatSpaceResponse> toChatSpaceResponses(final List<ChatSpace> entries) {
    return chatSpaceMapper.toChatSpaceResponses(entries);
  }

  @Override
  public void setMembershipInfo(final ChatSpaceResponse chatSpace, final ChatSpaceRequestToJoinStatus requestToJoinStatus, final JoinStatus joinStatus, final ChatSpaceMemberRole memberRole, final boolean isAMember, final boolean isAdmin, final boolean hasLeft, final boolean isRemoved) {
    chatSpaceMapper.setMembershipInfo(chatSpace, requestToJoinStatus, joinStatus, memberRole, isAMember, isAdmin, hasLeft, isRemoved);
  }

  @Override
  public ChatSpaceStatusInfo toChatSpaceStatusInfo(final ChatSpaceStatus status) {
    return chatSpaceMapper.toChatSpaceStatusInfo(status);
  }

  @Override
  public ChatSpaceMembershipInfo getMembershipInfo(final ChatSpaceMember entry, final ChatSpace chatSpace) {
    return chatSpaceMemberMapper.getMembershipInfo(entry, chatSpace);
  }

  @Override
  public List<ChatSpaceMemberResponse> toChatSpaceMemberResponses(final List<ChatSpaceMember> entries, final ChatSpace chatSpace) {
    return chatSpaceMemberMapper.toChatSpaceMemberResponses(entries, chatSpace);
  }

  @Override
  public List<ChatSpaceMemberResponse> toChatSpaceMemberResponsesPublic(final List<ChatSpaceMember> entries) {
    return chatSpaceMemberMapper.toChatSpaceMemberResponsesPublic(entries);
  }

  @Override
  public AttendanceInfo toAttendanceInfo(final StreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending, final boolean isASpeaker) {
    return toInfoMapper.toAttendanceInfo(stream, requestToJoinStatus, isAttending, isASpeaker);
  }

  @Override
  public IsASpeakerInfo toIsASpeakerInfo(final boolean isASpeaker) {
    return toInfoMapper.toIsASpeakerInfo(isASpeaker);
  }

  @Override
  public IsBlockedInfo toIsBlockedInfo(final boolean blocked, final String blockingUser) {
    return toInfoMapper.toIsBlockedInfo(blocked, blockingUser);
  }

  @Override
  public IsFollowingInfo toIsFollowingInfo(final boolean following, final String userBeingFollowed) {
    return toInfoMapper.toIsFollowingInfo(following, userBeingFollowed);
  }

  @Override
  public IsFollowedInfo toIsFollowedInfo(final boolean followed, final String userFollowing) {
    return toInfoMapper.toIsFollowedInfo(followed, userFollowing);
  }

  @Override
  public TotalFollowedInfo toTotalFollowedInfo(final Long followed, final String targetMemberName) {
    return toInfoMapper.toTotalFollowedInfo(followed, targetMemberName);
  }

  @Override
  public TotalFollowingInfo toTotalFollowingInfo(final Long following, final String targetMemberName) {
    return toInfoMapper.toTotalFollowingInfo(following, targetMemberName);
  }

  @Override
  public UserLikeInfo toLikeInfo(final boolean liked) {
    return toInfoMapper.toLikeInfo(liked);
  }

  @Override
  public IsDeletedInfo toIsDeletedInfo(final boolean deleted) {
    return toInfoMapper.toIsDeletedInfo(deleted);
  }

  @Override
  public ShareContactRequestStatusInfo toShareContactRequestStatusInfo(final ShareContactRequestStatus requestStatus) {
    return commonMapper.toShareContactRequestStatusInfo(requestStatus);
  }

  @Override
  public ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(final StreamResponse stream, final StreamAttendee existingAttendee) {
    return commonMapper.processAttendeeRequestToJoinStream(stream, existingAttendee);
  }

  @Override
  public NotAttendingStreamResponse notAttendingStream() {
    return commonMapper.notAttendingStream();
  }
}
