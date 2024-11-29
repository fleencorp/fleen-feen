package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.user.Follower;
import com.fleencorp.feen.model.response.user.UserResponse;

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
public class FollowerMapper {

  private FollowerMapper() {}

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
  public static UserResponse toFollowerResponse(final Follower entry) {
    if (nonNull(entry)) {
      return UserResponse.builder()
        .userId(entry.getFollowed().getMemberId())
        .fullName(entry.getFollowed().getFullName())
        .build();
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
  public static UserResponse toFollowingResponse(final Follower entry) {
    if (nonNull(entry)) {
      return UserResponse.builder()
        .userId(entry.getFollowing().getMemberId())
        .fullName(entry.getFollowing().getFullName())
        .build();
    }
    return null;
  }

  /**
   * Converts a list of {@link Follower} entries into a list of {@link UserResponse} objects.
   *
   * <p>This method processes the provided list of {@link Follower} entries, filtering out any {@code null} entries,
   * and mapping each valid {@link Follower} to a corresponding {@link UserResponse} using the {@link FollowerMapper}.
   * If the input list is {@code null} or empty, an empty list is returned.</p>
   *
   * @param entries the list of {@link Follower} entries to be converted, which may contain {@code null} values.
   * @return a list of {@link UserResponse} objects corresponding to the non-null {@link Follower} entries,
   *         or an empty list if the input is {@code null} or empty.
   */
  public static List<UserResponse> toFollowerResponses(final List<Follower> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(FollowerMapper::toFollowerResponse)
        .toList();
    }
    return List.of();
  }

  /**
   * Converts a list of {@link Follower} entries into a list of {@link UserResponse} objects.
   *
   * <p>This method processes the provided list of {@link Follower} entries, filtering out any {@code null} entries,
   * and mapping each valid {@link Follower} to a corresponding {@link UserResponse} using the {@link FollowerMapper}.
   * If the input list is {@code null} or empty, an empty list is returned.</p>
   *
   * @param entries the list of {@link Follower} entries to be converted, which may contain {@code null} values.
   * @return a list of {@link UserResponse} objects corresponding to the non-null {@link Follower} entries, or an empty list if the input is {@code null} or empty.
   */
  public static List<UserResponse> toFollowingResponses(final List<Follower> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(FollowerMapper::toFollowingResponse)
        .toList();
    }
    return List.of();
  }
}
