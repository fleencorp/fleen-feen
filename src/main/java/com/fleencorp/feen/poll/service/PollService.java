package com.fleencorp.feen.poll.service;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.exception.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.exception.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.model.dto.AddPollDto;
import com.fleencorp.feen.poll.model.dto.DeletePollDto;
import com.fleencorp.feen.poll.model.response.PollCreateResponse;
import com.fleencorp.feen.poll.model.response.PollDeleteResponse;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface PollService {

  PollCreateResponse addPoll(AddPollDto addPollDto, RegisteredUser user)
    throws MemberNotFoundException, ChatSpaceNotFoundException, NotAnAdminOfChatSpaceException,
      StreamNotFoundException, StreamNotCreatedByUserException;

  PollDeleteResponse deletePoll(DeletePollDto deletePollDto, RegisteredUser user) throws PollNotFoundException;
}
