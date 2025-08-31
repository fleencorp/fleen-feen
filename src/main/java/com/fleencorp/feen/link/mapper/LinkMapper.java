package com.fleencorp.feen.link.mapper;

import com.fleencorp.feen.link.model.domain.Link;
import com.fleencorp.feen.link.model.response.base.LinkResponse;

import java.util.Collection;

public interface LinkMapper {

  Collection<LinkResponse> toLinkResponses(Collection<Link> entries);
}
