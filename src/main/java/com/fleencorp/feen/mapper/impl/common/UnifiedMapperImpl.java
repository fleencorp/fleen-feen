package com.fleencorp.feen.mapper.impl.common;

import com.fleencorp.feen.block.user.model.info.HasBlockedInfo;
import com.fleencorp.feen.block.user.model.info.IsBlockedInfo;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import com.fleencorp.feen.chat.space.constant.member.ChatSpaceMemberRole;
import com.fleencorp.feen.chat.space.mapper.ChatSpaceMapper;
import com.fleencorp.feen.chat.space.mapper.ChatSpaceMemberMapper;
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
import com.fleencorp.feen.mapper.CommonMapper;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.model.info.share.contact.request.ShareContactRequestStatusInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowedInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowingInfo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnifiedMapperImpl implements UnifiedMapper {

  private final ChatSpaceMapper chatSpaceMapper;
  private final ChatSpaceMemberMapper chatSpaceMemberMapper;
  private final CommonMapper commonMapper;
  private final ToInfoMapper toInfoMapper;

  public UnifiedMapperImpl(
      final ChatSpaceMapper chatSpaceMapper,
      final ChatSpaceMemberMapper chatSpaceMemberMapper,
      final CommonMapper commonMapper,
      final ToInfoMapper toInfoMapper) {
    this.chatSpaceMapper = chatSpaceMapper;
    this.chatSpaceMemberMapper = chatSpaceMemberMapper;
    this.commonMapper = commonMapper;
    this.toInfoMapper = toInfoMapper;
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
  public IsBlockedInfo toIsBlockedInfo(final boolean blocked, final String blockingUser) {
    return toInfoMapper.toIsBlockedInfo(blocked, blockingUser);
  }

  @Override
  public HasBlockedInfo toHasBlockedInfo(final boolean hasBlocked, final String blockingUser) {
    return toInfoMapper.toHasBlockedInfo(hasBlocked, blockingUser);
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
  public UserBookmarkInfo toBookmarkInfo(final boolean isBookmarked) {
    return toInfoMapper.toBookmarkInfo(isBookmarked);
  }

  @Override
  public IsDeletedInfo toIsDeletedInfo(final boolean deleted) {
    return toInfoMapper.toIsDeletedInfo(deleted);
  }

  @Override
  public ShareContactRequestStatusInfo toShareContactRequestStatusInfo(final ShareContactRequestStatus requestStatus) {
    return commonMapper.toShareContactRequestStatusInfo(requestStatus);
  }
}
