package com.fleencorp.feen.like.service.impl;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.like.constant.LikeParentType;
import com.fleencorp.feen.like.constant.LikeType;
import com.fleencorp.feen.like.mapper.LikeMapper;
import com.fleencorp.feen.like.model.domain.Like;
import com.fleencorp.feen.like.model.dto.LikeDto;
import com.fleencorp.feen.like.model.holder.LikeParentDetailHolder;
import com.fleencorp.feen.like.model.response.LikeCreateResponse;
import com.fleencorp.feen.like.model.response.LikeResponse;
import com.fleencorp.feen.like.repository.LikeRepository;
import com.fleencorp.feen.like.service.LikeExternalQueryService;
import com.fleencorp.feen.like.service.LikeService;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.review.exception.core.ReviewNotFoundException;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;

@Service
public class LikeServiceImpl implements LikeService {

  private final LikeExternalQueryService likeExternalQueryService;
  private final LikeRepository likeRepository;
  private final LikeMapper likeMapper;
  private final Localizer localizer;

  public LikeServiceImpl(
    final LikeExternalQueryService likeExternalQueryService,
      final LikeRepository likeRepository,
      final LikeMapper likeMapper,
      final Localizer localizer) {
    this.likeExternalQueryService = likeExternalQueryService;
    this.likeRepository = likeRepository;
    this.likeMapper = likeMapper;
    this.localizer = localizer;
  }

  /**
   * Handles a like action for a given parent entity by the specified user.
   *
   * <p>If a like already exists for the parent entity and user, it is updated.
   * Otherwise, a new like is created. The parent entity’s like count is also
   * updated, and the result is returned in a localized response.</p>
   *
   * @param likeDto the data transfer object containing the like details
   * @param user the registered user performing the like action
   * @return a localized response containing the created or updated like and the updated like count
   * @throws StreamNotFoundException if the parent type is a stream and it is not found
   * @throws ChatSpaceNotFoundException if the parent type is a chat space and it is not found
   * @throws FailedOperationException if the operation cannot be completed
   */
  @Override
  @Transactional
  public LikeCreateResponse like(final LikeDto likeDto, final RegisteredUser user)
      throws StreamNotFoundException, ChatSpaceNotFoundException, FailedOperationException {
    final Long parentId = likeDto.getParentId();
    final LikeType likeType = likeDto.getLikeType();
    final LikeParentType parentType = likeDto.getLikeParentType();
    final Member member = user.toMember();

    final LikeParentDetailHolder detailsHolder = retrieveLikeOtherDetailsHolder(parentType, parentId);
    final Like like = createOrUpdateLike(likeDto, parentId, parentType, member, detailsHolder);

    likeRepository.save(like);
    final Integer parentTotalLikes = updateLikeCount(parentId, parentType, likeType);

    final LikeResponse likeResponse = likeMapper.toLikeResponse(like);
    final LikeCreateResponse likeCreateResponse = LikeCreateResponse.of(likeResponse, parentTotalLikes);
    return localizer.of(likeCreateResponse);
  }

  /**
   * Creates or updates a {@link Like} for the specified parent entity.
   *
   * <p>If a like already exists for the given parent entity and member, its type is updated
   * based on the provided {@link LikeDto}. Otherwise, a new like is created using the details
   * from the given {@link LikeParentDetailHolder}.</p>
   *
   * @param likeDto the data transfer object containing the like type
   * @param parentId the identifier of the parent entity
   * @param parentType the type of the parent entity (e.g., stream, chat space, review)
   * @param member the member performing the like action
   * @param detailsHolder holder containing the parent entity details (stream, chat space, review)
   * @return the newly created or updated {@link Like}
   */
  protected Like createOrUpdateLike(final LikeDto likeDto, final Long parentId, final LikeParentType parentType, final Member member, final LikeParentDetailHolder detailsHolder) {
    final ChatSpace chatSpace = detailsHolder.chatSpace();
    final Poll poll = detailsHolder.poll();
    final Review review = detailsHolder.review();
    final FleenStream stream = detailsHolder.stream();

    return findLikeByParent(parentId, parentType, member)
      .map(existingLike -> existingLike.updateType(likeDto.getLikeType()))
      .orElseGet(() -> likeDto.by(chatSpace, poll, review, stream, member));
  }

