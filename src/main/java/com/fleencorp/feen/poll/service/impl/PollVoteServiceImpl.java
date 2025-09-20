package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.poll.exception.option.PollOptionNotFoundException;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.exception.vote.*;
import com.fleencorp.feen.poll.mapper.PollUnifiedMapper;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollVote;
import com.fleencorp.feen.poll.model.dto.VotePollDto;
import com.fleencorp.feen.poll.model.holder.PollOptionEntriesHolder;
import com.fleencorp.feen.poll.model.holder.PollVoteAggregateHolder;
import com.fleencorp.feen.poll.model.holder.PollVoteEntriesHolder;
import com.fleencorp.feen.poll.model.info.IsVotedInfo;
import com.fleencorp.feen.poll.model.info.TotalPollVoteEntriesInfo;
import com.fleencorp.feen.poll.model.request.PollVoteSearchRequest;
import com.fleencorp.feen.poll.model.response.core.PollOptionResponse;
import com.fleencorp.feen.poll.model.response.core.PollVoteResponse;
import com.fleencorp.feen.poll.model.search.PollVoteSearchResult;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.poll.service.PollSearchService;
import com.fleencorp.feen.poll.service.PollVoteService;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.response.UserResponse;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class PollVoteServiceImpl implements PollVoteService {

  private final MemberService memberService;
  private final PollOperationsService pollOperationsService;
  private final PollSearchService pollSearchService;
  private final PollUnifiedMapper pollUnifiedMapper;
  private final Localizer localizer;

  public PollVoteServiceImpl(
      final MemberService memberService,
      final PollOperationsService pollOperationsService,
      final PollSearchService pollSearchService,
      final PollUnifiedMapper pollUnifiedMapper,
      final Localizer localizer) {
    this.memberService = memberService;
    this.pollOperationsService = pollOperationsService;
    this.pollSearchService = pollSearchService;
    this.pollUnifiedMapper = pollUnifiedMapper;
    this.localizer = localizer;
  }

  @Override
  @Transactional(readOnly = true)
  public PollVoteSearchResult findVotes(final Long pollId, final PollVoteSearchRequest searchRequest) {
    final Poll poll = pollOperationsService.findById(pollId)
      .orElseThrow(PollNotFoundException.of(pollId));

    final Pageable pageable = searchRequest.getPage();
    final Page<Member> page = searchRequest.hasOptionId()
      ? pollOperationsService.findVoters(pollId, searchRequest.getPollOptionId(), pageable)
      : pollOperationsService.findVoters(pollId, pageable);

    final Collection<UserResponse> voteResponses = poll.isAnonymous()
      ? pollUnifiedMapper.toPollVoteResponses(page.getContent())
      : List.of();

    final SearchResult<UserResponse> searchResult = toSearchResult(voteResponses, page);
    return PollVoteSearchResult.of(searchResult);
  }

  /**
   * Casts a vote for the specified {@link Poll} by the given {@link RegisteredUser}.
   *
   * <p>This method performs multiple steps: it retrieves the member entity, fetches the target poll,
   * validates the poll's eligibility for voting, validates the selected options, discards any previous votes,
   * creates and saves the new votes, updates poll statistics, and finally maps and returns a localized response
   * including updated vote details.</p>
   *
   * @param pollId the ID of the poll to vote on
   * @param votePollDto the DTO containing the selected option IDs
   * @param user the user submitting the vote
   * @return a {@link PollVoteResponse} containing updated vote data
   * @throws MemberNotFoundException if the user is not associated with a valid member
   * @throws PollNotFoundException if the poll does not exist
   * @throws PollVotingNotAllowedPollDeletedException if the poll has been deleted
   * @throws PollVotingNotAllowedPollEndedException if the poll has ended
   * @throws PollVotingNotAllowedPollNoOptionException if the poll has no options
   * @throws PollOptionNotFoundException if any of the selected options are invalid
   * @throws PollVotingNoMultipleChoiceException if multiple options are selected in a single-choice poll
   */
  @Override
  @Transactional
  public PollVoteResponse votePoll(final Long pollId, final VotePollDto votePollDto, final RegisteredUser user)
    throws MemberNotFoundException, PollNotFoundException, PollVotingNotAllowedPollDeletedException,
      PollVotingNotAllowedPollEndedException, PollVotingNotAllowedPollNoOptionException, PollOptionNotFoundException,
      PollVotingNoMultipleChoiceException {
    final Member member = memberService.findMember(user.getId());
    final Collection<Long> votePollOptionIds = votePollDto.getOptionIds();
    final Poll poll = pollSearchService.findPollById(pollId);

    // Validate poll eligibility
    poll.validatePollForVote();
    validatePoll(poll, votePollOptionIds);
    validateUserVoteEntries(poll, member);
    // Create and save new votes
    final Collection<PollVote> pollVotes = votePollDto.toPollVotes(poll, member);

    pollOperationsService.saveAll(pollVotes);
    pollOperationsService.incrementPollOptionTotalEntries(pollId, votePollOptionIds);

    final PollOptionEntriesHolder pollOptionEntriesHolder = pollOperationsService.findOptionEntries(pollId, poll.getPollOptionIds());
    final Collection<PollOptionResponse> pollOptionResponses = pollUnifiedMapper.toPollOptionResponses(poll.getOptions(), pollOptionEntriesHolder, votePollOptionIds);

    final PollVoteAggregateHolder pollVoteAggregateHolder = pollOperationsService.findPollVoteAggregate(pollId);
    poll.setTotalEntries(pollVoteAggregateHolder.totalVotes());
    pollOperationsService.save(poll);

    final TotalPollVoteEntriesInfo totalPollVoteEntriesInfo = pollUnifiedMapper.toTotalPollVoteEntriesInfo(poll.getTotalEntries());

    final IsVotedInfo isVotedInfo = pollUnifiedMapper.toIsVotedInfo(true);
    final PollVoteResponse voteResponse = PollVoteResponse.of(poll.getPollId(), totalPollVoteEntriesInfo, isVotedInfo, pollOptionResponses);
    return localizer.of(voteResponse);
  }

  /**
   * Performs a full validation of the given {@link Poll} before allowing a member to vote.
   *
   * <p>This method first extracts the poll's option IDs, then validates that the selected option IDs exist.
   * It ensures that the number of selected options complies with the poll's multiple choice setting.
   * Finally, it removes any previous votes cast by the member for this poll.</p>
   *
   * @param poll the poll to validate
   * @param votePollOptionIds the IDs of the options the member intends to vote for
   * @throws PollOptionNotFoundException if any of the selected option IDs are invalid
   * @throws PollVotingNoMultipleChoiceException if multiple options are selected in a single-choice poll
   */
  protected void validatePoll(final Poll poll, final Collection<Long> votePollOptionIds) {
    final Collection<Long> existingPollOptionIds = poll.getPollOptionIds();
    validatePollOptions(existingPollOptionIds, votePollOptionIds);
    validateMultipleChoice(poll, votePollOptionIds);
  }

  /**
   * Validates and prepares the voting state for the given {@link Member} on the specified {@link Poll}.
   *
   * <p>This method first retrieves any existing vote entries associated with the member for the given poll.
   * It then checks if the user has already voted using {@code ensureUserHasNotAlreadyVoted}, which throws
   * an exception if any prior votes exist. If no exception is thrown, it proceeds to discard any previous
   * votes and decrement associated counts using {@code discardPreviousVotesAndEntries}.</p>
   *
   * <p>This method should be used when the voting logic requires a clean state before accepting a new vote,
   * but still enforces a "vote only once" policy.</p>
   *
   * @param poll the poll in which the user is attempting to vote
   * @param member the member attempting to vote
   * @throws PollVotingAlreadyVotedException if the member has already voted in the poll
   */
  protected void validateUserVoteEntries(final Poll poll, final Member member) {
    final PollVoteEntriesHolder pollVoteEntriesHolder = pollOperationsService.findVotesByPollIdAndMemberId(poll.getPollId(), member.getMemberId());
    ensureUserHasNotAlreadyVoted(poll.getPollId(), member.getMemberId());
    discardPreviousVotesAndEntries(poll, member, pollVoteEntriesHolder);
  }

  /**
   * Validates that all provided poll option IDs exist within the set of allowed option IDs.
   *
   * <p>If any of the given {@code pollOptionIds} do not exist in {@code existingPollOptionsIds},
   * a {@link PollOptionNotFoundException} is thrown containing the invalid option IDs.</p>
   *
   * @param existingPollOptionsIds the list of valid poll option IDs
   * @param pollOptionIds the list of option IDs to validate
   * @throws PollOptionNotFoundException if any option ID is not found in the existing options
   */
  protected void validatePollOptions(final Collection<Long> existingPollOptionsIds, final Collection<Long> pollOptionIds) {
    final List<Long> invalidOptionIds = pollOptionIds.stream()
      .filter(optionId -> !existingPollOptionsIds.contains(optionId))
      .toList();

    if (!invalidOptionIds.isEmpty()) {
      throw PollOptionNotFoundException.of(invalidOptionIds);
    }
  }

  /**
   * Validates whether the selected options comply with the poll's multiple choice setting.
   *
   * <p>If multiple option IDs are provided and the poll is not configured to allow multiple choices,
   * a {@link PollVotingNoMultipleChoiceException} is thrown.</p>
   *
   * @param poll the poll being voted on
   * @param optionIds the selected option IDs
   * @throws PollVotingNoMultipleChoiceException if multiple options are selected for a single-choice poll
   */
  protected void validateMultipleChoice(final Poll poll, final Collection<Long> optionIds) {
    if (nonNull(optionIds) && optionIds.size() > 1 && poll.isSingleChoice()) {
      throw PollVotingNoMultipleChoiceException.of(poll.getPollId());
    }
  }

  /**
   * Discards any previous votes made by the given {@link Member} on the specified {@link Poll}.
   *
   * <p>This method retrieves the member's existing votes for the poll. If votes are found,
   * it first decrements the total entry counts for the previously selected poll options using
   * {@code pollOperationsService.decrementPollOptionTotalEntries}, then deletes the votes using
   * {@code pollOperationsService.deleteVoteByPollIdAndMemberId}.</p>
   *
   * @param poll the poll for which previous votes should be discarded
   * @param member the member whose previous votes are to be removed
   * @param pollVoteEntriesHolder the record containing the user votes for the poll
   */
  protected void discardPreviousVotesAndEntries(final Poll poll, final Member member, final PollVoteEntriesHolder pollVoteEntriesHolder) {
    if (pollVoteEntriesHolder.hasVotes()) {
      log.info("Did not discard previous votes?????");
      // Decrement totalEntries for previously voted options
      final Collection<Long> previousOptionIds = pollVoteEntriesHolder.getPollVoteIds();

      pollOperationsService.decrementPollOptionTotalEntries(poll.getPollId(), previousOptionIds);
      // Delete the previous votes
      pollOperationsService.deleteVoteByPollIdAndMemberId(poll.getPollId(), member.getMemberId());
    }
  }

  /**
   * Ensures that the given member has not already voted in the specified poll.
   *
   * <p>This method queries for any existing vote entries by the member in the poll.
   * If any votes are found, it throws an {@link IllegalStateException} to prevent
   * duplicate voting.</p>
   *
   * <p>This check is typically used in single-vote enforcement scenarios where a user
   * is allowed to vote only once per poll.</p>
   *
   * @param pollId the ID of the poll to check
   * @param memberId the ID of the member attempting to vote
   * @throws IllegalStateException if the member has already voted in the poll
   */
  protected void ensureUserHasNotAlreadyVoted(final Long pollId, final Long memberId) {
    final PollVoteEntriesHolder existingVotes = pollOperationsService.findVotesByPollIdAndMemberId(pollId, memberId);
    if (existingVotes.hasVotes()) {
      throw PollVotingAlreadyVotedException.of();
    }
  }

}
