package com.fleencorp.feen.service.external.google.chat;

import com.fleencorp.feen.chat.space.model.request.external.core.CreateChatSpaceRequest;
import com.google.chat.v1.Space;

public interface GoogleChatUpdateService {

  void addChatAppToSpace(String chatAppOrBotUsername, String spaceName);

  void updateNewSpaceHistoryState(CreateChatSpaceRequest createChatSpaceRequest, Space createdSpace);
}
