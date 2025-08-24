package com.fleencorp.feen.mapper.common;


import com.fleencorp.feen.block.user.model.info.HasBlockedInfo;
import com.fleencorp.feen.block.user.model.info.IsBlockedInfo;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import com.fleencorp.feen.chat.space.constant.member.ChatSpaceMemberRole;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.chat.space.model.info.core.ChatSpaceStatusInfo;
import com.fleencorp.feen.chat.space.model.info.membership.ChatSpaceMembershipInfo;
import com.fleencorp.feen.chat.space.model.response.core.ChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.member.base.ChatSpaceMemberResponse;
import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.common.constant.social.ShareContactRequestStatus;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.follower.model.info.IsFollowedInfo;
import com.fleencorp.feen.follower.model.info.IsFollowingInfo;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.model.info.share.contact.request.ShareContactRequestStatusInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowedInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowingInfo;
import com.fleencorp.feen.review.model.info.ReviewCountInfo;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
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

import java.util.Collection;
import java.util.List;

public interface UnifiedMapper {

  StreamAttendeeResponse toStreamAttendeeResponse(StreamAttendee entry, StreamResponse streamResponse);

  Collection<StreamAttendeeResponse> toStreamAttendeeResponsesPublic(List<StreamAttendee> entries, StreamResponse streamResponse);

  StreamResponse toStreamResponse(FleenStream entry);

  StreamResponse toStreamResponseNoJoinStatus(FleenStream entry);

  List<StreamResponse> toStreamResponses(List<FleenStream> entries);

  StreamStatusInfo toStreamStatusInfo(StreamStatus streamStatus);

  StreamVisibilityInfo toStreamVisibilityInfo(StreamVisibility streamVisibility);

  StreamTypeInfo toStreamTypeInfo(StreamType streamType);

  void update(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, boolean isAttending, boolean isASpeaker);

  ChatSpaceResponse toChatSpaceResponse(ChatSpace entry);

  ChatSpaceResponse toChatSpaceResponseByAdminUpdate(ChatSpace entry);

  List<ChatSpaceResponse> toChatSpaceResponses(List<ChatSpace> entries);

  void setMembershipInfo(ChatSpaceResponse chatSpace, ChatSpaceRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, ChatSpaceMemberRole memberRole, boolean isAMember, boolean isAdmin, boolean hasLeft, boolean isRemoved);

  ChatSpaceStatusInfo toChatSpaceStatusInfo(ChatSpaceStatus status);

  ChatSpaceMembershipInfo getMembershipInfo(ChatSpaceMember entry, ChatSpace chatSpace);

  List<ChatSpaceMemberResponse> toChatSpaceMemberResponses(List<ChatSpaceMember> entries, ChatSpace chatSpace);

  List<ChatSpaceMemberResponse> toChatSpaceMemberResponsesPublic(List<ChatSpaceMember> entries);

  AttendanceInfo toAttendanceInfo(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, boolean isAttending, boolean isASpeaker);

  IsASpeakerInfo toIsASpeakerInfo(boolean isASpeaker);

  IsBlockedInfo toIsBlockedInfo(boolean blocked, String blockingUser);

  HasBlockedInfo toHasBlockedInfo(boolean hasBlocked, String blockingUser);

  IsFollowingInfo toIsFollowingInfo(boolean following, String userBeingFollowed);

  IsFollowedInfo toIsFollowedInfo(boolean followed, String userFollowing);

  TotalFollowedInfo toTotalFollowedInfo(Long followed, String targetMemberName);

  TotalFollowingInfo toTotalFollowingInfo(Long following, String targetMemberName);

  UserLikeInfo toLikeInfo(boolean liked);

  AttendeeCountInfo toAttendeeCountInfo(Integer attendeeCount);

  ReviewCountInfo toReviewCountInfo(Integer reviewCount);

  ShareContactRequestStatusInfo toShareContactRequestStatusInfo(ShareContactRequestStatus requestStatus);

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(StreamResponse stream, StreamAttendee existingAttendee);

  UserBookmarkInfo toBookmarkInfo(boolean isBookmarked);

  IsDeletedInfo toIsDeletedInfo(boolean deleted);

  NotAttendingStreamResponse notAttendingStream();
}
