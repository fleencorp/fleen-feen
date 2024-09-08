package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.user.Follower;
import com.fleencorp.feen.model.response.user.UserResponse;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

/**
 * Utility class for mapping {@link Follower} entities to {@link UserResponse} DTOs.
 *
 * <p>This class provides static methods to convert {@link Follower} entities into {@link UserResponse} objects,
 * which are used to represent user details in the response layer of the application.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class FollowerMapper {

  private FollowerMapper() {}

  /**
   * Converts a {@link Follower} entity to a {@link UserResponse} DTO.
   *
   * @param follower the {@link Follower} entity to convert
   * @return a {@link UserResponse} representing the follower's details, or {@code null} if the {@link Follower} is {@code null}
   */
  public static UserResponse toFollowersOrFollowingResponse(final Follower follower) {
    if (nonNull(follower)) {
      return UserResponse.builder()
        .userId(follower.getFollower().getMemberId())
        .fullName(follower.getFollower().getFullName())
        .build();
    }
    return null;
  }

  /**
   * Converts a list of {@link Follower} entities to a list of {@link UserResponse} DTOs.
   *
   * @param entries the list of {@link Follower} entities to convert
   * @return a list of {@link UserResponse} DTOs, or an empty list if the input list is {@code null} or empty
   */
  public static List<UserResponse> toFollowerOrFollowingResponses(final List<Follower> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .map(FollowerMapper::toFollowersOrFollowingResponse)
        .filter(Objects::nonNull)
        .collect(toList());
    }
    return emptyList();
  }
}
