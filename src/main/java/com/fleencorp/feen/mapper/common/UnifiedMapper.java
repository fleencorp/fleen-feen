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

import java.util.List;

public interface UnifiedMapper {

  ChatSpaceResponse toChatSpaceResponse(ChatSpace entry);

  ChatSpaceResponse toChatSpaceResponseByAdminUpdate(ChatSpace entry);

  List<ChatSpaceResponse> toChatSpaceResponses(List<ChatSpace> entries);

  void setMembershipInfo(ChatSpaceResponse chatSpace, ChatSpaceRequestToJoinStatus requestToJoinStatus, JoinStatus joinStatus, ChatSpaceMemberRole memberRole, boolean isAMember, boolean isAdmin, boolean hasLeft, boolean isRemoved);

  ChatSpaceStatusInfo toChatSpaceStatusInfo(ChatSpaceStatus status);

  ChatSpaceMembershipInfo getMembershipInfo(ChatSpaceMember entry, ChatSpace chatSpace);

  List<ChatSpaceMemberResponse> toChatSpaceMemberResponses(List<ChatSpaceMember> entries, ChatSpace chatSpace);

  List<ChatSpaceMemberResponse> toChatSpaceMemberResponsesPublic(List<ChatSpaceMember> entries);

  IsBlockedInfo toIsBlockedInfo(boolean blocked, String blockingUser);

  HasBlockedInfo toHasBlockedInfo(boolean hasBlocked, String blockingUser);

  IsFollowingInfo toIsFollowingInfo(boolean following, String userBeingFollowed);

  IsFollowedInfo toIsFollowedInfo(boolean followed, String userFollowing);

  TotalFollowedInfo toTotalFollowedInfo(Long followed, String targetMemberName);

  TotalFollowingInfo toTotalFollowingInfo(Long following, String targetMemberName);

  UserLikeInfo toLikeInfo(boolean liked);

  ShareContactRequestStatusInfo toShareContactRequestStatusInfo(ShareContactRequestStatus requestStatus);

  UserBookmarkInfo toBookmarkInfo(boolean isBookmarked);

  IsDeletedInfo toIsDeletedInfo(boolean deleted);
}
