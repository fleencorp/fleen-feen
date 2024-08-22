package com.fleencorp.feen.service.impl.user;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.model.domain.user.Follower;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.response.user.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.user.FollowerRepository;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.user.FollowerService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.mapper.FollowerMapper.toFollowerOrFollowingResponses;

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

  private final FollowerRepository followerRepository;
  private final LocalizedResponse localizedResponse;

  /**
   * Constructs a new instance of {@link FollowerServiceImpl}.
   *
   * @param followerRepository the {@link FollowerRepository} used to access follower data
   * @param localizedResponse the service for adding localized message for responses
   */
  public FollowerServiceImpl(
      final FollowerRepository followerRepository,
      final LocalizedResponse localizedResponse) {
    this.followerRepository = followerRepository;
    this.localizedResponse = localizedResponse;
  }

  /**
   * Handles the process of a user following another user.
   *
   * @param userId the ID of the user to be followed
   * @param user the user who is following
   * @return a response object indicating the outcome of the follow operation
   */
  @Override
  @Transactional
  public FollowUserResponse followUser(final Long userId, final FleenUser user) {
    // Create a Member object for the user to be followed
    final Member followed = Member.of(userId);
    // Convert the current FleenUser to a Member object representing the follower
    final Member follower = user.toMember();

    // Check if the follower is already following the followed user
    followerRepository.findByFollowerAndFollowed(follower, followed)
      .ifPresentOrElse(
        // If already following, do nothing
        _ -> {},
        // If not already following, create and save a new Follower entity
        () -> {
          final Follower newFollower = Follower.of(follower, followed);
          followerRepository.save(newFollower);
      });

    // Return a response indicating the follow operation was successful
    return localizedResponse.of(FollowUserResponse.of());
  }

  /**
   * Handles the process of a user unfollowing another user.
   *
   * @param userId the ID of the user to be unfollowed
   * @param user   the user who is unfollowing
   * @return a response object indicating the outcome of the unfollow operation
   */
  @Override
  @Transactional
  public UnfollowUserResponse unfollowUser(final Long userId, final FleenUser user) {
    // Create a Member object for the user to be unfollowed
    final Member followed = Member.of(userId);
    // Convert the current FleenUser to a Member object representing the follower
    final Member follower = user.toMember();

    // Check if the follower is currently following the followed user
    followerRepository.findByFollowerAndFollowed(follower, followed)
      // If a follower relationship exists, delete it
      .ifPresent(followerRepository::delete);

    // Return a response indicating the unfollow operation was successful
    return localizedResponse.of(UnfollowUserResponse.of());
  }

  /**
   * Retrieves a paginated list of followers for a given user.
   *
   * @param followed the user whose followers are to be retrieved
   * @param searchRequest the search request containing pagination details
   * @return a paginated result containing a list of UserResponse views representing the followers
   */
  @Override
  public FollowersResponse getFollowers(final FleenUser followed, final SearchRequest searchRequest) {
    // Retrieve a paginated list of followers based on the given follower and search request
    final Page<Follower> page = followerRepository.findByFollowed(followed.toMember(), searchRequest.getPage());
    // Convert the list of followers to UserResponse views
    final List<UserResponse> views = toFollowerOrFollowingResponses(page.getContent());
    // Return the paginated search result containing the UserResponse views
    return localizedResponse.of(FollowersResponse.of(toSearchResult(views, page)));
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
   * @param follower the user who is following others
   * @param searchRequest the search request containing pagination details
   * @return a paginated result containing the list of users followed by the given follower
   */
  @Override
  public FollowingsResponse getUsersFollowing(final FleenUser follower, final SearchRequest searchRequest) {
    // Retrieve a paginated list of followers based on the given follower and search request
    final Page<Follower> page = followerRepository.findByFollower(follower.toMember(), searchRequest.getPage());
    // Convert the list of followers to UserResponse views
    final List<UserResponse> views = toFollowerOrFollowingResponses(page.getContent());
    // Return the paginated search result containing the UserResponse views
    return localizedResponse.of(FollowingsResponse.of(toSearchResult(views, page)));
  }
}
