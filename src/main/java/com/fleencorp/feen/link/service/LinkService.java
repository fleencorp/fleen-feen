package com.fleencorp.feen.link.service;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.link.model.dto.DeleteLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateStreamMusicLinkDto;
import com.fleencorp.feen.link.model.response.LinkDeleteResponse;
import com.fleencorp.feen.link.model.response.LinkStreamMusicUpdateResponse;
import com.fleencorp.feen.link.model.response.LinkUpdateResponse;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface LinkService {

  LinkStreamMusicUpdateResponse updateStreamMusicLink(UpdateStreamMusicLinkDto updateStreamMusicLinkDto, RegisteredUser user)
    throws StreamNotFoundException, FailedOperationException;

  LinkUpdateResponse updateChatSpaceLink(UpdateLinkDto updateLinkDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, FailedOperationException;

  LinkDeleteResponse deleteLinks(DeleteLinkDto deleteLinkDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, FailedOperationException;
}
