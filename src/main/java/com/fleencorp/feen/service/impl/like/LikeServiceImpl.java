package com.fleencorp.feen.service.impl.like;

import com.fleencorp.feen.constant.like.LikeParentType;
import com.fleencorp.feen.constant.like.LikeType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.model.contract.SetLikeInfo;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.like.Like;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.like.LikeDto;
import com.fleencorp.feen.model.holder.LikeOtherDetailsHolder;
import com.fleencorp.feen.model.info.like.LikeInfo;
import com.fleencorp.feen.model.response.like.LikeResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.like.LikeRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.like.LikeService;
import com.fleencorp.feen.service.stream.common.StreamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static java.util.Objects.nonNull;

@Service
public class LikeServiceImpl implements LikeService {

  private final ChatSpaceService chatSpaceService;
  private final StreamService streamService;
  private final LikeRepository likeRepository;
  private final ToInfoMapper toInfoMapper;

  public LikeServiceImpl(
      final ChatSpaceService chatSpaceService,
      final StreamService streamService,
      final LikeRepository likeRepository,
      final ToInfoMapper toInfoMapper) {
    this.chatSpaceService = chatSpaceService;
    this.streamService = streamService;
    this.likeRepository = likeRepository;
    this.toInfoMapper = toInfoMapper;
  }

  /**
   /**
   * Handles the like action for a given parent entity (either a stream or a chat space) by a user.
   *
   * <p>This method retrieves the relevant parent entity using the provided like data and user. It then
   * either creates a new like entry or updates an existing one, depending on whether the user has already
   * liked the entity. The like is saved, the like count on the parent entity is updated accordingly,
   * and a response is constructed to reflect the updated like information.</p>
   *
   * <p>The like count is updated based on whether the user is liking or unliking the parent entity.
   * The response includes the updated total like count, the parent entity's ID, title, and a LikeInfo
   * object that provides further details about the like.</p>
   *
   * @param likeDto the DTO representing the like action, including the parent entity ID and like type
   * @param user the authenticated user performing the like action
   * @return a LikeResponse containing the updated like count, parent details, and like info
   */
  @Override
  @Transactional
  public LikeResponse like(final LikeDto likeDto, final FleenUser user)
      throws StreamNotFoundException, ChatSpaceNotFoundException, FailedOperationException {
    final Long parentId = likeDto.getParentId();
    final LikeParentType parentType = likeDto.getLikeParentType();
    final Member member = user.toMember();

    final LikeOtherDetailsHolder detailsHolder = retrieveLikeOtherDetailsHolder(parentType, parentId);
    final FleenStream stream = detailsHolder.stream();
    final ChatSpace chatSpace = detailsHolder.chatSpace();

    final Like like = createOrUpdateLike(likeDto, parentId, parentType, member, stream, chatSpace);

    likeRepository.save(like);

    final boolean liked = LikeType.liked(likeDto.getLikeType());
    final Long total = updateLikeCount(parentId, parentType, likeDto.getLikeType());
    final LikeInfo likeInfo = toInfoMapper.toLikeInfo(liked);

    return LikeResponse.of(total, like.getParentId(), like.getParentTitle(), likeInfo);
  }

  /**
   * Creates a new {@link Like} entity or updates an existing one for the given parent entity and member.
   *
   * <p>If a like already exists for the specified member and parent entity, it updates the like type.
   * Otherwise, it creates a new like using the provided {@link FleenStream}, {@link ChatSpace}, and member information.</p>
   *
   * @param likeDto    the DTO containing like information, including the desired like type
   * @param parentId   the ID of the parent entity being liked
   * @param parentType the type of the parent entity (e.g., STREAM or CHAT_SPACE)
   * @param member     the member performing the like action
   * @param stream     the stream entity if the parent type is STREAM; otherwise {@code null}
   * @param chatSpace  the chat space entity if the parent type is CHAT_SPACE; otherwise {@code null}
   * @return the created or updated {@link Like} entity
   */
  protected Like createOrUpdateLike(final LikeDto likeDto, final Long parentId, final LikeParentType parentType, final Member member, final FleenStream stream, final ChatSpace chatSpace) {
    return findLikeByParent(parentId, parentType, member)
      .map(existingLike -> {
        existingLike.setLikeType(likeDto.getLikeType());
        return existingLike;
      })
      .orElseGet(() -> likeDto.by(stream, chatSpace, member));
  }

