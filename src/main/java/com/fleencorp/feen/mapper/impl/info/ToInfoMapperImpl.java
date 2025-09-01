package com.fleencorp.feen.mapper.impl.info;

import com.fleencorp.feen.block.user.constant.HasBlocked;
import com.fleencorp.feen.block.user.constant.IsBlocked;
import com.fleencorp.feen.block.user.model.info.HasBlockedInfo;
import com.fleencorp.feen.block.user.model.info.IsBlockedInfo;
import com.fleencorp.feen.bookmark.constant.BookmarkCount;
import com.fleencorp.feen.bookmark.constant.IsBookmarked;
import com.fleencorp.feen.bookmark.model.info.BookmarkCountInfo;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.common.constant.common.IsDeleted;
import com.fleencorp.feen.common.constant.common.ShareCount;
import com.fleencorp.feen.common.constant.stat.TotalFollowed;
import com.fleencorp.feen.common.constant.stat.TotalFollowing;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.common.model.info.ShareCountInfo;
import com.fleencorp.feen.follower.constant.IsFollowed;
import com.fleencorp.feen.follower.constant.IsFollowing;
import com.fleencorp.feen.follower.model.info.IsFollowedInfo;
import com.fleencorp.feen.follower.model.info.IsFollowingInfo;
import com.fleencorp.feen.like.constant.IsLiked;
import com.fleencorp.feen.like.constant.LikeCount;
import com.fleencorp.feen.like.model.info.LikeCountInfo;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.model.contract.Bookmarkable;
import com.fleencorp.feen.model.contract.Likeable;
import com.fleencorp.feen.model.info.user.profile.TotalFollowedInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowingInfo;
import com.fleencorp.feen.poll.constant.*;
import com.fleencorp.feen.poll.constant.core.PollVisibility;
import com.fleencorp.feen.poll.model.info.*;
import com.fleencorp.feen.review.constant.ReviewCount;
import com.fleencorp.feen.review.model.info.ReviewCountInfo;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

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
public class ToInfoMapperImpl extends BaseMapper implements ToInfoMapper {

  public ToInfoMapperImpl(final MessageSource messageSource) {
    super(messageSource);
  }

  /**
   * Constructs an {@link IsBlockedInfo} object based on the blocking status and the name of the user who blocked.
   *
   * <p>It uses the {@link IsBlocked} enum to determine message codes and translates them with the target user's name
   * to produce user-friendly, localized messages about the block status.</p>
   *
   * @param blocked {@code true} if the current user is blocked by the target user; {@code false} otherwise
   * @param blockingUserName the full name of the user who initiated the block
   * @return an {@link IsBlockedInfo} object containing the block status and localized messages
   */
  @Override
  public IsBlockedInfo toIsBlockedInfo(final boolean blocked, final String blockingUserName) {
    final IsBlocked isBlocked = IsBlocked.by(blocked);

    return IsBlockedInfo.of(
      blocked,
      translate(isBlocked.getMessageCode(), blockingUserName),
      translate(isBlocked.getMessageCode2(), blockingUserName),
      translate(isBlocked.getMessageCode3(), blockingUserName)
    );
  }

  /**
   * Constructs a {@link HasBlockedInfo} object based on the blocking status and the name of the user who was blocked.
   *
   * <p>It uses the {@link HasBlocked} enum to determine message codes and translates them with the target user's name
   * to produce user-friendly, localized messages about the block status.</p>
   *
   * @param blocked {@code true} if the current user has blocked the target user; {@code false} otherwise
   * @param blockingUserName the full name of the user who was blocked
   * @return a {@link HasBlockedInfo} object containing the block status and localized messages
   */
  @Override
  public HasBlockedInfo toHasBlockedInfo(final boolean blocked, final String blockingUserName) {
    final HasBlocked hasBlocked = HasBlocked.by(blocked);

    return HasBlockedInfo.of(
      blocked,
      translate(hasBlocked.getMessageCode(), blockingUserName),
      translate(hasBlocked.getMessageCode2(), blockingUserName),
      translate(hasBlocked.getMessageCode3(), blockingUserName)
    );
  }

