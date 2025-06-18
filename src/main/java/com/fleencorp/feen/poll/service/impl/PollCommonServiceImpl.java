package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.exception.poll.PollUpdateUnauthorizedException;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.service.PollCommonService;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.stereotype.Service;

@Service
public class PollCommonServiceImpl implements PollCommonService {

  private final ChatSpaceService chatSpaceService;
  private final PollOperationsService pollOperationsService;

  public PollCommonServiceImpl(
    final ChatSpaceService chatSpaceService,
    final PollOperationsService pollOperationsService) {
    this.chatSpaceService = chatSpaceService;
    this.pollOperationsService = pollOperationsService;
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
}
