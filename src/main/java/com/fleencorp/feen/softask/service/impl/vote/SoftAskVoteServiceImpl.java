package com.fleencorp.feen.softask.service.impl.vote;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteParentType;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteType;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.dto.vote.SoftAskVoteDto;
import com.fleencorp.feen.softask.model.factory.SoftAskVoteFactory;
import com.fleencorp.feen.softask.model.holder.SoftAskVoteParentDetailsHolder;
import com.fleencorp.feen.softask.model.response.vote.SoftAskVoteUpdateResponse;
import com.fleencorp.feen.softask.model.response.vote.core.SoftAskVoteResponse;
import com.fleencorp.feen.softask.repository.vote.SoftAskVoteRepository;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.softask.service.vote.SoftAskVoteService;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;

@Service
public class SoftAskVoteServiceImpl implements SoftAskVoteService {

  private final SoftAskOperationService softAskOperationService;
  private final SoftAskReplySearchService softAskReplySearchService;
  private final SoftAskSearchService softAskSearchService;
  private final SoftAskVoteRepository softAskVoteRepository;
  private final SoftAskMapper softAskMapper;

  public SoftAskVoteServiceImpl(
      final SoftAskOperationService softAskOperationService,
      final SoftAskReplySearchService softAskReplySearchService,
      final SoftAskSearchService softAskSearchService,
      final SoftAskVoteRepository softAskVoteRepository,
      final SoftAskMapper softAskMapper) {
    this.softAskOperationService = softAskOperationService;
    this.softAskReplySearchService = softAskReplySearchService;
    this.softAskSearchService = softAskSearchService;
    this.softAskVoteRepository = softAskVoteRepository;
    this.softAskMapper = softAskMapper;
  }

  /**
   * Records a vote on a {@code SoftAsk} or {@code SoftAskReply} by a registered user.
   *
   * <p>This method processes a voting action described in the provided DTO. It finds the relevant
   * parent entities based on the vote target type, creates or updates the vote accordingly,
   * persists the vote, updates the total vote count, and returns a response containing the
   * updated vote information.</p>
   *
   * <p>If the specified {@code SoftAsk} or {@code SoftAskReply} does not exist, or if the
   * vote parent type is invalid, the method throws the corresponding exceptions.</p>
   *
   * @param softAskVoteDto the DTO containing voting details such as target IDs and vote type
   * @param user           the registered user casting the vote
   * @return a {@link SoftAskVoteUpdateResponse} containing the vote ID and updated vote details
   * @throws SoftAskNotFoundException      if the target {@code SoftAsk} is not found
   * @throws SoftAskReplyNotFoundException if the target {@code SoftAskReply} is not found
   * @throws FailedOperationException      if the vote parent type is null or invalid
   */
  @Override
  public SoftAskVoteUpdateResponse vote(final SoftAskVoteDto softAskVoteDto, final RegisteredUser user)
      throws SoftAskNotFoundException, SoftAskReplyNotFoundException, FailedOperationException {
    final Long softAskId = softAskVoteDto.getSoftAskId();
    final Long softAskReplyId = softAskVoteDto.getSoftAskReplyId();
    final SoftAskVoteParentType parentType = softAskVoteDto.getVoteParentType();
    final IsAMember member = user.toMember();

    final SoftAskVoteParentDetailsHolder parentDetailsHolder = findSoftAskVoteParentDetailsHolder(softAskId, softAskReplyId, parentType);
    SoftAskVote softAskVote = createOrUpdateSoftVote(softAskVoteDto, parentType, member, parentDetailsHolder);
    softAskVote = softAskVoteRepository.save(softAskVote);

    final boolean voted = SoftAskVoteType.isVoted(softAskVoteDto.getVoteType());
    final Integer total = updateVoteCount(parentDetailsHolder.getSoftAskId(), parentDetailsHolder.getSoftAskReplyId(), parentType, softAskVoteDto.getVoteType());
    final SoftAskVoteResponse softAskVoteResponse = softAskMapper.toSoftAskVoteResponse(softAskVote, voted);
    softAskVoteResponse.setParentTotalVotes(total);

    return SoftAskVoteUpdateResponse.of(softAskVote.getVoteId(), softAskVoteResponse);
  }

