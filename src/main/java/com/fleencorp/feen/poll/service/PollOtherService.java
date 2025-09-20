package com.fleencorp.feen.poll.service;

import com.fleencorp.feen.poll.model.domain.Poll;

public interface PollOtherService {

  Poll findPollById(Long id);
}
