package com.fleencorp.feen.service.chat.space.event;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.model.dto.event.CreateChatSpaceEventDto;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.search.chat.space.event.ChatSpaceEventSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface ChatSpaceEventService {

  ChatSpaceEventSearchResult findChatSpaceEvents(Long chatSpaceId, SearchRequest searchRequest, FleenUser user);

  CreateStreamResponse createChatSpaceEvent(Long chatSpaceId, CreateChatSpaceEventDto createChatSpaceEventDto, FleenUser user);

}
