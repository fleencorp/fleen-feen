package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.service.PollOtherService;
import com.fleencorp.feen.poll.service.PollSearchService;
import org.springframework.stereotype.Service;

@Service
public class PollOtherServiceImpl implements PollOtherService {

  private final PollSearchService pollSearchService;

  public PollOtherServiceImpl(final PollSearchService pollSearchService) {
    this.pollSearchService = pollSearchService;
  }

  @Override
  public Poll findPollById(Long id) {
    return pollSearchService.findPollById(id);
  }
}
