package com.fleencorp.feen.chat.space.service.event;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.stream.model.dto.event.CreateChatSpaceEventDto;
import com.fleencorp.feen.stream.model.response.base.CreateStreamResponse;
import com.fleencorp.feen.chat.space.model.search.event.ChatSpaceEventSearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface ChatSpaceEventService {

  ChatSpaceEventSearchResult findChatSpaceEvents(Long chatSpaceId, SearchRequest searchRequest, RegisteredUser user);

  CreateStreamResponse createChatSpaceEvent(Long chatSpaceId, CreateChatSpaceEventDto createChatSpaceEventDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, CalendarNotFoundException, FailedOperationException;
}
