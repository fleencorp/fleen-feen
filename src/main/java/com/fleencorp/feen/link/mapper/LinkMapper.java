package com.fleencorp.feen.link.mapper;

import com.fleencorp.feen.link.model.domain.Link;
import com.fleencorp.feen.link.model.response.base.LinkResponse;

import java.util.List;

public interface LinkMapper {

  List<LinkResponse> toLinkResponses(List<Link> entries);
}
