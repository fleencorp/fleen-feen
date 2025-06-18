package com.fleencorp.feen.poll.service;

import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.user.model.domain.Member;

public interface PollCommonService {

  Poll findPollById(Long pollId);

  void checkUpdatePermission(Poll poll, Member member);
}
