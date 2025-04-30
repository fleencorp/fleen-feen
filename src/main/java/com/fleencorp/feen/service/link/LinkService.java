package com.fleencorp.feen.service.link;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.stream.FleenStreamNotFoundException;
import com.fleencorp.feen.model.dto.link.DeleteLinkDto;
import com.fleencorp.feen.model.dto.link.UpdateLinkDto;
import com.fleencorp.feen.model.dto.link.UpdateStreamMusicLinkDto;
import com.fleencorp.feen.model.request.search.LinkSearchRequest;
import com.fleencorp.feen.model.response.link.*;
import com.fleencorp.feen.model.response.link.availability.GetAvailableLinkTypeResponse;
import com.fleencorp.feen.model.response.link.availability.GetAvailableMusicLinkTypeResponse;
import com.fleencorp.feen.model.response.link.base.LinkResponse;
import com.fleencorp.feen.model.search.link.LinkSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

import java.util.List;

public interface LinkService {

  GetAvailableLinkTypeResponse getAvailableLinkType();

  GetAvailableMusicLinkTypeResponse getAvailableMusicLinkType();

  LinkSearchResult findLinks(LinkSearchRequest searchRequest, FleenUser user);

  List<LinkResponse> findChatSpaceLinks(Long chatSpaceId);

  UpdateStreamMusicLinkResponse updateStreamMusicLink(UpdateStreamMusicLinkDto updateStreamMusicLinkDto, FleenUser user)
    throws FleenStreamNotFoundException, FailedOperationException;

  UpdateLinkResponse updateLink(UpdateLinkDto updateLinkDto, FleenUser user)
    throws ChatSpaceNotFoundException, FailedOperationException;

  DeleteLinkResponse deleteLink(DeleteLinkDto deleteLinkDto, FleenUser user)
    throws ChatSpaceNotFoundException, FailedOperationException;
}
