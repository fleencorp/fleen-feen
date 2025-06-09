package com.fleencorp.feen.service.chat.space.event;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.model.dto.event.CreateChatSpaceEventDto;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.search.chat.space.event.ChatSpaceEventSearchResult;
import com.fleencorp.feen.user.security.RegisteredUser;

public interface ChatSpaceEventService {

  ChatSpaceEventSearchResult findChatSpaceEvents(Long chatSpaceId, SearchRequest searchRequest, RegisteredUser user);

  CreateStreamResponse createChatSpaceEvent(Long chatSpaceId, CreateChatSpaceEventDto createChatSpaceEventDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, CalendarNotFoundException, FailedOperationException;
}
