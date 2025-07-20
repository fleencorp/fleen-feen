package com.fleencorp.feen.softask.service.impl.vote;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteParentType;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteType;
import com.fleencorp.feen.softask.exception.core.SoftAskAnswerNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.dto.vote.SoftAskVoteDto;
import com.fleencorp.feen.softask.model.holder.SoftAskVoteParentDetailsHolder;
import com.fleencorp.feen.softask.model.response.vote.SoftAskVoteUpdateResponse;
import com.fleencorp.feen.softask.model.response.vote.core.SoftAskVoteResponse;
import com.fleencorp.feen.softask.repository.vote.SoftAskVoteRepository;
import com.fleencorp.feen.softask.service.answer.SoftAskAnswerSearchService;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.softask.service.vote.SoftAskVoteService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;

@Service
public class SoftAskVoteServiceImpl implements SoftAskVoteService {

  private final SoftAskAnswerSearchService softAskAnswerSearchService;
  private final SoftAskOperationService softAskOperationService;
  private final SoftAskReplySearchService softAskReplySearchService;
  private final SoftAskSearchService softAskSearchService;
  private final SoftAskVoteRepository softAskVoteRepository;
  private final SoftAskMapper softAskMapper;

  public SoftAskVoteServiceImpl(
    final SoftAskAnswerSearchService softAskAnswerSearchService,
    final SoftAskOperationService softAskOperationService,
    final SoftAskReplySearchService softAskReplySearchService,
    final SoftAskSearchService softAskSearchService,
    final SoftAskVoteRepository softAskVoteRepository,
    final SoftAskMapper softAskMapper) {
    this.softAskAnswerSearchService = softAskAnswerSearchService;
    this.softAskOperationService = softAskOperationService;
    this.softAskReplySearchService = softAskReplySearchService;
    this.softAskSearchService = softAskSearchService;
    this.softAskVoteRepository = softAskVoteRepository;
    this.softAskMapper = softAskMapper;
  }

  /**
   * Processes a vote action for a given vote DTO and user.
   *
   * <p>Retrieves the parent entity based on vote parent type and ID, then creates or updates
   * the vote by the member. Persists the vote and updates the total vote count accordingly.
   * Returns a response containing the updated vote information and total votes for the parent.</p>
   *
   * @param softAskVoteDto the DTO containing the vote details.
   * @param user the registered user performing the vote.
   * @return a {@link SoftAskVoteUpdateResponse} containing the updated vote ID and vote details.
   * @throws SoftAskAnswerNotFoundException if the referenced SoftAskAnswer does not exist.
   * @throws SoftAskReplyNotFoundException if the referenced SoftAskReply does not exist.
   * @throws SoftAskNotFoundException if the referenced SoftAsk does not exist.
   * @throws FailedOperationException if any operation fails during vote processing.
   */
  @Override
  public SoftAskVoteUpdateResponse vote(final SoftAskVoteDto softAskVoteDto, final RegisteredUser user)
      throws SoftAskAnswerNotFoundException, SoftAskReplyNotFoundException, SoftAskNotFoundException,
      FailedOperationException {
    final Long parentId = softAskVoteDto.getParentId();
    final SoftAskVoteParentType parentType = softAskVoteDto.getVoteParentType();
    final Member member = user.toMember();

    final SoftAskVoteParentDetailsHolder parentDetailsHolder = findSoftAskVoteParentDetailsHolder(parentType, parentId);
    SoftAskVote softAskVote = createOrUpdateSoftVote(softAskVoteDto, parentType, member, parentDetailsHolder);
    softAskVote = softAskVoteRepository.save(softAskVote);

    final boolean voted = SoftAskVoteType.isVoted(softAskVoteDto.getVoteType());
    final Integer total = updateVoteCount(parentId, parentType, softAskVoteDto.getVoteType());
    final SoftAskVoteResponse softAskVoteResponse = softAskMapper.toSoftAskVoteResponse(softAskVote, voted);
    softAskVoteResponse.setParentTotalVotes(total);

    return SoftAskVoteUpdateResponse.of(softAskVote.getVoteId(), softAskVoteResponse);
  }

  /**
   * Retrieves the parent entities related to a vote based on the parent type and ID.
   *
   * <p>Validates the parent type is not null. Depending on the type, fetches the corresponding
   * {@link SoftAsk}, {@link SoftAskAnswer}, or {@link SoftAskReply} entity by ID.</p>
   *
   * @param softAskVoteParentType the type of the vote parent entity.
   * @param parentId the ID of the parent entity.
   * @return a {@link SoftAskVoteParentDetailsHolder} containing the relevant parent entities.
   * @throws SoftAskNotFoundException if the SoftAsk entity is not found when required.
   * @throws SoftAskAnswerNotFoundException if the SoftAskAnswer entity is not found when required.
   * @throws SoftAskReplyNotFoundException if the SoftAskReply entity is not found when required.
   * @throws FailedOperationException if the parent type is null or operation fails.
   */
  private SoftAskVoteParentDetailsHolder findSoftAskVoteParentDetailsHolder(final SoftAskVoteParentType softAskVoteParentType, final Long parentId)
      throws SoftAskNotFoundException, SoftAskAnswerNotFoundException, SoftAskReplyNotFoundException,
      FailedOperationException {
    checkIsNull(softAskVoteParentType, FailedOperationException::new);

    final SoftAsk softAsk = SoftAskVoteParentType.isSoftAsk(softAskVoteParentType) ? softAskSearchService.findSoftAsk(parentId) : null;
    final SoftAskAnswer softAskAnswer = SoftAskVoteParentType.isSoftAskAnswer(softAskVoteParentType) ? softAskAnswerSearchService.findSoftAskAnswer(parentId) : null;
    final SoftAskReply softAskReply = SoftAskVoteParentType.isSoftAskReply(softAskVoteParentType) ? softAskReplySearchService.findSoftAskReply(parentId) : null;

    return SoftAskVoteParentDetailsHolder.of(softAsk, softAskAnswer, softAskReply);
  }

