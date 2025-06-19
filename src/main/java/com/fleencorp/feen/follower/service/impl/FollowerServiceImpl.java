package com.fleencorp.feen.follower.service.impl;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.follower.exception.core.FollowingNotFoundException;
import com.fleencorp.feen.follower.mapper.FollowerMapper;
import com.fleencorp.feen.follower.model.domain.Follower;
import com.fleencorp.feen.follower.model.dto.FollowOrUnfollowUserDto;
import com.fleencorp.feen.follower.model.info.IsFollowedInfo;
import com.fleencorp.feen.follower.model.info.IsFollowingInfo;
import com.fleencorp.feen.follower.model.request.FollowerSearchRequest;
import com.fleencorp.feen.follower.model.response.FollowUserResponse;
import com.fleencorp.feen.follower.model.response.UnfollowUserResponse;
import com.fleencorp.feen.follower.model.search.FollowerSearchResult;
import com.fleencorp.feen.follower.model.search.FollowingSearchResult;
import com.fleencorp.feen.follower.repository.FollowerRepository;
import com.fleencorp.feen.follower.service.FollowerService;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.model.contract.UserFollowStat;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.info.user.profile.TotalFollowedInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowingInfo;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.notification.NotificationService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.response.UserProfileResponse;
import com.fleencorp.feen.user.model.response.UserResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link FollowerService} interface, providing services related to followers.
 *
 * <p>This class is responsible for managing follower-related operations such as following and unfollowing users.
 * It interacts with the {@link FollowerRepository} to perform CRUD operations on follower data.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
public class FollowerServiceImpl implements FollowerService {

  private final NotificationMessageService notificationMessageService;
  private final NotificationService notificationService;
  private final FollowerRepository followerRepository;
  private final FollowerMapper followerMapper;
  private final ToInfoMapper toInfoMapper;
  private final Localizer localizer;

  public FollowerServiceImpl(
      final NotificationMessageService notificationMessageService,
      final NotificationService notificationService,
      final FollowerRepository followerRepository,
      final FollowerMapper followerMapper,
      final ToInfoMapper toInfoMapper,
      final Localizer localizer) {
    this.notificationMessageService = notificationMessageService;
    this.notificationService = notificationService;
    this.followerRepository = followerRepository;
    this.followerMapper = followerMapper;
    this.toInfoMapper = toInfoMapper;
    this.localizer = localizer;
  }

  /**
   * Retrieves a paginated list of followers for a given user.
   *
   * @param searchRequest the search request containing pagination details
   * @return a paginated result containing a list of UserResponse representing the followers
   */
  @Override
  @Transactional(readOnly = true)
  public FollowerSearchResult getFollowers(final FollowerSearchRequest searchRequest) {
    // Prepare parameters
    final Member member = searchRequest.getMember();
    final Pageable pageable = searchRequest.getPage();

    // Retrieve a paginated list of followers based on the given follower and search request
    final Page<Follower> page = followerRepository.findFollowersByUser(member, pageable);
    // Convert the list of followers to UserResponse
    final List<UserResponse> followerResponses = followerMapper.toFollowerResponses(page.getContent());
    // Process followers to set isFollowedInfo
    processFollowersDetail(followerResponses, member);
    // Create a search result
    final SearchResult searchResult = toSearchResult(followerResponses, page);
    // Create a search result with the responses and pagination details
    final FollowerSearchResult followerSearchResult = FollowerSearchResult.of(searchResult);
    // Return the search result
    return localizer.of(followerSearchResult);
  }

  /**
   * Retrieves a paginated list of users that are being followed by the specified user.
   *
   * <p>This method takes a `FleenUser` object representing the follower and a `SearchRequest`
   * object that contains pagination details. It queries the `followerRepository` to find
   * the users followed by the provided `follower`.</p>
   *
   * <p>The retrieved list of followers is then converted into `UserResponse` views, and the method
   * returns a paginated search result encapsulating these views.</p>
   *
   * @param searchRequest the search request containing pagination details
   * @return a paginated result containing the list of users followed by the given follower
   */
  @Override
  @Transactional(readOnly = true)
  public FollowingSearchResult getFollowings(final FollowerSearchRequest searchRequest) {
    // Prepare parameters
    final Member member = searchRequest.getMember();
    final Pageable pageable = searchRequest.getPage();

    // Retrieve a paginated list of followers based on the given follower and search request
    final Page<Follower> page = followerRepository.findByFollowing(member, pageable);
    // Convert the list of followers to UserResponse views
    final List<UserResponse> userResponses = followerMapper.toFollowingResponses(page.getContent());
    // Process followings to set isFollowingInfo
    processFollowingsDetail(userResponses, member);
    // Create the search result
    final SearchResult searchResult = toSearchResult(userResponses, page);
    // Create the following search result
    final FollowingSearchResult followingSearchResult = FollowingSearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
    return localizer.of(followingSearchResult);
  }

