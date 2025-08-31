package com.fleencorp.feen.model.contract;

import com.fleencorp.feen.link.model.response.base.LinkResponse;

import java.util.Collection;

public interface HasLinks extends HasId {

  void setLinks(final Collection<LinkResponse> links);
}
