package com.fleencorp.feen.service.external.google.chat;

import com.fleencorp.feen.chat.space.model.request.external.membership.AddChatSpaceMemberRequest;
import com.fleencorp.feen.chat.space.model.request.external.membership.RemoveChatSpaceMemberRequest;
import com.fleencorp.feen.chat.space.model.request.external.membership.RetrieveChatSpaceMemberRequest;
import com.fleencorp.feen.model.response.external.google.chat.membership.GoogleAddChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.external.google.chat.membership.GoogleRemoveChatSpaceMemberResponse;
import com.fleencorp.feen.model.response.external.google.chat.membership.base.GoogleChatSpaceMemberResponse;

public interface GoogleChatMemberService {

  GoogleAddChatSpaceMemberResponse addMember(AddChatSpaceMemberRequest addChatSpaceMemberRequest);

  GoogleChatSpaceMemberResponse retrieveMember(RetrieveChatSpaceMemberRequest retrieveChatSpaceMemberRequest);

  GoogleRemoveChatSpaceMemberResponse removeMember(RemoveChatSpaceMemberRequest removeChatSpaceMemberRequest);
}
