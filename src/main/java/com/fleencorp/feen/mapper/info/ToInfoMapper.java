package com.fleencorp.feen.mapper.info;

import com.fleencorp.feen.block.user.model.info.HasBlockedInfo;
import com.fleencorp.feen.block.user.model.info.IsBlockedInfo;
import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.follower.model.info.IsFollowedInfo;
import com.fleencorp.feen.follower.model.info.IsFollowingInfo;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.common.model.info.JoinStatusInfo;
import com.fleencorp.feen.like.model.info.LikeCountInfo;
import com.fleencorp.feen.review.model.info.ReviewCountInfo;
import com.fleencorp.feen.stream.model.info.attendance.AttendanceInfo;
import com.fleencorp.feen.stream.model.info.attendance.AttendeeCountInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsASpeakerInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsAttendingInfo;
import com.fleencorp.feen.stream.model.info.attendee.IsOrganizerInfo;
import com.fleencorp.feen.stream.model.info.attendee.StreamAttendeeRequestToJoinStatusInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowedInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowingInfo;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.poll.constant.core.PollVisibility;
import com.fleencorp.feen.poll.model.info.*;

public interface ToInfoMapper {

  StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatus(StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  JoinStatusInfo toJoinStatusInfo(JoinStatus joinStatus);

  JoinStatusInfo toJoinStatus(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, boolean isAttending);

  AttendanceInfo toAttendanceInfo(StreamResponse stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, boolean isAttending, boolean isASpeaker);

  StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatusInfo(StreamAttendeeRequestToJoinStatus requestToJoinStatus);

  IsAttendingInfo toIsAttendingInfo(boolean isAttending);

  IsASpeakerInfo toIsASpeakerInfo(boolean isASpeaker);

  IsOrganizerInfo toIsOrganizerInfo(boolean isOrganizer);

  IsBlockedInfo toIsBlockedInfo(boolean blocked, String blockingUser);

  HasBlockedInfo toHasBlockedInfo(boolean blocked, String blockingUserName);

  IsFollowingInfo toIsFollowingInfo(boolean following, String userBeingFollowedName);

  IsFollowedInfo toIsFollowedInfo(boolean followed, String userFollowingName);

  TotalFollowedInfo toTotalFollowedInfo(Long followed, String targetMemberName);

  TotalFollowingInfo toTotalFollowingInfo(Long following, String targetMemberName);

  UserLikeInfo toLikeInfo(boolean liked);

  IsDeletedInfo toIsDeletedInfo(boolean deleted);

  PollVisibilityInfo toPollVisibilityInfo(PollVisibility pollVisibility);

  IsAnonymousInfo toIsAnonymousInfo(boolean anonymous);

  IsEndedInfo toIsEnded(boolean ended);

  IsMultipleChoiceInfo toIsMultipleChoiceInfo(boolean multipleChoice);

  IsVotedInfo toIsVotedInfo(boolean voted);

  TotalPollVoteEntriesInfo toTotalPollVoteEntriesInfo(Integer pollVoteEntries);

  LikeCountInfo toLikeCountInfo(Integer likeCount);

  ReviewCountInfo toReviewCountInfo(Integer reviewCount);

  AttendeeCountInfo toAttendeeCountInfo(Integer attendeeCount);
}
