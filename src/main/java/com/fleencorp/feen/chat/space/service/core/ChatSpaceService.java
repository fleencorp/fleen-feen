package com.fleencorp.feen.chat.space.service.core;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceAlreadyDeletedException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotAnAdminException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.dto.core.CreateChatSpaceDto;
import com.fleencorp.feen.chat.space.model.dto.core.UpdateChatSpaceDto;
import com.fleencorp.feen.chat.space.model.dto.core.UpdateChatSpaceStatusDto;
import com.fleencorp.feen.chat.space.model.response.CreateChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.DeleteChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.update.UpdateChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.update.UpdateChatSpaceStatusResponse;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.model.domain.Member;

public interface ChatSpaceService {

  ChatSpace findChatSpace(Long chatSpaceId) throws ChatSpaceNotFoundException;

  CreateChatSpaceResponse createChatSpace(CreateChatSpaceDto createChatSpaceDto, RegisteredUser user);

  UpdateChatSpaceResponse updateChatSpace(Long chatSpaceId, UpdateChatSpaceDto updateChatSpaceDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, ChatSpaceNotAnAdminException,
      FailedOperationException;

  DeleteChatSpaceResponse deleteChatSpace(Long chatSpaceId, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, ChatSpaceNotAnAdminException,
      FailedOperationException;

  DeleteChatSpaceResponse deleteChatSpaceByAdmin(Long chatSpaceId, RegisteredUser user)
    throws ChatSpaceNotFoundException;

  UpdateChatSpaceStatusResponse updateChatSpaceStatus(Long chatSpaceId, UpdateChatSpaceStatusDto updateChatSpaceStatusDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, ChatSpaceAlreadyDeletedException, ChatSpaceNotAnAdminException,
    FailedOperationException;

  boolean verifyCreatorOrAdminOfChatSpace(ChatSpace chatSpace, Member member)
    throws FailedOperationException, ChatSpaceNotAnAdminException;

  boolean verifyCreatorOrAdminOfChatSpaceNoThrow(ChatSpace chatSpace, Long memberId)
    throws FailedOperationException, ChatSpaceNotAnAdminException;

  void increaseTotalMembersAndSave(ChatSpace chatSpace);

  Boolean existsByMembers(Member viewer, Member target);

  Integer updateLikeCount(Long chatSpaceId, boolean isLiked);

  Integer updateBookmarkCount(Long chatSpaceId, boolean bookmarked);
}