  /**
   * Enriches each {@link UserResponse} in the given collection with follower relationship details
   * in relation to the specified {@link Member}.
   *
   * <p>This method checks whether the given member follows any of the users represented in the response collection.
   * It first extracts user IDs from the responses and retrieves follow relationships where the member is following those users.
   * Then, it updates each {@code UserResponse} with information indicating whether the current user follows them
   * and whether they follow the current user. If the input is null or empty, or if no valid user IDs are found,
   * the method exits without processing further.</p>
   *
   * @param userResponses the collection of user responses to update with follow information
   * @param member the current member used to evaluate follower relationships
   */
  protected void processFollowersDetail(final Collection<UserResponse> userResponses, final Member member) {
    if (nonNull(userResponses) && !userResponses.isEmpty() && nonNull(member)) {
      // Extract user IDs
      final List<Long> userIds = extractUserIds(userResponses);
      if (userIds.isEmpty()) {
        return;
      }

      // Get following relationships (does FleenUser follow these users?)
      final List<Follower> followingRelationships = followerRepository.findByFollowingAndFollowers(member, userIds);
      final Set<Long> followingIds = extractFollowedIds(followingRelationships);

      userResponses.stream()
        .filter(Objects::nonNull)
        .forEach(response -> updateFollowedInfo(response, followingIds));
    }
  }

  /**
   * Extracts the IDs of followed users from a list of {@link Follower} relationships.
   *
   * <p>This method processes the given list of follower relationships and collects the
   * IDs of the users being followed. Each {@link Follower} object provides the ID of the
   * followed user, which is extracted and added to the resulting set. The returned set
   * contains unique followed user IDs corresponding to the input relationships.</p>
   *
   * @param relationships the list of follower relationships to process
   * @return a set of user IDs representing the followed users
   */
  private Set<Long> extractFollowedIds(final List<Follower> relationships) {
    return relationships.stream()
      .map(Follower::getFollowedId)
      .collect(Collectors.toSet());
  }

  /**
   * Updates the given {@link UserResponse} with information about the follow relationship
   * between the current user and the target user represented by the response.
   *
   * <p>This method checks whether the current user follows the target user by checking if the user's ID
   * is contained in the provided set of following IDs. It then creates and sets both follow direction
   * indicators on the response: one stating that the target user follows the current user, and another
   * indicating whether the current user follows the target user. This provides a complete picture
   * of the mutual following status between both users.</p>
   *
   * @param userResponse the user response to be enriched with follow status
   * @param followingIds the set of user IDs that the current user is following
   */
  private void updateFollowedInfo(final UserResponse userResponse, final Set<Long> followingIds) {
    final String fullName = userResponse.getFullName();

    // Determine if the user follows User
    final boolean isFollowing = followingIds.contains(userResponse.getUserId());

    // Create the info objects
    final IsFollowedInfo isFollowedInfo = toInfoMapper.toIsFollowedInfo(true, fullName); // Set isFollowedInfo (they follow the current user)
    final IsFollowingInfo isFollowingInfo = toInfoMapper.toIsFollowingInfo(isFollowing, fullName); // Set isFollowingInfo (current user follows them)

    // Set isFollowingInfo (current user follows them)
    userResponse.setIsFollowingInfo(isFollowingInfo);
    userResponse.setIsFollowedInfo(isFollowedInfo);
  }

  /**
   * Enriches each {@link UserResponse} in the given collection with following relationship details
   * in relation to the specified {@link Member}.
   *
   * <p>The method first extracts user IDs from the responses, then retrieves follow relationships
   * to determine which of those users follow the given member. Each {@code UserResponse} is then updated
   * with information indicating whether the current user follows them and whether they follow the current user.
   * If the input collection or member is null, or if the collection is empty, the method does nothing.</p>
   *
   * @param userResponses the collection of user responses to be updated with follow details
   * @param member the current member for whom follow relationships are evaluated
   */
  protected void processFollowingsDetail(final Collection<UserResponse> userResponses, final Member member) {
    if (nonNull(userResponses) && !userResponses.isEmpty() && nonNull(member)) {
      // Extract user IDs
      final List<Long> userIds = extractUserIds(userResponses);

      // Get followed-by relationships (do these users follow FleenUser?)
      final List<Follower> followedByRelationships = followerRepository.findByFollowedAndFollowings(member, userIds);
      final Set<Long> followedByIds = extractFollowingIds(followedByRelationships);

      // Process each response
      userResponses.stream()
        .filter(Objects::nonNull)
        .forEach(userResponse -> updateFollowingInfo(userResponse, followedByIds));
    }
  }

