package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.feen.bookmark.service.BookmarkOperationService;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceService;
import com.fleencorp.feen.like.service.LikeOperationService;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.exception.poll.PollUpdateUnauthorizedException;
import com.fleencorp.feen.poll.mapper.PollUnifiedMapper;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollOption;
import com.fleencorp.feen.poll.model.domain.PollVote;
import com.fleencorp.feen.poll.model.holder.PollResponseEntriesHolder;
import com.fleencorp.feen.poll.model.holder.PollVoteEntriesHolder;
import com.fleencorp.feen.poll.model.info.IsVotedInfo;
import com.fleencorp.feen.poll.model.response.core.PollOptionResponse;
import com.fleencorp.feen.poll.model.response.core.PollResponse;
import com.fleencorp.feen.poll.model.response.core.PollVoteResponse;
import com.fleencorp.feen.poll.service.PollCommonService;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.setEntityUpdatableByUser;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class PollCommonServiceImpl implements PollCommonService {

  private final BookmarkOperationService bookmarkOperationService;
  private final LikeOperationService likeOperationService;
  private final ChatSpaceService chatSpaceService;
  private final PollOperationsService pollOperationsService;
  private final PollUnifiedMapper pollUnifiedMapper;

  public PollCommonServiceImpl(
      final BookmarkOperationService bookmarkOperationService,
      final LikeOperationService likeOperationService,
      final ChatSpaceService chatSpaceService,
      final PollOperationsService pollOperationsService,
      final PollUnifiedMapper pollUnifiedMapper) {
    this.bookmarkOperationService = bookmarkOperationService;
    this.likeOperationService = likeOperationService;
    this.chatSpaceService = chatSpaceService;
    this.pollOperationsService = pollOperationsService;
    this.pollUnifiedMapper = pollUnifiedMapper;
  }

  /**
   * Retrieves a {@link Poll} by its ID or throws a {@link PollNotFoundException} if not found.
   *
   * <p>This method delegates to {@code pollOperationsService.findById}. If the poll is not present,
   * it throws an exception constructed with the given {@code pollId}.</p>
   *
   * @param pollId the ID of the poll to retrieve
   * @return the {@link Poll} associated with the given ID
   * @throws PollNotFoundException if no poll exists with the specified ID
   */
  @Override
  public Poll findPollById(final Long pollId) {
    return pollOperationsService.findById(pollId)
      .orElseThrow(PollNotFoundException.of(pollId));
  }

  /**
   * Verifies whether the given {@link Member} has permission to update the specified {@link Poll}.
   *
   * <p>If the poll belongs to a chat space, the method verifies that the member is either the creator
   * or an admin of that chat space using {@code chatSpaceService.verifyCreatorOrAdminOfChatSpace}.
   * If the poll belongs to a stream or has no parent, it checks whether the member is the author
   * using {@code poll.checkAuthor}.</p>
   *
   * @param poll the poll whose update permission is being checked
   * @param member the member attempting to update the poll
   * @throws PollUpdateUnauthorizedException if the member does not have update permissions
   */
  @Override
  public void checkUpdatePermission(final Poll poll, final Member member) {
    if (poll.hasAChatSpaceParent()) {
      chatSpaceService.verifyCreatorOrAdminOfChatSpace(poll.getChatSpace(), member);
    } else if (poll.hasAStreamParent() || poll.hasNoParent()) {
      poll.checkAuthor(member.getMemberId());
    }
  }

  /**
   * Enriches each {@link PollResponse} with the voting details of the specified {@link IsAMember}, if applicable.
   *
   * <p>If the member is {@code null} or the {@code pollResponseEntriesHolder} does not contain any polls,
   * the method returns immediately. Otherwise, it retrieves all poll IDs and fetches the corresponding
   * {@link PollVote} entries made by the member. It then updates each {@link PollResponse} with the user's vote,
   * using {@code setUserVote}.</p>
   *
   * @param pollResponseEntriesHolder the holder containing the poll responses to process
   * @param member the member whose voting details should be applied
   */
  @Override
  public void processPollOtherDetails(final PollResponseEntriesHolder pollResponseEntriesHolder, final IsAMember member) {
    if (isNull(member) || pollResponseEntriesHolder.hasNoPolls()) {
      return;
    }

    final Long memberId = member.getMemberId();
    final Collection<Long> pollIds = pollResponseEntriesHolder.getPollIds();
    if (pollIds.isEmpty()) {
      return;
    }

    final Collection<PollResponse> pollResponses = pollResponseEntriesHolder.pollResponses();
    final PollVoteEntriesHolder pollVoteEntriesHolder = pollOperationsService.findVotesByPollIdsAndMemberId(pollIds, memberId);

    pollResponses.stream()
      .filter(Objects::nonNull)
      .forEach(pollResponse -> {
        setUserVote(pollResponse, pollVoteEntriesHolder);
        setEntityUpdatableByUser(pollResponse, memberId);
      });

    bookmarkOperationService.populateBookmarkForReviews(pollResponses, member);
    likeOperationService.populateLikesForReviews(pollResponses, member);
  }

  /**
   * Sets the user's vote information on the given {@link PollResponse}, if available.
   *
   * <p>This method retrieves the list of {@link PollVote} entries for the poll using its ID from the provided
   * {@link PollVoteEntriesHolder}. If votes are found, it delegates to {@code setPollVoteOptions}
   * to populate the vote details in the response.</p>
   *
   * @param pollResponse the poll response to update with the user's vote
   * @param pollVoteEntriesHolder the holder containing vote entries mapped by poll ID
   */
  protected void setUserVote(final PollResponse pollResponse, final PollVoteEntriesHolder pollVoteEntriesHolder) {
    final Long pollId = pollResponse.getNumberId();
    final List<PollVote> votes = pollVoteEntriesHolder.getPollVotes(pollId);

    if (nonNull(votes) && !votes.isEmpty()) {
      setPollVoteOptions(pollResponse, votes);
    }
  }

  /**
   * Sets the poll vote options and voting status on the given {@link PollResponse}.
   *
   * <p>This method extracts the {@link PollOption} objects from the provided list of {@link PollVote} entities,
   * maps them to {@link PollOptionResponse} objects, and determines whether any votes were cast.
   * It then builds a {@link PollVoteResponse} containing the option responses and voting status,
   * and sets it on the provided {@code pollResponse}.</p>
   *
   * @param pollResponse the response object to enrich with vote information
   * @param votes the list of votes associated with the poll
   */
  protected void setPollVoteOptions(final PollResponse pollResponse, final List<PollVote> votes) {
    final List<PollOption> pollOptions = votes.stream()
      .map(PollVote::getPollOption)
      .toList();

    final Collection<PollOptionResponse> optionResponses = pollUnifiedMapper.toVotedPollOptionResponses(pollOptions);

    final boolean isVoted = !votes.isEmpty();
    final IsVotedInfo isVotedInfo = pollUnifiedMapper.toIsVotedInfo(isVoted);

    final PollVoteResponse pollVoteResponse = PollVoteResponse.of(optionResponses, isVotedInfo);
    pollResponse.setPollVote(pollVoteResponse);
  }


}
