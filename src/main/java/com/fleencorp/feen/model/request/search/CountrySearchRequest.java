package com.fleencorp.feen.model.request.search;

import com.fleencorp.base.model.request.search.SearchRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class CountrySearchRequest extends SearchRequest {

  public static CountrySearchRequest of(int pageSize) {
    CountrySearchRequest searchRequest = new CountrySearchRequest();
    searchRequest.setPageSize(pageSize);
    return searchRequest;
  }

}
