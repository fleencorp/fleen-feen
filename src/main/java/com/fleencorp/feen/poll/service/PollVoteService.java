package com.fleencorp.feen.poll.service;

import com.fleencorp.feen.poll.exception.option.PollOptionNotFoundException;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNoMultipleChoiceException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollDeletedException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollEndedException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollNoOptionException;
import com.fleencorp.feen.poll.model.dto.VotePollDto;
import com.fleencorp.feen.poll.model.request.PollVoteSearchRequest;
import com.fleencorp.feen.poll.model.response.core.PollVoteResponse;
import com.fleencorp.feen.poll.model.search.PollVoteSearchResult;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;

public interface PollVoteService {

  PollVoteSearchResult findVotes(Long pollId, PollVoteSearchRequest searchRequest);

  PollVoteResponse votePoll(Long pollId, VotePollDto votePollDto, RegisteredUser user)
    throws MemberNotFoundException, PollNotFoundException, PollVotingNotAllowedPollDeletedException,
      PollVotingNotAllowedPollEndedException, PollVotingNotAllowedPollNoOptionException, PollOptionNotFoundException,
    PollVotingNoMultipleChoiceException;
}
