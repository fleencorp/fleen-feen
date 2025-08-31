package com.fleencorp.feen.model.request.search;

import com.fleencorp.base.model.request.search.SearchRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CountrySearchRequest extends SearchRequest {

  public static CountrySearchRequest ofPageSize(final int pageSize) {
    final CountrySearchRequest searchRequest = new CountrySearchRequest();
    searchRequest.setPageSize(pageSize);

    return searchRequest;
  }

}
