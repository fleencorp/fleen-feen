package com.fleencorp.feen.service.chat.space;

import com.fleencorp.feen.model.dto.chat.CreateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.UpdateChatSpaceDto;
import com.fleencorp.feen.model.response.chat.space.*;
import com.fleencorp.feen.model.security.FleenUser;

public interface ChatSpaceService {

  CreateChatSpaceResponse createChatSpace(CreateChatSpaceDto createChatSpaceDto, FleenUser user);

  UpdateChatSpaceResponse updateChatSpace(Long chatSpaceId, UpdateChatSpaceDto updateChatSpaceDto, FleenUser user);

  DeleteChatSpaceResponse deleteChatSpace(Long chatSpaceId, FleenUser user);

  DeleteChatSpaceResponse deleteChatSpaceByAdmin(Long chatSpaceId, FleenUser user);

  EnableChatSpaceResponse enableChatSpace(Long chatSpaceId, FleenUser user);

  DisableChatSpaceResponse disableChatSpace(Long chatSpaceId, FleenUser user);
}