  /**
   * Finds and returns the parent details holder containing the {@code SoftAsk} and/or {@code SoftAskReply}
   * entities based on the provided IDs and parent type.
   *
   * <p>This method checks the {@code softAskVoteParentType} to determine whether to load a {@code SoftAsk}
   * or a {@code SoftAskReply}. If the parent type is {@code SOFT_ASK}, it loads the {@code SoftAsk} by its ID.
   * If the parent type is {@code SOFT_ASK_REPLY}, it loads the corresponding {@code SoftAskReply}.</p>
   *
   * <p>If the {@code softAskVoteParentType} is {@code null}, a {@link FailedOperationException} is thrown.
   * If the requested {@code SoftAsk} or {@code SoftAskReply} cannot be found, the respective exceptions
   * {@link SoftAskNotFoundException} or {@link SoftAskReplyNotFoundException} are thrown.</p>
   *
   * @param softAskId            the ID of the {@code SoftAsk}
   * @param softAskReplyId       the ID of the {@code SoftAskReply}, if applicable
   * @param softAskVoteParentType the type of parent entity to find (SoftAsk or SoftAskReply)
   * @return a {@link SoftAskVoteParentDetailsHolder} containing the found entities
   * @throws SoftAskNotFoundException      if the {@code SoftAsk} could not be found
   * @throws SoftAskReplyNotFoundException if the {@code SoftAskReply} could not be found
   * @throws FailedOperationException      if {@code softAskVoteParentType} is {@code null}
   */
  private SoftAskVoteParentDetailsHolder findSoftAskVoteParentDetailsHolder(final Long softAskId, final Long softAskReplyId, final SoftAskVoteParentType softAskVoteParentType)
      throws SoftAskNotFoundException, SoftAskReplyNotFoundException, FailedOperationException {
    checkIsNull(softAskVoteParentType, FailedOperationException::new);

    final SoftAsk softAsk = SoftAskVoteParentType.isSoftAsk(softAskVoteParentType) ? softAskSearchService.findSoftAsk(softAskId) : null;
    final SoftAskReply softAskReply = SoftAskVoteParentType.isSoftAskReply(softAskVoteParentType) ? softAskReplySearchService.findSoftAskReply(softAskId, softAskReplyId) : null;

    return SoftAskVoteParentDetailsHolder.of(softAsk, softAskReply);
  }

  /**
   * Creates a new vote or updates an existing vote for the specified SoftAsk or SoftAskReply.
   *
   * <p>If a vote already exists for the given member and target (determined by the
   * {@code softAskVoteParentType} and the IDs in {@code parentDetailsHolder}),
   * the vote type is updated to the one provided in {@code softAskVoteDto}.
   * If no existing vote is found, a new {@link SoftAskVote} is created using
   * the provided member, {@link SoftAsk}, and optional {@link SoftAskReply}.</p>
   *
   * @param softAskVoteDto         the DTO containing the vote type and related data
   * @param softAskVoteParentType  the type of entity the vote is associated with
   * @param member                 the member casting or updating the vote
   * @param parentDetailsHolder    holder containing the associated SoftAsk and optional SoftAskReply
   * @return the created or updated {@link SoftAskVote} entity (not yet persisted)
   */
  private SoftAskVote createOrUpdateSoftVote(final SoftAskVoteDto softAskVoteDto, final SoftAskVoteParentType softAskVoteParentType, final IsAMember member, final SoftAskVoteParentDetailsHolder parentDetailsHolder) {
    final SoftAsk softAsk = parentDetailsHolder.softAsk();
    final SoftAskReply softAskReply = parentDetailsHolder.softAskReply();

    return findVoteByParent(parentDetailsHolder.getSoftAskId(), parentDetailsHolder.getSoftAskReplyId(), softAskVoteParentType, member)
      .map(existingVote -> {
        existingVote.setVoteType(softAskVoteDto.getVoteType());
        return existingVote;
      })
      .orElseGet(() -> SoftAskVoteFactory.toSoftAskVote(softAskVoteDto, member, softAsk, softAskReply));
  }

