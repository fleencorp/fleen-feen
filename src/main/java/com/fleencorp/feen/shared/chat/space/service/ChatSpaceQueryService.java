package com.fleencorp.feen.shared.chat.space.service;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpaceMember;

import java.util.Optional;

public interface ChatSpaceQueryService {

  Optional<IsAChatSpace> findChatSpaceById(final Long chatSpaceId);

  @SuppressWarnings("unchecked")
  Optional<IsAChatSpaceMember> findByChatSpaceAndMemberAndStatus(
    Long chatSpaceId,
    Long memberId,
    ChatSpaceRequestToJoinStatus requestToJoinStatus
  );
}
