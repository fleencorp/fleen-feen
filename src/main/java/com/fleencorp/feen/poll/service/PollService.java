package com.fleencorp.feen.poll.service;

import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.model.dto.AddPollDto;
import com.fleencorp.feen.poll.model.dto.DeletePollDto;
import com.fleencorp.feen.poll.model.response.PollCreateResponse;
import com.fleencorp.feen.poll.model.response.PollDeleteResponse;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface PollService {

  PollCreateResponse addPoll(AddPollDto addPollDto, RegisteredUser user) throws MemberNotFoundException;

  PollCreateResponse streamAddPoll(Long streamId, AddPollDto addPollDto, RegisteredUser user)
    throws MemberNotFoundException, StreamNotFoundException, StreamNotCreatedByUserException;

  PollCreateResponse chatSpaceAddPoll(Long chatSpaceId, AddPollDto addPollDto, RegisteredUser user)
    throws MemberNotFoundException, ChatSpaceNotFoundException, NotAnAdminOfChatSpaceException;

  PollDeleteResponse deletePoll(DeletePollDto deletePollDto, RegisteredUser user) throws PollNotFoundException;
}