  /**
   * Retrieves additional details related to a like action based on the parent type and parent ID.
   *
   * <p>If the {@code likeParentType} is {@code STREAM}, the corresponding stream is fetched.
   * If it's {@code CHAT_SPACE}, the corresponding chat space is fetched.
   * Only one of {@code stream} or {@code chatSpace} will be non-null depending on the parent type.
   * The result is wrapped in a {@link LikeOtherDetailsHolder} object.</p>
   *
   * @param likeParentType the type of the entity being liked (STREAM or CHAT_SPACE)
   * @param parentId       the ID of the parent entity
   * @return a {@link LikeOtherDetailsHolder} containing either the stream or chat space
   * @throws FailedOperationException if {@code likeParentType} is {@code null} or if the entity lookup fails
   */
  protected LikeOtherDetailsHolder retrieveLikeOtherDetailsHolder(final LikeParentType likeParentType, final Long parentId) throws FailedOperationException {
    checkIsNull(likeParentType, FailedOperationException::new);

    final FleenStream stream = LikeParentType.isStream(likeParentType) ? streamService.findStream(parentId) : null;
    final ChatSpace chatSpace = LikeParentType.isChatSpace(likeParentType) ? chatSpaceService.findChatSpace(parentId) : null;

    return LikeOtherDetailsHolder.of(stream, chatSpace);
  }

  /**
   * Updates the like count for a given parent entity (either a stream or chat space) based on the like type.
   *
   * <p>Depending on the {@code likeParentType}, this method delegates to either {@code streamService} or {@code chatSpaceService}
   * to increment or decrement the like count based on whether the {@code likeType} is a like or an unlike.</p>
   *
   * @param parentId       the ID of the parent entity (stream or chat space)
   * @param likeParentType the type of the parent entity (STREAM or CHAT_SPACE)
   * @param likeType       the type of the like action (LIKE or UNLIKE)
   * @return the updated like count after the operation
   * @throws FailedOperationException if {@code parentId} is {@code null} or the {@code likeParentType} is not supported
   */
  protected Long updateLikeCount(final Long parentId, final LikeParentType likeParentType, final LikeType likeType) {
    checkIsNull(parentId, FailedOperationException::new);

    if (LikeParentType.isStream(likeParentType)) {
      return LikeType.isLike(likeType)
        ? streamService.incrementLikeCount(parentId)
        : streamService.decrementLikeCount(parentId);
    }

    if (LikeParentType.isChatSpace(likeParentType)) {
      return LikeType.isLike(likeType)
        ? chatSpaceService.incrementLikeCount(parentId)
        : chatSpaceService.decrementLikeCount(parentId);
    }

    throw FailedOperationException.of();
  }

  /**
   * Finds a {@link Like} entity associated with a given parent (either a stream or chat space) and member.
   *
   * <p>This method checks the {@code likeParentType} to determine the type of parent entity,
   * then queries the corresponding like repository method based on the parent type.</p>
   *
   * @param parentId       the ID of the stream or chat space to which the like is associated
   * @param likeParentType the type of the parent entity (e.g., STREAM or CHAT_SPACE)
   * @param member         the member who may have liked the entity
   * @return an {@link Optional} containing the found {@code Like} if it exists, or empty if not
   * @throws FailedOperationException if {@code parentId} is {@code null}
   */
  protected Optional<Like> findLikeByParent(final Long parentId, final LikeParentType likeParentType, final Member member) {
    checkIsNull(parentId, FailedOperationException::new);

    return switch (likeParentType) {
      case STREAM -> likeRepository.findByMemberAndStream(member.getMemberId(), parentId);
      case CHAT_SPACE -> likeRepository.findByMemberAndChatSpace(member.getMemberId(), parentId);
      case REVIEW -> likeRepository.findByMemberAndReview(member.getMemberId(), parentId);
    };
  }

