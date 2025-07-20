package com.fleencorp.feen.chat.space.service.update;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.chat.space.model.request.external.core.CreateChatSpaceRequest;
import com.fleencorp.feen.chat.space.model.request.external.core.DeleteChatSpaceRequest;
import com.fleencorp.feen.chat.space.model.request.external.core.UpdateChatSpaceRequest;
import com.fleencorp.feen.chat.space.model.request.external.membership.AddChatSpaceMemberRequest;
import com.fleencorp.feen.chat.space.model.request.external.membership.RemoveChatSpaceMemberRequest;

public interface ChatSpaceUpdateService {

  void createChatSpace(ChatSpace chatSpace, CreateChatSpaceRequest request);

  void updateChatSpace(UpdateChatSpaceRequest request);

  void deleteChatSpace(DeleteChatSpaceRequest request);

  void addMember(ChatSpaceMember chatSpaceMember, AddChatSpaceMemberRequest request);

  void removeMember(RemoveChatSpaceMemberRequest request);
}