  /**
   * Constructs an {@link IsFollowingInfo} object based on whether the current user is following the target user.
   *
   * <p>Uses the {@link IsFollowing} enum to determine the appropriate message codes, which are then translated into
   * user-friendly, localized messages including the name of the user being followed.</p>
   *
   * @param following {@code true} if the current user is following the target user; {@code false} otherwise
   * @param userBeingFollowedName the full name of the target user being followed
   * @return an {@link IsFollowingInfo} containing the following status and localized descriptive messages
   */
  @Override
  public IsFollowingInfo toIsFollowingInfo(final boolean following, final String userBeingFollowedName) {
    final IsFollowing isFollowing = IsFollowing.by(following);

    return IsFollowingInfo.of(
      following,
      translate(isFollowing.getMessageCode(), userBeingFollowedName),
      translate(isFollowing.getMessageCode2(), userBeingFollowedName),
      translate(isFollowing.getMessageCode3(), userBeingFollowedName),
      translate(isFollowing.getMessageCode4(), userBeingFollowedName)
    );
  }

  /**
   * Constructs an {@link IsFollowedInfo} object based on whether the target user is followed by the current user.
   *
   * <p>Uses the {@link IsFollowed} enum to determine the appropriate message codes, which are then translated into
   * user-friendly, localized messages including the name of the user following.</p>
   *
   * @param followed {@code true} if the target user is followed by the current user; {@code false} otherwise
   * @param userFollowingName the full name of the user who is following
   * @return an {@link IsFollowedInfo} containing the follow-back status and localized descriptive messages
   */
  @Override
  public IsFollowedInfo toIsFollowedInfo(final boolean followed, final String userFollowingName) {
    final IsFollowed isFollowed = IsFollowed.by(followed);

    return IsFollowedInfo.of(
      followed,
      translate(isFollowed.getMessageCode(), userFollowingName),
      translate(isFollowed.getMessageCode2(), userFollowingName),
      translate(isFollowed.getMessageCode3(), userFollowingName),
      translate(isFollowed.getMessageCode4(), userFollowingName)
    );
  }

  /**
   * Constructs a {@link TotalFollowedInfo} object representing the total number of users the target member is followed by.
   *
   * <p>Retrieves the appropriate message code from the {@link TotalFollowed} enum and translates it into localized messages
   * that describe the total followers count, with and without the target member's name.</p>
   *
   * @param followed the number of users following the target member
   * @param targetMemberName the full name of the target member being followed
   * @return a {@link TotalFollowedInfo} containing the follower count and localized descriptive messages
   */
  @Override
  public TotalFollowedInfo toTotalFollowedInfo(final Long followed, final String targetMemberName) {
    final TotalFollowed totalFollowed = TotalFollowed.TOTAL_FOLLOWED;

    return TotalFollowedInfo.of(followed,
      translate(totalFollowed.getMessageCode(), followed),
      translate(totalFollowed.getMessageCode(), targetMemberName, followed));
  }

  /**
   * Constructs a {@link TotalFollowingInfo} object representing the total number of users the target member is following.
   *
   * <p>Retrieves the appropriate message code from the {@link TotalFollowing} enum and translates it into localized messages
   * that describe the total following count, with and without the target member's name.</p>
   *
   * @param following the number of users the target member is following
   * @param targetMemberName the full name of the target member who is following others
   * @return a {@link TotalFollowingInfo} containing the following count and localized descriptive messages
   */
  @Override
  public TotalFollowingInfo toTotalFollowingInfo(final Long following, final String targetMemberName) {
    final TotalFollowing totalFollowing = TotalFollowing.TOTAL_FOLLOWING;

    return TotalFollowingInfo.of(following,
      translate(totalFollowing.getMessageCode(), following),
      translate(totalFollowing.getMessageCode(), targetMemberName, following));
  }