  /**
   * Creates a new {@link SoftAskVote} or updates an existing one based on the given DTO, parent type, member, and parent details.
   *
   * <p>Checks if a vote already exists for the member and parent entity; if yes, updates its vote type.
   * Otherwise, creates a new vote entity from the DTO and parent references.</p>
   *
   * @param softAskVoteDto the DTO containing vote data.
   * @param softAskVoteParentType the type of parent entity (SOFT_ASK, SOFT_ASK_ANSWER, SOFT_ASK_REPLY).
   * @param member the member casting the vote.
   * @param parentDetailsHolder the holder of parent entities and parent ID related to the vote.
   * @return the created or updated {@link SoftAskVote} entity.
   * @throws FailedOperationException if a required operation fails.
   */
  private SoftAskVote createOrUpdateSoftVote(final SoftAskVoteDto softAskVoteDto, final SoftAskVoteParentType softAskVoteParentType, final Member member, final SoftAskVoteParentDetailsHolder parentDetailsHolder)
      throws FailedOperationException {
    final SoftAsk softAsk = parentDetailsHolder.softAsk();
    final SoftAskAnswer softAskAnswer = parentDetailsHolder.softAskAnswer();
    final SoftAskReply softAskReply = parentDetailsHolder.softAskReply();

    return findVoteByParent(parentDetailsHolder.parentId(), softAskVoteParentType, member)
      .map(existingVote -> {
        existingVote.setVoteType(softAskVoteDto.getVoteType());
        return existingVote;
      })
      .orElseGet(() -> softAskVoteDto.by(member, softAsk, softAskAnswer, softAskReply));
  }

  /**
   * Finds an existing {@link SoftAskVote} by parent ID, parent type, and member.
   *
   * <p>Checks that the parent ID is not null, then queries the repository for a vote
   * matching the given member and parent entity based on the parent type.</p>
   *
   * @param parentId the ID of the parent entity to find the vote for.
   * @param softAskVoteParentType the type of the parent entity (SOFT_ASK, SOFT_ASK_ANSWER, or SOFT_ASK_REPLY).
   * @param member the member who cast the vote.
   * @return an {@link Optional} containing the found {@link SoftAskVote}, or empty if none found.
   * @throws FailedOperationException if the parentId is null.
   */
  private Optional<SoftAskVote> findVoteByParent(final Long parentId, final SoftAskVoteParentType softAskVoteParentType, final Member member) {
    checkIsNull(parentId, FailedOperationException::new);
    final Long memberId = member.getMemberId();

    return switch (softAskVoteParentType) {
      case SOFT_ASK -> softAskVoteRepository.findByMemberAndSoftAsk(memberId, parentId);
      case SOFT_ASK_ANSWER -> softAskVoteRepository.findByMemberAndSoftAskAnswer(memberId, parentId);
      case SOFT_ASK_REPLY -> softAskVoteRepository.findByMemberAndSoftAskReply(memberId, parentId);
    };
  }

  /**
   * Updates the vote count for a given parent entity based on the vote type.
   *
   * <p>Checks for null inputs, determines if the vote is an upvote or removal,
   * then increments or decrements the corresponding vote count accordingly.</p>
   *
   * @param parentId the ID of the parent entity whose vote count is updated.
   * @param parentType the type of the parent entity (e.g., SOFT_ASK, SOFT_ASK_ANSWER, SOFT_ASK_REPLY).
   * @param voteType the type of vote indicating if it is a vote or unvote.
   * @return the updated total vote count for the parent entity.
   * @throws FailedOperationException if any required parameter is null.
   */
  private Integer updateVoteCount(final Long parentId, final SoftAskVoteParentType parentType, final SoftAskVoteType voteType) {
    checkIsNullAny(List.of(parentId, parentType), FailedOperationException::new);
    final boolean isVoted = SoftAskVoteType.isVoted(voteType);

    return switch (parentType) {
      case SOFT_ASK -> isVoted
        ? softAskOperationService.incrementSoftAskVoteAndGetVoteCount(parentId)
        : softAskOperationService.decrementSoftAskVoteAndGetVoteCount(parentId);

      case SOFT_ASK_ANSWER -> isVoted
        ? softAskOperationService.incrementSoftAskAnswerVoteAndGetVoteCount(parentId)
        : softAskOperationService.decrementSoftAskAnswerVoteAndGetVoteCount(parentId);

      case SOFT_ASK_REPLY -> isVoted
        ? softAskOperationService.incrementSoftAskReplyVoteAndGetVoteCount(parentId)
        : softAskOperationService.decrementSoftAskReplyVoteAndGetVoteCount(parentId);
    };
  }

  /**
   * Counts the number of votes received on answers authored by the given member.
   *
   * <p>This method delegates to the repository to count how many times other users
   * have voted on this member's answers.</p>
   *
   * @param memberId the ID of the member whose answer votes are being counted
   * @return the total number of votes on the member's answers
   */
  public Integer countUserSoftAskAnswerVotes(final Long memberId) {
    return softAskVoteRepository.countVotesOnMyAnswers(memberId);
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
