package com.fleencorp.feen.chat.space.service.core;

import com.fleencorp.feen.chat.space.model.response.core.ChatSpaceResponse;
import com.fleencorp.feen.shared.security.RegisteredUser;

import java.util.List;

public interface ChatSpaceOtherService {

  void processOtherChatSpaceDetails(List<ChatSpaceResponse> chatSpacesResponses, RegisteredUser user);
}
