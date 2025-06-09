package com.fleencorp.feen.link.service;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.link.model.dto.DeleteLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateStreamMusicLinkDto;
import com.fleencorp.feen.link.model.response.LinkDeleteResponse;
import com.fleencorp.feen.link.model.response.LinkUpdateResponse;
import com.fleencorp.feen.link.model.response.LinkStreamMusicUpdateResponse;
import com.fleencorp.feen.link.model.response.availability.GetAvailableLinkTypeResponse;
import com.fleencorp.feen.link.model.response.availability.GetAvailableMusicLinkTypeResponse;
import com.fleencorp.feen.link.model.response.base.LinkResponse;
import com.fleencorp.feen.link.model.search.LinkSearchResult;
import com.fleencorp.feen.model.request.search.LinkSearchRequest;
import com.fleencorp.feen.user.model.security.RegisteredUser;

import java.util.List;

public interface LinkService {

  GetAvailableLinkTypeResponse getAvailableLinkTypes();

  GetAvailableMusicLinkTypeResponse getAvailableMusicLinkType();

  LinkSearchResult findLinks(LinkSearchRequest searchRequest, RegisteredUser user);

  List<LinkResponse> findChatSpaceLinks(Long chatSpaceId);

  LinkStreamMusicUpdateResponse updateStreamMusicLink(UpdateStreamMusicLinkDto updateStreamMusicLinkDto, RegisteredUser user)
    throws StreamNotFoundException, FailedOperationException;

  LinkUpdateResponse updateLink(UpdateLinkDto updateLinkDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, FailedOperationException;

  LinkDeleteResponse deleteLink(DeleteLinkDto deleteLinkDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, FailedOperationException;
}