  /**
   * Extracts the unique IDs of users being followed from a list of {@link Follower} relationships.
   *
   * <p>The method maps each {@link Follower} object to the ID of the member who is following someone
   * and collects these IDs into a {@link Set} to ensure uniqueness. This is typically used to determine
   * which users the current member is following.</p>
   *
   * @param relationships the list of {@link Follower} entities representing follow relationships
   * @return a set of user IDs representing the members being followed
   */
  private Set<Long> extractFollowingIds(final List<Follower> relationships) {
    return relationships.stream()
      .map(Follower::getFollowingId)
      .collect(Collectors.toSet());
  }

  /**
   * Updates the given {@link UserResponse} object with following and followed information
   * based on the current user's relationship with the target user.
   *
   * <p>The method determines whether the target user is followed by the current user by checking
   * if the user's ID is present in the provided set of followed IDs. It then constructs two info
   * objects: one indicating that the target user follows the current user, and another indicating
   * whether the current user follows the target user. These info objects are added to the response
   * to reflect the mutual following status.</p>
   *
   * @param userResponse the user response object to be enriched with following info
   * @param followedByIds the set of user IDs that the current user is following
   */
  private void updateFollowingInfo(final UserResponse userResponse, final Set<Long> followedByIds) {
    final String fullName = userResponse.getFullName();

    // Determine if the user follows User
    final boolean isFollowed = followedByIds.contains(userResponse.getUserId());

    // Create info objects
    final IsFollowingInfo isFollowingInfo = toInfoMapper.toIsFollowingInfo(true, fullName); // Set isFollowedInfo (they follow the current user)
    final IsFollowedInfo isFollowedInfo = toInfoMapper.toIsFollowedInfo(isFollowed, fullName); // Set isFollowingInfo (current user follows them)

    // Set info on response
    userResponse.setIsFollowingInfo(isFollowingInfo);
    userResponse.setIsFollowedInfo(isFollowedInfo);
  }

