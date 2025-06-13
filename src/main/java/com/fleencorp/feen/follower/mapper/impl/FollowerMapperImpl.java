package com.fleencorp.feen.follower.mapper.impl;

import com.fleencorp.feen.follower.mapper.FollowerMapper;
import com.fleencorp.feen.follower.model.domain.Follower;
import com.fleencorp.feen.follower.model.info.IsFollowedInfo;
import com.fleencorp.feen.follower.model.info.IsFollowingInfo;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.user.model.response.UserResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * Utility class for mapping {@link Follower} entities to {@link UserResponse} DTOs.
 *
 * <p>This class provides static methods to convert {@link Follower} entities into {@link UserResponse} objects,
 * which are used to represent user details in the response layer of the application.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class FollowerMapperImpl extends BaseMapper implements FollowerMapper {

  private final ToInfoMapper toInfoMapper;

  private FollowerMapperImpl(
      final MessageSource messageSource,
      final ToInfoMapper toInfoMapper) {
    super(messageSource);
    this.toInfoMapper = toInfoMapper;
  }

  /**
   * Converts a {@link Follower} object into a {@link UserResponse} object.
   *
   * <p>This method takes a {@link Follower} instance and maps its associated followed member's
   * ID and full name to create a corresponding {@link UserResponse}. If the provided
   * {@link Follower} is {@code null}, the method returns {@code null}.</p>
   *
   * @param entry the {@link Follower} object to be converted into a {@link UserResponse}.
   * @return a {@link UserResponse} containing the member ID and full name of the followed member,
   *         or {@code null} if the input {@link Follower} is {@code null}.
   */
  public UserResponse toFollowerResponse(final Follower entry) {
    if (nonNull(entry)) {
      final UserResponse userResponse = new UserResponse();
      userResponse.setUserId(entry.getFollowing().getMemberId());
      userResponse.setFullName(entry.getFollowing().getFullName());
      userResponse.setUsername(entry.getFollowing().getUsername());

      setDefaultFollowerAndFollowingInfo(userResponse);

      return userResponse;
    }
    return null;
  }

  /**
   * Converts a {@link Follower} object into a {@link UserResponse} object.
   *
   * <p>This method takes a {@link Follower} instance and maps its associated following member's
   * ID and full name to create a corresponding {@link UserResponse}. If the provided
   * {@link Follower} is {@code null}, the method returns {@code null}.</p>
   *
   * @param entry the {@link Follower} object to be converted into a {@link UserResponse}.
   * @return a {@link UserResponse} containing the member ID and full name of the following member,
   *         or {@code null} if the input {@link Follower} is {@code null}.
   */
  public UserResponse toFollowingResponse(final Follower entry) {
    if (nonNull(entry)) {
      final UserResponse userResponse = new UserResponse();
      userResponse.setUserId(entry.getFollowed().getMemberId());
      userResponse.setFullName(entry.getFollowed().getFullName());
      userResponse.setUsername(entry.getFollowed().getUsername());

      setDefaultFollowerAndFollowingInfo(userResponse);

      return userResponse;
    }
    return null;
  }

  /**
   * Sets the default follower and following information on the given {@link UserResponse}
   * with both statuses marked as false.
   *
   * <p>This method is used to initialize the follow-related fields of a user response when
   * no follower relationship data is available. It sets both the "is followed" and "is following"
   * flags to {@code false}, using the user's full name in the respective info objects. If the
   * {@code userResponse} is {@code null}, the method exits without making any changes.</p>
   *
   * @param userResponse the user response object to update with default follower and following info
   */
  private void setDefaultFollowerAndFollowingInfo(final UserResponse userResponse) {
    if (nonNull(userResponse)) {
      final IsFollowedInfo isFollowedInfo = toInfoMapper.toIsFollowedInfo(false, userResponse.getFullName());
      final IsFollowingInfo isFollowingInfo = toInfoMapper.toIsFollowingInfo(false, userResponse.getFullName());

      userResponse.setIsFollowedInfo(isFollowedInfo);
      userResponse.setIsFollowingInfo(isFollowingInfo);
    }
  }

  /**
   * Converts a list of {@link Follower} entries into a list of {@link UserResponse} objects.
   *
   * <p>This method processes the provided list of {@link Follower} entries, filtering out any {@code null} entries,
   * and mapping each valid {@link Follower} to a corresponding {@link UserResponse} using the {@link FollowerMapperImpl}.
   * If the input list is {@code null} or empty, an empty list is returned.</p>
   *
   * @param entries the list of {@link Follower} entries to be converted, which may contain {@code null} values.
   * @return a list of {@link UserResponse} objects corresponding to the non-null {@link Follower} entries,
   *         or an empty list if the input is {@code null} or empty.
   */
  @Override
  public List<UserResponse> toFollowerResponses(final List<Follower> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toFollowerResponse)
        .toList();
    }
    return List.of();
  }

  /**
   * Converts a list of {@link Follower} entries into a list of {@link UserResponse} objects.
   *
   * <p>This method processes the provided list of {@link Follower} entries, filtering out any {@code null} entries,
   * and mapping each valid {@link Follower} to a corresponding {@link UserResponse} using the {@link FollowerMapperImpl}.
   * If the input list is {@code null} or empty, an empty list is returned.</p>
   *
   * @param entries the list of {@link Follower} entries to be converted, which may contain {@code null} values.
   * @return a list of {@link UserResponse} objects corresponding to the non-null {@link Follower} entries, or an empty list if the input is {@code null} or empty.
   */
  @Override
  public List<UserResponse> toFollowingResponses(final List<Follower> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toFollowingResponse)
        .toList();
    }
    return List.of();
  }
}
