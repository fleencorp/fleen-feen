package com.fleencorp.feen.mapper.impl.info;

import com.fleencorp.feen.block.user.constant.HasBlocked;
import com.fleencorp.feen.block.user.constant.IsBlocked;
import com.fleencorp.feen.block.user.model.info.HasBlockedInfo;
import com.fleencorp.feen.block.user.model.info.IsBlockedInfo;
import com.fleencorp.feen.common.constant.common.IsDeleted;
import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.like.constant.LikeCount;
import com.fleencorp.feen.review.constant.ReviewCount;
import com.fleencorp.feen.stream.constant.attendee.*;
import com.fleencorp.feen.common.constant.stat.TotalFollowed;
import com.fleencorp.feen.common.constant.stat.TotalFollowing;
import com.fleencorp.feen.follower.constant.IsFollowed;
import com.fleencorp.feen.follower.constant.IsFollowing;
import com.fleencorp.feen.follower.model.info.IsFollowedInfo;
import com.fleencorp.feen.follower.model.info.IsFollowingInfo;
import com.fleencorp.feen.like.constant.IsLiked;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
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
import com.fleencorp.feen.poll.constant.*;
import com.fleencorp.feen.poll.constant.core.PollVisibility;
import com.fleencorp.feen.poll.model.info.*;
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
   * Converts the given request-to-join status into its corresponding status information.
   *
   * @param requestToJoinStatus the status of the request to join the stream
   * @return the request-to-join status information for the given status
   */
  @Override
  public StreamAttendeeRequestToJoinStatusInfo toRequestToJoinStatus(final StreamAttendeeRequestToJoinStatus requestToJoinStatus) {
    return StreamAttendeeRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode()));
  }


  /**
   * Converts the given {@link JoinStatus} into a {@link JoinStatusInfo} object.
   *
   * <p>This method checks if the provided {@link JoinStatus} is non-null and, if so, creates a
   * {@link JoinStatusInfo} instance using the {@link JoinStatus}, along with translations of its
   * associated message codes for localization purposes.</p>
   *
   * <p>The resulting {@link JoinStatusInfo} contains the join status details, including localized
   * messages that can be used to provide feedback to the user based on their join status.</p>
   *
   * @param joinStatus The {@link JoinStatus} to be converted into a {@link JoinStatusInfo} object.
   * @return The {@link JoinStatusInfo} object containing the join status and message codes, or
   *         <code>null</code> if the {@link JoinStatus} is <code>null</code>.
   */
  @Override
  public JoinStatusInfo toJoinStatusInfo(final JoinStatus joinStatus) {
    if (nonNull(joinStatus)) {
      return JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()), translate(joinStatus.getMessageCode3()));
    }
    return null;
  }

  /**
   * Converts the given stream response and request-to-join status into the corresponding join status information.
   *
   * @param stream the stream response to be used for determining the join status
   * @param requestToJoinStatus the status of the request to join the stream
   * @param isAttending {@code true} if the user is attending the stream, {@code false} otherwise
   * @return the join status information for the given stream and request-to-join status
   */
  @Override
  public JoinStatusInfo toJoinStatus(final StreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending) {
    final JoinStatus joinStatus = JoinStatus.getJoinStatus(requestToJoinStatus, stream.getVisibility(), stream.hasHappened(), isAttending);
    return JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()), translate(joinStatus.getMessageCode3()));
  }

  /**
   * Converts the provided stream and attendee details into an {@code AttendanceInfo} object.
   *
   * <p>This method generates an {@code AttendanceInfo} object using the provided stream details,
   * attendee request-to-join status, attendance status, and speaker status. It internally
   * converts each of these components into their respective response-friendly formats:
   * {@code StreamAttendeeRequestToJoinStatusInfo}, {@code JoinStatusInfo}, and {@code IsAttendingInfo}.</p>
   *
   * @param stream the stream details represented by {@code FleenStreamResponse}
   * @param requestToJoinStatus the attendee's request-to-join status
   * @param isAttending boolean flag indicating if the attendee is attending
   * @param isASpeaker boolean flag indicating if the attendee is also a speaker
   * @return an {@code AttendanceInfo} object containing the attendee's request-to-join, join, and attendance info
   */
  @Override
  public AttendanceInfo toAttendanceInfo(final StreamResponse stream, final StreamAttendeeRequestToJoinStatus requestToJoinStatus, final boolean isAttending, final boolean isASpeaker) {
    final StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo = toRequestToJoinStatusInfo(requestToJoinStatus);
    final JoinStatusInfo joinStatusInfo = toJoinStatus(stream, requestToJoinStatus, isAttending);
    final IsAttendingInfo isAttendingInfo = toIsAttendingInfo(isAttending);
    final IsASpeakerInfo isASpeakerInfo = toIsASpeakerInfo(isASpeaker);

    return AttendanceInfo.of(requestToJoinStatusInfo, joinStatusInfo, isAttendingInfo, isASpeakerInfo);
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
   * @param isAttending a boolean indicating whether the attendee is currently attending
   * @return an {@link IsAttendingInfo} object containing the attendance status and its corresponding localized message
   */
  @Override
  public IsAttendingInfo toIsAttendingInfo(final boolean isAttending) {
    return IsAttendingInfo.of(isAttending, translate(IsAttending.by(isAttending).getMessageCode()));
  }

  /**
   * Converts the given speaker status into an {@code IsASpeakerInfo} object.
   *
   * <p>This method determines whether the provided status indicates the user is a speaker
   * and constructs an {@code IsASpeakerInfo} instance with the appropriate translated message.
   * The message is resolved using the {@code IsASpeaker} enum, which provides the relevant
   * message code for translation based on the speaker status.</p>
   *
   * @param aSpeaker the boolean flag indicating whether the user is a speaker
   * @return an {@code IsASpeakerInfo} object containing the speaker status and a localized message
   */
  @Override
  public IsASpeakerInfo toIsASpeakerInfo(final boolean aSpeaker) {
    final IsASpeaker isASpeaker = IsASpeaker.by(aSpeaker);

    return IsASpeakerInfo.of(aSpeaker, translate(isASpeaker.getMessageCode()), translate(isASpeaker.getMessageCode2()));
  }

  /**
   * Converts a boolean value representing whether an attendee is an organizer into an
   * {@link IsOrganizerInfo} DTO.
   *
   * <p>This method maps the boolean value to an {@link IsOrganizer} enum, then generates
   * an {@link IsOrganizerInfo} object containing localized messages associated with the
   * organizer status. It uses the {@code translate} method to resolve the message codes
   * to localized strings for both the primary and secondary message codes.</p>
   *
   * @param organizer a boolean indicating whether the attendee is an organizer
   * @return an {@link IsOrganizerInfo} object containing the organizer status and
   *         its associated localized messages
   */
  @Override
  public IsOrganizerInfo toIsOrganizerInfo(final boolean organizer) {
    final IsOrganizer isOrganizer = IsOrganizer.by(organizer);

    return IsOrganizerInfo.of(organizer, translate(isOrganizer.getMessageCode()), translate(isOrganizer.getMessageCode2()));
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

  @Override
  public UserLikeInfo toLikeInfo(final boolean liked) {
    final IsLiked isLiked = IsLiked.by(liked);

    return UserLikeInfo.of(liked, translate(isLiked.getMessageCode()));
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
   * Converts a {@link PollVisibility} enum into a localized {@link PollVisibilityInfo} DTO.
   *
   * <p>The returned object includes the original enum value along with its localized label and message,
   * resolved using message codes associated with the visibility level.</p>
   *
   * @param pollVisibility the visibility enum of the poll
   * @return a {@link PollVisibilityInfo} containing the visibility, label, and message
   */
  @Override
  public PollVisibilityInfo toPollVisibilityInfo(final PollVisibility pollVisibility) {
    return PollVisibilityInfo.of(pollVisibility, translate(pollVisibility.getLabelCode()), translate(pollVisibility.getMessageCode()));
  }

  /**
   * Converts a boolean indicating anonymity into a localized {@link IsAnonymousInfo} DTO.
   *
   * <p>This method uses the {@link IsAnonymous} enum to resolve the appropriate message codes,
   * which are then translated and included in the response.</p>
   *
   * @param anonymous true if the poll is anonymous, false otherwise
   * @return a {@link IsAnonymousInfo} containing the boolean value and its localized messages
   */
  @Override
  public IsAnonymousInfo toIsAnonymousInfo(final boolean anonymous) {
    final IsAnonymous isAnonymous = IsAnonymous.by(anonymous);
    return IsAnonymousInfo.of(anonymous, translate(isAnonymous.getMessageCode()), translate(isAnonymous.getMessageCode2()));
  }

  /**
   * Converts a boolean indicating poll completion status into a localized {@link IsEndedInfo} DTO.
   *
   * <p>The method determines the appropriate {@link IsEnded} enum based on the boolean value,
   * translates its associated message codes, and returns the result.</p>
   *
   * @param ended true if the poll has ended, false otherwise
   * @return a {@link IsEndedInfo} with localized messages for the poll end state
   */
  @Override
  public IsEndedInfo toIsEnded(final boolean ended) {
    final IsEnded isEnded = IsEnded.by(ended);
    return IsEndedInfo.of(ended, translate(isEnded.getMessageCode()), translate(isEnded.getMessageCode2()));
  }

  /**
   * Converts a boolean indicating whether the poll allows multiple choices into
   * a localized {@link IsMultipleChoiceInfo} DTO.
   *
   * <p>The method maps the boolean to an {@link IsMultipleChoice} enum and translates
   * both the short and long message codes for that state.</p>
   *
   * @param multipleChoice true if the poll allows multiple choices, false if it is single-choice
   * @return a {@link IsMultipleChoiceInfo} containing the boolean value and its translations
   */
  @Override
  public IsMultipleChoiceInfo toIsMultipleChoiceInfo(final boolean multipleChoice) {
    final IsMultipleChoice isMultipleChoice = IsMultipleChoice.by(multipleChoice);
    return IsMultipleChoiceInfo.of(multipleChoice, translate(isMultipleChoice.getMessageCode()), translate(isMultipleChoice.getMessageCode2()));
  }

  /**
   * Converts a boolean indicating vote status into a detailed localized {@link IsVotedInfo} DTO.
   *
   * <p>The method maps the boolean to an {@link IsVoted} enum and translates four different
   * message codes associated with the vote state. This allows for richer UI feedback such as
   * subtitles, descriptions, or tooltips.</p>
   *
   * @param voted true if the user has voted, false otherwise
   * @return a {@link IsVotedInfo} with the vote flag and four localized message values
   */
  @Override
  public IsVotedInfo toIsVotedInfo(final boolean voted) {
    final IsVoted isVoted = IsVoted.by(voted);

    return IsVotedInfo.of(voted,
      translate(isVoted.getMessageCode()),
      translate(isVoted.getMessageCode2()),
      translate(isVoted.getMessageCode3()),
      translate(isVoted.getMessageCode4())
    );
  }

  /**
   * Wraps the total number of poll vote entries into a localized {@link TotalPollVoteEntriesInfo} DTO.
   *
   * <p>This method attaches both short and descriptive translations to the raw vote count
   * using message codes from the {@link TotalVoteEntries} enum.</p>
   *
   * @param pollVoteEntries the total number of votes cast in the poll
   * @return a {@link TotalPollVoteEntriesInfo} with the vote count and its translations
   */

  @Override
  public TotalPollVoteEntriesInfo toTotalPollVoteEntriesInfo(final Integer pollVoteEntries) {
    final TotalVoteEntries totalVoteEntries = TotalVoteEntries.totalVoteEntries();
    return TotalPollVoteEntriesInfo.of(pollVoteEntries,
      translate(totalVoteEntries.getMessageCode(), pollVoteEntries),
      translate(totalVoteEntries.getMessageCode2(), pollVoteEntries),
      translate(totalVoteEntries.getMessageCode3(), pollVoteEntries)
    );
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

  @Override
  public AttendeeCountInfo toAttendeeCountInfo(final Integer attendeeCount) {
    final AttendeeCount totalAttendeeCount = AttendeeCount.totalAttendee();
    return AttendeeCountInfo.of(attendeeCount,
        translate(totalAttendeeCount.getMessageCode(), attendeeCount),
        translate(totalAttendeeCount.getMessageCode2(), attendeeCount),
        translate(totalAttendeeCount.getMessageCode3(), attendeeCount)
      );
  }
}
