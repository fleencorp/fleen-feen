package com.fleencorp.feen.service.impl.like;

import com.fleencorp.feen.constant.like.LikeParentType;
import com.fleencorp.feen.constant.like.LikeType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.model.contract.Likeable;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.like.Like;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.model.dto.like.LikeDto;
import com.fleencorp.feen.model.holder.LikeOtherDetailsHolder;
import com.fleencorp.feen.model.info.like.UserLikeInfo;
import com.fleencorp.feen.model.projection.like.UserLikeInfoSelect;
import com.fleencorp.feen.model.response.like.LikeResponse;
import com.fleencorp.feen.model.response.review.ReviewResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.repository.like.LikeRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.like.LikeService;
import com.fleencorp.feen.service.review.ReviewService;
import com.fleencorp.feen.service.stream.StreamOperationsService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;

@Service
public class LikeServiceImpl implements LikeService {

  private final ChatSpaceService chatSpaceService;
  private final ReviewService reviewService;
  private final StreamOperationsService streamOperationsService;
  private final LikeRepository likeRepository;
  private final UnifiedMapper unifiedMapper;

  /**
   * Constructs a new {@code LikeServiceImpl}, responsible for handling like-related actions
   * on chat spaces, reviews, and streams.
   *
   * @param chatSpaceService service for managing chat spaces where likes can be applied
   * @param reviewService service for handling reviews which can be liked
   * @param streamOperationsService (lazy) service for managing streams and their interactions
   * @param likeRepository repository for persisting and querying like data
   * @param unifiedMapper general-purpose mapper for converting between entities and DTOs
   */
  public LikeServiceImpl(
      final ChatSpaceService chatSpaceService,
      final ReviewService reviewService,
      @Lazy final StreamOperationsService streamOperationsService,
      final LikeRepository likeRepository,
      final UnifiedMapper unifiedMapper) {
    this.chatSpaceService = chatSpaceService;
    this.reviewService = reviewService;
    this.streamOperationsService = streamOperationsService;
    this.likeRepository = likeRepository;
    this.unifiedMapper = unifiedMapper;
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
  public LikeResponse like(final LikeDto likeDto, final RegisteredUser user)
      throws StreamNotFoundException, ChatSpaceNotFoundException, FailedOperationException {
    final Long parentId = likeDto.getParentId();
    final LikeParentType parentType = likeDto.getLikeParentType();
    final Member member = user.toMember();

    final LikeOtherDetailsHolder detailsHolder = retrieveLikeOtherDetailsHolder(parentType, parentId);
    final Like like = createOrUpdateLike(likeDto, parentId, parentType, member, detailsHolder);

    likeRepository.save(like);

    final boolean liked = LikeType.liked(likeDto.getLikeType());
    final Long total = updateLikeCount(parentId, parentType, likeDto.getLikeType());
    final UserLikeInfo userLikeInfo = unifiedMapper.toLikeInfo(liked);

    return LikeResponse.of(like.getParentId(), like.getParentTitle(), total, userLikeInfo);
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
   * @param detailsHolder     the detail holder that can contain a stream and chat space
   * @return the created or updated {@link Like} entity
   */
  protected Like createOrUpdateLike(final LikeDto likeDto, final Long parentId, final LikeParentType parentType, final Member member, final LikeOtherDetailsHolder detailsHolder) {
    final FleenStream stream = detailsHolder.stream();
    final ChatSpace chatSpace = detailsHolder.chatSpace();

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

    final FleenStream stream = LikeParentType.isStream(likeParentType) ? streamOperationsService.findStream(parentId) : null;
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
      return LikeType.isLike(likeType) ? streamOperationsService.incrementLikeCount(parentId) : streamOperationsService.decrementLikeCount(parentId);
    }

    if (LikeParentType.isChatSpace(likeParentType)) {
      return LikeType.isLike(likeType) ? chatSpaceService.incrementLikeCount(parentId) : chatSpaceService.decrementLikeCount(parentId);
    }

    if (LikeParentType.isReview(likeParentType)) {
      return LikeType.isLike(likeType) ? reviewService.incrementLikeCount(parentId) : reviewService.decrementLikeCount(parentId);
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
   * Populates like information on chat space responses for chat spaces where the given member is not part of the membership.
   *
   * <p>This delegates to {@code populateLikesForNonMembership}, specifying {@code LikeParentType.CHAT_SPACE}
   * to indicate that the like context applies to chat space entities.</p>
   *
   * @param responses the collection of likeable chat space responses to populate likes for
   * @param membershipMap a map containing chat space IDs that the member is already part of
   * @param member the member for whom to populate like information
   * @param <T> a type that extends {@link Likeable}, representing responses that can be liked
   */
  @Override
  public <T extends Likeable> void populateChatSpaceLikesForNonMembership(final Collection<T> responses, final Map<Long, ?> membershipMap, final Member member) {
    populateLikesForNonMembership(responses, membershipMap, member, LikeParentType.CHAT_SPACE);
  }


  /**
   * Populates like information for chat space-related responses that the member is associated with through membership.
   *
   * <p>This delegates to {@code populateLikesForMembership}, targeting entities of type {@code CHAT_SPACE}.
   * It filters the provided {@code responses} to those present in the {@code membershipMap}, retrieves the like status
   * for the given {@code member}, and updates the responses accordingly.</p>
   *
   * @param responses      a collection of chat space-related responses implementing {@link Likeable}
   * @param membershipMap  a map of chat space IDs the member is a participant of
   * @param member         the member whose like data should be applied
   * @param <T>            a type that extends {@link Likeable}
   */
  @Override
  public <T extends Likeable> void populateChatSpaceLikesForMembership(final Collection<T> responses, final Map<Long, ?> membershipMap, final Member member) {
    populateLikesForMembership(responses, membershipMap, member, LikeParentType.CHAT_SPACE);
  }

  /**
   * Populates like information on stream responses for streams where the given member is not an attendee.
   *
   * <p>This method delegates the operation to {@code populateLikesForNonMembership} using
   * {@code LikeParentType.STREAM} as the type, indicating the operation is specific to stream entities.</p>
   *
   * @param responses the collection of stream-like responses to populate likes for
   * @param membershipMap a map of stream IDs representing existing attendance or membership
   * @param member the member for whom to populate like information
   * @param <T> a type that extends {@link Likeable}, representing responses that can be liked
   */
  @Override
  public <T extends Likeable> void populateStreamLikesForNonAttendance(final Collection<T> responses, final Map<Long, ?> membershipMap, final Member member) {
    populateLikesForNonMembership(responses, membershipMap, member, LikeParentType.STREAM);
  }

  /**
   * Populates like information for stream-related responses that the member is attending.
   *
   * <p>This delegates to {@code populateLikesForMembership}, targeting only entities of type {@code STREAM}.
   * It filters the provided {@code responses} to those present in the {@code membershipMap}, retrieves the like status
   * for the given {@code member}, and updates the responses accordingly.</p>
   *
   * @param responses      a collection of stream-related responses implementing {@link Likeable}
   * @param membershipMap  a map of stream IDs the member is attending
   * @param member         the member whose like data should be applied
   * @param <T>            a type that extends {@link Likeable}
   */
  @Override
  public <T extends Likeable> void populateStreamLikesForAttendance(final Collection<T> responses, final Map<Long, ?> membershipMap, final Member member) {
    populateLikesForMembership(responses, membershipMap, member, LikeParentType.STREAM);
  }

  /**
   * Populates like information for a list of reviews based on the user's like actions.
   *
   * <p>This method retrieves the user's like status for each review (LIKE or UNLIKE)
   * and sets the corresponding like information on each review response.</p>
   *
   * @param reviewResponses the list of review responses to populate
   * @param member the member whose like actions are being checked
   */
  @Override
  public void populateLikesForReviews(final Collection<ReviewResponse> reviewResponses, final Member member) {
    // Extract review IDs
    final List<Long> reviewIds = reviewResponses.stream()
      .map(Likeable::getNumberId)
      .toList();

    // Only proceed if there are review IDs
    if (!reviewIds.isEmpty()) {
      // Fetch like information for the specified review IDs and member
      final Map<Long, UserLikeInfoSelect> likeInfoMap = findLikesByParentIdsAndMember(reviewIds, member, LikeParentType.REVIEW);
      // Populate each review response with the corresponding like information
      setUserInfo(reviewResponses, likeInfoMap);
    }
  }

  /**
   * Populates the like information for responses where the user is not a member.
   *
   * <p>This method filters out the given responses that are not associated with a membership,
   * retrieves their like status (LIKE or UNLIKE), and sets the corresponding like information
   * in each response based on the user's actions.</p>
   *
   * @param responses the collection of responses to populate
   * @param membershipMap a map indicating membership status keyed by entity ID
   * @param member the member whose like information is being retrieved
   * @param parentType the parent type (e.g., STREAM, CHAT_SPACE) associated with the like
   * @param <T> a type that extends Likeable
   */
  protected <T extends Likeable> void populateLikesForNonMembership(final Collection<T> responses, final Map<Long, ?> membershipMap, final Member member, final LikeParentType parentType) {
    // Get IDs of responses that are not present in the membership map
    final List<Long> nonMembershipForEntitiesIds = responses.stream()
      .map(Likeable::getNumberId)
      .filter(id -> !membershipMap.containsKey(id))
      .toList();

    // Only proceed if there are non-member responses
    if (!nonMembershipForEntitiesIds.isEmpty()) {
      final Map<Long, UserLikeInfoSelect> likeInfoMap = findLikesByParentIdsAndMember(nonMembershipForEntitiesIds, member, parentType);
      // Populate each response with the corresponding like information
      setUserInfo(responses, likeInfoMap);
    }
  }

  /**
   * Populates like information for responses that are associated with the given member's memberships.
   *
   * <p>This method filters the provided {@code responses} to only those whose IDs exist in the {@code membershipMap}.
   * It then retrieves the like status of these entities by the given {@code member}, using the specified {@code parentType},
   * and updates the responses with the corresponding like information.</p>
   *
   * @param responses      a collection of responses implementing {@link Likeable}, each representing an entity that may be liked
   * @param membershipMap  a map whose keys are the IDs of entities the member is associated with (i.e., has membership in)
   * @param member         the member whose like data should be retrieved
   * @param parentType     the type of the parent entity (used to filter like records)
   * @param <T>            a type that extends {@link Likeable}
   */
  protected <T extends Likeable> void populateLikesForMembership(final Collection<T> responses, final Map<Long, ?> membershipMap, final Member member, final LikeParentType parentType) {
    // Get IDs of responses that are present in the membership map
    final List<Long> nonMembershipForEntitiesIds = responses.stream()
      .map(Likeable::getNumberId)
      .filter(membershipMap::containsKey)
      .toList();

    // Only proceed if there are non-member responses
    if (!nonMembershipForEntitiesIds.isEmpty()) {
      final Map<Long, UserLikeInfoSelect> likeInfoMap = findLikesByParentIdsAndMember(nonMembershipForEntitiesIds, member, parentType);
      // Populate each response with the corresponding like information
      setUserInfo(responses, likeInfoMap);
    }
  }

  /**
   * Sets the user like information on each response using the provided like info map.
   *
   * <p>This method checks if a response has a corresponding like record and maps it to
   * a {@link UserLikeInfo} object, which is then set on the response.</p>
   *
   * @param responses the list of responses implementing {@link Likeable}
   * @param likeInfoMap a map of parent IDs to {@link UserLikeInfoSelect} objects
   */
  protected void setUserInfo(final Collection<? extends Likeable> responses, final Map<Long, UserLikeInfoSelect> likeInfoMap) {
    responses.stream()
      .filter(Objects::nonNull)
      .filter(response -> likeInfoMap.containsKey(response.getNumberId()))
      .forEach(response -> {
        // Retrieve the like info for the current response
        final UserLikeInfoSelect info = likeInfoMap.get(response.getNumberId());
        // Map the like info to a UserLikeInfo object (true if liked)
        final UserLikeInfo userLikeInfo = unifiedMapper.toLikeInfo(info != null && info.isLiked());
        // Set the like info on the response
        response.setUserLikeInfo(userLikeInfo);
    });
  }

  /**
   * Retrieves a map of like information for the given member across multiple parent entities
   * (e.g., streams, chat spaces, reviews), based on the provided parent IDs and parent type.
   *
   * <p>The method queries the database for {@link UserLikeInfoSelect} entries where the parent type
   * and member match, and the like type is either LIKE or UNLIKE. The results are mapped to a map
   * using the appropriate parent ID based on the type.</p>
   *
   * @param parentIds the list of parent IDs (e.g., streamIds, chatSpaceIds, reviewIds)
   * @param member the member whose likes are to be retrieved
   * @param likeParentType the type of the parent entity ({@link LikeParentType})
   * @return a map of parent ID to {@link UserLikeInfoSelect} containing like information
   */
  protected Map<Long, UserLikeInfoSelect> findLikesByParentIdsAndMember(final List<Long> parentIds, final Member member, final LikeParentType likeParentType) {
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
        case STREAM -> info.getStreamId();
        case CHAT_SPACE -> info.getChatSpaceId();
        case REVIEW -> info.getReviewId();
      },
      Function.identity()
    ));
  }

}
