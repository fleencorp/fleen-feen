package com.fleencorp.feen.softask.service.other;

import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.stream.IsAStream;

public interface SoftAskQueryService {

  IsAChatSpace findChatSpaceOrThrow(Long chatSpaceId);

  IsAStream findStreamOrThrow(Long streamId);

  IsAMember findMemberOrThrow(Long memberId);
}
