package com.fleencorp.feen.mapper.info;

import com.fleencorp.feen.block.user.model.info.HasBlockedInfo;
import com.fleencorp.feen.block.user.model.info.IsBlockedInfo;
import com.fleencorp.feen.bookmark.model.info.BookmarkCountInfo;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.common.model.info.ShareCountInfo;
import com.fleencorp.feen.follower.model.info.IsFollowedInfo;
import com.fleencorp.feen.follower.model.info.IsFollowingInfo;
import com.fleencorp.feen.like.model.info.LikeCountInfo;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.model.contract.Bookmarkable;
import com.fleencorp.feen.model.contract.Likeable;
import com.fleencorp.feen.model.info.user.profile.TotalFollowedInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowingInfo;
import com.fleencorp.feen.review.model.info.ReviewCountInfo;

public interface ToInfoMapper {

  IsBlockedInfo toIsBlockedInfo(boolean blocked, String blockingUser);

  HasBlockedInfo toHasBlockedInfo(boolean blocked, String blockingUserName);

  IsFollowingInfo toIsFollowingInfo(boolean following, String userBeingFollowedName);

  IsFollowedInfo toIsFollowedInfo(boolean followed, String userFollowingName);

  TotalFollowedInfo toTotalFollowedInfo(Long followed, String targetMemberName);

  TotalFollowingInfo toTotalFollowingInfo(Long following, String targetMemberName);

  UserLikeInfo toLikeInfo(boolean isLiked);

  UserBookmarkInfo toBookmarkInfo(boolean isBookmarked);

  <T extends Bookmarkable> void setBookmarkInfo(T response, boolean isBookmarked, int bookmarkCount);

  <T extends Likeable> void setLikeInfo(T response, boolean isLiked, int likeCount);

  IsDeletedInfo toIsDeletedInfo(boolean deleted);

  LikeCountInfo toLikeCountInfo(Integer likeCount);

  BookmarkCountInfo toBookmarkCountInfo(Integer bookmarkCount);

  ShareCountInfo toShareCountInfo(Integer shareCount);

  ReviewCountInfo toReviewCountInfo(Integer reviewCount);
}
