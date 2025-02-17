package com.fleencorp.feen.service.chat.space;

import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.dto.chat.CreateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.UpdateChatSpaceDto;
import com.fleencorp.feen.model.response.chat.space.CreateChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.DeleteChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.update.DisableChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.update.EnableChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.update.UpdateChatSpaceResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface ChatSpaceService {

  ChatSpace findChatSpace(Long chatSpaceId);

  CreateChatSpaceResponse createChatSpace(CreateChatSpaceDto createChatSpaceDto, FleenUser user);

  UpdateChatSpaceResponse updateChatSpace(Long chatSpaceId, UpdateChatSpaceDto updateChatSpaceDto, FleenUser user);

  DeleteChatSpaceResponse deleteChatSpace(Long chatSpaceId, FleenUser user);

  DeleteChatSpaceResponse deleteChatSpaceByAdmin(Long chatSpaceId, FleenUser user);

  EnableChatSpaceResponse enableChatSpace(Long chatSpaceId, FleenUser user);

  DisableChatSpaceResponse disableChatSpace(Long chatSpaceId, FleenUser user);

  void verifyCreatorOrAdminOfSpace(ChatSpace chatSpace, FleenUser user);
}