  /**
   * Creates a {@link UserLikeInfo} instance representing the like status of the user.
   *
   * <p>The method determines the {@link IsLiked} state based on the provided
   * {@code liked} flag and resolves two localized messages from its associated message codes.
   * The resulting {@link UserLikeInfo} contains both the raw status and
   * the translated messages.</p>
   *
   * @param liked whether the entity is liked by the current user
   * @return a {@link UserLikeInfo} containing the like state and its localized descriptions
   */
  @Override
  public UserLikeInfo toLikeInfo(final boolean liked) {
    final IsLiked isLiked = IsLiked.by(liked);

    return UserLikeInfo.of(liked, translate(isLiked.getMessageCode()), translate(isLiked.getMessageCode2()));
  }

  /**
   * Creates a {@link UserBookmarkInfo} instance representing the bookmark status of the user.
   *
   * <p>The method determines the {@link IsBookmarked} state based on the provided
   * {@code bookmarked} flag and uses its message code to resolve a localized message.
   * The resulting {@link UserBookmarkInfo} contains both the raw status and
   * the translated message.</p>
   *
   * @param bookmarked whether the entity is bookmarked by the current user
   * @return a {@link UserBookmarkInfo} containing the bookmark state and its localized description
   */
  @Override
  public UserBookmarkInfo toBookmarkInfo(final boolean bookmarked) {
    final IsBookmarked isBookmarked = IsBookmarked.by(bookmarked);
    return UserBookmarkInfo.of(bookmarked, translate(isBookmarked.getMessageCode()));
  }

  /**
   * Populates the given {@link Bookmarkable} response object with user bookmark information
   * and the total bookmark count details.
   *
   * <p>If the response is non-null, a {@link UserBookmarkInfo} is created based on the
   * {@code isBookmarked} flag and assigned to the response. Similarly, a
   * {@link BookmarkCountInfo} is created from the given {@code bookmarkCount} and set on
   * the response.</p>
   *
   * @param response       the response object implementing {@link Bookmarkable}
   * @param isBookmarked   whether the current user has bookmarked the entity
   * @param bookmarkCount  the total number of bookmarks for the entity
   * @param <T>            the type of the response implementing {@link Bookmarkable}
   */
  @Override
  public <T extends Bookmarkable> void setBookmarkInfo(final T response, final boolean isBookmarked, final int bookmarkCount) {
    if (nonNull(response)) {
      final UserBookmarkInfo userBookmarkInfo = toBookmarkInfo(isBookmarked);
      response.setUserBookmarkInfo(userBookmarkInfo);

      final BookmarkCountInfo bookmarkCountInfo = toBookmarkCountInfo(bookmarkCount);
      response.setBookmarkCountInfo(bookmarkCountInfo);
    }
  }

  /**
   * Populates the given {@link Likeable} response object with user like information
   * and the total like count details.
   *
   * <p>If the response is non-null, a {@link UserLikeInfo} is created based on the
   * {@code isLiked} flag and assigned to the response. Similarly, a
   * {@link LikeCountInfo} is created from the given {@code likeCount} and set on
   * the response.</p>
   *
   * @param response  the response object implementing {@link Likeable}
   * @param isLiked   whether the current user has liked the entity
   * @param likeCount the total number of likes for the entity
   * @param <T>       the type of the response implementing {@link Likeable}
   */
  @Override
  public <T extends Likeable> void setLikeInfo(final T response, final boolean isLiked, final int likeCount) {
    if (nonNull(response)) {
      final UserLikeInfo userLikeInfo = toLikeInfo(isLiked);
      response.setUserLikeInfo(userLikeInfo);

      final LikeCountInfo likeCountInfo = toLikeCountInfo(likeCount);
      response.setLikeCountInfo(likeCountInfo);
    }
  }

