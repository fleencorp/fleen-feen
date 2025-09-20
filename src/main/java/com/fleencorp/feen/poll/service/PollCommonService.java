package com.fleencorp.feen.poll.service;

import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.holder.PollResponseEntriesHolder;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.user.model.domain.Member;

public interface PollCommonService {

  Poll findPollById(Long pollId);

  void checkUpdatePermission(Poll poll, Member member);

  void processPollOtherDetails(PollResponseEntriesHolder pollResponseEntriesHolder, IsAMember member);
}
