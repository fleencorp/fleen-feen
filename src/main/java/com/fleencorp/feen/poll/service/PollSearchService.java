package com.fleencorp.feen.poll.service;

import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.model.request.PollSearchRequest;
import com.fleencorp.feen.poll.model.response.GetDataRequiredToCreatePoll;
import com.fleencorp.feen.poll.model.response.PollRetrieveResponse;
import com.fleencorp.feen.poll.model.search.ChatSpacePollSearchResult;
import com.fleencorp.feen.poll.model.search.PollSearchResult;
import com.fleencorp.feen.poll.model.search.StreamPollSearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface PollSearchService {

  GetDataRequiredToCreatePoll getDataRequiredToCreatePoll();

  PollRetrieveResponse findPoll(Long pollId, final RegisteredUser user) throws PollNotFoundException;

  PollSearchResult findPolls(PollSearchRequest searchRequest, RegisteredUser user);

  ChatSpacePollSearchResult findChatSpacePolls(PollSearchRequest searchRequest, RegisteredUser user);

  StreamPollSearchResult findStreamPolls(PollSearchRequest searchRequest, RegisteredUser user);

  PollSearchResult findMyPolls(PollSearchRequest searchRequest, RegisteredUser user);
}
