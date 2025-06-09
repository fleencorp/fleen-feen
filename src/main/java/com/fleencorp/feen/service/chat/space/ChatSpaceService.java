package com.fleencorp.feen.service.chat.space;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.ChatSpaceAlreadyDeletedException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.model.dto.chat.CreateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.UpdateChatSpaceDto;
import com.fleencorp.feen.model.dto.chat.UpdateChatSpaceStatusDto;
import com.fleencorp.feen.model.response.chat.space.CreateChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.DeleteChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.update.UpdateChatSpaceResponse;
import com.fleencorp.feen.model.response.chat.space.update.UpdateChatSpaceStatusResponse;
import com.fleencorp.feen.user.security.RegisteredUser;

public interface ChatSpaceService {

  ChatSpace findChatSpace(Long chatSpaceId) throws ChatSpaceNotFoundException;

  CreateChatSpaceResponse createChatSpace(CreateChatSpaceDto createChatSpaceDto, RegisteredUser user);

  UpdateChatSpaceResponse updateChatSpace(Long chatSpaceId, UpdateChatSpaceDto updateChatSpaceDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, NotAnAdminOfChatSpaceException,
      FailedOperationException;

  DeleteChatSpaceResponse deleteChatSpace(Long chatSpaceId, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, NotAnAdminOfChatSpaceException,
      FailedOperationException;

  DeleteChatSpaceResponse deleteChatSpaceByAdmin(Long chatSpaceId, RegisteredUser user)
    throws ChatSpaceNotFoundException;

  UpdateChatSpaceStatusResponse updateChatSpaceStatus(Long chatSpaceId, UpdateChatSpaceStatusDto updateChatSpaceStatusDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, NotAnAdminOfChatSpaceException,
    FailedOperationException;

  boolean verifyCreatorOrAdminOfChatSpace(ChatSpace chatSpace, Member member)
    throws FailedOperationException, NotAnAdminOfChatSpaceException;

  void increaseTotalMembersAndSave(ChatSpace chatSpace);

  Boolean existsByMembers(Member viewer, Member target);

  Long incrementLikeCount(Long chatSpaceId);

  Long decrementLikeCount(Long chatSpaceId);
}
