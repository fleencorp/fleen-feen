package com.fleencorp.feen.link.service;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.link.model.dto.DeleteLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateStreamMusicLinkDto;
import com.fleencorp.feen.link.model.response.DeleteLinkResponse;
import com.fleencorp.feen.link.model.response.UpdateLinkResponse;
import com.fleencorp.feen.link.model.response.UpdateStreamMusicLinkResponse;
import com.fleencorp.feen.link.model.response.availability.GetAvailableLinkTypeResponse;
import com.fleencorp.feen.link.model.response.availability.GetAvailableMusicLinkTypeResponse;
import com.fleencorp.feen.link.model.response.base.LinkResponse;
import com.fleencorp.feen.link.model.search.LinkSearchResult;
import com.fleencorp.feen.model.request.search.LinkSearchRequest;
import com.fleencorp.feen.model.security.FleenUser;

import java.util.List;

public interface LinkService {

  GetAvailableLinkTypeResponse getAvailableLinkTypes();

  GetAvailableMusicLinkTypeResponse getAvailableMusicLinkType();

  LinkSearchResult findLinks(LinkSearchRequest searchRequest, FleenUser user);

  List<LinkResponse> findChatSpaceLinks(Long chatSpaceId);

  UpdateStreamMusicLinkResponse updateStreamMusicLink(UpdateStreamMusicLinkDto updateStreamMusicLinkDto, FleenUser user)
    throws StreamNotFoundException, FailedOperationException;

  UpdateLinkResponse updateLink(UpdateLinkDto updateLinkDto, FleenUser user)
    throws ChatSpaceNotFoundException, FailedOperationException;

  DeleteLinkResponse deleteLink(DeleteLinkDto deleteLinkDto, FleenUser user)
    throws ChatSpaceNotFoundException, FailedOperationException;
}
