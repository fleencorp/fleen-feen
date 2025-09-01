package com.fleencorp.feen.poll.service;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotAnAdminException;
import com.fleencorp.feen.poll.exception.option.PollUpdateCantChangeOptionsException;
import com.fleencorp.feen.poll.exception.poll.*;
import com.fleencorp.feen.poll.model.dto.UpdatePollDto;
import com.fleencorp.feen.poll.model.response.PollUpdateResponse;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface PollUpdateService {

  PollUpdateResponse updatePoll(Long pollId, UpdatePollDto updatePollDto, RegisteredUser user)
    throws PollNotFoundException, PollUpdateUnauthorizedException, PollUpdateCantChangeQuestionException,
      PollUpdateCantChangeMultipleChoiceException, PollUpdateCantChangeVisibilityException, PollUpdateCantChangeAnonymityException,
      PollUpdateCantChangeOptionsException, ChatSpaceNotAnAdminException;
}
