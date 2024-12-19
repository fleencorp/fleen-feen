package com.fleencorp.feen.service.impl.user;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.service.i18n.LocalizedResponse;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.user.Follower;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.social.follow.FollowOrUnfollowUserDto;
import com.fleencorp.feen.model.response.user.FollowUserResponse;
import com.fleencorp.feen.model.response.user.UnfollowUserResponse;
import com.fleencorp.feen.model.response.user.UserResponse;
import com.fleencorp.feen.model.search.social.follower.follower.EmptyFollowerSearchResult;
import com.fleencorp.feen.model.search.social.follower.follower.FollowerSearchResult;
import com.fleencorp.feen.model.search.social.follower.following.EmptyFollowingSearchResult;
import com.fleencorp.feen.model.search.social.follower.following.FollowingSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.user.FollowerRepository;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.notification.NotificationService;
import com.fleencorp.feen.service.user.FollowerService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.fleencorp.base.util.FleenUtil.handleSearchResult;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.mapper.impl.other.FollowerMapper.toFollowerResponses;
import static com.fleencorp.feen.mapper.impl.other.FollowerMapper.toFollowingResponses;
import static java.util.Objects.isNull;

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
  private final LocalizedResponse localizedResponse;

  /**
   * Constructs a new instance of {@link FollowerServiceImpl}.
   *
   * @param followerRepository the {@link FollowerRepository} used to access follower data
   * @param localizedResponse the service for adding localized message for responses
   */
  public FollowerServiceImpl(
      final NotificationMessageService notificationMessageService,
      final NotificationService notificationService,
      final FollowerRepository followerRepository,
      final LocalizedResponse localizedResponse) {
    this.notificationMessageService = notificationMessageService;
    this.notificationService = notificationService;
    this.followerRepository = followerRepository;
    this.localizedResponse = localizedResponse;
  }

  /**
   * Retrieves a paginated list of followers for a given user.
   *
   * @param searchRequest the search request containing pagination details
   * @param user the user whose followers are to be retrieved
   * @return a paginated result containing a list of UserResponse views representing the followers
   */
  @Override
  public FollowerSearchResult getFollowers(final SearchRequest searchRequest, final FleenUser user) {
    // Retrieve a paginated list of followers based on the given follower and search request
    final Page<Follower> page = followerRepository.findFollowersByUser(user.toMember(), searchRequest.getPage());
    // Convert the list of followers to UserResponse views
    final List<UserResponse> views = toFollowerResponses(page.getContent());
    // Return a search result view with the followers responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(FollowerSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyFollowerSearchResult.of(toSearchResult(List.of(), page)))
    );
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
   * @param user the user who is following others
   * @return a paginated result containing the list of users followed by the given follower
   */
  @Override
  public FollowingSearchResult getFollowings(final SearchRequest searchRequest, final FleenUser user) {
    // Retrieve a paginated list of followers based on the given follower and search request
    final Page<Follower> page = followerRepository.findByFollowing(user.toMember(), searchRequest.getPage());
    // Convert the list of followers to UserResponse views
    final List<UserResponse> views = toFollowingResponses(page.getContent());
    // Return a search result view with the followings responses and pagination details
    return handleSearchResult(
      page,
      localizedResponse.of(FollowingSearchResult.of(toSearchResult(views, page))),
      localizedResponse.of(EmptyFollowingSearchResult.of(toSearchResult(List.of(), page)))
    );
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
  public FollowUserResponse followUser(final FollowOrUnfollowUserDto followUserDto, final FleenUser user) {
    // Create a Member object for the user to be followed
    final Member followed = Member.of(followUserDto.getActualMemberId());
    // Verify user cannot follow itself
    verifyUserCannotFollowOrUnfollowSelf(followUserDto.getActualMemberId(), user.getId());
    // Convert the current FleenUser to a Member object representing the follower
    final Member following = user.toMember();

    // Check if the follower is already following the followed user
    followerRepository.findByFollowingAndFollowed(following, followed)
      .ifPresentOrElse(
        // If already following, do nothing
        _ -> {},
        // If not already following, create and save a new Follower entity
        () -> {
          final Follower newFollower = Follower.of(following, followed);
          followerRepository.save(newFollower);

          // Create and save notification
          final Notification notification = notificationMessageService.ofFollowing(newFollower, followed);
          notificationService.save(notification);
      });

    // Return a response indicating the follow operation was successful
    return localizedResponse.of(FollowUserResponse.of());
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
  public UnfollowUserResponse unfollowUser(final FollowOrUnfollowUserDto unfollowUserDto, final FleenUser user) {
    // Create a Member object for the user to be unfollowed
    final Member followed = Member.of(unfollowUserDto.getActualMemberId());
    // Verify user cannot unfollow itself
    verifyUserCannotFollowOrUnfollowSelf(unfollowUserDto.getActualMemberId(), user.getId());
    // Convert the current FleenUser to a Member object representing the follower
    final Member follower = user.toMember();

    // Check if the follower is currently following the followed user
    followerRepository.findByFollowingAndFollowed(follower, followed)
      // If a follower relationship exists, delete it
      .ifPresent(followerRepository::delete);

    // Return a response indicating the unfollow operation was successful
    return localizedResponse.of(UnfollowUserResponse.of());
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
    if (isNull(memberId) || isNull(userId)) {
      throw new FailedOperationException();
    } else if (Objects.equals(memberId, userId)) {
      throw new FailedOperationException();
    }
  }
}
