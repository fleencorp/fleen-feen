package com.fleencorp.feen.shared.poll.service;

import com.fleencorp.feen.shared.poll.contract.IsAPoll;

import java.util.Optional;

public interface PollQueryService {

  Optional<IsAPoll> findPollById(Long pollId);
}
