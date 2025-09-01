package com.fleencorp.feen.link.service;

import com.fleencorp.feen.link.constant.LinkParentType;
import com.fleencorp.feen.link.model.request.LinkSearchRequest;
import com.fleencorp.feen.link.model.response.availability.GetAvailableLinkTypeResponse;
import com.fleencorp.feen.link.model.response.availability.GetAvailableMusicLinkTypeResponse;
import com.fleencorp.feen.link.model.search.LinkSearchResult;
import com.fleencorp.feen.model.contract.HasLinks;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface LinkSearchService {

  GetAvailableLinkTypeResponse getAvailableLinkTypes();

  GetAvailableMusicLinkTypeResponse getAvailableMusicLinkType();

  LinkSearchResult findLinks(LinkSearchRequest searchRequest, RegisteredUser user);

  <T extends HasLinks> void findAndSetParentLinks(final T parent, final LinkParentType linkParentType);
}