  /**
   * Converts the given deletion status into an {@link IsDeletedInfo} object.
   *
   * <p>This method takes a boolean value representing whether an entity has been deleted or not and
   * maps it to an {@link IsDeleted} enum. It then constructs an {@link IsDeletedInfo} object using
   * this enum, along with translations of the associated message codes for localization.</p>
   *
   * <p>The resulting {@link IsDeletedInfo} provides information on the deletion status, including
   * localized message codes that can be used to display relevant messages to users.</p>
   *
   * @param deleted The boolean flag indicating whether the entity has been deleted.
   * @return The {@link IsDeletedInfo} object containing the deletion status and message codes.
   */
  @Override
  public IsDeletedInfo toIsDeletedInfo(final boolean deleted) {
    final IsDeleted isDeleted = IsDeleted.by(deleted);
    return IsDeletedInfo.of(deleted, translate(isDeleted.getMessageCode()), translate(isDeleted.getMessageCode2()));
  }




  /**
   * Converts a raw like count into a {@link LikeCountInfo} DTO,
   * including a translated message with the count embedded.
   *
   * <p>For example, if {@code likeCount} is 3 and the message code resolves to
   * "{0} likes", the resulting message will be "You have 3 likes".</p>
   *
   * @param likeCount the total number of likes
   * @return a {@link LikeCountInfo} containing the like count and its localized message
   */
  @Override
  public LikeCountInfo toLikeCountInfo(final Integer likeCount) {
    final LikeCount totalLikeCount = LikeCount.totalLikes();

    return LikeCountInfo.of(likeCount,
      translate(totalLikeCount.getMessageCode(), likeCount)
    );
  }

  /**
   * Converts the given bookmark count into a {@link BookmarkCountInfo} containing both
   * the numeric count and its localized message representation.
   *
   * <p>The method creates a {@link BookmarkCount} instance representing the total
   * bookmark count, then resolves a localized message using the provided count
   * and the message code from the {@link BookmarkCount}. Finally, it constructs
   * and returns a {@link BookmarkCountInfo} with this information.</p>
   *
   * @param bookmarkCount the total number of bookmarks
   * @return a {@link BookmarkCountInfo} containing the numeric count and a localized message
   */
  @Override
  public BookmarkCountInfo toBookmarkCountInfo(final Integer bookmarkCount) {
    final BookmarkCount totalBookmarkCount = BookmarkCount.totalBookmarks();
    return BookmarkCountInfo.of(bookmarkCount,
      translate(totalBookmarkCount.getMessageCode(), bookmarkCount)
    );
  }

  /**
   * Converts a share count value into a {@link ShareCountInfo} object.
   *
   * <p>This method creates a {@link ShareCountInfo} by combining the given
   * share count with a localized message obtained from the {@link ShareCount}
   * message code. The message is translated based on the total share count
   * context and the provided count.</p>
   *
   * @param shareCount the number of shares to include in the response
   * @return a {@link ShareCountInfo} containing the share count and its localized message
   */
  @Override
  public ShareCountInfo toShareCountInfo(final Integer shareCount) {
    final ShareCount totalShareCount = ShareCount.totalShares();
    return ShareCountInfo.of(shareCount,
      translate(totalShareCount.getMessageCode(), shareCount));
  }

  /**
   * Converts a raw review count into a {@link ReviewCountInfo} DTO,
   * including a translated message with the count embedded.
   *
   * <p>For example, if {@code reviewCount} is 5 and the message code resolves to
   * "{0} reviews", the resulting message will be "You have 5 reviews".</p>
   *
   * @param reviewCount the total number of reviews
   * @return a {@link ReviewCountInfo} containing the review count and its localized message
   */
  @Override
  public ReviewCountInfo toReviewCountInfo(final Integer reviewCount) {
    final ReviewCount totalReviewCount = ReviewCount.totalReviews();
    return ReviewCountInfo.of(reviewCount,
      translate(totalReviewCount.getMessageCode(), reviewCount)
    );
  }


}