  /**
   * Extracts a list of non-null user IDs from a collection of {@link UserResponse} objects.
   *
   * <p>The method filters out any null elements from the input collection, then maps each response
   * to its user ID. Any null user IDs are also excluded from the final result. The resulting list
   * contains only valid, non-null user IDs extracted from the provided responses.</p>
   *
   * @param responses the collection of {@link UserResponse} objects to process
   * @return a list of non-null user IDs extracted from the responses
   */
  private List<Long> extractUserIds(final Collection<UserResponse> responses) {
    return responses.stream()
      .filter(Objects::nonNull)
      .map(UserResponse::getUserId)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  /**
   * Handles the process of a user following another user.
   *
   * @param followUserDto the dto containing ID of the user to be followed
   * @param user the user who is following
   * @return a response object indicating the outcome of the follow operation
   */
  @Override
  @Transactional
  public FollowUserResponse followUser(final FollowOrUnfollowUserDto followUserDto, final RegisteredUser user) {
    // Prepare parameters
    final Member member = user.toMember();
    // Create a Member object for the user to be followed
    final Member targetMember = followUserDto.getMember();

    // Verify user cannot follow itself
    verifyUserCannotFollowOrUnfollowSelf(targetMember.getMemberId(), user.getId());
    // Verify following and other details
    final Follower followedMember = verifyFollowing(targetMember, member);
    // target member name
    final String targetMemberFullName = followedMember.getFollowedName();

    // Create the info
    final IsFollowingInfo isFollowingInfo = toInfoMapper.toIsFollowingInfo(true, targetMemberFullName);
    // Create the response
    final FollowUserResponse followUserResponse = FollowUserResponse.of(isFollowingInfo);
    // Set the follow stat
    setFollowerDetails(targetMember, followUserResponse);
    // Return a response indicating the follow operation was successful
    return localizer.of(followUserResponse);
  }

  /**
   * Ensures that the given {@code following} member is following the specified {@code followed} member.
   *
   * <p>If the relationship already exists, no action is taken. If not, a new {@link Follower} entity is created
   * and saved to represent the following relationship. Additionally, a follow notification is generated using
   * {@link NotificationMessageService#ofFollowing(Follower, Member)} and persisted via {@link NotificationService}.</p>
   *
   * @param followed the member who is being followed
   * @param following the member who is initiating the follow
   */
  private Follower verifyFollowing(final Member followed, final Member following) {
    // Check if the follower is already following the followed user
    return followerRepository.findByFollowingAndFollowed(following, followed)
      .orElseGet(() -> {
        // If not already following, create and save a new Follower entity
        final Follower newFollower = Follower.of(following, followed);
        followerRepository.save(newFollower);

        // Create and save notification
        final Notification notification = notificationMessageService.ofFollowing(newFollower, followed);
        notificationService.save(notification);

        return newFollower;
      });
  }


  /**
   * Handles the process of a user unfollowing another user.
   *
   * @param unfollowUserDto the dto containing ID of the user to be unfollowed
   * @param user   the user who is unfollowing
   * @return a response object indicating the outcome of the unfollow operation
   */
  @Override
  @Transactional
  public UnfollowUserResponse unfollowUser(final FollowOrUnfollowUserDto unfollowUserDto, final RegisteredUser user) {
    // Create a Member object for the user to be unfollowed
    final Member targetMember = unfollowUserDto.getMember();
    // Verify user cannot unfollow itself
    verifyUserCannotFollowOrUnfollowSelf(unfollowUserDto.getMemberId(), user.getId());
    // Convert the current FleenUser to a Member object representing the follower
    final Member follower = user.toMember();

    // Check if the follower is currently following the followed user
    final Follower targetMemberFollower = followerRepository.findByFollowingAndFollowed(follower, targetMember)
      .orElseThrow(FollowingNotFoundException::new);
    // Delete user if found
    followerRepository.delete(targetMemberFollower);
    // Get the user to unfollow name
    final String targetMemberFullName = targetMemberFollower.getFollowingName();

    // Create the info
    final IsFollowingInfo isFollowingInfo = toInfoMapper.toIsFollowingInfo(false, targetMemberFullName);
    // Create the response
    final UnfollowUserResponse unfollowUserResponse = UnfollowUserResponse.of(isFollowingInfo);
    // Set the follow stat
    setFollowerDetails(targetMember, unfollowUserResponse);
    // Return a response indicating the unfollow operation was successful
    return localizer.of(unfollowUserResponse);
  }

  /**
   * Populates follower-related details into the given {@link UserProfileResponse}.
   *
   * <p>Computes the total number of users following the target member and the total number the target member is following.
   * These counts are then mapped into localized {@link TotalFollowedInfo} and {@link TotalFollowingInfo} objects.</p>
   *
   * @param targetMember the member whose follower details are being retrieved
   * @param userFollowStat the response object to populate with follower data
   */
  @Override
  public void setFollowerDetails(final Member targetMember, final UserFollowStat userFollowStat) {
    final long totalFollowed = followerRepository.countByFollowed(targetMember.getMemberId());
    final long totalFollowing = followerRepository.countByFollowing(targetMember.getMemberId());

    final TotalFollowedInfo totalFollowedInfo = toInfoMapper.toTotalFollowedInfo(totalFollowed, targetMember.getFullName());
    final TotalFollowingInfo totalFollowingInfo = toInfoMapper.toTotalFollowingInfo(totalFollowing, targetMember.getFullName());

    userFollowStat.setTotalFollowedInfo(totalFollowedInfo);
    userFollowStat.setTotalFollowingInfo(totalFollowingInfo);
  }

  /**
   * Verifies that a user cannot follow or unfollow themselves, and throws a {@link FailedOperationException}
   * if the operation is invalid.
   *
   * <p>This method checks if either the provided {@code memberId} or {@code userId} is {@code null},
   * or if they are equal, indicating the user is attempting to follow or unfollow themselves.
   * If either condition is met, a {@link FailedOperationException} is thrown.</p>
   *
   * @param memberId the ID of the member being followed or unfollowed
   * @param userId   the ID of the user attempting the follow or unfollow operation
   * @throws FailedOperationException if the user attempts to follow or unfollow themselves,
   *                                  or if either {@code memberId} or {@code userId} is {@code null}
   */
  public void verifyUserCannotFollowOrUnfollowSelf(final Long memberId, final Long userId) {
    if (isNull(memberId) || isNull(userId) || Objects.equals(memberId, userId)) {
      throw new FailedOperationException();
    }
  }
}
