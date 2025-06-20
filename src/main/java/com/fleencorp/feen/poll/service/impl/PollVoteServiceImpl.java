package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.poll.exception.option.PollOptionNotFoundException;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNoMultipleChoiceException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollDeletedException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollEndedException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollNoOptionException;
import com.fleencorp.feen.poll.mapper.PollMapper;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollVote;
import com.fleencorp.feen.poll.model.dto.VotePollDto;
import com.fleencorp.feen.poll.model.holder.PollOptionEntriesHolder;
import com.fleencorp.feen.poll.model.holder.PollVoteAggregateHolder;
import com.fleencorp.feen.poll.model.holder.PollVoteEntriesHolder;
import com.fleencorp.feen.poll.model.request.PollVoteSearchRequest;
import com.fleencorp.feen.poll.model.response.base.PollOptionResponse;
import com.fleencorp.feen.poll.model.response.base.PollVoteResponse;
import com.fleencorp.feen.poll.model.search.PollVoteSearchResult;
import com.fleencorp.feen.poll.service.PollCommonService;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.poll.service.PollVoteService;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.response.UserResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.nonNull;

@Service
public class PollVoteServiceImpl implements PollVoteService {

  private final MemberService memberService;
  private final PollCommonService pollCommonService;
  private final PollOperationsService pollOperationsService;
  private final PollMapper pollMapper;
  private final Localizer localizer;

  public PollVoteServiceImpl(
      final MemberService memberService,
      final PollCommonService pollCommonService,
      final PollOperationsService pollOperationsService,
      final PollMapper pollMapper,
      final Localizer localizer) {
    this.memberService = memberService;
    this.pollCommonService = pollCommonService;
    this.pollOperationsService = pollOperationsService;
    this.pollMapper = pollMapper;
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
      ? pollMapper.toPollVoteResponses(page.getContent())
      : List.of();

    final SearchResult searchResult = toSearchResult(voteResponses, page);
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
    final Poll poll = pollCommonService.findPollById(pollId);

    // Validate poll eligibility
    poll.validatePollForVote();
    validatePoll(poll, votePollOptionIds, member);
    // Create and save new votes
    final Collection<PollVote> pollVotes = votePollDto.toPollVotes(poll, member);

    pollOperationsService.saveAll(pollVotes);
    pollOperationsService.incrementPollOptionTotalEntries(pollId, votePollOptionIds);

    final PollOptionEntriesHolder pollOptionEntriesHolder = pollOperationsService.findOptionEntries(pollId, votePollOptionIds);
    final Collection<PollOptionResponse> pollOptionResponses = pollMapper.toPollOptionResponses(poll.getOptions(), pollOptionEntriesHolder);

    final PollVoteAggregateHolder pollVoteAggregateHolder = pollOperationsService.findPollVoteAggregate(pollId);
    poll.setTotalEntries(pollVoteAggregateHolder.totalVotes());
    pollOperationsService.save(poll);

    final PollVoteResponse voteResponse = PollVoteResponse.of(poll.getPollId(), poll.getTotalEntries());
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
   * @param member the member attempting to vote
   * @throws PollOptionNotFoundException if any of the selected option IDs are invalid
   * @throws PollVotingNoMultipleChoiceException if multiple options are selected in a single-choice poll
   */
  protected void validatePoll(final Poll poll, final Collection<Long> votePollOptionIds, final Member member) {
    final Collection<Long> existingPollOptionIds = poll.getPollOptionIds();
    validatePollOptions(existingPollOptionIds, votePollOptionIds);
    validateMultipleChoice(poll, votePollOptionIds);
    discardPreviousVotesAndEntries(poll, member);
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
   */
  protected void discardPreviousVotesAndEntries(final Poll poll, final Member member) {
    final PollVoteEntriesHolder pollVoteEntriesHolder = pollOperationsService.findVotesByPollIdAndMemberId(poll.getPollId(), member.getMemberId());

    if (pollVoteEntriesHolder.hasVotes()) {
      // Decrement totalEntries for previously voted options
      final Collection<Long> previousOptionIds = pollVoteEntriesHolder.getPollVoteIds();

      pollOperationsService.decrementPollOptionTotalEntries(poll.getPollId(), previousOptionIds);
      // Delete the previous votes
      pollOperationsService.deleteVoteByPollIdAndMemberId(poll.getPollId(), member.getMemberId());
    }
  }

}