  /**
   * Finds an existing vote by the given member for the specified SoftAsk or SoftAskReply.
   *
   * <p>The method determines the repository query to execute based on the provided
   * {@code softAskVoteParentType}. If the vote parent type is {@code SOFT_ASK},
   * it searches for a vote associated with the given SoftAsk. If the vote parent type
   * is {@code SOFT_ASK_REPLY}, it searches for a vote associated with both the given
   * SoftAsk and SoftAskReply.</p>
   *
   * @param softAskId              the ID of the SoftAsk entity
   * @param softAskReplyId         the ID of the SoftAskReply entity, if applicable
   * @param softAskVoteParentType  the type of entity the vote is associated with
   * @param member                 the member whose vote is being searched for
   * @return an {@link Optional} containing the found {@link SoftAskVote}, or empty if none exists
   */
  private Optional<SoftAskVote> findVoteByParent(final Long softAskId, final Long softAskReplyId, final SoftAskVoteParentType softAskVoteParentType, final IsAMember member) {
    final Long memberId = member.getMemberId();

    return switch (softAskVoteParentType) {
      case SOFT_ASK -> softAskVoteRepository.findByMemberAndSoftAsk(memberId, softAskId);
      case SOFT_ASK_REPLY -> softAskVoteRepository.findByMemberAndSoftAskAndSoftAskReply(memberId, softAskId, softAskReplyId);
    };
  }

  /**
   * Updates the vote count for a SoftAsk or SoftAskReply based on the given vote type and parent type.
   *
   * <p>If the vote type represents a vote action, the corresponding vote count is incremented;
   * otherwise, the vote count is decremented. The operation is delegated to the
   * {@code softAskOperationService} using the provided parent type to determine whether
   * the vote applies to a {@code SoftAsk} or a {@code SoftAskReply}.</p>
   *
   * @param softAskId      the ID of the SoftAsk entity
   * @param softAskReplyId the ID of the SoftAskReply entity, if applicable
   * @param parentType     the type of the entity being voted on (SoftAsk or SoftAskReply)
   * @param voteType       the type of vote action to apply
   * @return the updated vote count after applying the operation
   */
  private Integer updateVoteCount(final Long softAskId, final Long softAskReplyId, final SoftAskVoteParentType parentType, final SoftAskVoteType voteType) {
    final boolean isVoted = SoftAskVoteType.isVoted(voteType);

    return switch (parentType) {
      case SOFT_ASK -> softAskOperationService.updateVoteCount(softAskId, isVoted);
      case SOFT_ASK_REPLY -> softAskOperationService.updateVoteCount(softAskId, softAskReplyId, isVoted);
    };
  }

  /**
   * Counts the number of votes received on replies authored by the given member.
   *
   * <p>This method delegates to the repository to count how many times other users
   * have voted on this member's replies.</p>
   *
   * @param memberId the ID of the member whose reply votes are being counted
   * @return the total number of votes on the member's replies
   */
  @Override
  public Integer countUserSoftAskReplyVotes(final Long memberId) {
    return softAskVoteRepository.countVotesOnMyReplies(memberId);
  }

  /**
   * Counts the number of votes received on SoftAsks authored by the given member.
   *
   * <p>This method delegates to the repository to count how many times other users
   * have voted on this member's SoftAsks.</p>
   *
   * @param memberId the ID of the member whose SoftAsk votes are being counted
   * @return the total number of votes on the member's SoftAsks
   */
  @Override
  public Integer countUserSoftAskVotes(final Long memberId) {
    return softAskVoteRepository.countVotesOnMySoftAsks(memberId);
  }
}