  /**
   * Retrieves the detailed holder of a like's parent entity, which may be a stream,
   * chat space, or review.
   *
   * <p>The method determines the parent entity type and delegates to the appropriate
   * service to fetch the corresponding entity. If the type is {@code null}, a
   * {@link FailedOperationException} is thrown. If the parent entity is not found,
   * the respective not-found exception is raised.</p>
   *
   * @param likeParentType the type of the parent entity (stream, chat space, or review)
   * @param parentId the identifier of the parent entity
   * @return a {@link LikeParentDetailHolder} containing the parent entity details
   * @throws FailedOperationException if {@code likeParentType} is {@code null}
   * @throws StreamNotFoundException if the parent type is a stream and no stream is found
   * @throws ChatSpaceNotFoundException if the parent type is a chat space and no chat space is found
   * @throws ReviewNotFoundException if the parent type is a review and no review is found
   */
  protected LikeParentDetailHolder retrieveLikeOtherDetailsHolder(final LikeParentType likeParentType, final Long parentId)
      throws ChatSpaceNotFoundException, ReviewNotFoundException, StreamNotFoundException,
      FailedOperationException {
    checkIsNull(likeParentType, FailedOperationException::new);

    final ChatSpace chatSpace = LikeParentType.isChatSpace(likeParentType) ? likeExternalQueryService.findChatSpaceById(parentId) : null;
    final Poll poll = LikeParentType.isPoll(likeParentType) ? likeExternalQueryService.findPollById(parentId) : null;
    final Review review = LikeParentType.isReview(likeParentType) ? likeExternalQueryService.findReviewById(parentId) : null;
    final FleenStream stream = LikeParentType.isStream(likeParentType) ? likeExternalQueryService.findStreamById(parentId) : null;

    return LikeParentDetailHolder.of(chatSpace, poll, review, stream, likeParentType);
  }

  /**
   * Updates the like count of a parent entity based on the provided like type.
   *
   * <p>The parent entity is identified by its identifier and type, which may represent
   * a stream, chat space, or review. If either the identifier or type is {@code null},
   * a {@link FailedOperationException} is thrown. The like type is used to determine
   * whether the count should be incremented or decremented.</p>
   *
   * @param parentId the identifier of the parent entity (stream, chat space, or review)
   * @param parentType the type of the parent entity
   * @param likeType the type of like determining whether the entity is liked or unliked
   * @return the updated like count for the specified parent entity
   * @throws FailedOperationException if {@code parentId} or {@code parentType} is {@code null}
   */
  protected Integer updateLikeCount(final Long parentId, final LikeParentType parentType, final LikeType likeType) {
    checkIsNullAny(List.of(parentId, parentType), FailedOperationException::new);
    final boolean isLiked = LikeType.isLiked(likeType);

    return switch (parentType) {
      case CHAT_SPACE -> likeExternalQueryService.updateChatSpaceLikeCount(parentId, isLiked);
      case POLL -> likeExternalQueryService.updatePollLikeCount(parentId, isLiked);
      case REVIEW -> likeExternalQueryService.updateReviewLikeCount(parentId, isLiked);
      case STREAM -> likeExternalQueryService.updateStreamLikeCount(parentId, isLiked);
    };
  }

  /**
   * Finds a {@link Like} made by the given member on a specified parent entity.
   *
   * <p>The parent entity is identified by its identifier and type, which may represent
   * a stream, chat space, or review. If the parent identifier is {@code null}, a
   * {@link FailedOperationException} is thrown. Otherwise, the repository is queried
   * for a matching like.</p>
   *
   * @param parentId the identifier of the parent entity (stream, chat space, or review)
   * @param likeParentType the type of the parent entity
   * @param member the member whose like is being searched for
   * @return an {@link Optional} containing the {@link Like} if found, or empty if none exists
   * @throws FailedOperationException if {@code parentId} is {@code null}
   */
  protected Optional<Like> findLikeByParent(final Long parentId, final LikeParentType likeParentType, final Member member) throws FailedOperationException {
    checkIsNull(parentId, FailedOperationException::new);
    final Long memberId = member.getMemberId();

    return switch (likeParentType) {
      case CHAT_SPACE -> likeRepository.findByMemberAndChatSpace(memberId, parentId);
      case POLL -> likeRepository.findByMemberAndPoll(memberId, parentId);
      case REVIEW -> likeRepository.findByMemberAndReview(memberId, parentId);
      case STREAM -> likeRepository.findByMemberAndStream(memberId, parentId);
    };
  }
}
