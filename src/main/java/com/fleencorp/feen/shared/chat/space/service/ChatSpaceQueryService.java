package com.fleencorp.feen.shared.chat.space.service;

import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;

import java.util.Optional;

public interface ChatSpaceQueryService {

  Optional<IsAChatSpace> findChatSpaceById(final Long chatSpaceId);
}
