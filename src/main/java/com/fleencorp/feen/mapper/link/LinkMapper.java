package com.fleencorp.feen.mapper.link;

import com.fleencorp.feen.model.domain.other.Link;
import com.fleencorp.feen.model.response.link.LinkResponse;

import java.util.List;

public interface LinkMapper {

  List<LinkResponse> toLinkResponses(List<Link> entries);
}
