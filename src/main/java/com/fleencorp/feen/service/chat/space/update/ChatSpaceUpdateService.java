package com.fleencorp.feen.service.chat.space.update;

import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.request.chat.space.CreateChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.DeleteChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.UpdateChatSpaceRequest;
import com.fleencorp.feen.model.request.chat.space.membership.AddChatSpaceMemberRequest;
import com.fleencorp.feen.model.request.chat.space.membership.RemoveChatSpaceMemberRequest;

public interface ChatSpaceUpdateService {

  void createChatSpace(ChatSpace chatSpace, CreateChatSpaceRequest request);

  void updateChatSpace(UpdateChatSpaceRequest request);

  void deleteChatSpace(DeleteChatSpaceRequest request);

  void addMember(ChatSpaceMember chatSpaceMember, AddChatSpaceMemberRequest request);

  void removeMember(RemoveChatSpaceMemberRequest request);
}
