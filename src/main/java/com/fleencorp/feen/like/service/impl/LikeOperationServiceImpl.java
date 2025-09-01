package com.fleencorp.feen.like.service.impl;

import com.fleencorp.feen.like.constant.LikeParentType;
import com.fleencorp.feen.like.constant.LikeType;
import com.fleencorp.feen.like.mapper.LikeMapper;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.like.model.projection.UserLikeInfoSelect;
import com.fleencorp.feen.like.repository.LikeRepository;
import com.fleencorp.feen.like.service.LikeOperationService;
import com.fleencorp.feen.model.contract.HasId;
import com.fleencorp.feen.model.contract.Likeable;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LikeOperationServiceImpl implements LikeOperationService {

  private final LikeRepository likeRepository;
  private final LikeMapper likeMapper;

  public LikeOperationServiceImpl(
      final LikeRepository likeRepository,
      final LikeMapper likeMapper) {
    this.likeRepository = likeRepository;
    this.likeMapper = likeMapper;
  }

  @Override
  public <T extends Likeable> void populateChatSpaceLikesFor(final Collection<T> responses, final IsAMember member) {
    populateLikeFor(responses, member, LikeParentType.CHAT_SPACE);
  }

  @Override
  public <T extends Likeable> void populateStreamLikesFor(final Collection<T> responses, final IsAMember member) {
    populateLikeFor(responses, member, LikeParentType.STREAM);
  }

  @Override
  public <T extends Likeable> void populateLikesForReviews(final Collection<T> reviewResponses, final IsAMember member) {
    populateLikeFor(reviewResponses, member, LikeParentType.REVIEW);
  }

  /**
   * Populates user-specific like information for a collection of {@link Likeable} responses.
   *
   * <p>This method extracts the IDs from the given {@code responses}, retrieves the user's
   * like information for the specified {@link LikeParentType}, and assigns the corresponding
   * {@link UserLikeInfo} to each response using {@link #setUserInfo(Collection, Map)}.
   * If the collection of entity IDs is empty, no action is taken.</p>
   *
   * @param <T> the type of response, which must implement {@link Likeable}
   * @param responses the collection of {@link Likeable} responses for which like information
   *                  should be populated; may be empty but not {@code null}
   * @param member the {@link IsAMember} whose like information will be applied to the responses
   * @param likeParentType the parent entity type (e.g., chat space, review, or stream)
   */
  protected <T extends Likeable> void populateLikeFor(final Collection<T> responses, final IsAMember member, final LikeParentType likeParentType) {
    final List<Long> entitiesIds = HasId.getIds(responses);

    if (!entitiesIds.isEmpty()) {
      final Map<Long, UserLikeInfoSelect> likeInfoMap = findLikesByParentIdsAndMember(entitiesIds, member, likeParentType);
      setUserInfo(responses, likeInfoMap);
    }
  }

  /**
   * Sets the user-specific like information on a collection of {@link Likeable} responses.
   *
   * <p>This method iterates through the provided {@code responses}, and for each non-null
   * element, checks whether a corresponding entry exists in the {@code likeInfoMap}.
   * If a mapping exists, it converts the {@link UserLikeInfoSelect} into a
   * {@link UserLikeInfo} and assigns it to the response. Otherwise, it assigns a
   * default {@link UserLikeInfo} indicating that the item is not liked.</p>
   *
   * @param responses the collection of {@link Likeable} response objects on which user
   *                  like information should be set; may be empty but not {@code null}
   * @param likeInfoMap a map of parent IDs to {@link UserLikeInfoSelect} objects, used
   *                    to determine whether each response has been liked
   */
  protected void setUserInfo(final Collection<? extends Likeable> responses, final Map<Long, UserLikeInfoSelect> likeInfoMap) {
    responses.stream()
      .filter(Objects::nonNull)
      .forEach(response -> {
        if (likeInfoMap.containsKey(response.getNumberId())) {
          final UserLikeInfoSelect info = likeInfoMap.get(response.getNumberId());
          final UserLikeInfo userLikeInfo = likeMapper.toLikeInfo(info != null && info.isLiked());
          response.setUserLikeInfo(userLikeInfo);
        } else {
          final UserLikeInfo userLikeInfo = likeMapper.toLikeInfo(false);
          response.setUserLikeInfo(userLikeInfo);
        }
    });
  }

  /**
   * Retrieves like information for the specified parent entities and member.
   *
   * <p>This method queries the database for {@link LikeType#LIKE} and {@link LikeType#UNLIKE}
   * entries associated with the given {@link Member} and {@link LikeParentType}. The results
   * are returned as a map keyed by the relevant parent ID (e.g., chat space, review, or stream),
   * with each value containing the corresponding {@link UserLikeInfoSelect} object.</p>
   *
   * <p>If no parent IDs are provided, this method returns an empty map.</p>
   *
   * @param parentIds the list of parent entity IDs for which like information should be retrieved;
   *                  may be {@code null} or empty
   * @param member the {@link IsAMember} for whom like information is being retrieved
   * @param likeParentType the type of parent entity (e.g., chat space, review, or stream)
   * @return a map where the key is the parent ID and the value is the corresponding
   *         {@link UserLikeInfoSelect}; an empty map if no parent IDs are provided
   */
  protected Map<Long, UserLikeInfoSelect> findLikesByParentIdsAndMember(final List<Long> parentIds, final IsAMember member, final LikeParentType likeParentType) {
    // Return empty map if no parent IDs are provided
    if (parentIds == null || parentIds.isEmpty()) {
      return Collections.emptyMap();
    }

    // Query the database for LIKE and UNLIKE entries for the given member and parent type
    final List<UserLikeInfoSelect> likes = likeRepository.findLikesByParentIdsAndMember(
      parentIds,
      member.getMemberId(),
      likeParentType,
      List.of(LikeType.LIKE, LikeType.UNLIKE)
    );

    // Map the results by the appropriate parent ID depending on the LikeParentType
    return likes.stream()
      .collect(Collectors.toMap(
      info -> switch (likeParentType) {
        case CHAT_SPACE -> info.getChatSpaceId();
        case REVIEW -> info.getReviewId();
        case STREAM -> info.getStreamId();
      },
      Function.identity()
    ));
  }

}
