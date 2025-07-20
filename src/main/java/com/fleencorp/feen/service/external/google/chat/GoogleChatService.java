package com.fleencorp.feen.service.external.google.chat;

import com.fleencorp.feen.chat.space.model.request.external.core.CreateChatSpaceRequest;
import com.fleencorp.feen.chat.space.model.request.external.core.DeleteChatSpaceRequest;
import com.fleencorp.feen.chat.space.model.request.external.core.RetrieveChatSpaceRequest;
import com.fleencorp.feen.chat.space.model.request.external.core.UpdateChatSpaceRequest;
import com.fleencorp.feen.chat.space.model.request.external.message.GoogleChatSpaceMessageRequest;
import com.fleencorp.feen.model.response.external.google.chat.chat.GoogleCreateChatSpaceResponse;
import com.fleencorp.feen.model.response.external.google.chat.chat.GoogleDeleteChatSpaceResponse;
import com.fleencorp.feen.model.response.external.google.chat.chat.GoogleRetrieveChatSpaceResponse;
import com.fleencorp.feen.model.response.external.google.chat.chat.GoogleUpdateChatSpaceResponse;

public interface GoogleChatService {

  GoogleCreateChatSpaceResponse createSpace(CreateChatSpaceRequest createChatSpaceRequest);

  GoogleRetrieveChatSpaceResponse retrieveSpace(RetrieveChatSpaceRequest retrieveChatSpaceRequest);

  GoogleUpdateChatSpaceResponse updateChatSpace(UpdateChatSpaceRequest updateChatSpaceRequest);

  GoogleDeleteChatSpaceResponse deleteChatSpace(DeleteChatSpaceRequest deleteChatSpaceRequest);

  void createCalendarEventMessageAndSendToChatSpace(GoogleChatSpaceMessageRequest chatSpaceMessageRequest);
}
