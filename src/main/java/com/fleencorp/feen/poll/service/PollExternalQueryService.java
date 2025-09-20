package com.fleencorp.feen.poll.service;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.stream.model.domain.FleenStream;

public interface PollExternalQueryService {
  IsAMember findMemberById(Long memberId);

  FleenStream findStreamById(Long streamId);

  ChatSpace findChatSpaceById(Long chatSpaceId);

  void verifyCreatorOrAdminOfChatSpace(IsAChatSpace chatSpace, IsAMember member);
}