  /**
   * Finds the like information for a chat space by a specific member.
   *
   * <p>This method uses the {@link LikeParentType#CHAT_SPACE} type to check if a member has liked
   * the specified chat space, identified by the given {@code parentId}. It calls the
   * {@link #findLikeInfo(Long, LikeParentType, LikeType, Member)} method, passing in the like
   * type as {@link LikeType#LIKE} to determine if the member has liked the chat space.</p>
   *
   * @param parentId the ID of the chat space
   * @param member the member performing the like action
   * @return a {@link LikeInfo} object indicating whether the member has liked the chat space
   */
  public LikeInfo findChatSpaceLikeByMember(final Long parentId, final Member member) {
    return findLikeInfo(parentId, LikeParentType.CHAT_SPACE, LikeType.LIKE, member);
  }

  /**
   * Finds the like information for a stream by a specific member.
   *
   * <p>This method uses the {@link LikeParentType#STREAM} type to check if a member has liked
   * the specified stream, identified by the given {@code parentId}. It calls the
   * {@link #findLikeInfo(Long, LikeParentType, LikeType, Member)} method, passing in the like
   * type as {@link LikeType#LIKE} to determine if the member has liked the stream.</p>
   *
   * @param parentId the ID of the stream
   * @param member the member performing the like action
   * @return a {@link LikeInfo} object indicating whether the member has liked the stream
   */
  public LikeInfo findStreamLikeByMember(final Long parentId, final Member member) {
    return findLikeInfo(parentId, LikeParentType.STREAM, LikeType.LIKE, member);
  }

  /**
   * Finds the like information for the given parent entity, like type, and member.
   *
   * <p>This method checks whether a like exists for a specified parent entity (such as a stream or chat space)
   * by the provided member. It uses the provided {@link LikeParentType} to determine the type of the parent entity
   * and {@link LikeType} to specify the type of like (like or dislike). The result is mapped to a {@link LikeInfo}
   * object which indicates whether the entity has been liked by the member.</p>
   *
   * @param parentId the ID of the parent entity (stream or chat space)
   * @param likeParentType the type of the parent entity (stream or chat space)
   * @param likeType the type of like (e.g., like or dislike)
   * @param member the member performing the like action
   * @return a {@link LikeInfo} object indicating whether the member has liked the parent entity
   * @throws FailedOperationException if the parentId is null
   */
  protected LikeInfo findLikeInfo(final Long parentId, final LikeParentType likeParentType, final LikeType likeType, final Member member) {
    checkIsNull(parentId, FailedOperationException::new);

    final boolean liked = likeRepository.existsLike(parentId, likeParentType, likeType, member.getMemberId());
    return toInfoMapper.toLikeInfo(liked);
  }

  /**
   * Sets the like information for a set like info object.
   *
   * <p>This method checks if the provided {@code setLikeInfo} and {@code user} are non-null.
   * If both are present, it retrieves the like information for the associated chat space
   * by calling {@link LikeService#findChatSpaceLikeByMember(Long, Member)} with the chat space's ID
   * and the member derived from the {@code user}. The retrieved like information is then set
   * on the {@code setLikeInfo} object.</p>
   *
   * @param setLikeInfo the object containing the like information to be set
   * @param user the user for whom the like information will be retrieved
   */
  @Override
  public void setUserLikeInfo(final SetLikeInfo setLikeInfo, final FleenUser user) {
    if (nonNull(setLikeInfo) && nonNull(user)) {
      final LikeInfo likeInfo = findChatSpaceLikeByMember(setLikeInfo.getNumberId(), user.toMember());
      setLikeInfo.setUserLikeInfo(likeInfo);
    }
  }

  @Override
  public void setUserLikeInfo(final List<? extends SetLikeInfo> setLikeInfos, final FleenUser user) {
    if (nonNull(setLikeInfos) && nonNull(user)) {
      setLikeInfos.forEach(setLikeInfo -> setUserLikeInfo(setLikeInfo, user));
    }
  }

}
